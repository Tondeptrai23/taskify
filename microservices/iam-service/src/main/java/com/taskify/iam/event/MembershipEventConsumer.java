package com.taskify.iam.event;

import com.taskify.commoncore.annotation.LoggingAround;
import com.taskify.commoncore.annotation.LoggingException;
import com.taskify.commoncore.event.member.MemberAddedEvent;
import com.taskify.commoncore.event.member.MemberRemovedEvent;
import com.taskify.commoncore.event.member.MemberRoleUpdatedEvent;
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

    @Autowired
    public MembershipEventConsumer(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @Transactional
    @RabbitListener(queues = "${rabbitmq.queue.iam-membership-added-events}")
    @LoggingAround
    @LoggingException
    public void handleMemberAddedEvent(@Payload MemberAddedEvent event) {
        userRoleService.assignOrganizationRoleToUser(
                event.getUserId(),
                event.getOrganizationId(),
                event.getRoleId()
        );
    }

    @Transactional
    @RabbitListener(queues = "${rabbitmq.queue.iam-membership-removed-events}")
    @LoggingAround
    @LoggingException
    public void handleMemberRemovedEvent(@Payload MemberRemovedEvent event) {
        userRoleService.removeOrganizationRoleFromUser(
                event.getUserId(),
                event.getOrganizationId()
        );
    }

    @Transactional
    @RabbitListener(queues = "${rabbitmq.queue.iam-membership-role-updated-events}")
    @LoggingAround
    @LoggingException
    public void handleMemberRoleUpdatedEvent(@Payload MemberRoleUpdatedEvent event) {
        userRoleService.updateOrganizationRoleForUser(
                event.getUserId(),
                event.getOrganizationId(),
                event.getNewRoleId()
        );
    }
}