package com.taskify.common.error.resource;

import com.taskify.common.error.ErrorCode;
import com.taskify.common.error.exception.ResourceNotFoundException;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(String message) {
        super(message, ErrorCode.USER_NOT_FOUND);
    }
}