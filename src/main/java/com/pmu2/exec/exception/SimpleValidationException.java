package com.pmu2.exec.exception;

/**
 * Simple exception for validation errors.
 * Replaces the over-engineered ValidationException hierarchy.
 */
public class SimpleValidationException extends RuntimeException {
    
    public SimpleValidationException(String message) {
        super(message);
    }
    
    public SimpleValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

