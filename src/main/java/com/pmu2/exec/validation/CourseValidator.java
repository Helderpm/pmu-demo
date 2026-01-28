package com.pmu2.exec.validation;

import com.pmu2.exec.domain.CourseRecord;
import com.pmu2.exec.exception.CourseNotFoundException;
import com.pmu2.exec.infrastrure.db.sql.CourseJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Component responsible for validating course-related business rules.
 * This component separates validation logic from service layer, following Single Responsibility Principle.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CourseValidator {

    private final CourseJpaRepository courseJpaRepository;

    /**
     * Validates that a course with the given ID exists in the database.
     *
     * @param courseId the ID of the course to validate
     * @throws CourseNotFoundException if the course is not found
     */
    public void validateCourseExistsById(Long courseId) {
        if (courseId == null) {
            throw new IllegalArgumentException("Course ID cannot be null");
        }

        if (!courseJpaRepository.existsById(courseId)) {
            log.warn("Course not found with ID: {}", courseId);
            throw new CourseNotFoundException(courseId);
        }
        
        log.debug("Course found with ID: {}", courseId);
    }

    /**
     * Validates course record data integrity.
     *
     * @param course the course record to validate
     * @throws IllegalArgumentException if validation fails
     */
    public void validateCourseRecord(CourseRecord course) {
        if (course == null) {
            throw new IllegalArgumentException("Course record cannot be null");
        }

        if (course.name() == null || course.name().trim().isEmpty()) {
            throw new IllegalArgumentException("Course name cannot be null or empty");
        }

        if (course.number() <= 0) {
            throw new IllegalArgumentException("Course number must be positive");
        }

        if (course.date() == null) {
            throw new IllegalArgumentException("Course date cannot be null");
        }

        if (course.date().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Course date cannot be in the past");
        }

        if (course.partants() == null || course.partants().isEmpty()) {
            throw new IllegalArgumentException("Course must have at least one partant");
        }

        log.debug("Course record validation passed for: {}", course.name());
    }

    /**
     * Validates course name format and constraints.
     *
     * @param courseName the course name to validate
     * @throws IllegalArgumentException if validation fails
     */
    public void validateCourseName(String courseName) {
        if (courseName == null || courseName.trim().isEmpty()) {
            throw new IllegalArgumentException("Course name cannot be null or empty");
        }

        if (courseName.length() > 255) {
            throw new IllegalArgumentException("Course name cannot exceed 255 characters");
        }

        log.debug("Course name validation passed for: {}", courseName);
    }

    /**
     * Validates that course date is not too far in the future.
     *
     * @param courseDate the course date to validate
     * @throws IllegalArgumentException if validation fails
     */
    public void validateCourseDate(LocalDate courseDate) {
        if (courseDate == null) {
            throw new IllegalArgumentException("Course date cannot be null");
        }

        if (courseDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Course date cannot be in the past");
        }

        // Allow courses up to 1 year in the future
        LocalDate maxFutureDate = LocalDate.now().plusYears(1);
        if (courseDate.isAfter(maxFutureDate)) {
            throw new IllegalArgumentException("Course date cannot be more than 1 year in the future");
        }

        log.debug("Course date validation passed for: {}", courseDate);
    }
}
