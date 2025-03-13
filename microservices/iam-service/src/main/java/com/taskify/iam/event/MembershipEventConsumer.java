package com.taskify.iam.event;

import com.taskify.commoncore.event.MemberAddedEvent;
import com.taskify.commoncore.event.MemberRemovedEvent;
import com.taskify.commoncore.event.MemberRoleUpdatedEvent;
import com.taskify.iam.service.user.UserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
public class MembershipEventConsumer {

    private final UserRoleService userRoleService;

    @Autowired
    public MembershipEventConsumer(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @Transactional
    @RabbitListener(queues = "${rabbitmq.queue.iam-membership-added-events}")
    public void handleMemberAddedEvent(@Payload MemberAddedEvent event) {
        try {
            log.info("Received member added event for user: {} in organization: {}",
                    event.getUserId(), event.getOrganizationId());

            userRoleService.assignOrganizationRoleToUser(
                    event.getUserId(),
                    event.getOrganizationId(),
                    event.getRoleId()
            );

            log.info("Successfully processed member added event for user: {} in organization: {}",
                    event.getUserId(), event.getOrganizationId());
        } catch (Exception e) {
            log.error("Error processing member added event for user: {} in organization: {}",
                    event.getUserId(), event.getOrganizationId(), e);
        }
    }

    @Transactional
    @RabbitListener(queues = "${rabbitmq.queue.iam-membership-removed-events}")
    public void handleMemberRemovedEvent(@Payload MemberRemovedEvent event) {
        try {
            log.info("Received member removed event for user: {} in organization: {}",
                    event.getUserId(), event.getOrganizationId());

            userRoleService.removeOrganizationRoleFromUser(
                    event.getUserId(),
                    event.getOrganizationId()
            );

            log.info("Successfully processed member removed event for user: {} in organization: {}",
                    event.getUserId(), event.getOrganizationId());
        } catch (Exception e) {
            log.error("Error processing member removed event for user: {} in organization: {}",
                    event.getUserId(), event.getOrganizationId(), e);
        }
    }

    @Transactional
    @RabbitListener(queues = "${rabbitmq.queue.iam-membership-role-updated-events}")
    public void handleMemberRoleUpdatedEvent(@Payload MemberRoleUpdatedEvent event) {
        try {
            log.info("Received member role updated event for user: {} in organization: {}",
                    event.getUserId(), event.getOrganizationId());

            userRoleService.updateOrganizationRoleForUser(
                    event.getUserId(),
                    event.getOrganizationId(),
                    event.getNewRoleId()
            );

            log.info("Successfully processed member role updated event for user: {} in organization: {}",
                    event.getUserId(), event.getOrganizationId());
        } catch (Exception e) {
            log.error("Error processing member role updated event for user: {} in organization: {}",
                    event.getUserId(), event.getOrganizationId(), e);
        }
    }
}