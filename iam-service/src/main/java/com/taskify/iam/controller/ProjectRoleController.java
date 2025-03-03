package com.taskify.iam.controller;

import com.taskify.common.dto.ApiResponse;
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
    public ResponseEntity<ApiResponse<List<ProjectRoleDto>>> getRoles(
            @PathVariable UUID projectId,
            @RequestHeader("X-Organization-Context") UUID organizationId
    ) {
        var roles = projectRoleService.getRoles(projectId);
        var response = new ApiResponse<>(projectRoleMapper.toDtoList(roles));
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/", ""})
    public ResponseEntity<ApiResponse<ProjectRoleDto>> createRole(
            @PathVariable UUID projectId,
            @RequestBody CreateProjectRoleDto roleDto,
            @RequestHeader("X-Organization-Context") UUID organizationId) {
        var role = projectRoleService.createRole(roleDto, projectId, organizationId);
        var response = new ApiResponse<>(projectRoleMapper.toDto(role));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<ApiResponse<ProjectRoleDto>> getRole(
            @PathVariable UUID projectId,
            @PathVariable UUID roleId) {
        var role = projectRoleService.getRole(roleId, projectId);
        var response = new ApiResponse<>(projectRoleMapper.toDto(role));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<ApiResponse<ProjectRoleDto>> updateRole(
            @PathVariable UUID projectId,
            @PathVariable UUID roleId,
            @RequestBody CreateProjectRoleDto roleDto) {
        var role = projectRoleService.updateRole(roleId, roleDto, projectId);
        var response = new ApiResponse<>(projectRoleMapper.toDto(role));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<ApiResponse<Void>> deleteRole(
            @PathVariable UUID projectId,
            @PathVariable UUID roleId) {
        projectRoleService.deleteRole(roleId, projectId);
        var response = new ApiResponse<Void>(null);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{roleId}/default")
    public ResponseEntity<ApiResponse<ProjectRoleDto>> setDefaultRole(
            @PathVariable UUID projectId,
            @PathVariable UUID roleId) {
        var role = projectRoleService.setDefaultRole(roleId, projectId);
        var response = new ApiResponse<>(projectRoleMapper.toDto(role));
        return ResponseEntity.ok(response);
    }
}