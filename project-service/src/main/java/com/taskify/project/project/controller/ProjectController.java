package com.taskify.project.project.controller;

import com.taskify.project.common.dto.BaseCollectionResponse;
import com.taskify.project.project.dto.CreateProjectDto;
import com.taskify.project.project.dto.ProjectCollectionRequest;
import com.taskify.project.project.dto.ProjectDto;
import com.taskify.project.project.entity.Project;
import com.taskify.project.project.mapper.ProjectMapper;
import com.taskify.project.project.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ProjectController {
    private final ProjectService _projectService;
    private final ProjectMapper _projectMapper;

    @Autowired
    public ProjectController(ProjectService projectService, ProjectMapper projectMapper) {
        this._projectService = projectService;
        this._projectMapper = projectMapper;
    }

    @GetMapping("/projects")
    public ResponseEntity<BaseCollectionResponse<ProjectDto>> getProjects(@ModelAttribute ProjectCollectionRequest filter) {
        Page<Project> projectPages = _projectService.getProjects(filter);
        Page<ProjectDto> projectDtos = projectPages.map(_projectMapper::toDto);

        return ResponseEntity.ok(BaseCollectionResponse.from(projectDtos));
    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<ProjectDto> getProject(@PathVariable UUID projectId) {
        return ResponseEntity.ok(_projectMapper.toDto(_projectService.getProject(projectId)));
    }

    @PutMapping("/projects/{projectId}")
    public ResponseEntity<ProjectDto> updateProject(@PathVariable UUID projectId, @RequestBody CreateProjectDto projectDto) {
        return ResponseEntity.ok(_projectMapper.toDto(_projectService.updateProject(projectId, projectDto)));
    }

    @DeleteMapping("/projects/{projectId}")
    public ResponseEntity<String> deleteProject(@PathVariable UUID projectId) {
        _projectService.deleteProject(projectId);
        return ResponseEntity.ok("Project deleted successfully");
    }
}
