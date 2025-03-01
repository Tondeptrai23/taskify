package com.taskify.organization.service;

import com.taskify.common.error.OrganizationNotFoundException;
import com.taskify.organization.dto.membership.MembershipCollectionRequest;
import com.taskify.organization.dto.role.OrganizationRoleDto;
import com.taskify.organization.entity.LocalUser;
import com.taskify.organization.entity.Membership;
import com.taskify.organization.entity.Organization;

import com.taskify.organization.integration.IamServiceClient;
import com.taskify.organization.integration.IamWebClient;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MembershipService {
    private final MembershipRepository membershipRepository;
    private final OrganizationRepository organizationRepository;
    private final LocalUserService localUserService;
    private final IamServiceClient iamServiceClient;

    @Autowired
    public MembershipService(
            MembershipRepository membershipRepository,
            OrganizationRepository organizationRepository,
            LocalUserService localUserService,
            IamServiceClient iamServiceClient
    ) {
        this.membershipRepository = membershipRepository;
        this.organizationRepository = organizationRepository;
        this.localUserService = localUserService;
        this.iamServiceClient = iamServiceClient;
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

        return updateOrganizationMemberships(organization, userIds, defaultRole.getId(), false);
    }

    @Transactional
    public List<Membership> updateMembers(UUID orgId, List<UUID> userIds, UUID roleId) {
        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new OrganizationNotFoundException("Organization not found"));

        OrganizationRoleDto role = iamServiceClient.getOrganizationRoleById(organization.getId(), roleId);

        return updateOrganizationMemberships(organization, userIds, role.getId(), true);
    }

    @Transactional
    public void deactivateMembers(UUID orgId, List<UUID> userIds) {
        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new OrganizationNotFoundException("Organization not found"));

        List<Membership> memberships = membershipRepository.findAllByOrgIdAndUserIdIn(orgId, userIds);

        memberships.forEach(membership -> membership.setActive(false));

        membershipRepository.saveAll(memberships);
    }

    /**
     * Core method to handle both adding and updating members
     *
     * @param organization    The organization
     * @param userIds         List of user IDs to add/update
     * @param deactivateFirst Whether to deactivate existing memberships first
     */
    private List<Membership> updateOrganizationMemberships(
            Organization organization,
            List<UUID> userIds,
            UUID roleId,
            boolean deactivateFirst
    ) {
        if (deactivateFirst) {
            deactivateMembers(organization.getId(), userIds);
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
                .map(user -> new Membership(organization, user))
                .collect(Collectors.toList());

        // Save all memberships
        membershipRepository.saveAll(existingMemberships);
        membershipRepository.saveAll(newMemberships);

        List<Membership> allMemberships = new ArrayList<>();
        allMemberships.addAll(existingMemberships);
        allMemberships.addAll(newMemberships);
        return allMemberships;
    }
}