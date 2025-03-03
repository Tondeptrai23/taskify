package com.taskify.common.error;

import com.taskify.common.dto.ApiError;
import com.taskify.common.error.exception.TaskifyException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public abstract class BaseExceptionHandler {
    @Value("${spring.application.name}")
    private String serviceName;

    @ExceptionHandler(TaskifyException.class)
    public ResponseEntity<ApiError> handleTaskifyException(TaskifyException ex, HttpServletRequest request) {
        ErrorCode errorCode = ex.getErrorCodeEnum();
        HttpStatus status = errorCode.getStatus();

        ApiError apiError = ApiError.builder()
                .success(false)
                .status(status.value())
                .code(errorCode.getCode())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .service(serviceName)
                .timestamp(ZonedDateTime.now())
                .details(ex.getDetails())
                .build();

        logError(ex, apiError);

        return new ResponseEntity<>(apiError, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getField(),
                        fieldError -> fieldError.getDefaultMessage() == null ?
                                "Invalid value" : fieldError.getDefaultMessage(),
                        (error1, error2) -> error1 // In case of duplicate keys
                ));

        ApiError apiError = ApiError.builder()
                .success(false)
                .status(HttpStatus.BAD_REQUEST.value())
                .code(CommonErrorCode.VALIDATION_ERROR.getCode())
                .message("Validation failed")
                .path(request.getRequestURI())
                .service(serviceName)
                .timestamp(ZonedDateTime.now())
                .details(validationErrors)
                .build();

        logError(ex, apiError);

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {
        ApiError apiError = ApiError.builder()
                .success(false)
                .status(HttpStatus.FORBIDDEN.value())
                .code(CommonErrorCode.FORBIDDEN.getCode())
                .message("Access denied: You don't have permission to perform this action")
                .path(request.getRequestURI())
                .service(serviceName)
                .timestamp(ZonedDateTime.now())
                .details(null)
                .build();

        logError(ex, apiError);

        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllUncaughtException(
            Exception ex, HttpServletRequest request) {
        ApiError apiError = ApiError.builder()
                .success(false)
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .code(CommonErrorCode.INTERNAL_ERROR.getCode())
                .message("An unexpected error occurred")
                .path(request.getRequestURI())
                .service(serviceName)
                .timestamp(ZonedDateTime.now())
                .details(null)
                .build();

        logError(ex, apiError);

        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void logError(Throwable ex, ApiError apiError) {
        if (apiError.getStatus() >= 500) {
            log.error("Error response: {}", apiError, ex);
        } else {
            log.warn("Error response: {}", apiError);
            log.debug("Error details:", ex);
        }
    }
}