package com.taskify.auth.exception;

import com.taskify.common.error.BusinessException;

public class UnauthorizedException extends BusinessException {
    public UnauthorizedException(String message) {
        super(message, "UNAUTHORIZED");
    }
}
