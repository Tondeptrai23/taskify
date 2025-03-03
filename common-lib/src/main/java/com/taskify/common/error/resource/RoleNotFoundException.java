package com.taskify.common.error.resource;

import com.taskify.common.error.ErrorCode;
import com.taskify.common.error.exception.ResourceNotFoundException;

public class RoleNotFoundException extends ResourceNotFoundException {
    public RoleNotFoundException(String message) {
        super(message, ErrorCode.ROLE_NOT_FOUND);
    }
}