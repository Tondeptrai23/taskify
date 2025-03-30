package com.taskify.auth.exception;

import com.taskify.commoncore.error.exception.ConflictException;

public class EmailAlreadyExistsException extends ConflictException {
    public EmailAlreadyExistsException(String message) {
        super(message, AuthErrorCode.EMAIL_EXISTS);
    }
}
