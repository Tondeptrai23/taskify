package com.taskify.organization.event;

import com.taskify.commoncore.annotation.LoggingAround;
import com.taskify.commoncore.annotation.LoggingException;
import com.taskify.commoncore.constant.SystemRole;
import com.taskify.commoncore.event.EventConstants;
import com.taskify.commoncore.event.user.UserCreatedEvent;
import com.taskify.commoncore.event.user.UserDeletedEvent;
import com.taskify.organization.entity.LocalUser;
import com.taskify.organization.repository.LocalUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class UserEventConsumer {

    private final LocalUserRepository userRepository;
    private final EventConstants eventConstants;

    public UserEventConsumer(LocalUserRepository localUserRepository,
                             EventConstants eventConstants) {
        this.userRepository = localUserRepository;
        this.eventConstants = eventConstants;
    }

    @Transactional
    @RabbitListener(queues = "#{eventConstants.getOrgUserCreatedQueue()}")
    @LoggingAround
    @LoggingException
    public void handleUserCreatedEvent(@Payload UserCreatedEvent event) {
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
                            LocalUser newUser = createUserFromEvent(event);
                            userRepository.save(newUser);
                            log.info("Created new user: {}", newUser.getId());
                        }
                );
    }

    @Transactional
    @RabbitListener(queues = "#{eventConstants.getOrgUserDeletedQueue()}")
    @LoggingAround
    @LoggingException
    public void handleUserDeletedEvent(@Payload UserDeletedEvent event) {
        // Delete user
        userRepository.deleteById(event.getId());
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