package com.taskify.iam.exception;

public class ConflictException extends RuntimeException {
    protected String errorCode;
    public ConflictException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
