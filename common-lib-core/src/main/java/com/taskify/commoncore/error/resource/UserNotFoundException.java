package com.taskify.commoncore.error.resource;

import com.taskify.commoncore.error.CommonErrorCode;
import com.taskify.commoncore.error.exception.ResourceNotFoundException;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(String message) {
        super(message, CommonErrorCode.USER_NOT_FOUND);
    }
}