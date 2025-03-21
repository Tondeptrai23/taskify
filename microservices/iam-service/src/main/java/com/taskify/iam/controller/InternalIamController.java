package com.taskify.iam.controller;

import com.taskify.commoncore.dto.ApiResponse;
import com.taskify.iam.dto.permission.UserPermissionsResponse;
import com.taskify.iam.dto.permission.VerifyPermissionRequest;
import com.taskify.iam.mapper.PermissionMapper;
import com.taskify.iam.mapper.RoleMapper;
import com.taskify.iam.service.permission.PermissionService;
import com.taskify.iam.service.permission.PermissionVerificationService;
import com.taskify.iam.service.role.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/internal")
public class InternalIamController {
    private final PermissionService permissionService;
    private final PermissionVerificationService permissionVerificationService;
    private final PermissionMapper permissionMapper;
    private final RoleService roleService;

    @Autowired
    public InternalIamController(
            PermissionService permissionService,
            PermissionVerificationService permissionVerificationService,
            PermissionMapper permissionMapper,
            RoleService roleService) {
        this.permissionService = permissionService;
        this.permissionVerificationService = permissionVerificationService;
        this.permissionMapper = permissionMapper;
        this.roleService = roleService;
    }

    @GetMapping("/contexts/{contextId}/roles/default")
    public ResponseEntity<ApiResponse<UUID>> getDefaultRole(
            @PathVariable UUID contextId) {
        var defaultRole = roleService.getDefaultRole(contextId);
        return ResponseEntity.ok(new ApiResponse<>(defaultRole.getId()));
    }

    @GetMapping("/contexts/{contextId}/permissions")
    public ResponseEntity<ApiResponse<UserPermissionsResponse>> getUserPermissionsInContext(
            @PathVariable UUID contextId,
            @RequestHeader("X-User-Id") UUID userId) {
        var permissions = permissionService.getUserPermissionsInContext(contextId, userId);
        var stringPermissions = permissionMapper.permissionsToStringList(Set.copyOf(permissions));

        var response = new ApiResponse<>(
                new UserPermissionsResponse(contextId, stringPermissions)
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/contexts/{contextId}/permissions/verify")
    public ResponseEntity<ApiResponse<Boolean>> verifyPermission(
            @PathVariable UUID contextId,
            @RequestBody VerifyPermissionRequest request,
            @RequestHeader("X-User-Id") UUID userId) {
        boolean hasPermission = permissionVerificationService.hasPermission(
                userId,
                contextId,
                request.getPermission()
        );
        return ResponseEntity.ok(new ApiResponse<>(hasPermission));
    }
}