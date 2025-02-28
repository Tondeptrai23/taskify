package com.taskify.iam.controller;

import com.taskify.iam.dto.role.RoleDto;
import com.taskify.iam.mapper.RoleMapper;
import com.taskify.iam.service.RoleService;
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
    private final RoleService _roleService;
    private final RoleMapper _roleMapper;

    @Autowired
    public InternalController(RoleService roleService, RoleMapper roleMapper) {
        _roleService = roleService;
        _roleMapper = roleMapper;
    }

    @GetMapping("/roles/default")
    public ResponseEntity<RoleDto> getDefaultRole(
            @RequestHeader("X-Organization-Context") UUID orgId
    ){
        return ResponseEntity.ok(_roleMapper.toDto(_roleService.getDefaultRole(orgId)));
    }
}
