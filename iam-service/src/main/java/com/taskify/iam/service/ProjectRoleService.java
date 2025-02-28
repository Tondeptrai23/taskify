package com.taskify.iam.service;

import com.taskify.common.error.OrganizationNotFoundException;
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
    private final ProjectRoleRepository projectRoleRepository;
    private final ProjectRepository projectRepository;
    private final ProjectRoleMapper projectRoleMapper;
    private final PermissionPrerequisiteValidator permissionPrerequisiteValidator;

    @Autowired
    public ProjectRoleService(ProjectRoleRepository projectRoleRepository,
                              ProjectRepository projectRepository,
                              ProjectRoleMapper projectRoleMapper,
                              PermissionPrerequisiteValidator permissionPrerequisiteValidator) {
        this.projectRoleRepository = projectRoleRepository;
        this.projectRepository = projectRepository;
        this.projectRoleMapper = projectRoleMapper;
        this.permissionPrerequisiteValidator = permissionPrerequisiteValidator;
    }

    public ProjectRole getRole(UUID roleId, UUID projectId) {
        return projectRoleRepository.findRoleByIdAndProjectIdWithPermissions(roleId, projectId)
                .orElseThrow(() -> new RoleNotFoundException("Project role not found"));
    }

    public List<ProjectRole> getRoles(UUID projectId) {
        var roles = projectRoleRepository.findAllWithPermissionsInProject(projectId);
        log.info("Project roles: {}", roles);
        return roles;
    }

    @Transactional
    public ProjectRole createRole(CreateProjectRoleDto roleDto, UUID projectId, UUID organizationId) {
        Project project = projectRepository.findByIdAndOrganizationId(projectId, organizationId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found or does not belong to the organization"));

        ProjectRole newRole = projectRoleMapper.toEntity(roleDto);
        newRole.setProject(project);

        if (roleDto.getPermissions() != null) {
            var permissions = permissionPrerequisiteValidator.validatePermissionPrerequisites(roleDto.getPermissions());
            newRole.setPermissions(new HashSet<>(permissions));
        }

        return projectRoleRepository.save(newRole);
    }

    @Transactional
    public ProjectRole updateRole(UUID roleId, CreateProjectRoleDto roleDto, UUID projectId) {
        ProjectRole existingRole = projectRoleRepository.findRoleByIdAndProjectId(roleId, projectId)
                .orElseThrow(() -> new RoleNotFoundException("Project role not found"));

        existingRole.setName(roleDto.getName());
        existingRole.setDescription(roleDto.getDescription());

        if (roleDto.getPermissions() != null) {
            var permissions = permissionPrerequisiteValidator.validatePermissionPrerequisites(roleDto.getPermissions());
            existingRole.setPermissions(new HashSet<>(permissions));
        }

        return projectRoleRepository.save(existingRole);
    }

    @Transactional
    public void deleteRole(UUID roleId, UUID projectId) {
        ProjectRole role = projectRoleRepository.findRoleByIdAndProjectId(roleId, projectId)
                .orElseThrow(() -> new RoleNotFoundException("Project role not found"));

        if (role.isDefault()) {
            throw new DefaultRoleDeletionException();
        }

        projectRoleRepository.delete(role);
    }

    public ProjectRole getDefaultRole(UUID projectId) {
        return projectRoleRepository.findDefaultRoleByProjectId(projectId)
                .orElseThrow(() -> new RoleNotFoundException("Default project role not found"));
    }

    @Transactional
    public ProjectRole setDefaultRole(UUID roleId, UUID projectId) {
        ProjectRole role = projectRoleRepository.findRoleByIdAndProjectId(roleId, projectId)
                .orElseThrow(() -> new RoleNotFoundException("Project role not found"));

        ProjectRole defaultRole = projectRoleRepository.findDefaultRoleByProjectId(projectId)
                .orElseThrow(() -> new RoleNotFoundException("Default project role not found"));

        if (defaultRole.getId().equals(roleId)) {
            return role;
        }

        defaultRole.setDefault(false);
        role.setDefault(true);

        projectRoleRepository.save(defaultRole);
        return projectRoleRepository.save(role);
    }
}