package com.taskify.auth.application.exception;

// TODO: Remove this exception after updating the common library
public class AuthApplicationException extends RuntimeException {
    private final AuthErrorCode errorCode;

    public AuthApplicationException(String message, AuthErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public AuthErrorCode getErrorCode() {
        return errorCode;
    }
}