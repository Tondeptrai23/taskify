package com.taskify.common.error;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    String getCode();

    HttpStatus getStatus();
}
