package com.taskify.iam.controller;

import com.taskify.common.dto.ApiResponse;
import com.taskify.iam.dto.permission.UserPermissionsResponse;
import com.taskify.iam.dto.permission.VerifyPermissionRequest;
import com.taskify.iam.dto.role.OrganizationRoleDto;
import com.taskify.iam.dto.role.ProjectRoleDto;
import com.taskify.iam.mapper.OrganizationRoleMapper;
import com.taskify.iam.mapper.PermissionMapper;
import com.taskify.iam.mapper.ProjectRoleMapper;
import com.taskify.iam.service.role.OrganizationRoleService;
import com.taskify.iam.service.permission.PermissionService;
import com.taskify.iam.service.permission.PermissionVerificationService;
import com.taskify.iam.service.role.ProjectRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/internal")
public class InternalIamController {
    private final OrganizationRoleService _orgRoleService;
    private final ProjectRoleService _projectRoleService;
    private final ProjectRoleMapper _projectRoleMapper;
    private final OrganizationRoleMapper _orgRoleMapper;
    private final PermissionService _permissionService;
    private final PermissionMapper _permissionMapper;
    private final PermissionVerificationService _permissionVerificationService;

    @Autowired
    public InternalIamController(OrganizationRoleService orgRoleService,
                                 OrganizationRoleMapper orgRoleMapper,
                                 ProjectRoleService projectRoleService,
                                 PermissionService permissionService,
                                 PermissionMapper permissionMapper,
                                    PermissionVerificationService permissionVerificationService,
                                 ProjectRoleMapper projectRoleMapper) {
        _orgRoleService = orgRoleService;
        _orgRoleMapper = orgRoleMapper;
        _projectRoleService = projectRoleService;
        _permissionService = permissionService;
        _permissionMapper = permissionMapper;
        _permissionVerificationService = permissionVerificationService;
        _projectRoleMapper = projectRoleMapper;
    }

    @GetMapping("/orgs/roles/default")
    public ResponseEntity<ApiResponse<OrganizationRoleDto>> getDefaultRoleInOrg(
            @RequestHeader("X-Organization-Context") UUID orgId
    ){
        var response =  _orgRoleMapper.toDto(_orgRoleService.getDefaultRole(orgId));
        return ResponseEntity.ok(new ApiResponse<>(response));
    }

    @GetMapping("projects/{projectId}/roles/default")
    public ResponseEntity<ApiResponse<ProjectRoleDto>> getDefaultRoleInProject(
            @PathVariable UUID projectId
    ){
        var response = _projectRoleMapper.toDto(_projectRoleService.getDefaultRole(projectId));
        return ResponseEntity.ok(new ApiResponse<>(response));
    }

    @GetMapping("/orgs/permissions")
    public ResponseEntity<ApiResponse<UserPermissionsResponse>> getPermissionsInOrg(
            @RequestHeader("X-Organization-Context") UUID orgId,
            @RequestHeader("X-User-Id") UUID userId
    ){
        var response = _permissionService.getOrganizationPermissionsOfUser(orgId, userId);
        var permissions = _permissionMapper.permissionsToStringList(Set.copyOf(response));
        return ResponseEntity.ok(new ApiResponse<>(
                new UserPermissionsResponse(orgId, permissions)
        ));
    }

    @GetMapping("/projects/{projectId}/permissions")
    public ResponseEntity<ApiResponse<UserPermissionsResponse>> getPermissionsInProject(
            @PathVariable UUID projectId,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-Organization-Context") UUID orgId
    ){
        var response = _permissionService.getProjectPermissionsOfUser(projectId, userId, orgId);
        var permissions = _permissionMapper.permissionsToStringList(Set.copyOf(response));
        return ResponseEntity.ok(new ApiResponse<>(
                new UserPermissionsResponse(orgId, permissions)
        ));
    }

    @PostMapping("/permissions/verify")
    public ResponseEntity<ApiResponse<Boolean>> verifyPermission(
            @RequestBody VerifyPermissionRequest request,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-Organization-Context") UUID orgId
    ){
        var response = _permissionVerificationService.hasPermission(
                userId,
                orgId,
                UUID.fromString(request.getProjectId()),
                request.getPermission()
        );
        return ResponseEntity.ok(new ApiResponse<>(response));
    }
}
    