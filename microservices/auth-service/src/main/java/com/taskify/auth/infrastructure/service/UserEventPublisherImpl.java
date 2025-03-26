package com.taskify.auth.infrastructure.service;

import com.taskify.auth.application.contracts.UserEventPublisher;
import com.taskify.auth.domain.entity.User;
import com.taskify.commoncore.annotation.LoggingAfter;
import com.taskify.commoncore.annotation.LoggingException;
import com.taskify.commoncore.event.EventConstants;
import com.taskify.commoncore.event.user.UserCreatedEvent;
import com.taskify.commoncore.event.user.UserDeletedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class UserEventPublisherImpl implements UserEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final EventConstants eventConstants;

    public UserEventPublisherImpl(RabbitTemplate rabbitTemplate,
                                EventConstants eventConstants) {
        this.rabbitTemplate = rabbitTemplate;
        this.eventConstants = eventConstants;
    }

    @LoggingException
    @LoggingAfter(
            value = "Publishing user created event for user: {}",
            args = {"user.getId()"}
    )
    public void publishUserCreatedEvent(User user) {
        UserCreatedEvent event = UserCreatedEvent.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .systemRole(user.getSystemRole().toString())
                .createdAt(user.getCreatedAt())
                .build();

        rabbitTemplate.convertAndSend(
                eventConstants.getUserEventsExchange(),
                eventConstants.getUserCreatedRoutingKey(),
                event
        );
    }

    @LoggingException
    @LoggingAfter(
            value = "Publishing user deleted event for user: {}",
            args = {"user.getId()"}
    )
    public void publishUserDeletedEvent(User user) {
        UserDeletedEvent event = new UserDeletedEvent(user.getId());

        rabbitTemplate.convertAndSend(
                eventConstants.getUserEventsExchange(),
                eventConstants.getUserDeletedRoutingKey(),
                event
        );
    }
}
