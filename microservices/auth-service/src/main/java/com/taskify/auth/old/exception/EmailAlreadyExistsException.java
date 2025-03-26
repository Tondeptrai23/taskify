package com.taskify.auth.old.exception;

import com.taskify.commoncore.error.exception.ConflictException;

public class EmailAlreadyExistsException extends ConflictException {
    public EmailAlreadyExistsException(String message) {
        super(message, AuthErrorCode.EMAIL_EXISTS);
    }
}
