package com.taskify.common.error.resource;

import com.taskify.common.error.ErrorCode;
import com.taskify.common.error.exception.ResourceNotFoundException;

public class ProjectNotFoundException extends ResourceNotFoundException {
    public ProjectNotFoundException(String message) {
        super(message, ErrorCode.PROJECT_NOT_FOUND);
    }
}