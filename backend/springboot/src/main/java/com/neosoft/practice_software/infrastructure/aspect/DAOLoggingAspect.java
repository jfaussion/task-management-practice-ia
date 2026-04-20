package com.neosoft.practice_software.infrastructure.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * AOP Aspect for logging DAO operations.
 * Provides automatic logging for all methods in DAO implementation classes.
 */
@Aspect
@Component
public class DAOLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(DAOLoggingAspect.class);

    /**
     * Around advice for all methods in DAO implementation classes.
     * Logs method execution with parameters, execution time, and results.
     * 
     * @param joinPoint The join point representing the method execution
     * @return The result of the method execution
     * @throws Throwable If the method execution throws an exception
     */
    @Around("execution(* com.neosoft.practice_software.infrastructure.jpa.dao.*.*(..))")
    public Object logDAOMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        // Log method entry with parameters (excluding sensitive data)
        String sanitizedArgs = sanitizeArguments(args);
        logger.debug("DAO Method Entry: {}.{}() with parameters: {}", className, methodName, sanitizedArgs);
        
        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            
            // Log successful execution with result summary
            String resultSummary = summarizeResult(result);
            logger.debug("DAO Method Success: {}.{}() completed in {}ms, result: {}", 
                        className, methodName, executionTime, resultSummary);
            
            return result;
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("DAO Method Error: {}.{}() failed after {}ms with parameters: {}", 
                        className, methodName, executionTime, sanitizedArgs, e);
            throw e;
        }
    }

    /**
     * After throwing advice for DAO methods.
     * Provides additional error logging for exceptions thrown by DAO methods.
     * 
     * @param joinPoint The join point where the exception was thrown
     * @param exception The exception that was thrown
     */
    @AfterThrowing(pointcut = "execution(* com.neosoft.practice_software.infrastructure.jpa.dao.*.*(..))", 
                   throwing = "exception")
    public void logDAOException(JoinPoint joinPoint, Throwable exception) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        
        logger.error("DAO Exception in {}.{}(): {}", className, methodName, exception.getMessage());
    }

    /**
     * Sanitizes method arguments for logging, removing or masking sensitive information.
     * 
     * @param args The method arguments
     * @return A sanitized string representation of the arguments
     */
    private String sanitizeArguments(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            
            Object arg = args[i];
            if (arg == null) {
                sb.append("null");
            } else if (arg instanceof String) {
                // For string arguments, show type and length to avoid logging sensitive data
                String str = (String) arg;
                sb.append("String(length=").append(str.length()).append(")");
                // Only show the actual value for short, non-sensitive strings (like search terms)
                if (str.length() <= 50 && !containsSensitiveKeywords(str)) {
                    sb.append(":\"").append(str).append("\"");
                }
            } else if (arg instanceof Pageable) {
                Pageable pageable = (Pageable) arg;
                sb.append("Pageable(page=").append(pageable.getPageNumber())
                  .append(", size=").append(pageable.getPageSize())
                  .append(", sort=").append(pageable.getSort()).append(")");
            } else {
                sb.append(arg.getClass().getSimpleName()).append("@").append(Integer.toHexString(arg.hashCode()));
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Checks if a string contains potentially sensitive keywords.
     * 
     * @param str The string to check
     * @return true if the string might contain sensitive information
     */
    private boolean containsSensitiveKeywords(String str) {
        if (str == null) {
            return false;
        }
        
        String lowerStr = str.toLowerCase();
        return lowerStr.contains("password") || 
               lowerStr.contains("token") || 
               lowerStr.contains("secret") ||
               lowerStr.contains("key") ||
               lowerStr.contains("auth");
    }

    /**
     * Creates a summary of the method result for logging purposes.
     * 
     * @param result The method result
     * @return A string summary of the result
     */
    private String summarizeResult(Object result) {
        if (result == null) {
            return "null";
        }
        
        if (result instanceof Collection) {
            Collection<?> collection = (Collection<?>) result;
            return "Collection(size=" + collection.size() + ")";
        }
        
        if (result instanceof Page) {
            Page<?> page = (Page<?>) result;
            return "Page(elements=" + page.getNumberOfElements() + 
                   ", totalElements=" + page.getTotalElements() + 
                   ", totalPages=" + page.getTotalPages() + 
                   ", page=" + page.getNumber() + ")";
        }
        
        if (result instanceof Boolean) {
            return "Boolean:" + result;
        }
        
        // For other objects, just show the class name
        return result.getClass().getSimpleName();
    }
}