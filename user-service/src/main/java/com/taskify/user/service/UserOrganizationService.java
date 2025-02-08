package com.taskify.user.service;

import com.taskify.user.dto.organization.OrganizationMemberCollectionRequest;
import com.taskify.user.entity.Organization;
import com.taskify.user.entity.OrganizationRole;
import com.taskify.user.entity.User;
import com.taskify.user.entity.UserOrganization;
import com.taskify.user.exception.OrganizationNotFoundException;
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

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserOrganizationService {
    private final UserMapper _userMapper;
    private final UserOrganizationMapper _userOrganizationMapper;
    private final UserRepository _userRepository;
    private final OrganizationRepository _organizationRepository;
    private final UserOrganizationRepository _userOrganizationRepository;
    private final OrganizationRoleRepository _organizationRoleRepository;

    @Autowired
    public UserOrganizationService(UserMapper userMapper,
                                   UserOrganizationMapper userOrganizationMapper,
                                   UserRepository userRepository,
                                   OrganizationRepository organizationRepository,
                                   UserOrganizationRepository userOrganizationRepository,
                                   OrganizationRoleRepository organizationRoleRepository) {
        _userMapper = userMapper;
        _userOrganizationMapper = userOrganizationMapper;
        _userRepository = userRepository;
        _organizationRepository = organizationRepository;
        _userOrganizationRepository = userOrganizationRepository;
        _organizationRoleRepository = organizationRoleRepository;
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
    public Organization addMembers(UUID orgId, List<UUID> userIds) {
        Organization organization = _organizationRepository.findById(orgId).orElseThrow(
                () -> new OrganizationNotFoundException("Organization not found")
        );

        OrganizationRole defaultRole = _organizationRoleRepository.getOrganizationRoleByDefault(true);
        List<User> users = _userRepository.findAllById(userIds);

        // Get existing memberships to avoid duplicates
        List<UserOrganization> existingMemberships = _userOrganizationRepository.findAllByOrgIdAndUserIdIn(orgId, userIds);
        Set<UUID> existingUserIds = existingMemberships.stream()
                .map(uo -> uo.getUser().getId())
                .collect(Collectors.toSet());

        List<UserOrganization> newMemberships = users.stream()
                .filter(user -> !existingUserIds.contains(user.getId()))
                .map(user -> {
                    UserOrganization userOrg = new UserOrganization();
                    userOrg.setOrganization(organization);
                    userOrg.setUser(user);
                    userOrg.setRole(defaultRole);
                    return userOrg;
                })
                .collect(Collectors.toList());

        _userOrganizationRepository.saveAll(newMemberships);

        return organization;
    }

    @Transactional
    public Organization updateMembers(UUID orgId, List<UUID> userIds, UUID roleId) {
        OrganizationRole role = _organizationRoleRepository.findById(roleId).orElseThrow(
                () -> new OrganizationNotFoundException("Role not found")
        );


        removeMembers(orgId, userIds);

        Organization organization = _organizationRepository.findById(orgId).orElse(null);
        List<User> users = _userRepository.findAllById(userIds);

        List<UserOrganization> userOrganizations = userIds.stream()
                .map(userId -> {
                    UserOrganization userOrganization = new UserOrganization();
                    userOrganization.setOrganization(organization);
                    userOrganization.setUser(users.stream()
                            .filter(user -> user.getId().equals(userId))
                            .findFirst()
                            .orElse(null));
                    userOrganization.setRole(role);
                    return userOrganization;
                })
                .collect(Collectors.toList());

        _userOrganizationRepository.saveAll(userOrganizations);

        return organization;
    }

    @Transactional
    public Organization removeMembers(UUID orgId, List<UUID> userIds) {
        Organization organization = _organizationRepository.findById(orgId).orElseThrow(
                () -> new OrganizationNotFoundException("Organization not found")
        );
        
        List<UserOrganization> userOrganizations = _userOrganizationRepository.findAllByOrgIdAndUserIdIn(orgId, userIds);

        _userOrganizationRepository.deleteAll(userOrganizations);

        return _organizationRepository.findById(orgId).orElse(null);
    }
}
