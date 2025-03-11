package com.taskify.commoncore.error.resource;

import com.taskify.commoncore.error.CommonErrorCode;
import com.taskify.commoncore.error.exception.ResourceNotFoundException;

public class RoleNotFoundException extends ResourceNotFoundException {
    public RoleNotFoundException(String message) {
        super(message, CommonErrorCode.ROLE_NOT_FOUND);
    }
}