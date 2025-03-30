package com.taskify.auth.presentation;

import com.taskify.auth.application.exception.AuthApplicationException;
import com.taskify.auth.infrastructure.exception.HttpStatusMapper;
import com.taskify.commoncore.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZonedDateTime;

@Slf4j
@RestControllerAdvice
public class AuthExceptionHandler {
    private final HttpStatusMapper statusMapper;

    @Autowired
    public AuthExceptionHandler(HttpStatusMapper statusMapper) {
        this.statusMapper = statusMapper;
    }

    @ExceptionHandler(AuthApplicationException.class)
    public ResponseEntity<ApiError> handleAuthApplicationException(
            AuthApplicationException ex, HttpServletRequest request) {

        String errorCode = ex.getErrorCode().getCode();
        HttpStatus status = statusMapper.getStatus(errorCode);

        ApiError apiError = ApiError.builder()
                .success(false)
                .status(status.value())
                .code(errorCode)
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .timestamp(ZonedDateTime.now())
                .build();

        log.debug(ex.getMessage(), ex);

        return new ResponseEntity<>(apiError, status);
    }
}