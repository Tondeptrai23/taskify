package com.taskify.iam.controller;

import com.taskify.commoncore.dto.ApiResponse;
import com.taskify.iam.dto.role.CreateRoleDto;
import com.taskify.iam.dto.role.RoleDto;
import com.taskify.iam.entity.Role;
import com.taskify.iam.mapper.RoleMapper;
import com.taskify.iam.service.role.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/contexts")
public class RoleController {
    private final RoleService roleService;
    private final RoleMapper roleMapper;

    @Autowired
    public RoleController(RoleService roleService,
                          RoleMapper roleMapper) {
        this.roleService = roleService;
        this.roleMapper = roleMapper;
    }

    @GetMapping("/{contextId}/roles")
    public ResponseEntity<ApiResponse<List<RoleDto>>> getRoles(@PathVariable UUID contextId) {
        List<Role> roles = roleService.getRoles(contextId);
        var response = new ApiResponse<>(roleMapper.toDtoList(roles));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{contextId}/roles")
    public ResponseEntity<ApiResponse<RoleDto>> createRole(
            @PathVariable UUID contextId,
            @RequestBody CreateRoleDto roleDto) {
        Role role = roleService.createRole(roleDto, contextId);
        var response = new ApiResponse<>(roleMapper.toDto(role));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{contextId}/roles/{roleId}")
    public ResponseEntity<ApiResponse<RoleDto>> getRole(
            @PathVariable UUID contextId,
            @PathVariable UUID roleId) {
        Role role = roleService.getRole(roleId, contextId);
        var response = new ApiResponse<>(roleMapper.toDto(role));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{contextId}/roles/{roleId}")
    public ResponseEntity<ApiResponse<RoleDto>> updateRole(
            @PathVariable UUID contextId,
            @PathVariable UUID roleId,
            @RequestBody CreateRoleDto roleDto) {
        Role role = roleService.updateRole(roleId, roleDto, contextId);
        var response = new ApiResponse<>(roleMapper.toDto(role));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{contextId}/roles/{roleId}")
    public ResponseEntity<ApiResponse<Void>> deleteRole(
            @PathVariable UUID contextId,
            @PathVariable UUID roleId) {
        roleService.deleteRole(roleId, contextId);
        var response = new ApiResponse<Void>(null);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{contextId}/roles/{roleId}/default")
    public ResponseEntity<ApiResponse<RoleDto>> setDefaultRole(
            @PathVariable UUID contextId,
            @PathVariable UUID roleId) {
        Role role = roleService.setDefaultRole(roleId, contextId);
        var response = new ApiResponse<>(roleMapper.toDto(role));
        return ResponseEntity.ok(response);
    }
}