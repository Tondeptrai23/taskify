package com.taskify.auth.event;

import com.taskify.auth.entity.User;
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

    public void publishUserCreatedEvent(User user) {
        try {
            UserCreatedEvent event = UserCreatedEvent.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .systemRole(user.getSystemRole().toString())
                    .createdAt(user.getCreatedAt())
                    .build();

            log.info("Publishing user created event for user: {}", user.getId());
            rabbitTemplate.convertAndSend(
                    eventConstants.getUserEventsExchange(),
                    eventConstants.getUserCreatedRoutingKey(),
                    event
            );
        } catch (Exception e) {
            log.error("Failed to publish user created event for user: {}", user.getId(), e);
            // Consider implementing a retry mechanism or fallback strategy
        }
    }

    public void publishUserDeletedEvent(User user) {
        try {
            UserDeletedEvent event = new UserDeletedEvent(user.getId());

            log.info("Publishing user deleted event for user: {}", user.getId());
            rabbitTemplate.convertAndSend(
                    eventConstants.getUserEventsExchange(),
                    eventConstants.getUserDeletedRoutingKey(),
                    event
            );
        } catch (Exception e) {
            log.error("Failed to publish user deleted event for user: {}", user.getId(), e);
            // Consider implementing a retry mechanism or fallback strategy
        }
    }
}
