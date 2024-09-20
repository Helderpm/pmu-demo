package com.pmu2.exec.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Represents a record of a participant in a race or event.
 *
 * @param id The unique identifier of the participant. Cannot be null.
 * @param name The name of the participant. Cannot be null or empty.
 * @param number The unique number assigned to the participant. Must be a positive integer.
 */
public record PartantRecord(
        @NotNull
        Integer id,
        @NotBlank(message = "Your Course needs a name.")
        String name,
        @NotNull @Positive(message = "positive number need.")
        int number){
}
