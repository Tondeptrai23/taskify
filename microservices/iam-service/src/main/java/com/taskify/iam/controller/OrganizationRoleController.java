package com.taskify.iam.controller;

import com.taskify.commoncore.dto.ApiResponse;
import com.taskify.iam.dto.role.CreateOrganizationRoleDto;
import com.taskify.iam.dto.role.OrganizationRoleDto;
import com.taskify.iam.mapper.OrganizationRoleMapper;
import com.taskify.iam.service.role.OrganizationRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orgs/roles")
public class OrganizationRoleController {
    private final OrganizationRoleService _roleService;
    private final OrganizationRoleMapper _roleMapper;

    @Autowired
    public OrganizationRoleController(OrganizationRoleService roleService,
                                      OrganizationRoleMapper roleMapper) {
        _roleService = roleService;
        _roleMapper = roleMapper;
    }

    @GetMapping({"/", ""})
    public ResponseEntity<ApiResponse<List<OrganizationRoleDto>>> getRoles(
            @RequestHeader("X-Organization-Context") UUID organizationId
    ) {
        var roles = _roleService.getRoles(organizationId);
        var response = new ApiResponse<>(_roleMapper.toDtoList(roles));
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/", ""})
    public ResponseEntity<ApiResponse<OrganizationRoleDto>> createRole(
            @RequestBody CreateOrganizationRoleDto roleDto,
            @RequestHeader("X-Organization-Context") UUID organizationId) {
        var role = _roleService.createRole(roleDto, organizationId);
        var response = new ApiResponse<>(_roleMapper.toDto(role));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<ApiResponse<OrganizationRoleDto>> getRole(
            @PathVariable UUID roleId,
            @RequestHeader("X-Organization-Context") UUID organizationId) {
        var role = _roleService.getRole(roleId, organizationId);
        var response = new ApiResponse<>(_roleMapper.toDto(role));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<ApiResponse<OrganizationRoleDto>> updateRole(
            @PathVariable UUID roleId,
            @RequestBody CreateOrganizationRoleDto roleDto,
            @RequestHeader("X-Organization-Context") UUID organizationId) {
        var role = _roleService.updateRole(roleId, roleDto, organizationId);
        var response = new ApiResponse<>(_roleMapper.toDto(role));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<ApiResponse<Void>> deleteRole(
            @PathVariable UUID roleId,
            @RequestHeader("X-Organization-Context") UUID organizationId) {
        _roleService.deleteRole(roleId, organizationId);
        var response = new ApiResponse<Void>(null);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{roleId}/default")
    public ResponseEntity<ApiResponse<OrganizationRoleDto>> setDefaultRole(
            @PathVariable UUID roleId,
            @RequestHeader("X-Organization-Context") UUID organizationId) {
        var role = _roleService.setDefaultRole(roleId, organizationId);
        var response = new ApiResponse<>(_roleMapper.toDto(role));
        return ResponseEntity.ok(response);
    }
}
