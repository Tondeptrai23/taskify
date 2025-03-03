package com.taskify.iam.exception;

import com.taskify.common.error.exception.ConflictException;

public class DefaultRoleDeletionException extends ConflictException {
    public DefaultRoleDeletionException() {
        super("Cannot delete default role", IamErrorCode.DEFAULT_ROLE_DELETION);
    }
}