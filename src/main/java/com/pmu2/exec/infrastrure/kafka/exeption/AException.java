package com.pmu2.exec.infrastrure.kafka.exeption;

/**
 * Custom exception class for handling specific exceptions related to Kafka operations.
 * This class extends the RuntimeException class, allowing it to be used in
 * unchecked exception scenarios.
 *
 * @since 1.0 (replace with the appropriate version number)
 */
public class AException extends RuntimeException {

    /**
     * Constructs a new AException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the
     *                {@link #getMessage()} method)
     */
    public AException(String message) {
        super(message);
    }
}