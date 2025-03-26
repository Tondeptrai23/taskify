package com.taskify.auth.old.exception;

import com.taskify.commoncore.error.exception.UnauthorizedException;

public class InvalidCredentialException extends UnauthorizedException {
    public InvalidCredentialException(String message) {
        super(message, AuthErrorCode.INVALID_CREDENTIALS);
    }
}
