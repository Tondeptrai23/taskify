package com.taskify.auth.exception;

import com.taskify.common.error.ConflictException;

public class UsernameAlreadyExistsException extends ConflictException {
    public UsernameAlreadyExistsException(String message) {
        super(message, "USERNAME_EXISTS");
    }
}
