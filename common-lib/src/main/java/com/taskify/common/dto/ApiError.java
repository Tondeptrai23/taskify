package com.taskify.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.taskify.common.error.exception.TaskifyException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {
    private boolean success; // Always false for errors
    private int status;      // HTTP status code
    private String code;     // Application-specific error code
    private String message;  // Human-readable error message
    private String path;     // Request path that generated the error
    private String service;  // Service that generated the error

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime timestamp;

    // For validation errors or additional error details (future use)
    private Object details;
}