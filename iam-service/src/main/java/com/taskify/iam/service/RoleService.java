package com.taskify.iam.service;

import com.taskify.common.error.OrganizationNotFoundException;
import com.taskify.common.error.RoleNotFoundException;
import com.taskify.iam.dto.role.CreateRoleDto;
import com.taskify.iam.entity.OrganizationRole;
import com.taskify.iam.exception.DefaultRoleDeletionException;
import com.taskify.iam.mapper.RoleMapper;
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
public class RoleService {
    private final OrganizationRoleRepository _orgRoleRepository;
    private final OrganizationRepository _organizationRepository;
    private final RoleMapper _roleMapper;

    private final PermissionPrerequisiteValidator _permissionCreationValidator;

    @Autowired
    public RoleService(OrganizationRoleRepository roleRepository,
                       PermissionPrerequisiteValidator permissionCreationValidator,
                       OrganizationRepository organizationRepository,
                       RoleMapper roleMapper) {
        _orgRoleRepository = roleRepository;
        _permissionCreationValidator = permissionCreationValidator;
        _organizationRepository = organizationRepository;
        _roleMapper = roleMapper;
    }

    public OrganizationRole getRole(UUID roleId, UUID organizationId) {
        return _orgRoleRepository.findRoleByIdAndOrgIdWithPermissions(roleId, organizationId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));
    }

    public List<OrganizationRole> getRoles(UUID organizationId) {
        var roles = _orgRoleRepository.findAllWithPermissionsInOrg(organizationId.toString());

        log.info("Roles: {}", roles);

        return roles;
    }

    @Transactional
    public OrganizationRole createRole(CreateRoleDto role, UUID organizationId) {
        var newRole = _roleMapper.toEntity(role);
        var createdRole = _orgRoleRepository.save(newRole);

        var organization = _organizationRepository.findById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException("Organization not found"));
        createdRole.setOrganization(organization);

        if (role.getPermissions() != null) {
            var permissions = _permissionCreationValidator.validatePermissionPrerequisites(role.getPermissions());

            createdRole.setPermissions(new HashSet<>(permissions));
        }

        return _orgRoleRepository.save(createdRole);
    }

    @Transactional
    public OrganizationRole updateRole(UUID roleId, CreateRoleDto role, UUID organizationId) {
        var existingRole = _orgRoleRepository.findRoleByIdAndOrgId(roleId, organizationId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));

        existingRole.setName(role.getName());
        existingRole.setDescription(role.getDescription());

        if (role.getPermissions() != null) {
            var permissions = _permissionCreationValidator.validatePermissionPrerequisites(role.getPermissions());

            existingRole.setPermissions(new HashSet<>(permissions));
        }

        return _orgRoleRepository.save(existingRole);
    }

    @Transactional
    public void deleteRole(UUID roleId, UUID organizationId) {
        var role = _orgRoleRepository.findRoleByIdAndOrgId(roleId, organizationId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));

        if (role.isDefault()){
            throw new DefaultRoleDeletionException();
        }

        _orgRoleRepository.delete(role);
    }

    public OrganizationRole getDefaultRole(UUID organizationId) {
        return _orgRoleRepository.findDefaultRoleByOrgId(organizationId)
                .orElseThrow(() -> new RoleNotFoundException("Default role not found"));
    }

    @Transactional
    public OrganizationRole setDefaultRole(UUID roleId, UUID organizationId) {
        var role = _orgRoleRepository.findRoleByIdAndOrgId(roleId, organizationId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));

        var defaultRole = _orgRoleRepository.findDefaultRoleByOrgId(organizationId)
                .orElseThrow(() -> new RoleNotFoundException("Default role not found"));

        if (defaultRole.getId().equals(roleId)) {
            return role;
        }

        defaultRole.setDefault(false);
        role.setDefault(true);

        _orgRoleRepository.save(defaultRole);
        return _orgRoleRepository.save(role);
    }
}

