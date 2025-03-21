package com.taskify.iam.event;

import com.taskify.commoncore.annotation.LoggingAround;
import com.taskify.commoncore.annotation.LoggingException;
import com.taskify.commoncore.event.EventConstants;
import com.taskify.commoncore.event.org.OrganizationCreatedEvent;
import com.taskify.commoncore.event.org.OrganizationDeletedEvent;
import com.taskify.commoncore.event.org.OrganizationUpdatedEvent;
import com.taskify.iam.entity.Context;
import com.taskify.iam.entity.ContextType;
import com.taskify.iam.repository.ContextRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class OrganizationEventConsumer {

    private final ContextRepository contextRepository;
    private final EventConstants eventConstants;

    @Autowired
    public OrganizationEventConsumer(ContextRepository contextRepository,
                                     EventConstants eventConstants) {
        this.contextRepository = contextRepository;
        this.eventConstants = eventConstants;
    }

    @Transactional
    @RabbitListener(queues = "#{eventConstants.getIamOrganizationCreatedQueue()}")
    @LoggingAround
    @LoggingException
    public void handleOrganizationCreatedEvent(@Payload OrganizationCreatedEvent event) {
        Optional<Context> existingContext = contextRepository.findByExternalIdAndType(
                event.getId().toString(), ContextType.ORGANIZATION);

        if (existingContext.isPresent()) {
            log.info("Context for organization already exists: {}", event.getId());
            return;
        }

        // Create a new context for this organization
        Context newContext = new Context();
        newContext.setId(event.getId());
        newContext.setName(event.getName());
        newContext.setType(ContextType.ORGANIZATION);
        newContext.setPath("/orgs/" + event.getId());

        contextRepository.save(newContext);
        log.info("Created new context for organization: {}", event.getId());
    }

    @Transactional
    @RabbitListener(queues = "#{eventConstants.getIamOrganizationUpdatedQueue()}")
    @LoggingAround
    @LoggingException
    public void handleOrganizationUpdatedEvent(@Payload OrganizationUpdatedEvent event) {
        contextRepository.findByExternalIdAndType(event.getId().toString(), ContextType.ORGANIZATION)
                .ifPresent(context -> {
                    context.setName(event.getName());
                    contextRepository.save(context);
                    log.info("Updated context for organization: {}", event.getId());
                });
    }

    @Transactional
    @RabbitListener(queues = "#{eventConstants.getIamOrganizationDeletedQueue()}")
    @LoggingAround
    @LoggingException
    public void handleOrganizationDeletedEvent(@Payload OrganizationDeletedEvent event) {
        contextRepository.findByExternalIdAndType(event.getId().toString(), ContextType.ORGANIZATION)
                .ifPresent(context -> {
                    // Delete all child contexts as well (e.g., projects)
                    List<Context> descendants = contextRepository.findAllDescendants(context.getId());
                    for (Context descendant : descendants) {
                        contextRepository.delete(descendant);
                    }

                    contextRepository.delete(context);
                    log.info("Deleted context for organization: {}", event.getId());
                });
    }
}