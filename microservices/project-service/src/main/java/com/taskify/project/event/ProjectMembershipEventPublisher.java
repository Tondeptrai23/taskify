package com.taskify.project.event;

import com.taskify.commoncore.annotation.LoggingAfter;
import com.taskify.commoncore.annotation.LoggingException;
import com.taskify.commoncore.event.EventConstants;
import com.taskify.commoncore.event.projectmember.*;
import com.taskify.project.entity.ProjectMembership;
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
public class ProjectMembershipEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final EventConstants eventConstants;

    public ProjectMembershipEventPublisher(RabbitTemplate rabbitTemplate,
                                           EventConstants eventConstants) {
        this.rabbitTemplate = rabbitTemplate;
        this.eventConstants = eventConstants;
    }

    @LoggingException
    @LoggingAfter(
            value = "Publishing project member batch added event for project: {} with {} members",
            args = {"projectId", "memberships.size()"}
    )
    public void publishProjectMemberAddedEvent(UUID projectId, UUID organizationId, List<ProjectMembership> memberships) {
        List<ProjectMemberBatchAddedEvent.MemberData> memberData = memberships.stream()
                .map(membership -> ProjectMemberBatchAddedEvent.MemberData.builder()
                        .id(membership.getId())
                        .userId(membership.getUser().getId())
                        .roleId(membership.getRoleId())
                        .build())
                .collect(Collectors.toList());

        ProjectMemberBatchAddedEvent event = ProjectMemberBatchAddedEvent.builder()
                .projectId(projectId)
                .organizationId(organizationId)
                .members(memberData)
                .timestamp(ZonedDateTime.now())
                .build();

        rabbitTemplate.convertAndSend(
                eventConstants.getProjectMembershipEventsExchange(),
                eventConstants.getProjectMemberAddedRoutingKey(),
                event);
    }

    @LoggingException
    @LoggingAfter(
            value = "Publishing project member batch removed event for project: {} with {} members",
            args = {"projectId", "membershipIdToUserIdMap.size()"}
    )
    public void publishProjectMemberRemovedEvent(UUID projectId, UUID organizationId, Map<UUID, UUID> membershipIdToUserIdMap) {
        List<ProjectMemberBatchRemovedEvent.MemberReference> memberReferences = membershipIdToUserIdMap.entrySet().stream()
                .map(entry -> ProjectMemberBatchRemovedEvent.MemberReference.builder()
                        .id(entry.getKey())
                        .userId(entry.getValue())
                        .build())
                .collect(Collectors.toList());

        ProjectMemberBatchRemovedEvent event = ProjectMemberBatchRemovedEvent.builder()
                .projectId(projectId)
                .organizationId(organizationId)
                .members(memberReferences)
                .timestamp(ZonedDateTime.now())
                .build();

        rabbitTemplate.convertAndSend(
                eventConstants.getProjectMembershipEventsExchange(),
                eventConstants.getProjectMemberRemovedRoutingKey(),
                event);
    }

    @LoggingException
    @LoggingAfter(
            value = "Publishing project member batch role updated event for project: {} with {} members",
            args = {"projectId", "membershipsWithOldRoles.size()"}
    )
    public void publishProjectMemberRoleUpdatedEvent(UUID projectId, UUID organizationId, UUID newRoleId,
                                                     List<ProjectMembershipWithOldRole> membershipsWithOldRoles) {
        List<ProjectMemberBatchRoleUpdatedEvent.MemberRoleUpdate> memberUpdates = membershipsWithOldRoles.stream()
                .map(item -> ProjectMemberBatchRoleUpdatedEvent.MemberRoleUpdate.builder()
                        .id(item.getMembership().getId())
                        .userId(item.getMembership().getUser().getId())
                        .oldRoleId(item.getOldRoleId())
                        .build())
                .collect(Collectors.toList());

        ProjectMemberBatchRoleUpdatedEvent event = ProjectMemberBatchRoleUpdatedEvent.builder()
                .projectId(projectId)
                .organizationId(organizationId)
                .newRoleId(newRoleId)
                .members(memberUpdates)
                .timestamp(ZonedDateTime.now())
                .build();

        rabbitTemplate.convertAndSend(
                eventConstants.getProjectMembershipEventsExchange(),
                eventConstants.getProjectMemberRoleUpdatedRoutingKey(),
                event);
    }

    @lombok.Value
    public static class ProjectMembershipWithOldRole {
        ProjectMembership membership;
        UUID oldRoleId;
    }
}