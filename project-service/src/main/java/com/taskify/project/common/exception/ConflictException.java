package com.taskify.project.common.exception;

import lombok.Getter;

@Getter
public class ConflictException extends RuntimeException {
    protected String errorCode;

    public ConflictException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
