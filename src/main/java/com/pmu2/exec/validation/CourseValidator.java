package com.pmu2.exec.validation;

import com.pmu2.exec.config.ValidationConfig;
import com.pmu2.exec.domain.CourseRecord;
import com.pmu2.exec.exception.NotFoundException;
import com.pmu2.exec.exception.SimpleValidationException;
import com.pmu2.exec.infrastructure.db.sql.CourseJpaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Component responsible for validating course-related existence and integrity rules.
 */
@Component
@RequiredArgsConstructor
public class CourseValidator {

    private static final Logger log = LoggerFactory.getLogger(CourseValidator.class);

    private final CourseJpaRepository courseJpaRepository;
    private final ValidationConfig validationConfig;

    public void validateCourseExistsById(Long courseId) {
        if (courseId == null) {
            throw new NotFoundException("courseId cannot be null");
        }

        if (!courseJpaRepository.existsById(courseId)) {
            throw new NotFoundException("Course", courseId);
        }
    }

    public void validateCourseIntegrity(CourseRecord course) {
        if (course == null) {
            throw new NotFoundException("course cannot be null");
        }

        // Validate date is not too far in the future (business rule)
        LocalDate maxFutureDate = LocalDate.now().plusMonths(validationConfig.getCourse().getMaxFutureMonths());
        if (course.date().isAfter(maxFutureDate)) {
            throw new SimpleValidationException(
                "Course date cannot be more than " + validationConfig.getCourse().getMaxFutureMonths() + " months in future"
            );
        }

        log.debug("Course integrity validation passed for: {}", course.name());
    }

    public void validateCourseNameIntegrity(String courseName) {
        if (courseName == null || courseName.trim().isEmpty()) {
            throw new SimpleValidationException("courseName cannot be empty");
        }

        log.debug("Course name integrity validation passed for: {}", courseName);
    }

    public void validateCourseName(String courseName) {
        if (courseName == null || courseName.trim().isEmpty()) {
            throw new SimpleValidationException("Course name cannot be null or empty");
        }
        if (courseName.length() > 255) {
            throw new SimpleValidationException("Course name cannot exceed 255 characters");
        }

        log.debug("Course name validation passed for: {}", courseName);
    }

    public void validateCourseDate(LocalDate courseDate) {
        if (courseDate == null) {
            throw new SimpleValidationException("Course date cannot be null");
        }
        if (courseDate.isBefore(LocalDate.now())) {
            throw new SimpleValidationException("Course date cannot be in the past");
        }
        if (courseDate.isAfter(LocalDate.now().plusYears(1))) {
            throw new SimpleValidationException("Course date cannot be more than 1 year in the future");
        }
    }

    /**
     * Validates a complete course record including all its components.
     *
     * @param course the course record to validate
     * @throws SimpleValidationException if validation fails
     */
    public void validateCourseRecord(CourseRecord course) {
        if (course == null) {
            throw new SimpleValidationException("Course record cannot be null");
        }
        
        validateCourseName(course.name());
        validateCourseDate(course.date());
        validateCourseIntegrity(course);
    }

    public void validateCourseNameUnique(String courseName) {
        if (courseName == null || courseName.trim().isEmpty()) {
            throw new SimpleValidationException("courseName cannot be empty");
        }

        // Check if course name already exists (case-insensitive)
        boolean exists = courseJpaRepository.findByName(courseName).stream()
            .anyMatch(course -> course.getName().equalsIgnoreCase(courseName.trim()));
            
        if (exists) {
            throw new SimpleValidationException("Course with name '" + courseName + "' already exists");
        }

        log.debug("Course name uniqueness validation passed for: {}", courseName);
    }
}
