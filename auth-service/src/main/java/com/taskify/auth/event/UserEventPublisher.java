package com.taskify.auth.event;

import com.taskify.auth.entity.User;
import com.taskify.common.event.UserCreatedEvent;
import com.taskify.common.event.UserDeletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.user-events}")
    private String userEventsExchange;

    public UserEventPublisher(
            RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
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
            rabbitTemplate.convertAndSend(userEventsExchange, "user.created", event);
        } catch (Exception e) {
            log.error("Failed to publish user created event for user: {}", user.getId(), e);
            // Consider implementing a retry mechanism or fallback strategy
        }
    }

    public void publishUserDeletedEvent(User user) {
        try {
            UserDeletedEvent event = new UserDeletedEvent(user.getId());

            log.info("Publishing user deleted event for user: {}", user.getId());
            rabbitTemplate.convertAndSend(userEventsExchange, "user.deleted", event);
        } catch (Exception e) {
            log.error("Failed to publish user deleted event for user: {}", user.getId(), e);
            // Consider implementing a retry mechanism or fallback strategy
        }
    }
}
