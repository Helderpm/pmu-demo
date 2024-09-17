package com.pmu2.exec.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a record of a course, including its details and participants.
 *
 * @param courseId The unique identifier for the course. Must be a positive integer.
 * @param name The name of the course. Cannot be null or empty.
 * @param number A specific number associated with the course. Must be a positive integer.
 * @param date The date when the course is scheduled. Cannot be null.
 * @param partants A list of participants in the course. Cannot be null.
 */
@Builder
public record CourseRecord(@NotNull @Positive Integer courseId,
                           @NotBlank(message = "Your Course needs a name.")
                           String name,
                           @NotNull @Positive(message = "positive number need.")
                           int number,
                           @NotNull(message = "Your Course needs a date.")
                           LocalDate date,
                           @NotNull List<PartantRecord> partants) {
}
