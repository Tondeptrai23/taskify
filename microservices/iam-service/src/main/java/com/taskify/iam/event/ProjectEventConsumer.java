package com.taskify.iam.event;

import com.taskify.commoncore.annotation.LoggingAround;
import com.taskify.commoncore.annotation.LoggingException;
import com.taskify.commoncore.event.EventConstants;
import com.taskify.commoncore.event.project.ProjectCreatedEvent;
import com.taskify.commoncore.event.project.ProjectDeletedEvent;
import com.taskify.commoncore.event.project.ProjectUpdatedEvent;
import com.taskify.iam.entity.LocalOrganization;
import com.taskify.iam.entity.Project;
import com.taskify.iam.repository.OrganizationRepository;
import com.taskify.iam.repository.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class ProjectEventConsumer {

    private final ProjectRepository projectRepository;
    private final OrganizationRepository organizationRepository;
    private final EventConstants eventConstants;

    @Autowired
    public ProjectEventConsumer(
            ProjectRepository projectRepository,
            OrganizationRepository organizationRepository,
            EventConstants eventConstants) {
        this.projectRepository = projectRepository;
        this.organizationRepository = organizationRepository;
        this.eventConstants = eventConstants;
    }

    @Transactional
    @RabbitListener(queues = "#{eventConstants.getIamProjectCreatedQueue()}")
    @LoggingAround
    @LoggingException
    public void handleProjectCreatedEvent(@Payload ProjectCreatedEvent event) {
        // Check if the project already exists
        Optional<Project> existingProject = projectRepository.findById(event.getId());

        if (existingProject.isPresent()) {
            // Update the existing project
            Project project = existingProject.get();
            updateProjectFromEvent(project, event);
            projectRepository.save(project);
            log.info("Updated existing project: {}", project.getId());
        } else {
            // Create a new project
            Project newProject = createProjectFromEvent(event);
            projectRepository.save(newProject);
            log.info("Created new project: {}", newProject.getId());
        }
    }

    @Transactional
    @RabbitListener(queues = "#{eventConstants.getIamProjectUpdatedQueue()}")
    @LoggingAround
    @LoggingException
    public void handleProjectUpdatedEvent(@Payload ProjectUpdatedEvent event) {
        Optional<Project> projectOptional = projectRepository.findById(event.getId());

        if (projectOptional.isPresent()) {
            Project project = projectOptional.get();
            project.setKey(event.getKey());

            // Set organization if it changes or is missing
            if (project.getOrganization() == null ||
                    !project.getOrganization().getId().equals(event.getOrganizationId())) {
                setProjectOrganization(project, event.getOrganizationId());
            }

            projectRepository.save(project);
            log.info("Updated project: {}", event.getId());
        } else {
            // If the project doesn't exist, create it (recovery mechanism)
            log.warn("Project not found for update. Creating it: {}", event.getId());
            Project newProject = new Project();
            newProject.setId(event.getId());
            newProject.setKey(event.getKey());
            setProjectOrganization(newProject, event.getOrganizationId());

            projectRepository.save(newProject);
        }
    }

    @Transactional
    @RabbitListener(queues = "#{eventConstants.getIamProjectDeletedQueue()}")
    @LoggingAround
    @LoggingException
    public void handleProjectDeletedEvent(@Payload ProjectDeletedEvent event) {
        projectRepository.deleteById(event.getId());
    }

    private Project createProjectFromEvent(ProjectCreatedEvent event) {
        Project project = new Project();
        project.setId(event.getId());
        project.setKey(event.getKey());

        // Set organization
        setProjectOrganization(project, event.getOrganizationId());

        return project;
    }

    private void updateProjectFromEvent(Project project, ProjectCreatedEvent event) {
        project.setKey(event.getKey());

        // Update organization if it changes
        if (project.getOrganization() == null ||
                !project.getOrganization().getId().equals(event.getOrganizationId())) {
            setProjectOrganization(project, event.getOrganizationId());
        }
    }

    private void setProjectOrganization(Project project, UUID organizationId) {
        organizationRepository.findById(organizationId).ifPresentOrElse(
                project::setOrganization,
                () -> {
                    // Create organization placeholder if not found
                    log.warn("Organization not found: {}. Creating placeholder.", organizationId);
                    LocalOrganization org = new LocalOrganization();
                    org.setId(organizationId);
                    organizationRepository.save(org);
                    project.setOrganization(org);
                }
        );
    }
}