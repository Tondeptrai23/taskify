package com.taskify.iam.service;

import com.taskify.common.error.ProjectNotFoundException;
import com.taskify.common.error.RoleNotFoundException;
import com.taskify.iam.dto.role.CreateProjectRoleDto;
import com.taskify.iam.entity.Project;
import com.taskify.iam.entity.ProjectRole;
import com.taskify.iam.exception.DefaultRoleDeletionException;
import com.taskify.iam.mapper.ProjectRoleMapper;
import com.taskify.iam.repository.ProjectRepository;
import com.taskify.iam.repository.ProjectRoleRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ProjectRoleService {
    private final ProjectRoleRepository _projectRoleRepository;
    private final ProjectRepository _projectRepository;
    private final ProjectRoleMapper _projectRoleMapper;
    private final PermissionPrerequisiteValidator _permissionPrerequisiteValidator;

    @Autowired
    public ProjectRoleService(ProjectRoleRepository projectRoleRepository,
                              ProjectRepository projectRepository,
                              ProjectRoleMapper projectRoleMapper,
                              PermissionPrerequisiteValidator permissionPrerequisiteValidator) {
        this._projectRoleRepository = projectRoleRepository;
        this._projectRepository = projectRepository;
        this._projectRoleMapper = projectRoleMapper;
        this._permissionPrerequisiteValidator = permissionPrerequisiteValidator;
    }

    public ProjectRole getRole(UUID roleId, UUID projectId) {
        return _projectRoleRepository.findRoleByIdAndProjectIdWithPermissions(roleId, projectId)
                .orElseThrow(() -> new RoleNotFoundException("Project role not found"));
    }

    public List<ProjectRole> getRoles(UUID projectId) {
        var roles = _projectRoleRepository.findAllWithPermissionsInProject(projectId);
        log.info("Project roles: {}", roles);
        return roles;
    }

    @Transactional
    public ProjectRole createRole(CreateProjectRoleDto roleDto, UUID projectId, UUID organizationId) {
        Project project = _projectRepository.findByIdAndOrganizationId(projectId, organizationId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found or does not belong to the organization"));

        ProjectRole newRole = _projectRoleMapper.toEntity(roleDto);
        newRole.setProject(project);

        if (roleDto.getPermissions() != null) {
            var permissions = _permissionPrerequisiteValidator.validatePermissionPrerequisites(roleDto.getPermissions());
            newRole.setPermissions(new HashSet<>(permissions));
        }

        return _projectRoleRepository.save(newRole);
    }

    @Transactional
    public ProjectRole updateRole(UUID roleId, CreateProjectRoleDto roleDto, UUID projectId) {
        ProjectRole existingRole = _projectRoleRepository.findRoleByIdAndProjectId(roleId, projectId)
                .orElseThrow(() -> new RoleNotFoundException("Project role not found"));

        existingRole.setName(roleDto.getName());
        existingRole.setDescription(roleDto.getDescription());

        if (roleDto.getPermissions() != null) {
            var permissions = _permissionPrerequisiteValidator.validatePermissionPrerequisites(roleDto.getPermissions());
            existingRole.setPermissions(new HashSet<>(permissions));
        }

        return _projectRoleRepository.save(existingRole);
    }

    @Transactional
    public void deleteRole(UUID roleId, UUID projectId) {
        ProjectRole role = _projectRoleRepository.findRoleByIdAndProjectId(roleId, projectId)
                .orElseThrow(() -> new RoleNotFoundException("Project role not found"));

        if (role.isDefault()) {
            throw new DefaultRoleDeletionException();
        }

        _projectRoleRepository.delete(role);
    }

    public ProjectRole getDefaultRole(UUID projectId) {
        return _projectRoleRepository.findDefaultRoleByProjectId(projectId)
                .orElseThrow(() -> new RoleNotFoundException("Default project role not found"));
    }

    @Transactional
    public ProjectRole setDefaultRole(UUID roleId, UUID projectId) {
        ProjectRole role = _projectRoleRepository.findRoleByIdAndProjectId(roleId, projectId)
                .orElseThrow(() -> new RoleNotFoundException("Project role not found"));

        ProjectRole defaultRole = _projectRoleRepository.findDefaultRoleByProjectId(projectId)
                .orElseThrow(() -> new RoleNotFoundException("Default project role not found"));

        if (defaultRole.getId().equals(roleId)) {
            return role;
        }

        _projectRoleRepository.updateDefaultRole(defaultRole.getId(), false);
        return _projectRoleRepository.updateDefaultRole(roleId, true);
    }
}