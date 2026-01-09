package org.example.shopify.AOP;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    // Advice 1: Measure execution time for all service methods
    @Around("execution(* org.example.shopify.Service.*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - start;
        System.out.println(joinPoint.getSignature() + " executed in " + executionTime + "ms");

        return proceed;
    }

    // Advice 2: Log when an exception is thrown in the Service layer
    @AfterThrowing(pointcut = "execution(* org.example.shopify.Service.*.*(..))", throwing = "ex")
    public void logServiceException(JoinPoint joinPoint, Exception ex) {
        // Catch IllegalOrderStateException and ResourceNotFoundException
        System.out.println("ALERT: Exception in " + joinPoint.getSignature().getName() +
                " | Message: " + ex.getMessage());
    }
}
