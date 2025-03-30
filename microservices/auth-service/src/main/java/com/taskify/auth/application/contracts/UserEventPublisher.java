package com.taskify.auth.application.contracts;

import com.taskify.auth.domain.entity.User;

public interface UserEventPublisher {
    void publishUserCreatedEvent(User user);
    void publishUserDeletedEvent(User user);
}
