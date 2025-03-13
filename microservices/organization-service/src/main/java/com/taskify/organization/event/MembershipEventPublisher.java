package com.taskify.organization.event;

import com.taskify.commoncore.event.MemberAddedEvent;
import com.taskify.commoncore.event.MemberRemovedEvent;
import com.taskify.commoncore.event.MemberRoleUpdatedEvent;
import com.taskify.organization.entity.Membership;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.UUID;

@Slf4j
@Component
public class MembershipEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.membership-events}")
    private String membershipEventsExchange;

    public MembershipEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishMemberAddedEvent(Membership membership) {
        try {
            MemberAddedEvent event = MemberAddedEvent.builder()
                    .id(membership.getId())
                    .organizationId(membership.getOrganization().getId())
                    .userId(membership.getUser().getId())
                    .roleId(membership.getRoleId())
                    .isAdmin(membership.isAdmin())
                    .timestamp(ZonedDateTime.now())
                    .build();

            log.info("Publishing member added event for user: {} in organization: {}",
                    membership.getUser().getId(), membership.getOrganization().getId());
            rabbitTemplate.convertAndSend(membershipEventsExchange, "membership.added", event);
        } catch (Exception e) {
            log.error("Failed to publish member added event for user: {} in organization: {}",
                    membership.getUser().getId(), membership.getOrganization().getId(), e);
        }
    }

    public void publishMemberRemovedEvent(UUID membershipId, UUID organizationId, UUID userId) {
        try {
            MemberRemovedEvent event = MemberRemovedEvent.builder()
                    .id(membershipId)
                    .organizationId(organizationId)
                    .userId(userId)
                    .timestamp(ZonedDateTime.now())
                    .build();

            log.info("Publishing member removed event for user: {} in organization: {}",
                    userId, organizationId);
            rabbitTemplate.convertAndSend(membershipEventsExchange, "membership.removed", event);
        } catch (Exception e) {
            log.error("Failed to publish member removed event for user: {} in organization: {}",
                    userId, organizationId, e);
        }
    }

    public void publishMemberRoleUpdatedEvent(Membership membership, UUID oldRoleId) {
        try {
            MemberRoleUpdatedEvent event = MemberRoleUpdatedEvent.builder()
                    .id(membership.getId())
                    .organizationId(membership.getOrganization().getId())
                    .userId(membership.getUser().getId())
                    .newRoleId(membership.getRoleId())
                    .oldRoleId(oldRoleId)
                    .isAdmin(membership.isAdmin())
                    .timestamp(ZonedDateTime.now())
                    .build();

            log.info("Publishing member role updated event for user: {} in organization: {}",
                    membership.getUser().getId(), membership.getOrganization().getId());
            rabbitTemplate.convertAndSend(membershipEventsExchange, "membership.role.updated", event);
        } catch (Exception e) {
            log.error("Failed to publish member role updated event for user: {} in organization: {}",
                    membership.getUser().getId(), membership.getOrganization().getId(), e);
        }
    }
}