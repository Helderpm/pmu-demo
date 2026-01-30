package com.pmu2.exec.exception;

/**
 * Simple exception for business logic violations.
 * Replaces the multiple business exception classes.
 */
public class BusinessException extends RuntimeException {
    
    public BusinessException(String message) {
        super(message);
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

