package com.taskify.project.event;

import com.taskify.commoncore.annotation.LoggingAfter;
import com.taskify.commoncore.annotation.LoggingException;
import com.taskify.commoncore.event.EventConstants;
import com.taskify.commoncore.event.project.ProjectCreatedEvent;
import com.taskify.commoncore.event.project.ProjectDeletedEvent;
import com.taskify.commoncore.event.project.ProjectUpdatedEvent;
import com.taskify.project.entity.Project;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.UUID;

@Slf4j
@Component
public class ProjectEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final EventConstants eventConstants;

    public ProjectEventPublisher(RabbitTemplate rabbitTemplate,
                                 EventConstants eventConstants) {
        this.rabbitTemplate = rabbitTemplate;
        this.eventConstants = eventConstants;
    }

    @LoggingException
    @LoggingAfter(
            value = "Publishing project created event for project: {}",
            args = {"project.getId()"}
    )
    public void publishProjectCreatedEvent(Project project) {
        ProjectCreatedEvent event = ProjectCreatedEvent.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .key(project.getKey())
                .organizationId(project.getOrganizationId())
                .authorId(project.getAuthorId())
                .status(project.getStatus())
                .timestamp(ZonedDateTime.now())
                .build();

        rabbitTemplate.convertAndSend(
                eventConstants.getProjectEventsExchange(),
                eventConstants.getProjectCreatedRoutingKey(),
                event);
    }

    @LoggingException
    @LoggingAfter(
            value = "Publishing project updated event for project: {}",
            args = {"project.getId()"}
    )
    public void publishProjectUpdatedEvent(Project project) {
        ProjectUpdatedEvent event = ProjectUpdatedEvent.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .key(project.getKey())
                .authorId(project.getAuthorId())
                .status(project.getStatus())
                .timestamp(ZonedDateTime.now())
                .build();

        rabbitTemplate.convertAndSend(
                eventConstants.getProjectEventsExchange(),
                eventConstants.getProjectUpdatedRoutingKey(),
                event);
    }

    @LoggingException
    @LoggingAfter(
            value = "Publishing project deleted event for project: {}",
            args = {"projectId"}
    )
    public void publishProjectDeletedEvent(UUID projectId, UUID organizationId) {
        ProjectDeletedEvent event = ProjectDeletedEvent.builder()
                .id(projectId)
                .organizationId(organizationId)
                .timestamp(ZonedDateTime.now())
                .build();

        rabbitTemplate.convertAndSend(
                eventConstants.getProjectEventsExchange(),
                eventConstants.getProjectDeletedRoutingKey(),
                event);
    }
}