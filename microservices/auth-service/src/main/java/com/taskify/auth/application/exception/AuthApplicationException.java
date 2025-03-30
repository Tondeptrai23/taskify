package com.taskify.auth.application.exception;

import lombok.Getter;

// TODO: Remove this exception after updating the common library
@Getter
public class AuthApplicationException extends RuntimeException {
    private final AuthErrorCode errorCode;

    public AuthApplicationException(String message, AuthErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public AuthApplicationException(String message, AuthErrorCode errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}