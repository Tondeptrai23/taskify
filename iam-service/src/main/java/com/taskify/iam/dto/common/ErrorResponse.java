package com.taskify.iam.dto.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
    private String message;
    private String errorCode;
    private Integer statusCode;

    public ErrorResponse(String message, String errorCode, Integer status) {
        this.message = message;
        this.errorCode = errorCode;
        this.statusCode = status;
    }
}
