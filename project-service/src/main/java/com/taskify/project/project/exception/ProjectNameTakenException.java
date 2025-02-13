package com.taskify.project.project.exception;

import com.taskify.project.common.exception.ConflictException;

public class ProjectNameTakenException extends ConflictException {
    public ProjectNameTakenException(String message) {
        super(message, "PROJECT_NAME_TAKEN");
    }
}
