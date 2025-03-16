package com.taskify.iam.event;

import com.taskify.commoncore.annotation.LoggingAround;
import com.taskify.commoncore.annotation.LoggingException;
import com.taskify.commoncore.event.EventConstants;
import com.taskify.commoncore.event.member.*;
import com.taskify.iam.service.user.UserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class MembershipEventConsumer {

    private final UserRoleService userRoleService;
    private final EventConstants eventConstants;

    @Autowired
    public MembershipEventConsumer(UserRoleService userRoleService,
                                   EventConstants eventConstants) {
        this.userRoleService = userRoleService;
        this.eventConstants = eventConstants;
    }

    @Transactional
    @RabbitListener(queues = "#{eventConstants.getIamMembershipAddedQueue()}")
    @LoggingAround(value = "Processing batch member added event with {} members", args = {"event.getMembers().size()"})
    @LoggingException
    public void handleMemberBatchAddedEvent(@Payload MemberBatchAddedEvent event) {
        // Process all members in the batch
        for (MemberBatchAddedEvent.MemberData member : event.getMembers()) {
            try {
                userRoleService.assignOrganizationRoleToUser(
                        member.getUserId(),
                        event.getOrganizationId(),
                        member.getRoleId()
                );
            } catch (Exception e) {
                // Log error but continue processing other members
                log.error("Error processing member {} in batch add event: {}",
                        member.getId(), e.getMessage(), e);
            }
        }
    }

    @Transactional
    @RabbitListener(queues = "#{eventConstants.getIamMembershipRemovedQueue()}")
    @LoggingAround(value = "Processing batch member removed event with {} members", args = {"event.getMembers().size()"})
    @LoggingException
    public void handleMemberBatchRemovedEvent(@Payload MemberBatchRemovedEvent event) {
        // Process all members in the batch
        for (MemberBatchRemovedEvent.MemberReference member : event.getMembers()) {
            try {
                userRoleService.removeOrganizationRoleFromUser(
                        member.getUserId(),
                        event.getOrganizationId()
                );
            } catch (Exception e) {
                // Log error but continue processing other members
                log.error("Error processing member {} in batch remove event: {}",
                        member.getId(), e.getMessage(), e);
            }
        }
    }

    @Transactional
    @RabbitListener(queues = "#{eventConstants.getIamMembershipRoleUpdatedQueue()}")
    @LoggingAround(value = "Processing batch member role updated event with {} members", args = {"event.getMembers().size()"})
    @LoggingException
    public void handleMemberBatchRoleUpdatedEvent(@Payload MemberBatchRoleUpdatedEvent event) {
        // Process all members in the batch
        for (MemberBatchRoleUpdatedEvent.MemberRoleUpdate member : event.getMembers()) {
            try {
                userRoleService.updateOrganizationRoleForUser(
                        member.getUserId(),
                        event.getOrganizationId(),
                        event.getNewRoleId()
                );
            } catch (Exception e) {
                // Log error but continue processing other members
                log.error("Error processing member {} in batch role update event: {}",
                        member.getId(), e.getMessage(), e);
            }
        }
    }
}