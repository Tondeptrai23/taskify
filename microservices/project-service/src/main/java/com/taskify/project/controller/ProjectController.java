package com.taskify.project.controller;

import com.taskify.commoncore.constant.Permission;
import com.taskify.commoncore.dto.ApiCollectionResponse;
import com.taskify.commoncore.dto.ApiResponse;
import com.taskify.project.annotation.RequiresPermissions;
import com.taskify.project.dto.project.CreateProjectDto;
import com.taskify.project.dto.project.ProjectCollectionRequest;
import com.taskify.project.dto.project.ProjectDto;
import com.taskify.project.dto.project.UpdateProjectDto;
import com.taskify.project.entity.Project;
import com.taskify.project.mapper.ProjectMapper;
import com.taskify.project.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService projectService;
    private final ProjectMapper projectMapper;

    @Autowired
    public ProjectController(
            ProjectService projectService,
            ProjectMapper projectMapper
    ) {
        this.projectService = projectService;
        this.projectMapper = projectMapper;
    }

    @RequiresPermissions(value = {Permission.VIEW_PROJECT})
    @GetMapping({"", "/"})
    public ResponseEntity<ApiResponse<ApiCollectionResponse<ProjectDto>>> getAllProjects(
            @RequestHeader("X-Organization-Context") UUID organizationId,
            @ModelAttribute ProjectCollectionRequest filter
    ) {
        Page<Project> projects = projectService.getAllProjects(organizationId, filter);
        Page<ProjectDto> projectDtos = projects.map(projectMapper::toDto);

        var response = new ApiResponse<>(ApiCollectionResponse.from(projectDtos));
        return ResponseEntity.ok(response);
    }

    @RequiresPermissions(value = {Permission.CREATE_PROJECT})
    @PostMapping({"", "/"})
    public ResponseEntity<ApiResponse<ProjectDto>> createProject(
            @RequestHeader("X-Organization-Context") UUID organizationId,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody CreateProjectDto createProjectDto
    ) {
        Project project = projectService.createProject(createProjectDto, organizationId, userId);
        var response = new ApiResponse<>(projectMapper.toDto(project));
        return ResponseEntity.ok(response);
    }

    @RequiresPermissions(value = {Permission.VIEW_PROJECT})
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectDto>> getProjectById(
            @PathVariable("id") UUID id,
            @RequestHeader("X-Organization-Context") UUID organizationId
    ) {
        Project project = projectService.getProjectById(id, organizationId);
        var response = new ApiResponse<>(projectMapper.toDto(project));
        return ResponseEntity.ok(response);
    }

    @RequiresPermissions(value = {Permission.VIEW_PROJECT})
    @GetMapping("/key/{key}")
    public ResponseEntity<ApiResponse<ProjectDto>> getProjectByKey(
            @PathVariable("key") String key,
            @RequestHeader("X-Organization-Context") UUID organizationId
    ) {
        Project project = projectService.getProjectByKey(key, organizationId);
        var response = new ApiResponse<>(projectMapper.toDto(project));
        return ResponseEntity.ok(response);
    }

    @RequiresPermissions(value = {Permission.UPDATE_PROJECT})
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectDto>> updateProject(
            @PathVariable("id") UUID id,
            @RequestHeader("X-Organization-Context") UUID organizationId,
            @RequestBody UpdateProjectDto updateProjectDto
    ) {
        Project project = projectService.updateProject(id, updateProjectDto, organizationId);
        var response = new ApiResponse<>(projectMapper.toDto(project));
        return ResponseEntity.ok(response);
    }

    @RequiresPermissions(value = {Permission.DELETE_PROJECT})
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProject(
            @PathVariable("id") UUID id,
            @RequestHeader("X-Organization-Context") UUID organizationId
    ) {
        projectService.deleteProject(id, organizationId);
        var response = new ApiResponse<>("Project deleted successfully");
        return ResponseEntity.ok(response);
    }
}