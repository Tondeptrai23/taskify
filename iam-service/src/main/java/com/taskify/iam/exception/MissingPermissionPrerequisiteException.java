package com.taskify.iam.exception;

import com.taskify.common.error.ConflictException;

import java.util.List;

public class MissingPermissionPrerequisiteException extends ConflictException {
    public MissingPermissionPrerequisiteException(List<String> missingPermissions) {
        super("Missing permissions: " + String.join(", ", missingPermissions), "MISSING_PERMISSION_PREREQUISITE");
    }
}