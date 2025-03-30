package com.taskify.auth.infrastructure.exception;

import com.taskify.auth.application.exception.AuthErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class HttpStatusMapper {
    private final Map<String, HttpStatus> statusMap;

    public HttpStatusMapper() {
        statusMap = new HashMap<>();

        statusMap.put(AuthErrorCode.INVALID_CREDENTIALS.getCode(), HttpStatus.UNAUTHORIZED);
        statusMap.put(AuthErrorCode.USERNAME_EXISTS.getCode(), HttpStatus.CONFLICT);
        statusMap.put(AuthErrorCode.EMAIL_EXISTS.getCode(), HttpStatus.CONFLICT);
        statusMap.put(AuthErrorCode.USER_NOT_FOUND.getCode(), HttpStatus.NOT_FOUND);
        statusMap.put(AuthErrorCode.TOKEN_INVALID.getCode(), HttpStatus.UNAUTHORIZED);
        statusMap.put(AuthErrorCode.TOKEN_EXPIRED.getCode(), HttpStatus.UNAUTHORIZED);
        statusMap.put(AuthErrorCode.TOKEN_REVOKED.getCode(), HttpStatus.UNAUTHORIZED);
    }

    public HttpStatus getStatus(String errorCode) {
        return statusMap.getOrDefault(errorCode, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}