package com.pmu2.exec.exception;

/**
 * Exception thrown when a business rule related to courses is violated.
 * This exception is used specifically for domain-level business logic violations,
 * separate from infrastructure or validation concerns.
 */
public class CourseBusinessException extends RuntimeException {

    /**
     * Constructs a new CourseBusinessException with the specified detail message.
     *
     * @param message the detail message explaining which business rule was violated
     */
    public CourseBusinessException(String message) {
        super(message);
    }

    /**
     * Constructs a new CourseBusinessException with the specified detail message and cause.
     *
     * @param message the detail message explaining which business rule was violated
     * @param cause the cause of the exception
     */
    public CourseBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
