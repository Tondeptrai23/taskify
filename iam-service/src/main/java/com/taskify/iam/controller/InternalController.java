package com.taskify.iam.controller;

import com.taskify.iam.dto.role.OrganizationRoleDto;
import com.taskify.iam.mapper.OrganizationRoleMapper;
import com.taskify.iam.service.OrganizationRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/internal")
public class InternalController {
    private final OrganizationRoleService _orgRoleService;
    private final OrganizationRoleMapper _orgRoleMapper;

    @Autowired
    public InternalController(OrganizationRoleService roleService, OrganizationRoleMapper roleMapper) {
        _orgRoleService = roleService;
        _orgRoleMapper = roleMapper;
    }

    @GetMapping("/roles/default")
    public ResponseEntity<OrganizationRoleDto> getDefaultRole(
            @RequestHeader("X-Organization-Context") UUID orgId
    ){
        return ResponseEntity.ok(_orgRoleMapper.toDto(_orgRoleService.getDefaultRole(orgId)));
    }
}
