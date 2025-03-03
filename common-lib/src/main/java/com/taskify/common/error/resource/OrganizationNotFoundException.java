package com.taskify.common.error.resource;

import com.taskify.common.error.ErrorCode;
import com.taskify.common.error.exception.ResourceNotFoundException;

public class OrganizationNotFoundException extends ResourceNotFoundException {
    public OrganizationNotFoundException(String message) {
        super(message, ErrorCode.ORG_NOT_FOUND);
    }
}