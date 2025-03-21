package com.taskify.iam.event;

import com.taskify.commoncore.annotation.LoggingAround;
import com.taskify.commoncore.annotation.LoggingException;
import com.taskify.commoncore.event.EventConstants;
import com.taskify.commoncore.event.projectmember.*;
import com.taskify.iam.service.user.UserProjectRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class ProjectMembershipEventConsumer {

    private final UserProjectRoleService userProjectRoleService;
    private final EventConstants eventConstants;

    @Autowired
    public ProjectMembershipEventConsumer(UserProjectRoleService userProjectRoleService,
                                          EventConstants eventConstants) {
        this.userProjectRoleService = userProjectRoleService;
        this.eventConstants = eventConstants;
    }

    @Transactional
    @RabbitListener(queues = "#{eventConstants.getIamProjectMemberAddedQueue()}")
    @LoggingAround(value = "Processing project member batch added event with {} members", args = {"event.getMembers().size()"})
    @LoggingException
    public void handleProjectMemberBatchAddedEvent(@Payload ProjectMemberBatchAddedEvent event) {
        // Process all members in the batch
        for (ProjectMemberBatchAddedEvent.MemberData member : event.getMembers()) {
            try {
                userProjectRoleService.assignProjectRoleToUser(
                        member.getUserId(),
                        event.getProjectId(),
                        event.getOrganizationId(),
                        member.getRoleId()
                );
            } catch (Exception e) {
                // Log error but continue processing other members
                log.error("Error processing member {} in project batch add event: {}",
                        member.getId(), e.getMessage(), e);
            }
        }
    }

    @Transactional
    @RabbitListener(queues = "#{eventConstants.getIamProjectMemberRemovedQueue()}")
    @LoggingAround(value = "Processing project member batch removed event with {} members", args = {"event.getMembers().size()"})
    @LoggingException
    public void handleProjectMemberBatchRemovedEvent(@Payload ProjectMemberBatchRemovedEvent event) {
        // Process all members in the batch
        for (ProjectMemberBatchRemovedEvent.MemberReference member : event.getMembers()) {
            try {
                userProjectRoleService.removeProjectRoleFromUser(
                        member.getUserId(),
                        event.getProjectId(),
                        event.getOrganizationId()
                );
            } catch (Exception e) {
                // Log error but continue processing other members
                log.error("Error processing member {} in project batch remove event: {}",
                        member.getId(), e.getMessage(), e);
            }
        }
    }

    @Transactional
    @RabbitListener(queues = "#{eventConstants.getIamProjectMemberRoleUpdatedQueue()}")
    @LoggingAround(value = "Processing project member batch role updated event with {} members", args = {"event.getMembers().size()"})
    @LoggingException
    public void handleProjectMemberBatchRoleUpdatedEvent(@Payload ProjectMemberBatchRoleUpdatedEvent event) {
        // Process all members in the batch
        for (ProjectMemberBatchRoleUpdatedEvent.MemberRoleUpdate member : event.getMembers()) {
            try {
                userProjectRoleService.updateProjectRoleForUser(
                        member.getUserId(),
                        event.getProjectId(),
                        event.getOrganizationId(),
                        event.getNewRoleId()
                );
            } catch (Exception e) {
                // Log error but continue processing other members
                log.error("Error processing member {} in project batch role update event: {}",
                        member.getId(), e.getMessage(), e);
            }
        }
    }
}