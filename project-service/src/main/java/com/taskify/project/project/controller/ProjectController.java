package com.taskify.project.project.controller;

import com.taskify.project.project.dto.ProjectDto;
import com.taskify.project.project.mapper.ProjectMapper;
import com.taskify.project.project.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<List<ProjectDto>> getProjects() {
        return ResponseEntity.ok(_projectMapper.toDtoList(_projectService.getProjects()));
    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<ProjectDto> getProject(@PathVariable UUID projectId) {
        return ResponseEntity.ok(_projectMapper.toDto(_projectService.getProject(projectId)));
    }
}
