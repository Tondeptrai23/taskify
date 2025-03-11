package com.taskify.commoncore.error.resource;

import com.taskify.commoncore.error.CommonErrorCode;
import com.taskify.commoncore.error.exception.ResourceNotFoundException;

public class OrganizationNotFoundException extends ResourceNotFoundException {
    public OrganizationNotFoundException(String message) {
        super(message, CommonErrorCode.ORG_NOT_FOUND);
    }
}