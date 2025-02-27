package com.taskify.iam.exception;

import com.taskify.common.error.ConflictException;

public class DefaultRoleDeletionException extends ConflictException {
    public DefaultRoleDeletionException() {
        super("Cannot delete default role", "DEFAULT_ROLE_DELETION");
    }
}
