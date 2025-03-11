package com.taskify.commoncore.error.resource;

import com.taskify.commoncore.error.CommonErrorCode;
import com.taskify.commoncore.error.exception.ResourceNotFoundException;

public class ProjectNotFoundException extends ResourceNotFoundException {
    public ProjectNotFoundException(String message) {
        super(message, CommonErrorCode.PROJECT_NOT_FOUND);
    }
}