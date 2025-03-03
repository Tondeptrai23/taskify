package com.taskify.iam.controller;

import com.taskify.common.dto.ApiResponse;
import com.taskify.iam.dto.role.OrganizationRoleDto;
import com.taskify.iam.dto.role.ProjectRoleDto;
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
public class InternalIamController {
    private final OrganizationRoleService _orgRoleService;
    private final ProjectRoleService _projectRoleService;
    private final ProjectRoleMapper _projectRoleMapper;
    private final OrganizationRoleMapper _orgRoleMapper;

    @Autowired
    public InternalIamController(OrganizationRoleService orgRoleService,
                                 OrganizationRoleMapper orgRoleMapper,
                                 ProjectRoleService projectRoleService,
                                 ProjectRoleMapper projectRoleMapper) {
        _orgRoleService = orgRoleService;
        _orgRoleMapper = orgRoleMapper;
        _projectRoleService = projectRoleService;
        _projectRoleMapper = projectRoleMapper;
    }

    @GetMapping("/orgs/roles/default")
    public ResponseEntity<ApiResponse<OrganizationRoleDto>> getDefaultRoleInOrg(
            @RequestHeader("X-Organization-Context") UUID orgId
    ){
        var response =  _orgRoleMapper.toDto(_orgRoleService.getDefaultRole(orgId));
        return ResponseEntity.ok(new ApiResponse<>(response));
    }

    @GetMapping("projects/{projectId}/roles/default")
    public ResponseEntity<ApiResponse<ProjectRoleDto>> getDefaultRoleInProject(
            @PathVariable UUID projectId
    ){
        var response = _projectRoleMapper.toDto(_projectRoleService.getDefaultRole(projectId));
        return ResponseEntity.ok(new ApiResponse<>(response));
    }
}
