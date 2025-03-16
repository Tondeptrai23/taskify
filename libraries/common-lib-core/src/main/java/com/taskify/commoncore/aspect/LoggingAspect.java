package com.taskify.commoncore.aspect;

import com.taskify.commoncore.annotation.LoggingAround;
import com.taskify.commoncore.annotation.LoggingBefore;
import com.taskify.commoncore.annotation.LoggingAfter;
import com.taskify.commoncore.annotation.LoggingException;
import com.taskify.commoncore.utils.ArgumentFormatterUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("@annotation(loggingAround)")
    public Object logAround(ProceedingJoinPoint joinPoint, LoggingAround loggingAround) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String messageTemplate = loggingAround.value();
        String[] argNames = loggingAround.args();

        Object[] formattedArgs = ArgumentFormatterUtils.getFormattedArgs(joinPoint, argNames);

        if (!StringUtils.hasText(messageTemplate)) {
            log.info("[BEFORE] Method {} called with arguments {}", methodName, joinPoint.getArgs());
        } else {
            log.info("[BEFORE] " + messageTemplate, formattedArgs);
        }

        Object result = joinPoint.proceed();

        if (!StringUtils.hasText(messageTemplate)) {
            log.info("[AFTER] Method {} returned {}", methodName, result);
        } else {
            log.info("[AFTER] " + messageTemplate + " completed", formattedArgs);
        }

        return result;
    }

    @Before("@annotation(loggingBefore)")
    public void logBefore(JoinPoint joinPoint, LoggingBefore loggingBefore) {
        String methodName = joinPoint.getSignature().getName();
        String messageTemplate = loggingBefore.value();
        String[] argNames = loggingBefore.args();

        Object[] formattedArgs = ArgumentFormatterUtils.getFormattedArgs(joinPoint, argNames);

        if (!StringUtils.hasText(messageTemplate)) {
            log.info("[BEFORE] Method {} called with arguments {}", methodName, joinPoint.getArgs());
        } else {
            log.info("[BEFORE] " + messageTemplate, formattedArgs);
        }
    }

    @AfterReturning(pointcut = "@annotation(loggingAfter)", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, LoggingAfter loggingAfter, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String messageTemplate = loggingAfter.value();
        String[] argNames = loggingAfter.args();

        Object[] formattedArgs = ArgumentFormatterUtils.getFormattedArgs(joinPoint, argNames);

        if (!StringUtils.hasText(messageTemplate)) {
            log.info("[AFTER] Method {} returned {}", methodName, result);
        } else {
            log.info("[AFTER] " + messageTemplate + " completed", formattedArgs);
        }
    }

    @AfterThrowing(pointcut = "@annotation(loggingException)", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, LoggingException loggingException, Throwable exception) {
        String methodName = joinPoint.getSignature().getName();
        String messageTemplate = loggingException.value();
        String[] argNames = loggingException.args();

        Object[] formattedArgs = ArgumentFormatterUtils.getFormattedArgs(joinPoint, argNames);
        Object[] argsWithException = new Object[formattedArgs.length + 1];
        System.arraycopy(formattedArgs, 0, argsWithException, 0, formattedArgs.length);
        argsWithException[formattedArgs.length] = exception;

        if (!StringUtils.hasText(messageTemplate)) {
            log.error("[ERROR] Method {} threw exception {}", methodName, exception);
        } else {
            log.error("[ERROR] " + messageTemplate, argsWithException);
        }
    }
}