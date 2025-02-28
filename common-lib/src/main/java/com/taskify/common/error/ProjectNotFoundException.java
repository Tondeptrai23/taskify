package com.taskify.common.error;

public class ProjectNotFoundException extends ResourceNotFoundException {
    public ProjectNotFoundException(String message) {
        super(message, "PROJECT_NOT_FOUND");
    }
}