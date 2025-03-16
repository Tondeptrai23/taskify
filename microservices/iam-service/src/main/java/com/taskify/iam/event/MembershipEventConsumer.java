package com.taskify.iam.event;

import com.taskify.commoncore.annotation.LoggingAround;
import com.taskify.commoncore.annotation.LoggingException;
import com.taskify.commoncore.event.EventConstants;
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
    private final EventConstants eventConstants;

    @Autowired
    public MembershipEventConsumer(UserRoleService userRoleService,
                                   EventConstants eventConstants) {
        this.userRoleService = userRoleService;
        this.eventConstants = eventConstants;
    }

    @Transactional
    @RabbitListener(queues = "#{eventConstants.getIamMembershipAddedQueue()}")
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
    @RabbitListener(queues = "#{eventConstants.getIamMembershipRemovedQueue()}")
    @LoggingAround
    @LoggingException
    public void handleMemberRemovedEvent(@Payload MemberRemovedEvent event) {
        userRoleService.removeOrganizationRoleFromUser(
                event.getUserId(),
                event.getOrganizationId()
        );
    }

    @Transactional
    @RabbitListener(queues = "#{eventConstants.getIamMembershipRoleUpdatedQueue()}")
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