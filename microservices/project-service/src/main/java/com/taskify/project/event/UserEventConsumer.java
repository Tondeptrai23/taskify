package com.taskify.project.event;

import com.taskify.commoncore.annotation.LoggingAround;
import com.taskify.commoncore.annotation.LoggingException;
import com.taskify.commoncore.constant.SystemRole;
import com.taskify.commoncore.event.user.UserCreatedEvent;
import com.taskify.commoncore.event.user.UserDeletedEvent;
import com.taskify.project.entity.LocalUser;
import com.taskify.project.repository.LocalUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Component
public class UserEventConsumer {

    private final LocalUserRepository localUserRepository;

    @Autowired
    public UserEventConsumer(LocalUserRepository localUserRepository) {
        this.localUserRepository = localUserRepository;
    }

    @Transactional
    @RabbitListener(queues = "taskify.user-created.events.project")
    @LoggingAround
    @LoggingException
    public void handleUserCreatedEvent(@Payload UserCreatedEvent event) {
        log.info("Received UserCreatedEvent for user: {}", event.getId());

        // Check if user already exists
        Optional<LocalUser> existingUser = localUserRepository.findById(event.getId());

        if (existingUser.isPresent()) {
            // Update existing user
            LocalUser user = existingUser.get();
            user.setUsername(event.getUsername());
            // Don't update email as it's a unique identifier
            localUserRepository.save(user);
            log.info("Updated existing user: {}", user.getId());
        } else {
            // Create new user
            LocalUser newUser = new LocalUser();
            newUser.setId(event.getId());
            newUser.setUsername(event.getUsername());
            newUser.setEmail(event.getEmail());
            newUser.setSystemRole(SystemRole.valueOf(event.getSystemRole()));

            localUserRepository.save(newUser);
            log.info("Created new user: {}", newUser.getId());
        }
    }

    @Transactional
    @RabbitListener(queues = "taskify.user-deleted.events.project")
    @LoggingAround
    @LoggingException
    public void handleUserDeletedEvent(@Payload UserDeletedEvent event) {
        log.info("Received UserDeletedEvent for user: {}", event.getId());
        localUserRepository.deleteById(event.getId());
        log.info("Deleted user: {}", event.getId());
    }
}