package com.taskify.iam.controller;

import com.taskify.iam.dto.role.CreateRoleDto;
import com.taskify.iam.dto.role.RoleDto;
import com.taskify.iam.mapper.RoleMapper;
import com.taskify.iam.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/roles")
public class RoleController {
    private final RoleService _roleService;
    private final RoleMapper _roleMapper;

    @Autowired
    public RoleController(RoleService roleService,
                          RoleMapper roleMapper) {
        _roleService = roleService;
        _roleMapper = roleMapper;
    }

    @GetMapping({"/", ""})
    public ResponseEntity<List<RoleDto>> getRoles(
            @RequestHeader("X-Organization-Context") UUID organizationId
    ) {
        var roles = _roleService.getRoles(organizationId);
        return ResponseEntity.ok(_roleMapper.toDtoList(roles));
    }
}
