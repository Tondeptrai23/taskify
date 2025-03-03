package com.taskify.auth.exception;

import com.taskify.common.error.exception.ConflictException;

public class UsernameAlreadyExistsException extends ConflictException {
    public UsernameAlreadyExistsException(String message) {
        super(message, AuthErrorCode.USERNAME_EXISTS);
    }
}
