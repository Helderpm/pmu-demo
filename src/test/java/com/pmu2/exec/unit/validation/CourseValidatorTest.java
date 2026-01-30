package com.pmu2.exec.unit.validation;

import com.pmu2.exec.domain.CourseRecord;
import com.pmu2.exec.domain.PartantRecord;
import com.pmu2.exec.config.ValidationConfig;
import com.pmu2.exec.exception.NotFoundException;
import com.pmu2.exec.exception.SimpleValidationException;
import com.pmu2.exec.infrastructure.db.sql.CourseJpaRepository;
import com.pmu2.exec.validation.CourseValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CourseValidatorTest {

    @Mock
    private CourseJpaRepository courseJpaRepository;
    
    @Mock
    private ValidationConfig validationConfig;

    private CourseValidator courseValidator;
    
    @BeforeEach
    void setUp() {
        // Mock ValidationConfig to avoid NullPointerException
        ValidationConfig.CourseValidation courseValidation = mock(ValidationConfig.CourseValidation.class);
        when(validationConfig.getCourse()).thenReturn(courseValidation);
        when(courseValidation.getMaxFutureMonths()).thenReturn(6);
        
        courseValidator = new CourseValidator(courseJpaRepository, validationConfig);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 100L, 999L})
    void shouldValidateExistingCourseById(Long courseId) {
        // Given
        when(courseJpaRepository.existsById(courseId)).thenReturn(true);

        // When & Then
        assertDoesNotThrow(() -> courseValidator.validateCourseExistsById(courseId));
        verify(courseJpaRepository).existsById(courseId);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 100L, 999L})
    void shouldThrowExceptionWhenCourseNotFoundById(Long courseId) {
        // Given
        when(courseJpaRepository.existsById(courseId)).thenReturn(false);

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> courseValidator.validateCourseExistsById(courseId));
        assertEquals(String.valueOf(courseId), exception.getCourseId());
        verify(courseJpaRepository).existsById(courseId);
    }

    @Test
    void shouldThrowExceptionWhenCourseIdIsNull() {
        // Given
        Long courseId = null;

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> courseValidator.validateCourseExistsById(courseId));
        assertEquals("courseId cannot be null", exception.getMessage());
        verifyNoInteractions(courseJpaRepository);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidCourseRecords")
    void shouldThrowExceptionForInvalidCourseRecords(String description, CourseRecord course, String expectedMessage) {
        // When & Then
        SimpleValidationException exception = assertThrows(SimpleValidationException.class,
            () -> courseValidator.validateCourseRecord(course));
        assertEquals(expectedMessage, exception.getMessage());
    }

    private static Stream<Arguments> provideInvalidCourseRecords() {
        List<PartantRecord> partants = List.of(new PartantRecord(1, "Horse 1", 1));
        
        return Stream.of(
            Arguments.of("null course record", null, "Course record cannot be null"),
            Arguments.of("null course name", 
                new CourseRecord(1, null, 100, LocalDate.now().plusDays(10), partants), 
                "Course name cannot be null or empty"),
            Arguments.of("empty course name", 
                new CourseRecord(1, "   ", 100, LocalDate.now().plusDays(10), partants), 
                "Course name cannot be null or empty"),
            Arguments.of("null course date", 
                new CourseRecord(1, "Test Course", 100, null, partants), 
                "Course date cannot be null")
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"Test Course", "Course Name", "A", "Valid Course Name 123"})
    void shouldValidateValidCourseName(String courseName) {
        // When & Then
        assertDoesNotThrow(() -> courseValidator.validateCourseName(courseName));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void shouldThrowExceptionWhenCourseNameIsNullOrEmpty(String courseName) {
        // When & Then
        SimpleValidationException exception = assertThrows(SimpleValidationException.class,
            () -> courseValidator.validateCourseName(courseName));
        assertEquals("Course name cannot be null or empty", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {256, 300, 1000})
    void shouldThrowExceptionWhenCourseNameIsTooLong(int length) {
        // Given
        String courseName = "A".repeat(length);

        // When & Then
        SimpleValidationException exception = assertThrows(SimpleValidationException.class,
            () -> courseValidator.validateCourseName(courseName));
        assertEquals("Course name cannot exceed 255 characters", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 30, 365})
    void shouldValidateValidCourseDate(int daysInFuture) {
        // Given
        LocalDate courseDate = LocalDate.now().plusDays(daysInFuture);

        // When & Then
        assertDoesNotThrow(() -> courseValidator.validateCourseDate(courseDate));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidCourseDates")
    void shouldThrowExceptionForInvalidCourseDates(String description, LocalDate courseDate, String expectedMessage) {
        // When & Then
        SimpleValidationException exception = assertThrows(SimpleValidationException.class,
            () -> courseValidator.validateCourseDate(courseDate));
        assertEquals(expectedMessage, exception.getMessage());
    }

    private static Stream<Arguments> provideInvalidCourseDates() {
        return Stream.of(
            Arguments.of("null date", null, "Course date cannot be null"),
            Arguments.of("past date", LocalDate.now().minusDays(1), "Course date cannot be in the past"),
            Arguments.of("more than one year in future", LocalDate.now().plusYears(1).plusDays(1), "Course date cannot be more than 1 year in the future")
        );
    }

    @Test
    void shouldValidateValidCourseRecord() {
        // Given
        List<PartantRecord> partants = List.of(
            new PartantRecord(1, "Horse 1", 1),
            new PartantRecord(2, "Horse 2", 2)
        );
        CourseRecord course = new CourseRecord(1, "Test Course", 100, LocalDate.now().plusDays(10), partants);

        // When & Then
        assertDoesNotThrow(() -> courseValidator.validateCourseRecord(course));
    }

    @Test
    void shouldValidateCourseIntegrity() {
        // Given
        List<PartantRecord> partants = List.of(new PartantRecord(1, "Horse 1", 1));
        CourseRecord course = new CourseRecord(1, "Test Course", 100, LocalDate.now().plusMonths(3), partants);

        // When & Then
        assertDoesNotThrow(() -> courseValidator.validateCourseIntegrity(course));
    }

    @Test
    void shouldThrowExceptionWhenCourseIntegrityIsNull() {
        // Given
        CourseRecord course = null;

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> courseValidator.validateCourseIntegrity(course));
        assertEquals("course cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCourseDateIsTooFarInFuture() {
        // Given
        List<PartantRecord> partants = List.of(new PartantRecord(1, "Horse 1", 1));
        CourseRecord course = new CourseRecord(1, "Test Course", 100, LocalDate.now().plusMonths(7), partants);

        // When & Then
        SimpleValidationException exception = assertThrows(SimpleValidationException.class,
            () -> courseValidator.validateCourseIntegrity(course));
        assertEquals("Course date cannot be more than 6 months in future", exception.getMessage());
    }

    @Test
    void shouldValidateCourseNameIntegrity() {
        // Given
        String courseName = "Valid Course Name";

        // When & Then
        assertDoesNotThrow(() -> courseValidator.validateCourseNameIntegrity(courseName));
    }

    @Test
    void shouldThrowExceptionWhenCourseNameIntegrityIsNull() {
        // Given
        String courseName = null;

        // When & Then
        SimpleValidationException exception = assertThrows(SimpleValidationException.class,
            () -> courseValidator.validateCourseNameIntegrity(courseName));
        assertEquals("courseName cannot be empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCourseNameIntegrityIsEmpty() {
        // Given
        String courseName = "   ";

        // When & Then
        SimpleValidationException exception = assertThrows(SimpleValidationException.class,
            () -> courseValidator.validateCourseNameIntegrity(courseName));
        assertEquals("courseName cannot be empty", exception.getMessage());
    }

    @Test
    void shouldValidateUniqueCourseName() {
        // Given
        String courseName = "Unique Course";
        when(courseJpaRepository.findByName(courseName)).thenReturn(List.of());

        // When & Then
        assertDoesNotThrow(() -> courseValidator.validateCourseNameUnique(courseName));
        verify(courseJpaRepository).findByName(courseName);
    }

    @Test
    void shouldThrowExceptionWhenCourseNameIsNotUnique() {
        // Given
        String courseName = "Existing Course";
        com.pmu2.exec.infrastructure.db.sql.CourseEntity existingCourse = new com.pmu2.exec.infrastructure.db.sql.CourseEntity();
        existingCourse.setName("Existing Course");
        when(courseJpaRepository.findByName(courseName)).thenReturn(List.of(existingCourse));

        // When & Then
        SimpleValidationException exception = assertThrows(SimpleValidationException.class,
            () -> courseValidator.validateCourseNameUnique(courseName));
        assertEquals("Course with name '" + courseName + "' already exists", exception.getMessage());
        verify(courseJpaRepository).findByName(courseName);
    }

    @Test
    void shouldThrowExceptionWhenUniqueCourseNameIsNull() {
        // Given
        String courseName = null;

        // When & Then
        SimpleValidationException exception = assertThrows(SimpleValidationException.class,
            () -> courseValidator.validateCourseNameUnique(courseName));
        assertEquals("courseName cannot be empty", exception.getMessage());
        verifyNoInteractions(courseJpaRepository);
    }

    @Test
    void shouldThrowExceptionWhenUniqueCourseNameIsEmpty() {
        // Given
        String courseName = "   ";

        // When & Then
        SimpleValidationException exception = assertThrows(SimpleValidationException.class,
            () -> courseValidator.validateCourseNameUnique(courseName));
        assertEquals("courseName cannot be empty", exception.getMessage());
        verifyNoInteractions(courseJpaRepository);
    }
}
