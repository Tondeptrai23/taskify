package com.taskify.organization.event;

import com.taskify.commoncore.annotation.LoggingAfter;
import com.taskify.commoncore.annotation.LoggingException;
import com.taskify.commoncore.event.EventConstants;
import com.taskify.commoncore.event.member.MemberAddedEvent;
import com.taskify.commoncore.event.member.MemberRemovedEvent;
import com.taskify.commoncore.event.member.MemberRoleUpdatedEvent;
import com.taskify.organization.entity.Membership;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.UUID;

@Slf4j
@Component
public class MembershipEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final EventConstants eventConstants;

    public MembershipEventPublisher(RabbitTemplate rabbitTemplate,
                                    EventConstants eventConstants) {
        this.rabbitTemplate = rabbitTemplate;
        this.eventConstants = eventConstants;
    }

    @LoggingException
    @LoggingAfter(
            value = "Publishing member added event for user: {} in organization: {}",
            args = {"membership.getUser().getId()", "membership.getOrganization().getId()"}
    )
    public void publishMemberAddedEvent(Membership membership) {
        MemberAddedEvent event = MemberAddedEvent.builder()
                .id(membership.getId())
                .organizationId(membership.getOrganization().getId())
                .userId(membership.getUser().getId())
                .roleId(membership.getRoleId())
                .isAdmin(membership.isAdmin())
                .timestamp(ZonedDateTime.now())
                .build();

        rabbitTemplate.convertAndSend(
                eventConstants.getMembershipEventsExchange(),
                eventConstants.getMembershipAddedRoutingKey(),
                event);
    }

    @LoggingException
    @LoggingAfter(
            value = "Publishing member removed event for user: {} in organization: {}",
            args = {"userId", "organizationId"}
    )
    public void publishMemberRemovedEvent(UUID membershipId, UUID organizationId, UUID userId) {
        MemberRemovedEvent event = MemberRemovedEvent.builder()
                .id(membershipId)
                .organizationId(organizationId)
                .userId(userId)
                .timestamp(ZonedDateTime.now())
                .build();

        rabbitTemplate.convertAndSend(
                eventConstants.getMembershipEventsExchange(),
                eventConstants.getMembershipRemovedRoutingKey(),
                event);

    }

    @LoggingException
    @LoggingAfter(
            value = "Publishing member role updated event for user: {} in organization: {}",
            args = {"membership.getUser().getId()", "membership.getOrganization().getId()"}
    )
    public void publishMemberRoleUpdatedEvent(Membership membership, UUID oldRoleId) {
        MemberRoleUpdatedEvent event = MemberRoleUpdatedEvent.builder()
                .id(membership.getId())
                .organizationId(membership.getOrganization().getId())
                .userId(membership.getUser().getId())
                .newRoleId(membership.getRoleId())
                .oldRoleId(oldRoleId)
                .isAdmin(membership.isAdmin())
                .timestamp(ZonedDateTime.now())
                .build();

        rabbitTemplate.convertAndSend(
                eventConstants.getMembershipEventsExchange(),
                eventConstants.getMembershipRoleUpdatedRoutingKey(),
                event);
    }
}