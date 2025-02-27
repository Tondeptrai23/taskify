package com.taskify.iam.service;

import com.taskify.iam.dto.role.CreateRoleDto;
import com.taskify.iam.entity.Role;
import com.taskify.common.error.ConflictException;
import com.taskify.iam.exception.DefaultRoleDeletionException;
import com.taskify.iam.exception.ResourceNotFoundException;
import com.taskify.iam.mapper.RoleMapper;
import com.taskify.iam.repository.OrganizationRepository;
import com.taskify.iam.repository.PermissionRepository;
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
    private final PermissionRepository _permissionRepository;
    private final OrganizationRepository _organizationRepository;
    private final RoleMapper _roleMapper;

    @Autowired
    public RoleService(RoleRepository roleRepository,
                       PermissionRepository permissionRepository,
                          OrganizationRepository organizationRepository,
                       RoleMapper roleMapper) {
        _roleRepository = roleRepository;
        _permissionRepository = permissionRepository;
        _organizationRepository = organizationRepository;
        _roleMapper = roleMapper;
    }

    public Role getRole(UUID roleId, UUID organizationId) {
        return _roleRepository.findRoleByIdAndOrgIdWithPermissions(roleId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found", "ROLE_NOT_FOUND"));
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
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found", "ORG_NOT_FOUND"));
        createdRole.setOrganization(organization);

        if (role.getPermissions() != null) {
            var permissions = _permissionRepository.findPermissionsByNameIn(role.getPermissions());

            createdRole.setPermissions(new HashSet<>(permissions));
        }

        return _roleRepository.save(createdRole);
    }

    @Transactional
    public Role updateRole(UUID roleId, CreateRoleDto role, UUID organizationId) {
        var existingRole = _roleRepository.findRoleByIdAndOrgId(roleId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found", "ROLE_NOT_FOUND"));

        existingRole.setName(role.getName());
        existingRole.setDescription(role.getDescription());

        if (role.getPermissions() != null) {
            var permissions = _permissionRepository.findPermissionsByNameIn(role.getPermissions());

            existingRole.setPermissions(new HashSet<>(permissions));
        }

        return _roleRepository.save(existingRole);
    }

    @Transactional
    public void deleteRole(UUID roleId, UUID organizationId) {
        var role = _roleRepository.findRoleByIdAndOrgId(roleId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found", "ROLE_NOT_FOUND"));

        if (role.isDefault()){
            throw new DefaultRoleDeletionException();
        }

        _roleRepository.delete(role);
    }
}
