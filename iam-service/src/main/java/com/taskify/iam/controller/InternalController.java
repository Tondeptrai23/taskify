package com.taskify.iam.controller;

import com.taskify.iam.dto.role.OrganizationRoleDto;
import com.taskify.iam.dto.role.ProjectRoleDto;
import com.taskify.iam.entity.ProjectRole;
import com.taskify.iam.mapper.OrganizationRoleMapper;
import com.taskify.iam.mapper.ProjectRoleMapper;
import com.taskify.iam.service.OrganizationRoleService;
import com.taskify.iam.service.ProjectRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal")
public class InternalController {
    private final OrganizationRoleService _orgRoleService;
    private final ProjectRoleService _projectRoleService;
    private final ProjectRoleMapper _projectRoleMapper;
    private final OrganizationRoleMapper _orgRoleMapper;

    @Autowired
    public InternalController(OrganizationRoleService roleService,
                              OrganizationRoleMapper roleMapper,
                              ProjectRoleService projectRoleService,
                              ProjectRoleMapper projectRoleMapper) {
        _orgRoleService = roleService;
        _orgRoleMapper = roleMapper;
    }

    @GetMapping("/roles/default")
    public ResponseEntity<OrganizationRoleDto> getDefaultRoleInOrg(
            @RequestHeader("X-Organization-Context") UUID orgId
    ){
        return ResponseEntity.ok(_orgRoleMapper.toDto(_orgRoleService.getDefaultRole(orgId)));
    }

    @GetMapping("projects/{projectId}/roles/default")
    public ResponseEntity<ProjectRoleDto> getDefaultRoleInProject(
            @PathVariable UUID projectId
    ){
        return ResponseEntity.ok(_projectRoleMapper.toDto(_projectRoleService.getDefaultRole(projectId)));
    }
}
