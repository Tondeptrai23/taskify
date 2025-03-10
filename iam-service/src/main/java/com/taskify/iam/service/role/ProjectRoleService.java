package com.taskify.iam.service.role;

import com.taskify.common.error.resource.ProjectNotFoundException;
import com.taskify.common.error.resource.RoleNotFoundException;
import com.taskify.iam.dto.role.CreateProjectRoleDto;
import com.taskify.iam.dto.role.ProjectRoleDto;
import com.taskify.iam.entity.Project;
import com.taskify.iam.entity.Role;
import com.taskify.iam.mapper.ProjectRoleMapper;
import com.taskify.iam.repository.ProjectRepository;
import com.taskify.iam.repository.ProjectRoleRepository;
import com.taskify.iam.service.permission.PermissionPrerequisiteValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class ProjectRoleService extends BaseRoleService<CreateProjectRoleDto> {
    private final ProjectRoleRepository _projectRoleRepository;
    private final ProjectRepository _projectRepository;
    private final ProjectRoleMapper _projectRoleMapper;

    @Autowired
    public ProjectRoleService(
            ProjectRoleRepository projectRoleRepository,
            ProjectRepository projectRepository,
            ProjectRoleMapper projectRoleMapper,
            PermissionPrerequisiteValidator permissionPrerequisiteValidator) {
        super(permissionPrerequisiteValidator);
        this._projectRoleRepository = projectRoleRepository;
        this._projectRepository = projectRepository;
        this._projectRoleMapper = projectRoleMapper;
    }

    @Override
    protected Optional<Role> findRoleByIdWithPermissions(UUID roleId, UUID contextId) {
        return _projectRoleRepository.findRoleByIdAndProjectIdWithPermissions(roleId, contextId);
    }

    @Override
    protected Optional<Role> findRoleById(UUID roleId, UUID contextId) {
        return _projectRoleRepository.findRoleByIdAndProjectId(roleId, contextId);
    }

    @Override
    protected List<Role> findAllRolesWithPermissions(UUID contextId) {
        return _projectRoleRepository.findAllWithPermissionsInProject(contextId);
    }

    @Override
    protected Optional<Role> findDefaultRoleByContextId(UUID contextId) {
        return _projectRoleRepository.findDefaultRoleByProjectId(contextId);
    }

    @Override
    protected void validateContext(UUID contextId) {
        _projectRepository.findById(contextId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));
    }

    @Override
    protected Role mapDtoToEntity(CreateProjectRoleDto createRoleDto) {
        return _projectRoleMapper.toEntity(createRoleDto);
    }

    @Override
    protected void updateRoleFields(Role existingRole, CreateProjectRoleDto roleDto) {
        existingRole.setName(roleDto.getName());
        existingRole.setDescription(roleDto.getDescription());
    }

    @Override
    protected void setRoleContext(Role role, UUID contextId) {
        Project project = _projectRepository.findById(contextId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));
        role.setProject(project);
    }

    @Override
    protected List<String> getRolePermissions(CreateProjectRoleDto createRoleDto) {
        return createRoleDto.getPermissions();
    }

    @Override
    protected Role saveRole(Role role) {
        return _projectRoleRepository.save(role);
    }

    @Override
    protected void deleteRoleFromRepository(Role role) {
        _projectRoleRepository.delete(role);
    }

    @Override
    protected RoleNotFoundException createRoleNotFoundException(String message) {
        return new RoleNotFoundException(message);
    }

    // Special method for project-specific logic with organization validation
    public Role createRole(CreateProjectRoleDto roleDto, UUID projectId, UUID organizationId) {
        Project project = _projectRepository.findByIdAndOrganizationId(projectId, organizationId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found or does not belong to the organization"));

        Role newRole = _projectRoleMapper.toEntity(roleDto);
        newRole.setProject(project);

        if (roleDto.getPermissions() != null) {
            var permissions = _permissionPrerequisiteValidator.validatePermissionPrerequisites(roleDto.getPermissions());
            newRole.setPermissions(new HashSet<>(permissions));
        }

        return _projectRoleRepository.save(newRole);
    }
}