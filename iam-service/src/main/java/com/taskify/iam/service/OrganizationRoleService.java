package com.taskify.iam.service;

import com.taskify.common.error.resource.OrganizationNotFoundException;
import com.taskify.common.error.resource.RoleNotFoundException;
import com.taskify.iam.dto.role.CreateOrganizationRoleDto;
import com.taskify.iam.entity.LocalOrganization;
import com.taskify.iam.entity.Role;
import com.taskify.iam.exception.DefaultRoleDeletionException;
import com.taskify.iam.mapper.OrganizationRoleMapper;
import com.taskify.iam.repository.OrganizationRepository;
import com.taskify.iam.repository.OrganizationRoleRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class OrganizationRoleService {
    private final OrganizationRoleRepository _orgRoleRepository;
    private final OrganizationRepository _organizationRepository;
    private final OrganizationRoleMapper _roleMapper;
    private final PermissionPrerequisiteValidator _permissionCreationValidator;

    @Autowired
    public OrganizationRoleService(OrganizationRoleRepository roleRepository,
                                   PermissionPrerequisiteValidator permissionCreationValidator,
                                   OrganizationRepository organizationRepository,
                                   OrganizationRoleMapper roleMapper) {
        this._orgRoleRepository = roleRepository;
        this._permissionCreationValidator = permissionCreationValidator;
        this._organizationRepository = organizationRepository;
        this._roleMapper = roleMapper;
    }

    public Role getRole(UUID roleId, UUID organizationId) {
        return _orgRoleRepository.findRoleByIdAndOrgIdWithPermissions(roleId, organizationId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));
    }

    public List<Role> getRoles(UUID organizationId) {
        var roles = _orgRoleRepository.findAllWithPermissionsInOrg(organizationId);
        log.info("Roles: {}", roles);
        return roles;
    }

    @Transactional
    public Role createRole(CreateOrganizationRoleDto roleDto, UUID organizationId) {
        LocalOrganization organization = _organizationRepository.findById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException("Organization not found"));

        Role newRole = _roleMapper.toEntity(roleDto);
        newRole.setOrganization(organization);

        if (roleDto.getPermissions() != null) {
            var permissions = _permissionCreationValidator.validatePermissionPrerequisites(roleDto.getPermissions());
            newRole.setPermissions(new HashSet<>(permissions));
        }

        return _orgRoleRepository.save(newRole);
    }

    @Transactional
    public Role updateRole(UUID roleId, CreateOrganizationRoleDto roleDto, UUID organizationId) {
        Role existingRole = _orgRoleRepository.findRoleByIdAndOrgId(roleId, organizationId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));

        existingRole.setName(roleDto.getName());
        existingRole.setDescription(roleDto.getDescription());

        if (roleDto.getPermissions() != null) {
            var permissions = _permissionCreationValidator.validatePermissionPrerequisites(roleDto.getPermissions());
            existingRole.setPermissions(new HashSet<>(permissions));
        }

        return _orgRoleRepository.save(existingRole);
    }

    @Transactional
    public void deleteRole(UUID roleId, UUID organizationId) {
        Role role = _orgRoleRepository.findRoleByIdAndOrgId(roleId, organizationId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));

        if (role.isDefault()) {
            throw new DefaultRoleDeletionException();
        }

        _orgRoleRepository.delete(role);
    }

    public Role getDefaultRole(UUID organizationId) {
        return _orgRoleRepository.findDefaultRoleByOrgId(organizationId)
                .orElseThrow(() -> new RoleNotFoundException("Default role not found"));
    }

    @Transactional
    public Role setDefaultRole(UUID roleId, UUID organizationId) {
        Role role = _orgRoleRepository.findRoleByIdAndOrgId(roleId, organizationId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));

        Role defaultRole = _orgRoleRepository.findDefaultRoleByOrgId(organizationId)
                .orElseThrow(() -> new RoleNotFoundException("Default role not found"));

        if (defaultRole.getId().equals(roleId)) {
            return role;
        }

        defaultRole.setDefault(false);
        _orgRoleRepository.save(defaultRole);

        role.setDefault(true);
        return _orgRoleRepository.save(role);
    }
}