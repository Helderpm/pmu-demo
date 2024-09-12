package com.pmu2.exec.domain;

import java.time.LocalDate;
import java.util.List;

public record CourseRecord(Integer courseId,
                           String name,
                           int number,
                           LocalDate date,
                           List<PartantRecord>partants) {
}
