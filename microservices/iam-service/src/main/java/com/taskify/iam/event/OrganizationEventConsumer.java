package com.taskify.iam.event;

import com.taskify.commoncore.annotation.LoggingAround;
import com.taskify.commoncore.annotation.LoggingException;
import com.taskify.commoncore.event.org.OrganizationCreatedEvent;
import com.taskify.commoncore.event.org.OrganizationDeletedEvent;
import com.taskify.commoncore.event.org.OrganizationUpdatedEvent;
import com.taskify.iam.entity.LocalOrganization;
import com.taskify.iam.repository.OrganizationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Component
public class OrganizationEventConsumer {

    private final OrganizationRepository organizationRepository;

    @Autowired
    public OrganizationEventConsumer(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Transactional
    @RabbitListener(queues = "${rabbitmq.queue.iam-organization-created-events}")
    @LoggingAround
    @LoggingException
    public void handleOrganizationCreatedEvent(@Payload OrganizationCreatedEvent event) {
        organizationRepository.findById(event.getId())
                .ifPresentOrElse(
                        existingOrg -> {
                            // Update existing organization
                            updateExistingOrganization(existingOrg, event);
                            organizationRepository.save(existingOrg);
                            log.info("Updated existing organization: {}", existingOrg.getId());
                        },
                        () -> {
                            // Create new organization
                            LocalOrganization newOrg = createOrganizationFromEvent(event);
                            organizationRepository.save(newOrg);
                            log.info("Created new organization: {}", newOrg.getId());
                        }
                );
    }

    @Transactional
    @RabbitListener(queues = "${rabbitmq.queue.iam-organization-updated-events}")
    @LoggingAround
    @LoggingException
    public void handleOrganizationUpdatedEvent(@Payload OrganizationUpdatedEvent event) {
        Optional<LocalOrganization> organizationOptional = organizationRepository.findById(event.getId());

        if (organizationOptional.isPresent()) {
            LocalOrganization organization = organizationOptional.get();
            organization.setName(event.getName());
            organization.setOwnerId(event.getOwnerId());
            organizationRepository.save(organization);
            log.info("Updated organization from event: {}", event.getId());
        } else {
            log.warn("Cannot update non-existent organization: {}", event.getId());
            // Create the organization if it doesn't exist
            LocalOrganization newOrg = new LocalOrganization();
            newOrg.setId(event.getId());
            newOrg.setName(event.getName());
            newOrg.setOwnerId(event.getOwnerId());
            organizationRepository.save(newOrg);
            log.info("Created missing organization during update: {}", newOrg.getId());
        }
    }

    @Transactional
    @RabbitListener(queues = "${rabbitmq.queue.iam-organization-deleted-events}")
    @LoggingAround
    @LoggingException
    public void handleOrganizationDeletedEvent(@Payload OrganizationDeletedEvent event) {
        organizationRepository.deleteById(event.getId());
        log.info("Deleted organization: {}", event.getId());
    }

    private LocalOrganization createOrganizationFromEvent(OrganizationCreatedEvent event) {
        LocalOrganization organization = new LocalOrganization();
        organization.setId(event.getId());
        organization.setName(event.getName());
        organization.setOwnerId(event.getOwnerId());
        return organization;
    }

    private void updateExistingOrganization(LocalOrganization organization, OrganizationCreatedEvent event) {
        organization.setName(event.getName());
        organization.setOwnerId(event.getOwnerId());
    }
}