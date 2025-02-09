package com.taskify.user.service;

import com.taskify.user.dto.organization.OrganizationMemberCollectionRequest;
import com.taskify.user.entity.Organization;
import com.taskify.user.entity.OrganizationRole;
import com.taskify.user.entity.User;
import com.taskify.user.entity.UserOrganization;
import com.taskify.user.exception.OrganizationNotFoundException;
import com.taskify.user.exception.ResourceNotFoundException;
import com.taskify.user.mapper.UserMapper;
import com.taskify.user.mapper.UserOrganizationMapper;
import com.taskify.user.repository.OrganizationRepository;
import com.taskify.user.repository.OrganizationRoleRepository;
import com.taskify.user.repository.UserOrganizationRepository;
import com.taskify.user.repository.UserRepository;
import com.taskify.user.specification.UserOrganizationSpecifications;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserOrganizationService {
    private final UserRepository _userRepository;
    private final UserOrganizationRepository _userOrganizationRepository;
    private final OrganizationService _organizationService;

    @Autowired
    public UserOrganizationService(UserRepository userRepository,
                                   UserOrganizationRepository userOrganizationRepository,
                                   OrganizationService organizationService) {
        _userRepository = userRepository;
        _userOrganizationRepository = userOrganizationRepository;
        _organizationService = organizationService;

    }

    public Page<UserOrganization> getOrganizationMembers(UUID orgId, OrganizationMemberCollectionRequest filter) {
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

        return _userOrganizationRepository.findAll(
                UserOrganizationSpecifications.withFilters(orgId, filter),
                pageable
        );
    }

    @Transactional
    public List<UserOrganization> addMembers(UUID orgId, List<UUID> userIds) {
        Organization organization = _organizationService.getOrganizationById(orgId);
        OrganizationRole defaultRole = _organizationService.getDefaultRole();

        return updateOrganizationMemberships(organization, userIds, defaultRole, false);
    }

    @Transactional
    public List<UserOrganization> updateMembers(UUID orgId, List<UUID> userIds, UUID roleId) {
        Organization organization = _organizationService.getOrganizationById(orgId);
        OrganizationRole role = _organizationService.getRoleOrThrow(roleId);

        return updateOrganizationMemberships(organization, userIds, role, true);
    }

    @Transactional
    public void deactivateMembers(UUID orgId, List<UUID> userIds) {
        Organization organization = _organizationService.getOrganizationById(orgId);

        List<UserOrganization> userOrganizations = _userOrganizationRepository.findAllByOrgIdAndUserIdIn(orgId, userIds);

        userOrganizations.forEach(membership -> membership.setActive(false));

        _userOrganizationRepository.saveAll(userOrganizations);
    }

    /**
     * Core method to handle both adding and updating members
     *
     * @param organization    The organization
     * @param userIds         List of user IDs to add/update
     * @param role            Role to assign
     * @param deactivateFirst Whether to deactivate existing memberships first
     */
    private List<UserOrganization> updateOrganizationMemberships(
            Organization organization,
            List<UUID> userIds,
            OrganizationRole role,
            boolean deactivateFirst
    ) {
        if (deactivateFirst) {
            deactivateMembers(organization.getId(), userIds);
        }

        List<User> users = _userRepository.findAllById(userIds);
        List<UserOrganization> existingMemberships = _userOrganizationRepository
                .findAllByOrgIdAndUserIdIn(organization.getId(), userIds);

        // Reactivate existing memberships
        existingMemberships.forEach(membership -> {
            membership.setActive(true);
            membership.setJoinedAt(ZonedDateTime.now());
            membership.setRole(role);
        });

        // Create new memberships for users who never joined
        Set<UUID> existingUserIds = existingMemberships.stream()
                .map(uo -> uo.getUser().getId())
                .collect(Collectors.toSet());

        List<UserOrganization> newMemberships = users.stream()
                .filter(user -> !existingUserIds.contains(user.getId()))
                .map(user -> new UserOrganization(organization, user, role))
                .collect(Collectors.toList());

        // Save all memberships
        _userOrganizationRepository.saveAll(existingMemberships);
        _userOrganizationRepository.saveAll(newMemberships);

        List<UserOrganization> allMemberships = new ArrayList<>();
        allMemberships.addAll(existingMemberships);
        allMemberships.addAll(newMemberships);
        return allMemberships;
    }
}
