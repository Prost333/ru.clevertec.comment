package ru.clevertec.comment.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggCommentAspect {

    @Pointcut(value = "execution(* ru.clevertec.comment.controller.CommentController.*(..))")
    public void logClient() {
    }

    @Around("logClient()")
    public Object logMethodExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        long startTime = System.currentTimeMillis();
        log.info("Method " + methodName + " started");
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        log.info("Method " + methodName + " executed in " + executionTime + " millisecond");
        return result;
    }

    @AfterThrowing(value = "logClient()", throwing = "e")
    public void afterUser (Throwable e){
        log.error(e.getMessage(), e);
    }


}
