package com.pmu2.exec.unit.domain;

import com.pmu2.exec.domain.CourseRecord;
import com.pmu2.exec.domain.PartantRecord;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CourseRecordTest {

    private Validator validator;
    private List<PartantRecord> validPartants;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        validPartants = List.of(
            new PartantRecord(1, "Horse 1", 1),
            new PartantRecord(2, "Horse 2", 2),
            new PartantRecord(3, "Horse 3", 3)
        );
    }

    @Test
    void shouldCreateValidCourseRecord() {
        // Given
        Integer courseId = 100;
        String name = "Test Course";
        int number = 5;
        LocalDate date = LocalDate.now().plusDays(10);
        List<PartantRecord> partants = validPartants;

        // When
        CourseRecord courseRecord = new CourseRecord(courseId, name, number, date, partants);

        // Then
        assertEquals(courseId, courseRecord.courseId());
        assertEquals(name, courseRecord.name());
        assertEquals(number, courseRecord.number());
        assertEquals(date, courseRecord.date());
        assertEquals(partants, courseRecord.partants());
    }

    @Test
    void shouldValidateCourseRecordWithAllValidFields() {
        // Given
        CourseRecord courseRecord = new CourseRecord(100, "Valid Course Name", 5, 
            LocalDate.now().plusDays(1), validPartants);

        // When
        Set<ConstraintViolation<CourseRecord>> violations = validator.validate(courseRecord);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailValidationWhenCourseIdIsNull() {
        // Given
        CourseRecord courseRecord = new CourseRecord(null, "Valid Course Name", 5, 
            LocalDate.now().plusDays(1), validPartants);

        // When
        Set<ConstraintViolation<CourseRecord>> violations = validator.validate(courseRecord);

        // Then
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("ne doit pas être nul"));
    }

    @Test
    void shouldFailValidationWhenCourseIdIsNegative() {
        // Given
        CourseRecord courseRecord = new CourseRecord(-1, "Valid Course Name", 5, 
            LocalDate.now().plusDays(1), validPartants);

        // When
        Set<ConstraintViolation<CourseRecord>> violations = validator.validate(courseRecord);

        // Then
        assertEquals(1, violations.size());
        assertEquals("Course ID must be positive", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidationWhenCourseIdIsZero() {
        // Given
        CourseRecord courseRecord = new CourseRecord(0, "Valid Course Name", 5, 
            LocalDate.now().plusDays(1), validPartants);

        // When
        Set<ConstraintViolation<CourseRecord>> violations = validator.validate(courseRecord);

        // Then
        assertEquals(1, violations.size());
        assertEquals("Course ID must be positive", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidationWhenCourseNameIsNull() {
        // Given
        CourseRecord courseRecord = new CourseRecord(100, null, 5, 
            LocalDate.now().plusDays(1), validPartants);

        // When
        Set<ConstraintViolation<CourseRecord>> violations = validator.validate(courseRecord);

        // Then
        assertEquals(1, violations.size());
        assertEquals("Course name cannot be null or empty", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidationWhenCourseNameIsEmpty() {
        // Given
        CourseRecord courseRecord = new CourseRecord(100, "", 5, 
            LocalDate.now().plusDays(1), validPartants);

        // When
        Set<ConstraintViolation<CourseRecord>> violations = validator.validate(courseRecord);

        // Then
        assertTrue(violations.size() >= 1);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("ne doit pas être vide") || 
                                                     v.getMessage().contains("Course name cannot be null or empty")));
    }

    @Test
    void shouldFailValidationWhenCourseNameIsTooShort() {
        // Given
        CourseRecord courseRecord = new CourseRecord(100, "A", 5, 
            LocalDate.now().plusDays(1), validPartants);

        // When
        Set<ConstraintViolation<CourseRecord>> violations = validator.validate(courseRecord);

        // Then
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("between 2 and 255 characters"));
    }

    @Test
    void shouldFailValidationWhenCourseNameIsTooLong() {
        // Given
        String longName = "A".repeat(256);
        CourseRecord courseRecord = new CourseRecord(100, longName, 5, 
            LocalDate.now().plusDays(1), validPartants);

        // When
        Set<ConstraintViolation<CourseRecord>> violations = validator.validate(courseRecord);

        // Then
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("between 2 and 255 characters"));
    }

    @Test
    void shouldFailValidationWhenCourseNumberIsBelowMinimum() {
        // Given
        CourseRecord courseRecord = new CourseRecord(100, "Valid Course Name", 0, 
            LocalDate.now().plusDays(1), validPartants);

        // When
        Set<ConstraintViolation<CourseRecord>> violations = validator.validate(courseRecord);

        // Then
        assertEquals(1, violations.size());
        assertEquals("Course number must be at least 1", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidationWhenCourseNumberIsAboveMaximum() {
        // Given
        CourseRecord courseRecord = new CourseRecord(100, "Valid Course Name", 1000, 
            LocalDate.now().plusDays(1), validPartants);

        // When
        Set<ConstraintViolation<CourseRecord>> violations = validator.validate(courseRecord);

        // Then
        assertEquals(1, violations.size());
        assertEquals("Course number cannot exceed 999", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidationWhenCourseDateIsNull() {
        // Given
        CourseRecord courseRecord = new CourseRecord(100, "Valid Course Name", 5, 
            null, validPartants);

        // When
        Set<ConstraintViolation<CourseRecord>> violations = validator.validate(courseRecord);

        // Then
        assertEquals(1, violations.size());
        assertEquals("Course date cannot be null", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidationWhenCourseDateIsInPast() {
        // Given
        CourseRecord courseRecord = new CourseRecord(100, "Valid Course Name", 5, 
            LocalDate.now().minusDays(1), validPartants);

        // When
        Set<ConstraintViolation<CourseRecord>> violations = validator.validate(courseRecord);

        // Then
        assertEquals(1, violations.size());
        assertEquals("Course date must be in the future", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidationWhenCourseDateIsToday() {
        // Given
        CourseRecord courseRecord = new CourseRecord(100, "Valid Course Name", 5, 
            LocalDate.now(), validPartants);

        // When
        Set<ConstraintViolation<CourseRecord>> violations = validator.validate(courseRecord);

        // Then
        assertEquals(1, violations.size());
        assertEquals("Course date must be in the future", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidationWhenPartantsIsNull() {
        // Given
        CourseRecord courseRecord = new CourseRecord(100, "Valid Course Name", 5, 
            LocalDate.now().plusDays(1), null);

        // When
        Set<ConstraintViolation<CourseRecord>> violations = validator.validate(courseRecord);

        // Then
        assertEquals(1, violations.size());
        assertEquals("Course partants cannot be null", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidationWhenPartantsListIsEmpty() {
        // Given
        CourseRecord courseRecord = new CourseRecord(100, "Valid Course Name", 5, 
            LocalDate.now().plusDays(1), List.of());

        // When
        Set<ConstraintViolation<CourseRecord>> violations = validator.validate(courseRecord);

        // Then
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("between 3 and 20 partants"));
    }

    @Test
    void shouldFailValidationWhenPartantsListHasTooFewPartants() {
        // Given
        List<PartantRecord> tooFewPartants = List.of(
            new PartantRecord(1, "Horse 1", 1),
            new PartantRecord(2, "Horse 2", 2)
        );
        CourseRecord courseRecord = new CourseRecord(100, "Valid Course Name", 5, 
            LocalDate.now().plusDays(1), tooFewPartants);

        // When
        Set<ConstraintViolation<CourseRecord>> violations = validator.validate(courseRecord);

        // Then
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("between 3 and 20 partants"));
    }

    @Test
    void shouldFailValidationWhenPartantsListHasTooManyPartants() {
        // Given
        List<PartantRecord> tooManyPartants = List.of(
            new PartantRecord(1, "Horse 1", 1),
            new PartantRecord(2, "Horse 2", 2),
            new PartantRecord(3, "Horse 3", 3),
            new PartantRecord(4, "Horse 4", 4),
            new PartantRecord(5, "Horse 5", 5),
            new PartantRecord(6, "Horse 6", 6),
            new PartantRecord(7, "Horse 7", 7),
            new PartantRecord(8, "Horse 8", 8),
            new PartantRecord(9, "Horse 9", 9),
            new PartantRecord(10, "Horse 10", 10),
            new PartantRecord(11, "Horse 11", 11),
            new PartantRecord(12, "Horse 12", 12),
            new PartantRecord(13, "Horse 13", 13),
            new PartantRecord(14, "Horse 14", 14),
            new PartantRecord(15, "Horse 15", 15),
            new PartantRecord(16, "Horse 16", 16),
            new PartantRecord(17, "Horse 17", 17),
            new PartantRecord(18, "Horse 18", 18),
            new PartantRecord(19, "Horse 19", 19),
            new PartantRecord(20, "Horse 20", 20),
            new PartantRecord(21, "Horse 21", 21)
        );
        CourseRecord courseRecord = new CourseRecord(100, "Valid Course Name", 5, 
            LocalDate.now().plusDays(1), tooManyPartants);

        // When
        Set<ConstraintViolation<CourseRecord>> violations = validator.validate(courseRecord);

        // Then
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("between 3 and 20 partants"));
    }

    @Test
    void shouldHaveValidToString() {
        // Given
        CourseRecord courseRecord = new CourseRecord(100, "Test Course", 5, 
            LocalDate.now().plusDays(1), validPartants);

        // When
        String toString = courseRecord.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("CourseRecord"));
        assertTrue(toString.contains("courseId=100"));
        assertTrue(toString.contains("name=Test Course"));
        assertTrue(toString.contains("number=5"));
    }

    @Test
    void shouldTestEqualsAndHashCode() {
        // Given
        CourseRecord course1 = new CourseRecord(100, "Test Course", 5, 
            LocalDate.now().plusDays(1), validPartants);
        CourseRecord course2 = new CourseRecord(100, "Test Course", 5, 
            LocalDate.now().plusDays(1), validPartants);
        CourseRecord course3 = new CourseRecord(101, "Test Course", 5, 
            LocalDate.now().plusDays(1), validPartants);

        // Then
        assertEquals(course1, course2);
        assertEquals(course1.hashCode(), course2.hashCode());
        assertNotEquals(course1, course3);
        assertNotEquals(course1.hashCode(), course3.hashCode());
    }

    @Test
    void shouldTestEqualsWithSameObject() {
        // Given
        CourseRecord courseRecord = new CourseRecord(100, "Test Course", 5, 
            LocalDate.now().plusDays(1), validPartants);

        // Then
        assertEquals(courseRecord, courseRecord);
    }

    @Test
    void shouldTestEqualsWithNull() {
        // Given
        CourseRecord courseRecord = new CourseRecord(100, "Test Course", 5, 
            LocalDate.now().plusDays(1), validPartants);

        // Then
        assertNotEquals(null, courseRecord);
    }

    @Test
    void shouldTestEqualsWithDifferentClass() {
        // Given
        CourseRecord courseRecord = new CourseRecord(100, "Test Course", 5, 
            LocalDate.now().plusDays(1), validPartants);

        // Then
        assertNotEquals("string", courseRecord);
    }
}
