package com.taskify.auth.event;

import com.taskify.auth.entity.User;
import com.taskify.commoncore.annotation.LoggingAfter;
import com.taskify.commoncore.annotation.LoggingException;
import com.taskify.commoncore.event.EventConstants;
import com.taskify.commoncore.event.user.UserCreatedEvent;
import com.taskify.commoncore.event.user.UserDeletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final EventConstants eventConstants;

    public UserEventPublisher(RabbitTemplate rabbitTemplate,
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
