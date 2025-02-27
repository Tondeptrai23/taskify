package com.taskify.iam.controller;

import com.taskify.iam.dto.permission.PermissionDto;
import com.taskify.iam.dto.permission.PermissionGroupDto;
import com.taskify.iam.mapper.PermissionMapper;
import com.taskify.iam.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
    public ResponseEntity<List<PermissionDto>> getPermissions(
            @RequestParam(required = false) Long groupId) {
        var permissions = _permissionService.getPermissions(groupId);
        return ResponseEntity.ok(_permissionMapper.toDtoList(permissions));
    }

    @GetMapping("/perm-groups")
    public ResponseEntity<List<PermissionGroupDto>> getPermissionGroups() {
        var groups = _permissionService.getPermissionGroups();
        return ResponseEntity.ok(_permissionMapper.toGroupDtoList(groups));
    }
}