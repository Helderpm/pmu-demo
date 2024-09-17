package com.pmu2.exec.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record CourseRecord(@NotNull @Positive Integer courseId,
                           @NotBlank(message = "Your Course needs a name.")
                           String name,
                           @NotNull @Positive(message = "positive number need.")
                           int number,
                           @NotNull(message = "Your Course needs a date.")
                           LocalDate date,
                           @NotNull List<PartantRecord>partants) {

}
