package com.taskify.iam.service.role;

import com.taskify.commoncore.error.resource.RoleNotFoundException;
import com.taskify.iam.dto.role.CreateRoleDto;
import com.taskify.iam.entity.Context;
import com.taskify.iam.entity.Permission;
import com.taskify.iam.entity.Role;
import com.taskify.iam.exception.ContextNotFoundException;
import com.taskify.iam.exception.DefaultRoleDeletionException;
import com.taskify.iam.repository.ContextRepository;
import com.taskify.iam.repository.RoleRepository;
import com.taskify.iam.service.permission.PermissionPrerequisiteValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final ContextRepository contextRepository;
    private final PermissionPrerequisiteValidator permissionPrerequisiteValidator;

    @Autowired
    public RoleService(
            RoleRepository roleRepository,
            ContextRepository contextRepository,
            PermissionPrerequisiteValidator permissionPrerequisiteValidator) {
        this.roleRepository = roleRepository;
        this.contextRepository = contextRepository;
        this.permissionPrerequisiteValidator = permissionPrerequisiteValidator;
    }

    public Role getRole(UUID roleId, UUID contextId) {
        return roleRepository.findRoleByIdAndContextIdWithPermissions(roleId, contextId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));
    }

    public List<Role> getRoles(UUID contextId) {
        return roleRepository.findAllWithPermissionsByContextId(contextId);
    }

    @Transactional
    public Role createRole(CreateRoleDto createRoleDto, UUID contextId) {
        Context context = contextRepository.findById(contextId)
                .orElseThrow(() -> new ContextNotFoundException("Context not found"));

        Role role = new Role();
        role.setName(createRoleDto.getName());
        role.setDescription(createRoleDto.getDescription());
        role.setContext(context);

        if (createRoleDto.getPermissions() != null) {
            List<Permission> permissions = permissionPrerequisiteValidator
                    .validatePermissionPrerequisites(createRoleDto.getPermissions());
            role.setPermissions(new HashSet<>(permissions));
        }

        return roleRepository.save(role);
    }

    @Transactional
    public Role updateRole(UUID roleId, CreateRoleDto roleDto, UUID contextId) {
        Role existingRole = roleRepository.findRoleByIdAndContextId(roleId, contextId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));

        existingRole.setName(roleDto.getName());
        existingRole.setDescription(roleDto.getDescription());

        if (roleDto.getPermissions() != null) {
            List<Permission> permissions = permissionPrerequisiteValidator
                    .validatePermissionPrerequisites(roleDto.getPermissions());
            existingRole.setPermissions(new HashSet<>(permissions));
        }

        return roleRepository.save(existingRole);
    }

    @Transactional
    public void deleteRole(UUID roleId, UUID contextId) {
        Role role = roleRepository.findRoleByIdAndContextId(roleId, contextId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));

        if (role.isDefault()) {
            throw new DefaultRoleDeletionException();
        }

        roleRepository.delete(role);
    }

    public Role getDefaultRole(UUID contextId) {
        return roleRepository.findDefaultRoleByContextId(contextId)
                .orElseThrow(() -> new RoleNotFoundException("Default role not found"));
    }

    @Transactional
    public Role setDefaultRole(UUID roleId, UUID contextId) {
        Role role = roleRepository.findRoleByIdAndContextId(roleId, contextId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));

        Role defaultRole = roleRepository.findDefaultRoleByContextId(contextId)
                .orElseThrow(() -> new RoleNotFoundException("Default role not found"));

        if (defaultRole.getId().equals(roleId)) {
            return role;
        }

        defaultRole.setDefault(false);
        roleRepository.save(defaultRole);

        role.setDefault(true);
        return roleRepository.save(role);
    }
}