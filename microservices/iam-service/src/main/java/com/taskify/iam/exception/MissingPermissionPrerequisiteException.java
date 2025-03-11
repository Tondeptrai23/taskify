package com.taskify.iam.exception;

import com.taskify.commoncore.error.exception.ConflictException;

import java.util.List;

public class MissingPermissionPrerequisiteException extends ConflictException {
    public MissingPermissionPrerequisiteException(List<String> missingPermissions) {
        super("Missing permissions: " + String.join(", ", missingPermissions), IamErrorCode.MISSING_PERMISSION_PREREQUISITE);
    }
}