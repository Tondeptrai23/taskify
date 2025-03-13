package com.taskify.organization.service;

import com.taskify.commoncore.error.resource.OrganizationNotFoundException;
import com.taskify.organization.dto.membership.MembershipCollectionRequest;
import com.taskify.organization.dto.role.OrganizationRoleDto;
import com.taskify.organization.entity.LocalUser;
import com.taskify.organization.entity.Membership;
import com.taskify.organization.entity.Organization;
import com.taskify.organization.event.MembershipEventPublisher;
import com.taskify.organization.integration.IamServiceClient;
import com.taskify.organization.repository.MembershipRepository;
import com.taskify.organization.repository.OrganizationRepository;
import com.taskify.organization.specification.MembershipSpecifications;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MembershipService {
    private final MembershipRepository membershipRepository;
    private final OrganizationRepository organizationRepository;
    private final LocalUserService localUserService;
    private final IamServiceClient iamServiceClient;
    private final MembershipEventPublisher membershipEventPublisher;

    @Autowired
    public MembershipService(
            MembershipRepository membershipRepository,
            OrganizationRepository organizationRepository,
            LocalUserService localUserService,
            IamServiceClient iamServiceClient,
            MembershipEventPublisher membershipEventPublisher
    ) {
        this.membershipRepository = membershipRepository;
        this.organizationRepository = organizationRepository;
        this.localUserService = localUserService;
        this.iamServiceClient = iamServiceClient;
        this.membershipEventPublisher = membershipEventPublisher;
    }

    public Page<Membership> getOrganizationMembers(UUID orgId, MembershipCollectionRequest filter) {
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

        return membershipRepository.findAll(
                MembershipSpecifications.withFilters(orgId, filter),
                pageable
        );
    }

    @Transactional
    public List<Membership> addMembers(UUID orgId, List<UUID> userIds) {
        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new OrganizationNotFoundException("Organization not found"));

        OrganizationRoleDto defaultRole = iamServiceClient.getDefaultOrganizationRole(organization.getId());

        List<Membership> memberships = updateOrganizationMemberships(organization, userIds, defaultRole.getId(), false);

        // Publish events for new memberships
        memberships.stream()
                .filter(m -> m.getJoinedAt() != null && m.getJoinedAt().isEqual(ZonedDateTime.now().withNano(0)))
                .forEach(membershipEventPublisher::publishMemberAddedEvent);

        return memberships;
    }

    @Transactional
    public List<Membership> updateMembers(UUID orgId, List<UUID> userIds, UUID roleId) {
        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new OrganizationNotFoundException("Organization not found"));

        OrganizationRoleDto role = iamServiceClient.getOrganizationRoleById(organization.getId(), roleId);

        // Get existing memberships to track old role IDs
        List<Membership> existingMemberships = membershipRepository.findAllByOrgIdAndUserIdIn(orgId, userIds);

        // Create a map of userId to oldRoleId
        Map<UUID, UUID> oldRoleMap = existingMemberships.stream()
                .collect(Collectors.toMap(
                        m -> m.getUser().getId(),
                        Membership::getRoleId
                ));

        List<Membership> memberships = updateOrganizationMemberships(organization, userIds, role.getId(), true);

        // Publish events for updated roles
        memberships.forEach(membership -> {
            UUID userId = membership.getUser().getId();
            UUID oldRoleId = oldRoleMap.get(userId);

            // Only publish if the role actually changed
            if (oldRoleId != null && !oldRoleId.equals(membership.getRoleId())) {
                membershipEventPublisher.publishMemberRoleUpdatedEvent(membership, oldRoleId);
            }
        });

        return memberships;
    }

    @Transactional
    public void deactivateMembers(UUID orgId, List<UUID> userIds) {
        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new OrganizationNotFoundException("Organization not found"));

        List<Membership> memberships = membershipRepository.findAllByOrgIdAndUserIdIn(orgId, userIds);

        // Store membership details before deactivation for events
        for (Membership membership : memberships) {
            UUID membershipId = membership.getId();
            UUID userId = membership.getUser().getId();

            membership.setActive(false);
            membershipRepository.save(membership);

            // Publish the member removed event
            membershipEventPublisher.publishMemberRemovedEvent(membershipId, orgId, userId);
        }
    }

    /**
     * Core method to handle both adding and updating members
     *
     * @param organization    The organization
     * @param userIds         List of user IDs to add/update
     * @param roleId          Role ID to assign
     * @param deactivateFirst Whether to deactivate existing memberships first
     */
    private List<Membership> updateOrganizationMemberships(
            Organization organization,
            List<UUID> userIds,
            UUID roleId,
            boolean deactivateFirst
    ) {
        if (deactivateFirst) {
            // We don't want to publish events here since we're just modifying
            // Call the internal method directly
            deactivateMembersInternal(organization.getId(), userIds);
        }

        List<LocalUser> users = localUserService.getUsersByIds(userIds);
        List<Membership> existingMemberships = membershipRepository
                .findAllByOrgIdAndUserIdIn(organization.getId(), userIds);

        // Reactivate existing memberships
        existingMemberships.forEach(membership -> {
            membership.setActive(true);
            membership.setJoinedAt(ZonedDateTime.now());
            membership.setRoleId(roleId);
        });

        // Create new memberships for users who never joined
        Set<UUID> existingUserIds = existingMemberships.stream()
                .map(m -> m.getUser().getId())
                .collect(Collectors.toSet());

        List<Membership> newMemberships = users.stream()
                .filter(user -> !existingUserIds.contains(user.getId()))
                .map(user -> {
                    Membership membership = new Membership(organization, user);
                    membership.setRoleId(roleId);
                    return membership;
                })
                .collect(Collectors.toList());

        // Save all memberships
        membershipRepository.saveAll(existingMemberships);
        membershipRepository.saveAll(newMemberships);

        List<Membership> allMemberships = new ArrayList<>();
        allMemberships.addAll(existingMemberships);
        allMemberships.addAll(newMemberships);
        return allMemberships;
    }

    // Private method for internal deactivation without publishing events
    private void deactivateMembersInternal(UUID orgId, List<UUID> userIds) {
        List<Membership> memberships = membershipRepository.findAllByOrgIdAndUserIdIn(orgId, userIds);
        memberships.forEach(membership -> membership.setActive(false));
        membershipRepository.saveAll(memberships);
    }
}