package com.taskify.auth.application.exception;

import com.taskify.commoncore.error.CommonErrorCode;

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