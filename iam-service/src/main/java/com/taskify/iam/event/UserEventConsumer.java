package com.taskify.iam.event;

import com.taskify.common.event.UserCreatedEvent;
import com.taskify.iam.entity.User;
import com.taskify.iam.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class UserEventConsumer {

    private final UserRepository userRepository;

    public UserEventConsumer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @RabbitListener(queues = "${rabbitmq.queue.iam-user-events}")
    public void handleUserCreatedEvent(@Payload UserCreatedEvent event) {
        try {
            log.info("Received user created event for user: {}", event.getId());

            // Check if user already exists and update if needed
            userRepository.findById(event.getId())
                    .ifPresentOrElse(
                            existingUser -> {
                                // Update existing user
                                updateExistingUser(existingUser, event);
                                userRepository.save(existingUser);
                                log.info("Updated existing user: {}", existingUser.getId());
                            },
                            () -> {
                                // Create new user
                                User newUser = createUserFromEvent(event);
                                userRepository.save(newUser);
                                log.info("Created new user: {}", newUser.getId());
                            }
                    );
        } catch (Exception e) {
            log.error("Error processing user created event for user: {}", event.getId(), e);
            // Consider implementing a dead-letter queue or retry mechanism
        }
    }

    private User createUserFromEvent(UserCreatedEvent event) {
        User user = new User();
        user.setId(event.getId());
        user.setUsername(event.getUsername());
        user.setEmail(event.getEmail());
        user.setSystemRole(event.getSystemRole());
        return user;
    }

    private void updateExistingUser(User user, UserCreatedEvent event) {
        user.setUsername(event.getUsername());
        user.setEmail(event.getEmail());
        user.setSystemRole(event.getSystemRole());
    }
}