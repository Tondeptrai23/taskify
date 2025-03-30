package com.taskify.auth.application.aop;

import com.taskify.auth.application.exception.AuthApplicationException;
import com.taskify.auth.application.exception.AuthErrorCode;
import com.taskify.auth.domain.exception.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Aspect that intercepts methods annotated with @TranslateDomainException
 * and translates AuthDomainException instances to application exceptions
 */
@Aspect
@Component
public class DomainExceptionHandlerAspect {
    private static final Map<Class<? extends AuthDomainException>, AuthErrorCode> ERROR_MAPPINGS = Map.of(
            InvalidCredentialsException.class, AuthErrorCode.INVALID_CREDENTIALS,
            TokenExpiredException.class, AuthErrorCode.TOKEN_EXPIRED,
            TokenRevokedException.class, AuthErrorCode.TOKEN_REVOKED,
            TokenValidationException.class, AuthErrorCode.TOKEN_INVALID
    );

    @Around("@annotation(com.taskify.auth.application.aop.TranslateDomainException)")
    public Object handleDomainExceptions(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (AuthDomainException e) {
            String methodName = joinPoint.getSignature().toShortString();
            throw translate(e);
        }
    }

    /**
     * Translates domain exceptions to application exceptions using error code mapping
     */
    private AuthApplicationException translate(AuthDomainException exception) {
        AuthErrorCode errorCode = ERROR_MAPPINGS.getOrDefault(
                exception.getClass(),
                AuthErrorCode.UNKNOWN
        );

        return new AuthApplicationException(exception.getMessage(), errorCode);
    }
}