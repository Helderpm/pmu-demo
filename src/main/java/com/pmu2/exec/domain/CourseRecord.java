package com.pmu2.exec.domain;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a record of a course, including its details and participants.
 *
 * @param courseId The unique identifier for the course. Must be a positive integer.
 * @param name The name of the course. Cannot be null or empty, must be 2-255 characters.
 * @param number A specific number associated with the course. Must be between 1-999.
 * @param date The date when the course is scheduled. Cannot be null and must be in the future.
 * @param partants A list of participants in the course. Cannot be null, must have 3-20 partants.
 */
@Builder
public record CourseRecord(
        @NotNull 
        @Positive(message = "Course ID must be positive")
        Integer courseId,
        
        @NotBlank(message = "Course name cannot be null or empty")
        @Size(min = 2, max = 255, message = "Course name must be between 2 and 255 characters")
        String name,
        
        @NotNull
        @Min(value = 1, message = "Course number must be at least 1")
        @Max(value = 999, message = "Course number cannot exceed 999")
        int number,
        
        @NotNull(message = "Course date cannot be null")
        @Future(message = "Course date must be in the future")
        LocalDate date,
        
        @NotNull(message = "Course partants cannot be null")
        @Size(min = 3, max = 20, message = "Course must have between 3 and 20 partants")
        List<PartantRecord> partants) {
}

