package com.taskify.organization.event;

import com.taskify.commoncore.annotation.LoggingAfter;
import com.taskify.commoncore.annotation.LoggingException;
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

    @LoggingException
    @LoggingAfter(
            value = "Publishing organization created event for organization: {}",
            args = {"organization.getId()"}
    )
    public void publishOrganizationCreatedEvent(Organization organization) {
        OrganizationCreatedEvent event = OrganizationCreatedEvent.builder()
                .id(organization.getId())
                .name(organization.getName())
                .description(organization.getDescription())
                .ownerId(organization.getOwnerId())
                .timestamp(ZonedDateTime.now())
                .build();

        rabbitTemplate.convertAndSend(
                eventConstants.getOrganizationEventsExchange(),
                eventConstants.getOrganizationCreatedRoutingKey(),
                event);
    }

    @LoggingException
    @LoggingAfter(
            value = "Publishing organization updated event for organization: {}",
            args = {"organization.getId()"}
    )
    public void publishOrganizationUpdatedEvent(Organization organization) {
        OrganizationUpdatedEvent event = OrganizationUpdatedEvent.builder()
                .id(organization.getId())
                .name(organization.getName())
                .description(organization.getDescription())
                .ownerId(organization.getOwnerId())
                .timestamp(ZonedDateTime.now())
                .build();

        rabbitTemplate.convertAndSend(
                eventConstants.getOrganizationEventsExchange(),
                eventConstants.getOrganizationUpdatedRoutingKey(),
                event);
    }

    @LoggingException
    @LoggingAfter(
            value = "Publishing organization deleted event for organization: {}",
            args = {"organizationId"}
    )
    public void publishOrganizationDeletedEvent(UUID organizationId) {
        OrganizationDeletedEvent event = OrganizationDeletedEvent.builder()
                .id(organizationId)
                .timestamp(ZonedDateTime.now())
                .build();

        rabbitTemplate.convertAndSend(
                eventConstants.getOrganizationEventsExchange(),
                eventConstants.getOrganizationDeletedRoutingKey(),
                event);
    }
}