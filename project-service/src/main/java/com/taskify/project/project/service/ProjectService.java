package com.taskify.project.project.service;

import com.taskify.project.common.exception.ResourceNotFoundException;
import com.taskify.project.project.dto.CreateProjectDto;
import com.taskify.project.project.entity.Project;
import com.taskify.project.project.entity.ProjectStatus;
import com.taskify.project.project.exception.ProjectNameTakenException;
import com.taskify.project.project.exception.ProjectNotFoundException;
import com.taskify.project.project.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectService {
    private final ProjectRepository _projectRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this._projectRepository = projectRepository;
    }

    public List<Project> getProjects() {
        return _projectRepository.findAll();
    }

    public Project getProject(UUID projectId) {
        return _projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));
    }

    public Project createProject(CreateProjectDto createProjectDto) {
        if (_projectRepository.existsProjectByName(createProjectDto.getName())) {
            throw new ProjectNameTakenException("Project name already taken");
        }

        Project project = new Project();
        project.setName(createProjectDto.getName());
        project.setDescription(createProjectDto.getDescription());
        project.setOrganizationId(createProjectDto.getOrganizationId());
        project.setStatus(ProjectStatus.ACTIVE);

        return _projectRepository.save(project);
    }

    public Project updateProject(UUID projectId, CreateProjectDto projectData) {
        Project project = this.getProject(projectId);

        var targetName = projectData.getName();
        if (project.getName().equals(targetName) && _projectRepository.existsProjectByName(targetName)) {
            throw new ProjectNameTakenException("Project name already taken");
        }

        project.setName(projectData.getName());
        project.setDescription(projectData.getDescription());
        project.setOrganizationId(projectData.getOrganizationId());
        project.setStatus(ProjectStatus.ACTIVE);

        return _projectRepository.save(project);
    }

    public void deleteProject(UUID projectId) {
        Project project = this.getProject(projectId);

        _projectRepository.delete(project);
    }
}

