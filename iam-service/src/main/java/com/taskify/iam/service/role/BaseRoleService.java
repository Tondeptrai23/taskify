package com.taskify.iam.service.role;

import com.taskify.common.error.resource.RoleNotFoundException;
import com.taskify.iam.entity.Role;
import com.taskify.iam.exception.DefaultRoleDeletionException;
import com.taskify.iam.service.permission.PermissionPrerequisiteValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Abstract base service implementing Template Method pattern for role management operations.
 * Provides common CRUD operations while allowing subclasses to define domain-specific logic
 * for organization roles or project roles.
 *
 * @param <C> The DTO type used for creating/updating roles (e.g., CreateOrganizationRoleDto)
 */
@Slf4j
public abstract class BaseRoleService<C> {
    protected final PermissionPrerequisiteValidator _permissionPrerequisiteValidator;

    protected BaseRoleService(PermissionPrerequisiteValidator permissionPrerequisiteValidator) {
        this._permissionPrerequisiteValidator = permissionPrerequisiteValidator;
    }

    /**
     * Retrieves a role with its permissions by ID within a specific context.
     *
     * @param roleId The unique identifier of the role
     * @param contextId The organization or project context ID
     * @return The role entity including permissions
     * @throws RoleNotFoundException If the role does not exist in the given context
     */
    public Role getRole(UUID roleId, UUID contextId) {
        return findRoleByIdWithPermissions(roleId, contextId)
                .orElseThrow(() -> createRoleNotFoundException("Role not found"));
    }

    /**
     * Retrieves all roles with their permissions within a specific context.
     *
     * @param contextId The organization or project context ID
     * @return List of role entities including their permissions
     */
    public List<Role> getRoles(UUID contextId) {
        var roles = findAllRolesWithPermissions(contextId);
        log.info("Roles for context {}: {}", contextId, roles);
        return roles;
    }

    /**
     * Creates a new role within a specific context.
     *
     * @param createRoleDto DTO containing the role details
     * @param contextId The organization or project context ID
     * @return The newly created role entity
     * @throws RuntimeException If permission prerequisites are not satisfied or context validation fails
     */
    @Transactional
    public Role createRole(C createRoleDto, UUID contextId) {
        validateContext(contextId);

        Role newRole = mapDtoToEntity(createRoleDto);
        setRoleContext(newRole, contextId);

        if (getRolePermissions(createRoleDto) != null) {
            var permissions = _permissionPrerequisiteValidator
                    .validatePermissionPrerequisites(getRolePermissions(createRoleDto));
            newRole.setPermissions(new HashSet<>(permissions));
        }

        return saveRole(newRole);
    }

    /**
     * Updates an existing role within a specific context.
     *
     * @param roleId The unique identifier of the role to update
     * @param roleDto DTO containing the updated role details
     * @param contextId The organization or project context ID
     * @return The updated role entity
     * @throws RoleNotFoundException If the role does not exist in the given context
     * @throws RuntimeException If permission prerequisites are not satisfied
     */
    @Transactional
    public Role updateRole(UUID roleId, C roleDto, UUID contextId) {
        Role existingRole = findRoleById(roleId, contextId)
                .orElseThrow(() -> createRoleNotFoundException("Role not found"));

        updateRoleFields(existingRole, roleDto);

        if (getRolePermissions(roleDto) != null) {
            var permissions = _permissionPrerequisiteValidator
                    .validatePermissionPrerequisites(getRolePermissions(roleDto));
            existingRole.setPermissions(new HashSet<>(permissions));
        }

        return saveRole(existingRole);
    }

    /**
     * Deletes a role within a specific context, if it's not the default role.
     *
     * @param roleId The unique identifier of the role to delete
     * @param contextId The organization or project context ID
     * @throws RoleNotFoundException If the role does not exist in the given context
     * @throws DefaultRoleDeletionException If attempting to delete a default role
     */
    @Transactional
    public void deleteRole(UUID roleId, UUID contextId) {
        Role role = findRoleById(roleId, contextId)
                .orElseThrow(() -> createRoleNotFoundException("Role not found"));

        if (role.isDefault()) {
            throw new DefaultRoleDeletionException();
        }

        deleteRoleFromRepository(role);
    }

    /**
     * Retrieves the default role for a specific context.
     *
     * @param contextId The organization or project context ID
     * @return The default role entity
     * @throws RoleNotFoundException If no default role exists for the given context
     */
    public Role getDefaultRole(UUID contextId) {
        return findDefaultRoleByContextId(contextId)
                .orElseThrow(() -> createRoleNotFoundException("Default role not found"));
    }

    /**
     * Sets a role as the default for a specific context.
     * Updates the previous default role to non-default.
     *
     * @param roleId The unique identifier of the role to set as default
     * @param contextId The organization or project context ID
     * @return The updated role entity now set as default
     * @throws RoleNotFoundException If the role or current default role does not exist
     */
    @Transactional
    public Role setDefaultRole(UUID roleId, UUID contextId) {
        Role role = findRoleById(roleId, contextId)
                .orElseThrow(() -> createRoleNotFoundException("Role not found"));

        Role defaultRole = findDefaultRoleByContextId(contextId)
                .orElseThrow(() -> createRoleNotFoundException("Default role not found"));

        if (defaultRole.getId().equals(roleId)) {
            return role;
        }

        defaultRole.setDefault(false);
        saveRole(defaultRole);

        role.setDefault(true);
        return saveRole(role);
    }

    // Abstract methods to be implemented by subclasses

    /**
     * Finds a role by ID with all permissions loaded.
     *
     * @param roleId The unique identifier of the role
     * @param contextId The organization or project context ID
     * @return Optional containing the role with permissions if found
     */
    protected abstract Optional<Role> findRoleByIdWithPermissions(UUID roleId, UUID contextId);

    /**
     * Finds a role by ID without necessarily loading permissions.
     *
     * @param roleId The unique identifier of the role
     * @param contextId The organization or project context ID
     * @return Optional containing the role if found
     */
    protected abstract Optional<Role> findRoleById(UUID roleId, UUID contextId);

    /**
     * Finds all roles with their permissions for a specific context.
     *
     * @param contextId The organization or project context ID
     * @return List of roles with permissions
     */
    protected abstract List<Role> findAllRolesWithPermissions(UUID contextId);

    /**
     * Finds the default role for a specific context.
     *
     * @param contextId The organization or project context ID
     * @return Optional containing the default role if found
     */
    protected abstract Optional<Role> findDefaultRoleByContextId(UUID contextId);

    /**
     * Validates that the context (organization or project) exists.
     *
     * @param contextId The organization or project context ID
     * @throws RuntimeException If the context does not exist
     */
    protected abstract void validateContext(UUID contextId);

    /**
     * Maps a creation DTO to a role entity.
     *
     * @param createRoleDto The DTO containing role data
     * @return A new role entity populated from the DTO
     */
    protected abstract Role mapDtoToEntity(C createRoleDto);

    /**
     * Updates the fields of an existing role from a DTO.
     *
     * @param existingRole The existing role entity to update
     * @param roleDto The DTO containing updated data
     */
    protected abstract void updateRoleFields(Role existingRole, C roleDto);

    /**
     * Sets the appropriate context (organization or project) on a role.
     *
     * @param role The role entity to update
     * @param contextId The organization or project context ID
     */
    protected abstract void setRoleContext(Role role, UUID contextId);

    /**
     * Extracts the list of permission names from a role DTO.
     *
     * @param createRoleDto The role DTO
     * @return List of permission names
     */
    protected abstract List<String> getRolePermissions(C createRoleDto);

    /**
     * Persists a role entity to the database.
     *
     * @param role The role entity to save
     * @return The saved role entity
     */
    protected abstract Role saveRole(Role role);

    /**
     * Deletes a role entity from the database.
     *
     * @param role The role entity to delete
     */
    protected abstract void deleteRoleFromRepository(Role role);

    /**
     * Creates a domain-specific RoleNotFoundException.
     *
     * @param message The error message
     * @return A role not found exception
     */
    protected abstract RoleNotFoundException createRoleNotFoundException(String message);
}