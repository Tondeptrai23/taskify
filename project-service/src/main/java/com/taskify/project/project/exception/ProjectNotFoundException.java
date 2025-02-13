package com.taskify.project.project.exception;

import com.taskify.project.common.exception.ResourceNotFoundException;

public class ProjectNotFoundException extends ResourceNotFoundException {
    public ProjectNotFoundException(String message) {
        super(message, "PROJECT_NOT_FOUND");
    }
}
