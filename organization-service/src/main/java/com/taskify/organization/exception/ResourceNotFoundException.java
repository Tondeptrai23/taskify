package com.taskify.organization.exception;

public class ResourceNotFoundException extends RuntimeException {
    protected String errorCode;

    public ResourceNotFoundException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
