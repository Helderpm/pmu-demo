package com.pmu2.exec.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * Record representing a Partant (participant/horse) in the domain layer.
 * This is an immutable domain object with validation annotations.
 *
 * @param id     the unique identifier for the partant
 * @param name   the name of the partant
 * @param number the racing number of the partant
 */
public record PartantRecord(
    Integer id,
    
    @NotBlank(message = "Partant name cannot be blank")
    @Size(min = 2, max = 50, message = "Partant name must be between 2 and 50 characters")
    String name,
    
    @Min(value = 1, message = "Partant number must be at least 1")
    @Max(value = 99, message = "Partant number must be at most 99")
    int number
) {
}

