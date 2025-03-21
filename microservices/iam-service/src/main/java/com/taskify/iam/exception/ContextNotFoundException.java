package com.taskify.iam.exception;

import com.taskify.commoncore.error.CommonErrorCode;
import com.taskify.commoncore.error.exception.ResourceNotFoundException;

public class ContextNotFoundException extends ResourceNotFoundException {
    public ContextNotFoundException(String message) {
        super(message, CommonErrorCode.RESOURCE_NOT_FOUND);
    }
}