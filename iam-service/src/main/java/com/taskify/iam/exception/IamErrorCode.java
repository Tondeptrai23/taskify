package com.taskify.iam.exception;

import com.taskify.commoncore.error.ErrorCode;
import org.springframework.http.HttpStatus;

public enum IamErrorCode implements ErrorCode {
    DEFAULT_ROLE_DELETION("IAM_DEFAULT_ROLE_DELETION", HttpStatus.CONFLICT),
    MISSING_PERMISSION_PREREQUISITE("IAM_MISSING_PERMISSION_PREREQUISITE", HttpStatus.CONFLICT);

    private final String code;
    private final HttpStatus status;

    IamErrorCode(String code, HttpStatus status) {
        this.code = code;
        this.status = status;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }
}