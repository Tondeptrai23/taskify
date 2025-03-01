package com.taskify.auth.exception;

import com.taskify.common.error.ConflictException;

public class EmailAlreadyExistsException extends ConflictException {
    public EmailAlreadyExistsException(String message) {
        super(message, "EMAIL_EXISTS");
    }
}
