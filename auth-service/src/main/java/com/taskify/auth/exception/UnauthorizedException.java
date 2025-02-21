package com.taskify.auth.exception;

public class UnauthorizedException extends RuntimeException {
    protected String errorCode;
    public UnauthorizedException(String message) {
        super(message);
        errorCode = "UNAUTHORIZED";
    }

    public String getErrorCode() {
        return errorCode;
    }
}
