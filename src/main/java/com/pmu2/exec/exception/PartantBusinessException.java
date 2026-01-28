package com.pmu2.exec.exception;

/**
 * Exception thrown when a business rule related to partants is violated.
 * This exception is used specifically for domain-level business logic violations,
 * separate from infrastructure or validation concerns.
 */
public class PartantBusinessException extends RuntimeException {

    /**
     * Constructs a new PartantBusinessException with the specified detail message.
     *
     * @param message the detail message explaining which business rule was violated
     */
    public PartantBusinessException(String message) {
        super(message);
    }

    /**
     * Constructs a new PartantBusinessException with the specified detail message and cause.
     *
     * @param message the detail message explaining which business rule was violated
     * @param cause the cause of the exception
     */
    public PartantBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
