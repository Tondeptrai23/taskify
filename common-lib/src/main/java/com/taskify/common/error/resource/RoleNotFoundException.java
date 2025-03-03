package com.taskify.common.error.resource;

import com.taskify.common.error.CommonErrorCode;
import com.taskify.common.error.exception.ResourceNotFoundException;

public class RoleNotFoundException extends ResourceNotFoundException {
    public RoleNotFoundException(String message) {
        super(message, CommonErrorCode.ROLE_NOT_FOUND);
    }
}