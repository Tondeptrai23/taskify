package com.taskify.organization.event;

import com.taskify.common.constant.SystemRole;
import com.taskify.common.event.UserCreatedEvent;
import com.taskify.organization.entity.LocalUser;
import com.taskify.organization.repository.LocalUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class UserEventConsumer {

    private final LocalUserRepository _localUserRepository;

    public UserEventConsumer(LocalUserRepository localUserRepository) {
        this._localUserRepository = localUserRepository;
    }

    @Transactional
    @RabbitListener(queues = "${rabbitmq.queue.org-user-events}")
    public void handleUserCreatedEvent(@Payload UserCreatedEvent event) {
        try {
            log.info("Received user created event for user: {}", event.getId());

            // Check if user already exists and update if needed
            _localUserRepository.findById(event.getId())
                    .ifPresentOrElse(
                            existingUser -> {
                                // Update existing user
                                updateExistingUser(existingUser, event);
                                _localUserRepository.save(existingUser);
                                log.info("Updated existing user: {}", existingUser.getId());
                            },
                            () -> {
                                // Create new user
                                LocalUser newUser = createUserFromEvent(event);
                                _localUserRepository.save(newUser);
                                log.info("Created new user: {}", newUser.getId());
                            }
                    );
        } catch (Exception e) {
            log.error("Error processing user created event for user: {}", event.getId(), e);
            // Consider implementing a dead-letter queue or retry mechanism
        }
    }

    private LocalUser createUserFromEvent(UserCreatedEvent event) {
        LocalUser user = new LocalUser();
        user.setId(event.getId());
        user.setUsername(event.getUsername());
        user.setEmail(event.getEmail());
        user.setSystemRole(SystemRole.valueOf(event.getSystemRole()));
        return user;
    }

    private void updateExistingUser(LocalUser user, UserCreatedEvent event) {
        user.setUsername(event.getUsername());
        user.setEmail(event.getEmail());
        user.setSystemRole(SystemRole.valueOf(event.getSystemRole()));
    }
}