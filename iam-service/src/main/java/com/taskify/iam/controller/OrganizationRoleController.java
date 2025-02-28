package com.taskify.iam.controller;

import com.taskify.iam.dto.role.CreateOrganizationRoleDto;
import com.taskify.iam.dto.role.OrganizationRoleDto;
import com.taskify.iam.mapper.OrganizationRoleMapper;
import com.taskify.iam.service.OrganizationRoleService;
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
    public ResponseEntity<List<OrganizationRoleDto>> getRoles(
            @RequestHeader("X-Organization-Context") UUID organizationId
    ) {
        var roles = _roleService.getRoles(organizationId);
        return ResponseEntity.ok(_roleMapper.toDtoList(roles));
    }

    @PostMapping({"/", ""})
    public ResponseEntity<OrganizationRoleDto> createRole(
            @RequestBody CreateOrganizationRoleDto roleDto,
            @RequestHeader("X-Organization-Context") UUID organizationId) {
        var role = _roleService.createRole(roleDto, organizationId);
        return ResponseEntity.ok(_roleMapper.toDto(role));
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<OrganizationRoleDto> getRole(
            @PathVariable UUID roleId,
            @RequestHeader("X-Organization-Context") UUID organizationId) {
        var role = _roleService.getRole(roleId, organizationId);
        return ResponseEntity.ok(_roleMapper.toDto(role));
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<OrganizationRoleDto> updateRole(
            @PathVariable UUID roleId,
            @RequestBody CreateOrganizationRoleDto roleDto,
            @RequestHeader("X-Organization-Context") UUID organizationId) {
        var role = _roleService.updateRole(roleId, roleDto, organizationId);
        return ResponseEntity.ok(_roleMapper.toDto(role));
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> deleteRole(
            @PathVariable UUID roleId,
            @RequestHeader("X-Organization-Context") UUID organizationId) {
        _roleService.deleteRole(roleId, organizationId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{roleId}/default")
    public ResponseEntity<OrganizationRoleDto> setDefaultRole(
            @PathVariable UUID roleId,
            @RequestHeader("X-Organization-Context") UUID organizationId) {
        var role = _roleService.setDefaultRole(roleId, organizationId);
        return ResponseEntity.ok(_roleMapper.toDto(role));
    }
}
