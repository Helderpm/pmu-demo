package com.pmu2.exec.exception;

/**
 * Exception thrown when a partant (participant) is not found.
 * This exception provides specific error handling for partant-related operations.
 */
public class PartantNotFoundException extends RuntimeException {

    /**
     * Constructs a new PartantNotFoundException with the specified partant ID.
     *
     * @param id the ID of the partant that was not found
     */
    public PartantNotFoundException(Long id) {
        super("Partant not found with id: " + id);
    }

    /**
     * Constructs a new PartantNotFoundException with the specified partant name.
     *
     * @param name the name of the partant that was not found
     */
    public PartantNotFoundException(String name) {
        super("Partant not found with name: " + name);
    }

    /**
     * Constructs a new PartantNotFoundException with the specified detail message.
     *
     * @param message the detail message explaining why the partant was not found
     */
    public PartantNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
