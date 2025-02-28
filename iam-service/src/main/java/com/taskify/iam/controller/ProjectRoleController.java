package com.taskify.iam.controller;

import com.taskify.iam.dto.role.CreateProjectRoleDto;
import com.taskify.iam.dto.role.ProjectRoleDto;
import com.taskify.iam.mapper.ProjectRoleMapper;
import com.taskify.iam.service.ProjectRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects/{projectId}/roles")
public class ProjectRoleController {
    private final ProjectRoleService projectRoleService;
    private final ProjectRoleMapper projectRoleMapper;

    @Autowired
    public ProjectRoleController(ProjectRoleService projectRoleService,
                                 ProjectRoleMapper projectRoleMapper) {
        this.projectRoleService = projectRoleService;
        this.projectRoleMapper = projectRoleMapper;
    }

    @GetMapping({"/", ""})
    public ResponseEntity<List<ProjectRoleDto>> getRoles(
            @PathVariable UUID projectId,
            @RequestHeader("X-Organization-Context") UUID organizationId
    ) {
        var roles = projectRoleService.getRoles(projectId);
        return ResponseEntity.ok(projectRoleMapper.toDtoList(roles));
    }

    @PostMapping({"/", ""})
    public ResponseEntity<ProjectRoleDto> createRole(
            @PathVariable UUID projectId,
            @RequestBody CreateProjectRoleDto roleDto,
            @RequestHeader("X-Organization-Context") UUID organizationId) {
        var role = projectRoleService.createRole(roleDto, projectId, organizationId);
        return ResponseEntity.ok(projectRoleMapper.toDto(role));
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<ProjectRoleDto> getRole(
            @PathVariable UUID projectId,
            @PathVariable UUID roleId) {
        var role = projectRoleService.getRole(roleId, projectId);
        return ResponseEntity.ok(projectRoleMapper.toDto(role));
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<ProjectRoleDto> updateRole(
            @PathVariable UUID projectId,
            @PathVariable UUID roleId,
            @RequestBody CreateProjectRoleDto roleDto) {
        var role = projectRoleService.updateRole(roleId, roleDto, projectId);
        return ResponseEntity.ok(projectRoleMapper.toDto(role));
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> deleteRole(
            @PathVariable UUID projectId,
            @PathVariable UUID roleId) {
        projectRoleService.deleteRole(roleId, projectId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{roleId}/default")
    public ResponseEntity<ProjectRoleDto> setDefaultRole(
            @PathVariable UUID projectId,
            @PathVariable UUID roleId) {
        var role = projectRoleService.setDefaultRole(roleId, projectId);
        return ResponseEntity.ok(projectRoleMapper.toDto(role));
    }
}