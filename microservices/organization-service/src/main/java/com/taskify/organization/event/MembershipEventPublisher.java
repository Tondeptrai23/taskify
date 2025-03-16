package com.taskify.organization.event;

import com.taskify.commoncore.annotation.LoggingAfter;
import com.taskify.commoncore.annotation.LoggingException;
import com.taskify.commoncore.event.EventConstants;
import com.taskify.commoncore.event.member.*;
import com.taskify.organization.entity.Membership;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
            value = "Publishing member batch added event for organization: {} with {} members",
            args = {"organizationId", "memberships.size()"}
    )
    public void publishMemberAddedEvent(UUID organizationId, List<Membership> memberships) {
        List<MemberBatchAddedEvent.MemberData> memberData = memberships.stream()
                .map(membership -> MemberBatchAddedEvent.MemberData.builder()
                        .id(membership.getId())
                        .userId(membership.getUser().getId())
                        .roleId(membership.getRoleId())
                        .isAdmin(membership.isAdmin())
                        .build())
                .collect(Collectors.toList());

        MemberBatchAddedEvent event = MemberBatchAddedEvent.builder()
                .organizationId(organizationId)
                .members(memberData)
                .timestamp(ZonedDateTime.now())
                .build();

        rabbitTemplate.convertAndSend(
                eventConstants.getMembershipEventsExchange(),
                eventConstants.getMembershipAddedRoutingKey(),
                event);
    }

    @LoggingException
    @LoggingAfter(
            value = "Publishing member batch removed event for organization: {} with {} members",
            args = {"organizationId", "memberIds.size()"}
    )
    public void publishMemberRemovedEvent(UUID organizationId, Map<UUID, UUID> membershipIdToUserIdMap) {
        List<MemberBatchRemovedEvent.MemberReference> memberReferences = membershipIdToUserIdMap.entrySet().stream()
                .map(entry -> MemberBatchRemovedEvent.MemberReference.builder()
                        .id(entry.getKey())
                        .userId(entry.getValue())
                        .build())
                .collect(Collectors.toList());

        MemberBatchRemovedEvent event = MemberBatchRemovedEvent.builder()
                .organizationId(organizationId)
                .members(memberReferences)
                .timestamp(ZonedDateTime.now())
                .build();

        rabbitTemplate.convertAndSend(
                eventConstants.getMembershipEventsExchange(),
                eventConstants.getMembershipRemovedRoutingKey(),
                event);
    }

    @LoggingException
    @LoggingAfter(
            value = "Publishing member batch role updated event for organization: {} with {} members",
            args = {"organizationId", "membershipsWithOldRoles.size()"}
    )
    public void publishMemberRoleUpdatedEvent(UUID organizationId, UUID newRoleId,
                                                   List<MembershipWithOldRole> membershipsWithOldRoles) {
        List<MemberBatchRoleUpdatedEvent.MemberRoleUpdate> memberUpdates = membershipsWithOldRoles.stream()
                .map(item -> MemberBatchRoleUpdatedEvent.MemberRoleUpdate.builder()
                        .id(item.getMembership().getId())
                        .userId(item.getMembership().getUser().getId())
                        .oldRoleId(item.getOldRoleId())
                        .isAdmin(item.getMembership().isAdmin())
                        .build())
                .collect(Collectors.toList());

        MemberBatchRoleUpdatedEvent event = MemberBatchRoleUpdatedEvent.builder()
                .organizationId(organizationId)
                .newRoleId(newRoleId)
                .members(memberUpdates)
                .timestamp(ZonedDateTime.now())
                .build();

        rabbitTemplate.convertAndSend(
                eventConstants.getMembershipEventsExchange(),
                eventConstants.getMembershipRoleUpdatedRoutingKey(),
                event);
    }

    @lombok.Value
    public static class MembershipWithOldRole {
        Membership membership;
        UUID oldRoleId;
    }
}