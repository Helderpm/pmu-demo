package com.pmu2.exec.unit.domain;

import com.pmu2.exec.config.ValidationConfig;
import com.pmu2.exec.domain.CourseRecord;
import com.pmu2.exec.domain.PartantRecord;
import com.pmu2.exec.domain.service.CourseDomainService;
import com.pmu2.exec.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CourseDomainServiceTest {

    @Mock
    private ValidationConfig validationConfig;
    
    private CourseDomainService courseDomainService;

    @BeforeEach
    void setUp() {
        // Setup default validation rules for testing
        ValidationConfig.CourseValidation courseValidation = new ValidationConfig.CourseValidation();
        courseValidation.setMaxFutureMonths(6);
        courseValidation.setMinPartants(3);
        courseValidation.setMaxPartants(20);
        courseValidation.setMinBettingPartants(5);
        courseValidation.setMaxBettingPartants(15);
        courseValidation.setMinBettingHours(24);
        courseValidation.setMinModificationHours(48);
        
        validationConfig = new ValidationConfig();
        validationConfig.setCourse(courseValidation);
        
        courseDomainService = new CourseDomainService(validationConfig);
    }

    @Nested
    class CourseCreationValidation {

        @Test
        void shouldValidateValidCourse() {
            // Given
            List<PartantRecord> partants = List.of(
                new PartantRecord(1, "Horse 1", 1),
                new PartantRecord(2, "Horse 2", 2),
                new PartantRecord(3, "Horse 3", 3)
            );
            CourseRecord course = new CourseRecord(1, "Test Course", 100, LocalDate.now().plusDays(10), partants);

            // When & Then
            assertDoesNotThrow(() -> courseDomainService.validateCourseCreation(course));
        }

        @Test
        void shouldThrowExceptionWhenDateIsMoreThan6MonthsInFuture() {
            // Given
            List<PartantRecord> partants = List.of(
                new PartantRecord(1, "Horse 1", 1),
                new PartantRecord(2, "Horse 2", 2),
                new PartantRecord(3, "Horse 3", 3)
            );
            CourseRecord course = new CourseRecord(1, "Test Course", 100, LocalDate.now().plusMonths(7), partants);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                () -> courseDomainService.validateCourseCreation(course));
            assertTrue(exception.getMessage().contains("Course date cannot be more than 6 months in future"));
        }

        @Test
        void shouldThrowExceptionWhenLessThan3Partants() {
            // Given
            List<PartantRecord> partants = List.of(
                new PartantRecord(1, "Horse 1", 1),
                new PartantRecord(2, "Horse 2", 2)
            );
            CourseRecord course = new CourseRecord(1, "Test Course", 100, LocalDate.now().plusDays(10), partants);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                () -> courseDomainService.validateCourseCreation(course));
            assertTrue(exception.getMessage().contains("Course must have at least 3 partants"));
        }

        @Test
        void shouldThrowExceptionWhenMoreThan20Partants() {
            // Given
            List<PartantRecord> partants = java.util.stream.IntStream.range(1, 22)
                .mapToObj(i -> new PartantRecord(i, "Horse " + i, i))
                .toList();
            CourseRecord course = new CourseRecord(1, "Test Course", 100, LocalDate.now().plusDays(10), partants);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                () -> courseDomainService.validateCourseCreation(course));
            assertTrue(exception.getMessage().contains("Course cannot have more than 20 partants"));
        }

        @Test
        void shouldThrowExceptionWhenDuplicatePartantNumbers() {
            // Given
            List<PartantRecord> partants = List.of(
                new PartantRecord(1, "Horse 1", 1),
                new PartantRecord(2, "Horse 2", 2),
                new PartantRecord(3, "Horse 3", 2) // Duplicate number
            );
            CourseRecord course = new CourseRecord(1, "Test Course", 100, LocalDate.now().plusDays(10), partants);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                () -> courseDomainService.validateCourseCreation(course));
            assertTrue(exception.getMessage().contains("Duplicate partant number found"));
        }

        @Test
        void shouldThrowExceptionWhenPartantNumbersNotSequential() {
            // Given
            List<PartantRecord> partants = List.of(
                new PartantRecord(1, "Horse 1", 1),
                new PartantRecord(2, "Horse 2", 3), // Missing 2
                new PartantRecord(3, "Horse 3", 4)
            );
            CourseRecord course = new CourseRecord(1, "Test Course", 100, LocalDate.now().plusDays(10), partants);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                () -> courseDomainService.validateCourseCreation(course));
            assertTrue(exception.getMessage().contains("Partant numbers must be sequential starting from 1"));
        }
    }

    @Nested
    class BettingEligibility {

        @Test
        void shouldReturnTrueWhenCourseIsEligibleForBetting() {
            // Given
            List<PartantRecord> partants = List.of(
                new PartantRecord(1, "Horse 1", 1),
                new PartantRecord(2, "Horse 2", 2),
                new PartantRecord(3, "Horse 3", 3),
                new PartantRecord(4, "Horse 4", 4),
                new PartantRecord(5, "Horse 5", 5)
            );
            CourseRecord course = new CourseRecord(1, "Test Course", 100, LocalDate.now().plusDays(2), partants);

            // When
            boolean isEligible = courseDomainService.isEligibleForBetting(course);

            // Then
            assertTrue(isEligible);
        }

        @Test
        void shouldReturnFalseWhenCourseIsTooSoon() {
            // Given
            List<PartantRecord> partants = List.of(
                new PartantRecord(1, "Horse 1", 1),
                new PartantRecord(2, "Horse 2", 2),
                new PartantRecord(3, "Horse 3", 3),
                new PartantRecord(4, "Horse 4", 4),
                new PartantRecord(5, "Horse 5", 5)
            );
            CourseRecord course = new CourseRecord(1, "Test Course", 100, LocalDate.now(), partants);

            // When
            boolean isEligible = courseDomainService.isEligibleForBetting(course);

            // Then
            assertFalse(isEligible);
        }

        @Test
        void shouldReturnFalseWhenTooFewPartants() {
            // Given
            List<PartantRecord> partants = List.of(
                new PartantRecord(1, "Horse 1", 1),
                new PartantRecord(2, "Horse 2", 2),
                new PartantRecord(3, "Horse 3", 3),
                new PartantRecord(4, "Horse 4", 4)
            );
            CourseRecord course = new CourseRecord(1, "Test Course", 100, LocalDate.now().plusDays(2), partants);

            // When
            boolean isEligible = courseDomainService.isEligibleForBetting(course);

            // Then
            assertFalse(isEligible);
        }

        @Test
        void shouldReturnFalseWhenTooManyPartants() {
            // Given
            List<PartantRecord> partants = java.util.stream.IntStream.range(1, 17)
                .mapToObj(i -> new PartantRecord(i, "Horse " + i, i))
                .toList();
            CourseRecord course = new CourseRecord(1, "Test Course", 100, LocalDate.now().plusDays(2), partants);

            // When
            boolean isEligible = courseDomainService.isEligibleForBetting(course);

            // Then
            assertFalse(isEligible);
        }
    }

    @Nested
    class DifficultyCalculation {

        @Test
        void shouldCalculateDifficultyForNormalCourse() {
            // Given
            List<PartantRecord> partants = List.of(
                new PartantRecord(1, "Horse 1", 1),
                new PartantRecord(2, "Horse 2", 2),
                new PartantRecord(3, "Horse 3", 3)
            );
            CourseRecord course = new CourseRecord(1, "Test Course", 50, LocalDate.now().plusDays(10), partants);

            // When
            int difficulty = courseDomainService.calculateCourseDifficulty(course);

            // Then
            assertEquals(4, difficulty); // Base 5 - 1 for <5 partants
        }

        @Test
        void shouldCalculateHigherDifficultyForManyPartants() {
            // Given
            List<PartantRecord> partants = java.util.stream.IntStream.range(1, 12)
                .mapToObj(i -> new PartantRecord(i, "Horse " + i, i))
                .toList();
            CourseRecord course = new CourseRecord(1, "Test Course", 50, LocalDate.now().plusDays(10), partants);

            // When
            int difficulty = courseDomainService.calculateCourseDifficulty(course);

            // Then
            assertEquals(7, difficulty); // Base 5 + 2 for >10 partants
        }

        @Test
        void shouldCalculateHigherDifficultyForHighCourseNumber() {
            // Given
            List<PartantRecord> partants = List.of(
                new PartantRecord(1, "Horse 1", 1),
                new PartantRecord(2, "Horse 2", 2),
                new PartantRecord(3, "Horse 3", 3)
            );
            CourseRecord course = new CourseRecord(1, "Test Course", 150, LocalDate.now().plusDays(10), partants);

            // When
            int difficulty = courseDomainService.calculateCourseDifficulty(course);

            // Then
            assertEquals(5, difficulty); // Base 5 - 1 for <5 partants + 1 for >100 number
        }
    }
}
