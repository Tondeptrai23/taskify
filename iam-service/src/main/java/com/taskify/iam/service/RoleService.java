package com.taskify.iam.service;

import com.taskify.common.error.OrganizationNotFoundException;
import com.taskify.common.error.RoleNotFoundException;
import com.taskify.iam.dto.role.CreateRoleDto;
import com.taskify.iam.entity.Role;
import com.taskify.iam.exception.DefaultRoleDeletionException;
import com.taskify.iam.mapper.RoleMapper;
import com.taskify.iam.repository.OrganizationRepository;
import com.taskify.iam.repository.RoleRepository;
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
    private final RoleRepository _roleRepository;
    private final OrganizationRepository _organizationRepository;
    private final RoleMapper _roleMapper;

    private final PermissionPrerequisiteValidator _permissionCreationValidator;

    @Autowired
    public RoleService(RoleRepository roleRepository,
                       PermissionPrerequisiteValidator permissionCreationValidator,
                       OrganizationRepository organizationRepository,
                       RoleMapper roleMapper) {
        _roleRepository = roleRepository;
        _permissionCreationValidator = permissionCreationValidator;
        _organizationRepository = organizationRepository;
        _roleMapper = roleMapper;
    }

    public Role getRole(UUID roleId, UUID organizationId) {
        return _roleRepository.findRoleByIdAndOrgIdWithPermissions(roleId, organizationId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));
    }

    public List<Role> getRoles(UUID organizationId) {
        var roles = _roleRepository.findAllWithPermissionsInOrg(organizationId.toString());

        log.info("Roles: {}", roles);

        return roles;
    }

    @Transactional
    public Role createRole(CreateRoleDto role, UUID organizationId) {
        var newRole = _roleMapper.toEntity(role);
        var createdRole = _roleRepository.save(newRole);

        var organization = _organizationRepository.findById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException("Organization not found"));
        createdRole.setOrganization(organization);

        if (role.getPermissions() != null) {
            var permissions = _permissionCreationValidator.validatePermissionPrerequisites(role.getPermissions());

            createdRole.setPermissions(new HashSet<>(permissions));
        }

        return _roleRepository.save(createdRole);
    }

    @Transactional
    public Role updateRole(UUID roleId, CreateRoleDto role, UUID organizationId) {
        var existingRole = _roleRepository.findRoleByIdAndOrgId(roleId, organizationId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));

        existingRole.setName(role.getName());
        existingRole.setDescription(role.getDescription());

        if (role.getPermissions() != null) {
            var permissions = _permissionCreationValidator.validatePermissionPrerequisites(role.getPermissions());

            existingRole.setPermissions(new HashSet<>(permissions));
        }

        return _roleRepository.save(existingRole);
    }

    @Transactional
    public void deleteRole(UUID roleId, UUID organizationId) {
        var role = _roleRepository.findRoleByIdAndOrgId(roleId, organizationId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));

        if (role.isDefault()){
            throw new DefaultRoleDeletionException();
        }

        _roleRepository.delete(role);
    }
}
