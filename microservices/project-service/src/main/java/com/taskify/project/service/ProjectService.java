package com.taskify.project.service;

import com.taskify.commoncore.error.exception.ConflictException;
import com.taskify.commoncore.error.resource.OrganizationNotFoundException;
import com.taskify.commoncore.error.resource.ProjectNotFoundException;
import com.taskify.project.dto.project.CreateProjectDto;
import com.taskify.project.dto.project.ProjectCollectionRequest;
import com.taskify.project.dto.project.UpdateProjectDto;
import com.taskify.project.entity.Project;
import com.taskify.project.mapper.ProjectMapper;
import com.taskify.project.repository.ProjectRepository;
import com.taskify.project.specification.ProjectSpecifications;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Autowired
    public ProjectService(
            ProjectRepository projectRepository,
            ProjectMapper projectMapper
    ) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
    }

    public Page<Project> getAllProjects(UUID organizationId, ProjectCollectionRequest filter) {
        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                Sort.by(
                        filter.getSortDirection().equalsIgnoreCase("asc")
                                ? Sort.Direction.ASC
                                : Sort.Direction.DESC,
                        filter.getSortBy()
                )
        );

        return projectRepository.findAll(
                ProjectSpecifications.withFilters(organizationId, filter),
                pageable
        );
    }

    @Transactional
    public Project createProject(CreateProjectDto createProjectDto, UUID organizationId, UUID authorId) {
        // Check for key uniqueness within organization
        if (projectRepository.existsByOrganizationIdAndKey(organizationId, createProjectDto.getKey().toUpperCase())) {
            throw new ConflictException("Project key must be unique within organization");
        }

        Project project = projectMapper.toEntity(createProjectDto);
        project.setOrganizationId(organizationId);
        project.setAuthorId(authorId);

        // Ensure key is uppercase
        project.setKey(createProjectDto.getKey().toUpperCase());

        return projectRepository.save(project);
    }

    public Project getProjectById(UUID projectId, UUID organizationId) {
        return projectRepository.findByIdAndOrganizationId(projectId, organizationId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));
    }

    public Project getProjectByKey(String key, UUID organizationId) {
        return projectRepository.findByOrganizationIdAndKey(organizationId, key.toUpperCase())
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with key: " + key));
    }

    @Transactional
    public Project updateProject(UUID projectId, UpdateProjectDto updateProjectDto, UUID organizationId) {
        Project project = this.getProjectById(projectId, organizationId);

        projectMapper.updateEntity(project, updateProjectDto);
        return projectRepository.save(project);
    }

    @Transactional
    public void deleteProject(UUID projectId, UUID organizationId) {
        Project project = this.getProjectById(projectId, organizationId);
        projectRepository.delete(project);
    }

    public List<Project> getProjectsByOrganizationId(UUID organizationId) {
        return projectRepository.findAllByOrganizationId(organizationId);
    }
}