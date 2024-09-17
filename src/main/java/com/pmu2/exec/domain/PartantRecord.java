package com.pmu2.exec.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PartantRecord(
        @NotNull
        Integer id,
        @NotBlank(message = "Your Course needs a name.")
        String name,
        @NotNull @Positive(message = "positive number need.")
        int number){
}
