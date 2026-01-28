package com.pmu2.exec.exception;

/**
 * Exception thrown when a course is not found.
 * This exception provides specific error handling for course-related operations.
 */
public class CourseNotFoundException extends RuntimeException {

    /**
     * Constructs a new CourseNotFoundException with the specified course ID.
     *
     * @param id the ID of the course that was not found
     */
    public CourseNotFoundException(Long id) {
        super("Course not found with id: " + id);
    }

    /**
     * Constructs a new CourseNotFoundException with the specified detail message.
     *
     * @param message the detail message explaining why the course was not found
     */
    public CourseNotFoundException(String message) {
        super(message);
    }
}
