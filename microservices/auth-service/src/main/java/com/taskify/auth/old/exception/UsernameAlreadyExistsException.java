package com.taskify.auth.old.exception;

import com.taskify.commoncore.error.exception.ConflictException;

public class UsernameAlreadyExistsException extends ConflictException {
    public UsernameAlreadyExistsException(String message) {
        super(message, AuthErrorCode.USERNAME_EXISTS);
    }
}
