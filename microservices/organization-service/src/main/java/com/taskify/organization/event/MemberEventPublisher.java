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
public class MemberEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.member-events}")
    private String memberEventsExchange;

    public MemberEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishMemberAddedEvent(Membership membership) {
        try {
            MemberAddedEvent event = MemberAddedEvent.builder()
                    .organizationId(membership.getOrganization().getId())
                    .userId(membership.getUser().getId())
                    .roleId(membership.getRoleId())
                    .isAdmin(membership.isAdmin())
                    .joinedAt(membership.getJoinedAt())
                    .build();

            log.info("Publishing member added event for user: {} in organization: {}",
                    membership.getUser().getId(), membership.getOrganization().getId());
            rabbitTemplate.convertAndSend(memberEventsExchange, "member.added", event);
        } catch (Exception e) {
            log.error("Failed to publish member added event", e);
        }
    }

    public void publishMemberRemovedEvent(Membership membership) {
        try {
            MemberRemovedEvent event = MemberRemovedEvent.builder()
                    .organizationId(membership.getOrganization().getId())
                    .userId(membership.getUser().getId())
                    .removedAt(ZonedDateTime.now())
                    .build();

            log.info("Publishing member removed event for user: {} in organization: {}",
                    membership.getUser().getId(), membership.getOrganization().getId());
            rabbitTemplate.convertAndSend(memberEventsExchange, "member.removed", event);
        } catch (Exception e) {
            log.error("Failed to publish member removed event", e);
        }
    }

    public void publishMemberRoleChangedEvent(Membership membership, UUID oldRoleId) {
        try {
            MemberRoleUpdatedEvent event = MemberRoleUpdatedEvent.builder()
                    .organizationId(membership.getOrganization().getId())
                    .userId(membership.getUser().getId())
                    .oldRoleId(oldRoleId)
                    .newRoleId(membership.getRoleId())
                    .updatedAt(ZonedDateTime.now())
                    .build();

            log.info("Publishing member role changed event for user: {} in organization: {}",
                    membership.getUser().getId(), membership.getOrganization().getId());
            rabbitTemplate.convertAndSend(memberEventsExchange, "member.role.changed", event);
        } catch (Exception e) {
            log.error("Failed to publish member role changed event", e);
        }
    }
}