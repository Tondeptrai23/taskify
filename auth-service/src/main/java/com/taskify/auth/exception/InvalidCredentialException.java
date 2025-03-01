package com.taskify.auth.exception;

import com.taskify.common.error.UnauthorizedException;

public class InvalidCredentialException extends UnauthorizedException {
    public InvalidCredentialException(String message) {
        super(message, "INVALID_CREDENTIALS");
    }
}
