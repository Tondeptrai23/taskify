package com.taskify.organization.exception;

import com.taskify.commonweb.error.BaseExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class OrgExceptionHandler extends BaseExceptionHandler {
    // Service-specific exception handlers here
}