package com.taskify.iam.event;

import com.taskify.commoncore.annotation.LoggingAround;
import com.taskify.commoncore.annotation.LoggingException;
import com.taskify.commoncore.event.EventConstants;
import com.taskify.commoncore.event.org.OrganizationCreatedEvent;
import com.taskify.commoncore.event.org.OrganizationDeletedEvent;
import com.taskify.commoncore.event.org.OrganizationUpdatedEvent;
import com.taskify.commoncore.event.project.ProjectCreatedEvent;
import com.taskify.commoncore.event.project.ProjectDeletedEvent;
import com.taskify.commoncore.event.project.ProjectUpdatedEvent;
import com.taskify.iam.entity.Context;
import com.taskify.iam.entity.ContextType;
import com.taskify.iam.repository.ContextRepository;
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

    private final ContextRepository contextRepository;
    private final EventConstants eventConstants;

    @Autowired
    public ProjectEventConsumer(
            ContextRepository contextRepository,
            EventConstants eventConstants) {
        this.contextRepository = contextRepository;
        this.eventConstants = eventConstants;
    }

    @Transactional
    @RabbitListener(queues = "#{eventConstants.getIamProjectCreatedQueue()}")
    @LoggingAround
    @LoggingException
    public void handleProjectCreatedEvent(@Payload ProjectCreatedEvent event) {
        // First check if project context already exists
        Optional<Context> existingContext = contextRepository.findById(event.getId());

        if (existingContext.isPresent()) {
            log.info("Context for project already exists: {}", event.getId());
            return;
        }

        // Find the parent organization context
        Optional<Context> parentContext = contextRepository.findById(event.getOrganizationId());

        if (!parentContext.isPresent()) {
            log.error("Parent organization context not found for project: {}", event.getId());
            return;
        }

        // Create a new context for this project
        Context newContext = new Context();
        newContext.setId(event.getId());
        newContext.setName(event.getName());
        newContext.setType(ContextType.PROJECT);
        newContext.setParent(parentContext.get());
        newContext.setPath(parentContext.get().getPath() + "/projects/" + event.getId());

        // Save the new context
        Context savedContext = contextRepository.save(newContext);

        // Update parent's children collection
        Context parent = parentContext.get();
        parent.getChildren().add(savedContext);
        contextRepository.save(parent);
    }

    @Transactional
    @RabbitListener(queues = "#{eventConstants.getIamProjectUpdatedQueue()}")
    @LoggingAround
    @LoggingException
    public void handleProjectUpdatedEvent(@Payload ProjectUpdatedEvent event) {
        contextRepository.findById(event.getId())
                .ifPresent(context -> {
                    context.setName(event.getName());
                    contextRepository.save(context);
                    log.info("Updated context for project: {}", event.getId());
                });
    }

    @Transactional
    @RabbitListener(queues = "#{eventConstants.getIamProjectDeletedQueue()}")
    @LoggingAround
    @LoggingException
    public void handleProjectDeletedEvent(@Payload ProjectDeletedEvent event) {
        contextRepository.findById(event.getId())
                .ifPresent(context -> {
                    // If it has a parent, update the parent's children list
                    if (context.getParent() != null) {
                        Context parent = context.getParent();
                        parent.getChildren().remove(context);
                        contextRepository.save(parent);
                    }

                    contextRepository.delete(context);
                    log.info("Deleted context for project: {}", event.getId());
                });
    }
}