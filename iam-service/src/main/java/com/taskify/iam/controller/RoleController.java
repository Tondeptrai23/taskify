package com.taskify.iam.controller;

import com.taskify.iam.dto.role.RoleDto;
import com.taskify.iam.mapper.RoleMapper;
import com.taskify.iam.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public ResponseEntity<List<RoleDto>> getRoles() {
        return ResponseEntity.ok(_roleMapper.toDtoList(_roleService.getRoles()));
    }
}
