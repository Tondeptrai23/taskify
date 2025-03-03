package com.taskify.common.error.resource;

import com.taskify.common.error.CommonErrorCode;
import com.taskify.common.error.exception.ResourceNotFoundException;

public class ProjectNotFoundException extends ResourceNotFoundException {
    public ProjectNotFoundException(String message) {
        super(message, CommonErrorCode.PROJECT_NOT_FOUND);
    }
}