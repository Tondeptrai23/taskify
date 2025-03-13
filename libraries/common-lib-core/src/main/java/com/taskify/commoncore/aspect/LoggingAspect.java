package com.taskify.commoncore.aspect;

import com.taskify.commoncore.annotation.LoggingAround;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {
    @Around("@annotation(com.taskify.commoncore.annotation.LoggingAround)")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Method {} called with arguments {}", joinPoint.getSignature().getName(), joinPoint.getArgs());
        Object result = joinPoint.proceed();
        log.info("Method {} returned {}", joinPoint.getSignature().getName(), result);
        return result;
    }

    @Before("@annotation(com.taskify.commoncore.annotation.LoggingBefore)")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Method {} called with arguments {}", joinPoint.getSignature().getName(), joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "@annotation(com.taskify.commoncore.annotation.LoggingAfter)", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("Method {} returned {}", joinPoint.getSignature().getName(), result);
    }

    @AfterThrowing(pointcut = "@annotation(com.taskify.commoncore.annotation.LoggingException)", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        log.error("Method {} threw exception {}", joinPoint.getSignature().getName(), exception);
    }
}
