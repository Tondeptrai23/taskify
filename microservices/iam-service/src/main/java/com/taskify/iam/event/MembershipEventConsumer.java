package com.taskify.iam.event;

import com.taskify.commoncore.annotation.LoggingAround;
import com.taskify.commoncore.annotation.LoggingException;
import com.taskify.commoncore.event.EventConstants;
import com.taskify.commoncore.event.orgmember.*;
import com.taskify.commoncore.event.projectmember.ProjectMemberBatchAddedEvent;
import com.taskify.commoncore.event.projectmember.ProjectMemberBatchRemovedEvent;
import com.taskify.commoncore.event.projectmember.ProjectMemberBatchRoleUpdatedEvent;
import com.taskify.iam.entity.Context;
import com.taskify.iam.entity.ContextType;
import com.taskify.iam.repository.ContextRepository;
import com.taskify.iam.service.role.UserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

@Slf4j
@Component
public class MembershipEventConsumer {

    private final UserRoleService userRoleService;
    private final ContextRepository contextRepository;
    private final EventConstants eventConstants;

    @Autowired
    public MembershipEventConsumer(
            UserRoleService userRoleService,
            ContextRepository contextRepository,
            EventConstants eventConstants) {
        this.userRoleService = userRoleService;
        this.contextRepository = contextRepository;
        this.eventConstants = eventConstants;
    }

    @Transactional
    @RabbitListener(queues = "#{eventConstants.getIamMembershipAddedQueue()}")
    @LoggingAround(value = "Processing batch member added event with {} members", args = {"event.getMembers().size()"})
    @LoggingException
    public void handleMemberBatchAddedEvent(@Payload MemberBatchAddedEvent event) {
        processMemberBatchEvent(event.getOrganizationId(), ContextType.ORGANIZATION, event.getMembers(),
                (member, contextId) -> userRoleService.assignRoleToUser(member.getUserId(), contextId, member.getRoleId()));
    }

    @Transactional
    @RabbitListener(queues = "#{eventConstants.getIamMembershipRemovedQueue()}")
    @LoggingAround(value = "Processing batch member removed event with {} members", args = {"event.getMembers().size()"})
    @LoggingException
    public void handleMemberBatchRemovedEvent(@Payload MemberBatchRemovedEvent event) {
        processMemberBatchEvent(event.getOrganizationId(), ContextType.ORGANIZATION, event.getMembers(),
                (member, contextId) -> userRoleService.removeRoleFromUser(member.getUserId(), contextId));
    }

    @Transactional
    @RabbitListener(queues = "#{eventConstants.getIamMembershipRoleUpdatedQueue()}")
    @LoggingAround(value = "Processing batch member role updated event with {} members", args = {"event.getMembers().size()"})
    @LoggingException
    public void handleMemberBatchRoleUpdatedEvent(@Payload MemberBatchRoleUpdatedEvent event) {
        processMemberBatchEvent(event.getOrganizationId(), ContextType.ORGANIZATION, event.getMembers(),
                (member, contextId) -> userRoleService.updateUserRole(member.getUserId(), contextId, event.getNewRoleId()));
    }

    @Transactional
    @RabbitListener(queues = "#{eventConstants.getIamProjectMemberAddedQueue()}")
    @LoggingAround(value = "Processing project member batch added event with {} members", args = {"event.getMembers().size()"})
    @LoggingException
    public void handleProjectMemberBatchAddedEvent(@Payload ProjectMemberBatchAddedEvent event) {
        processMemberBatchEvent(event.getProjectId(), ContextType.PROJECT, event.getMembers(),
                (member, contextId) -> userRoleService.assignRoleToUser(member.getUserId(), contextId, member.getRoleId()));
    }

    @Transactional
    @RabbitListener(queues = "#{eventConstants.getIamProjectMemberRemovedQueue()}")
    @LoggingAround(value = "Processing project member batch removed event with {} members", args = {"event.getMembers().size()"})
    @LoggingException
    public void handleProjectMemberBatchRemovedEvent(@Payload ProjectMemberBatchRemovedEvent event) {
        processMemberBatchEvent(event.getProjectId(), ContextType.PROJECT, event.getMembers(),
                (member, contextId) -> userRoleService.removeRoleFromUser(member.getUserId(), contextId));
    }

    @Transactional
    @RabbitListener(queues = "#{eventConstants.getIamProjectMemberRoleUpdatedQueue()}")
    @LoggingAround(value = "Processing project member batch role updated event with {} members", args = {"event.getMembers().size()"})
    @LoggingException
    public void handleProjectMemberBatchRoleUpdatedEvent(@Payload ProjectMemberBatchRoleUpdatedEvent event) {
        processMemberBatchEvent(event.getProjectId(), ContextType.PROJECT, event.getMembers(),
                (member, contextId) -> userRoleService.updateUserRole(member.getUserId(), contextId, event.getNewRoleId()));
    }

    private <T> void processMemberBatchEvent(UUID contextId, ContextType contextType, Iterable<T> members, BiConsumer<T, UUID> memberProcessor) {
        Optional<Context> context = contextRepository.findByExternalIdAndType(contextId.toString(), contextType);

        if (!context.isPresent()) {
            log.error("Context not found for {}: {}", contextType, contextId);
            return;
        }

        UUID contextUUID = context.get().getId();

        for (T member : members) {
            try {
                memberProcessor.accept(member, contextUUID);
            } catch (Exception e) {
                log.error("Error processing member {} in batch event: {}", member, e.getMessage(), e);
            }
        }
    }
}