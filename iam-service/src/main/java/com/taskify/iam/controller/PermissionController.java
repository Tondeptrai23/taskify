package com.taskify.iam.controller;

import com.taskify.common.dto.ApiResponse;
import com.taskify.iam.dto.permission.PermissionDto;
import com.taskify.iam.dto.permission.PermissionGroupDto;
import com.taskify.iam.mapper.PermissionMapper;
import com.taskify.iam.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PermissionController {
    private final PermissionService _permissionService;
    private final PermissionMapper _permissionMapper;

    @Autowired
    public PermissionController(PermissionService permissionService,
                                PermissionMapper permissionMapper) {
        _permissionService = permissionService;
        _permissionMapper = permissionMapper;
    }

    @GetMapping("/permissions")
    public ResponseEntity<ApiResponse<List<PermissionDto>>> getPermissions(
            @RequestParam(required = false) Long groupId) {
        var permissions = _permissionService.getPermissions(groupId);
        var response = new ApiResponse<>(_permissionMapper.toDtoList(permissions));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/perm-groups")
    public ResponseEntity<ApiResponse<List<PermissionGroupDto>>> getPermissionGroups() {
        var groups = _permissionService.getPermissionGroups();
        var response = new ApiResponse<>(_permissionMapper.toGroupDtoList(groups));
        return ResponseEntity.ok(response);
    }
}