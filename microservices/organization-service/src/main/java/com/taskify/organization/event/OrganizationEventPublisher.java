package com.taskify.organization.event;

import com.taskify.commoncore.event.EventConstants;
import com.taskify.commoncore.event.org.OrganizationDeletedEvent;
import com.taskify.commoncore.event.org.OrganizationUpdatedEvent;
import com.taskify.commoncore.event.org.OrganizationCreatedEvent;
import com.taskify.organization.entity.Organization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.UUID;

@Slf4j
@Component
public class OrganizationEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final EventConstants eventConstants;

    public OrganizationEventPublisher(RabbitTemplate rabbitTemplate, EventConstants eventConstants) {
        this.rabbitTemplate = rabbitTemplate;
        this.eventConstants = eventConstants;
    }

    public void publishOrganizationCreatedEvent(Organization organization) {
        try {
            OrganizationCreatedEvent event = OrganizationCreatedEvent.builder()
                    .id(organization.getId())
                    .name(organization.getName())
                    .description(organization.getDescription())
                    .ownerId(organization.getOwnerId())
                    .timestamp(ZonedDateTime.now())
                    .build();

            log.info("Publishing organization created event for organization: {}", organization.getId());
            rabbitTemplate.convertAndSend(
                    eventConstants.getOrganizationEventsExchange(),
                    eventConstants.getOrganizationCreatedRoutingKey(),
                    event);
        } catch (Exception e) {
            log.error("Failed to publish organization created event for organization: {}", organization.getId(), e);
        }
    }

    public void publishOrganizationUpdatedEvent(Organization organization) {
        try {
            OrganizationUpdatedEvent event = OrganizationUpdatedEvent.builder()
                    .id(organization.getId())
                    .name(organization.getName())
                    .description(organization.getDescription())
                    .ownerId(organization.getOwnerId())
                    .timestamp(ZonedDateTime.now())
                    .build();

            log.info("Publishing organization updated event for organization: {}", organization.getId());
            rabbitTemplate.convertAndSend(
                    eventConstants.getOrganizationEventsExchange(),
                    eventConstants.getOrganizationUpdatedRoutingKey(),
                    event);
        } catch (Exception e) {
            log.error("Failed to publish organization updated event for organization: {}", organization.getId(), e);
        }
    }

    public void publishOrganizationDeletedEvent(UUID organizationId) {
        try {
            OrganizationDeletedEvent event = OrganizationDeletedEvent.builder()
                    .id(organizationId)
                    .timestamp(ZonedDateTime.now())
                    .build();

            log.info("Publishing organization deleted event for organization: {}", organizationId);
            rabbitTemplate.convertAndSend(
                    eventConstants.getOrganizationEventsExchange(),
                    eventConstants.getOrganizationDeletedRoutingKey(),
                    event);
        } catch (Exception e) {
            log.error("Failed to publish organization deleted event for organization: {}", organizationId, e);
        }
    }
}