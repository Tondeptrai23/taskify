package com.taskify.project.service;

import com.taskify.commoncore.error.resource.ProjectNotFoundException;
import com.taskify.project.dto.membership.MembershipCollectionRequest;
import com.taskify.project.dto.project.ProjectRoleDto;
import com.taskify.project.entity.LocalUser;
import com.taskify.project.entity.Project;
import com.taskify.project.entity.ProjectMembership;
import com.taskify.project.event.ProjectMembershipEventPublisher;
import com.taskify.project.event.ProjectMembershipEventPublisher.ProjectMembershipWithOldRole;
import com.taskify.project.integration.IamServiceClient;
import com.taskify.project.repository.LocalUserRepository;
import com.taskify.project.repository.ProjectMembershipRepository;
import com.taskify.project.repository.ProjectRepository;
import com.taskify.project.specification.ProjectMembershipSpecifications;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProjectMembershipService {
    private final ProjectMembershipRepository projectMembershipRepository;
    private final ProjectRepository projectRepository;
    private final IamServiceClient iamServiceClient;
    private final ProjectMembershipEventPublisher projectMembershipEventPublisher;
    private final LocalUserRepository localUserRepository;

    @Autowired
    public ProjectMembershipService(
            ProjectMembershipRepository projectMembershipRepository,
            ProjectRepository projectRepository,
            IamServiceClient iamServiceClient,
            ProjectMembershipEventPublisher projectMembershipEventPublisher,
            LocalUserRepository localUserRepository) {
        this.projectMembershipRepository = projectMembershipRepository;
        this.projectRepository = projectRepository;
        this.iamServiceClient = iamServiceClient;
        this.projectMembershipEventPublisher = projectMembershipEventPublisher;
        this.localUserRepository = localUserRepository;
    }

    public Page<ProjectMembership> getProjectMembers(UUID projectId, MembershipCollectionRequest filter) {
        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                Sort.by(
                        filter.getSortDirection().equalsIgnoreCase("asc")
                                ? Sort.Direction.ASC
                                : Sort.Direction.DESC,
                        filter.getSortBy()
                )
        );

        return projectMembershipRepository.findAll(
                ProjectMembershipSpecifications.withFilters(projectId, filter),
                pageable
        );
    }

    @Transactional
    public List<ProjectMembership> addMembers(UUID projectId, List<UUID> userIds, UUID organizationId) {
        Project project = projectRepository.findByIdAndOrganizationId(projectId, organizationId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));

        // Get default role for the project
        ProjectRoleDto defaultRole = iamServiceClient.getDefaultProjectRole(project.getId());

        List<ProjectMembership> memberships = updateProjectMemberships(project, userIds, defaultRole.getId(), false);

        // Publish batch event for new memberships
        List<ProjectMembership> newMemberships = memberships.stream()
                .filter(m -> m.getJoinedAt() != null && m.getJoinedAt().isEqual(ZonedDateTime.now().withNano(0)))
                .collect(Collectors.toList());

        if (!newMemberships.isEmpty()) {
            projectMembershipEventPublisher.publishProjectMemberAddedEvent(project.getId(), project.getOrganizationId(), newMemberships);
        }

        return memberships;
    }

    @Transactional
    public List<ProjectMembership> updateMembers(UUID projectId, List<UUID> userIds, UUID roleId, UUID organizationId) {
        Project project = projectRepository.findByIdAndOrganizationId(projectId, organizationId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));

        // Validate the role exists for this project
        ProjectRoleDto role = iamServiceClient.getProjectRoleById(project.getId(), roleId);

        // Get existing memberships to track old role IDs
        List<ProjectMembership> existingMemberships = projectMembershipRepository.findAllByProjectIdAndUserIdIn(projectId, userIds);

        // Create a map of userId to oldRoleId
        Map<UUID, UUID> oldRoleMap = existingMemberships.stream()
                .collect(Collectors.toMap(
                        m -> m.getUser().getId(),
                        ProjectMembership::getRoleId
                ));

        List<ProjectMembership> memberships = updateProjectMemberships(project, userIds, role.getId(), true);

        // Create list of memberships with old roles
        List<ProjectMembershipWithOldRole> membershipsWithOldRoles = memberships.stream()
                .filter(membership -> {
                    UUID userId = membership.getUser().getId();
                    UUID oldRoleId = oldRoleMap.get(userId);
                    // Only include if the role actually changed
                    return oldRoleId != null && !oldRoleId.equals(membership.getRoleId());
                })
                .map(membership -> new ProjectMembershipWithOldRole(
                        membership,
                        oldRoleMap.get(membership.getUser().getId())
                ))
                .collect(Collectors.toList());

        // Publish batch event for role updates
        if (!membershipsWithOldRoles.isEmpty()) {
            projectMembershipEventPublisher.publishProjectMemberRoleUpdatedEvent(
                    project.getId(),
                    project.getOrganizationId(),
                    role.getId(),
                    membershipsWithOldRoles
            );
        }

        return memberships;
    }

    @Transactional
    public void deactivateMembers(UUID projectId, List<UUID> userIds, UUID organizationId) {
        Project project = projectRepository.findByIdAndOrganizationId(projectId, organizationId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));

        List<ProjectMembership> memberships = projectMembershipRepository.findAllByProjectIdAndUserIdIn(projectId, userIds);

        // Store membership details before deactivation for batch event
        Map<UUID, UUID> membershipIdToUserIdMap = new HashMap<>();
        for (ProjectMembership membership : memberships) {
            UUID membershipId = membership.getId();
            UUID userId = membership.getUser().getId();

            membershipIdToUserIdMap.put(membershipId, userId);
            membership.setActive(false);
        }

        projectMembershipRepository.saveAll(memberships);

        // Publish batch event for removals
        if (!membershipIdToUserIdMap.isEmpty()) {
            projectMembershipEventPublisher.publishProjectMemberRemovedEvent(
                    project.getId(),
                    project.getOrganizationId(),
                    membershipIdToUserIdMap
            );
        }
    }

    /**
     * Core method to handle both adding and updating members
     *
     * @param project         The project
     * @param userIds         List of user IDs to add/update
     * @param roleId          Role ID to assign
     * @param deactivateFirst Whether to deactivate existing memberships first
     */
    private List<ProjectMembership> updateProjectMemberships(
            Project project,
            List<UUID> userIds,
            UUID roleId,
            boolean deactivateFirst
    ) {
        if (deactivateFirst) {
            // We don't want to publish events here since we're just modifying
            // Call the internal method directly
            deactivateMembersInternal(project.getId(), userIds);
        }

        List<LocalUser> users = localUserRepository.findAllById(userIds);
        List<ProjectMembership> existingMemberships = projectMembershipRepository
                .findAllByProjectIdAndUserIdIn(project.getId(), userIds);

        // Reactivate existing memberships
        existingMemberships.forEach(membership -> {
            membership.setActive(true);
            membership.setRoleId(roleId);
        });

        // Create new memberships for users who never joined
        Set<UUID> existingUserIds = existingMemberships.stream()
                .map(m -> m.getUser().getId())
                .collect(Collectors.toSet());

        List<ProjectMembership> newMemberships = users.stream()
                .filter(user -> !existingUserIds.contains(user.getId()))
                .map(user -> {
                    ProjectMembership membership = new ProjectMembership(project, user);
                    membership.setRoleId(roleId);
                    return membership;
                })
                .collect(Collectors.toList());

        // Save all memberships
        projectMembershipRepository.saveAll(existingMemberships);
        projectMembershipRepository.saveAll(newMemberships);

        List<ProjectMembership> allMemberships = new ArrayList<>();
        allMemberships.addAll(existingMemberships);
        allMemberships.addAll(newMemberships);
        return allMemberships;
    }

    // Private method for internal deactivation without publishing events
    private void deactivateMembersInternal(UUID projectId, List<UUID> userIds) {
        List<ProjectMembership> memberships = projectMembershipRepository.findAllByProjectIdAndUserIdIn(projectId, userIds);
        memberships.forEach(membership -> membership.setActive(false));
        projectMembershipRepository.saveAll(memberships);
    }
}