package com.taskify.commoncore.error.exception;

import com.taskify.commoncore.error.ErrorCode;
import lombok.Getter;

public class TaskifyException extends RuntimeException {
    private final ErrorCode errorCode;
    @Getter
    private final Object details;

    public TaskifyException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.details = null;
    }

    public TaskifyException(String message, ErrorCode errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.details = null;
    }

    public TaskifyException(String message, ErrorCode errorCode, Object details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }

    public TaskifyException(String message, ErrorCode errorCode, Object details, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.details = details;
    }

    public String getErrorCode() {
        return errorCode.getCode();
    }

    public ErrorCode getErrorCodeEnum() {
        return errorCode;
    }
}