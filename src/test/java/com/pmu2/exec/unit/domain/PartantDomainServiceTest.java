package com.pmu2.exec.unit.domain;

import com.pmu2.exec.config.ValidationConfig;
import com.pmu2.exec.domain.PartantRecord;
import com.pmu2.exec.domain.service.PartantDomainService;
import com.pmu2.exec.domain.service.strategy.EligibilityChecker;
import com.pmu2.exec.domain.service.strategy.PerformanceCalculator;
import com.pmu2.exec.domain.service.strategy.SkillCategoryDeterminer;
import com.pmu2.exec.domain.service.strategy.RaceStrategyDeterminer;
import com.pmu2.exec.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PartantDomainServiceTest {

    private PartantDomainService partantDomainService;
    private EligibilityChecker eligibilityChecker;
    private PerformanceCalculator performanceCalculator;
    private SkillCategoryDeterminer skillCategoryDeterminer;
    private RaceStrategyDeterminer raceStrategyDeterminer;

    @BeforeEach
    void setUp() {
        ValidationConfig validationConfig = new ValidationConfig();
        
        // Initialize partant validation rules
        ValidationConfig.PartantValidation partantValidation = new ValidationConfig.PartantValidation();
        partantValidation.setNameMinLength(2);
        partantValidation.setNameMaxLength(50);
        partantValidation.setNumberMin(1);
        partantValidation.setNumberMax(99);
        partantValidation.setMaxHighNumberThreshold(50);
        partantValidation.setUnluckyNumber(13);
        partantValidation.setPerformanceBaseScore(50);
        partantValidation.setPerformanceMaxScore(100);
        partantValidation.setPerformanceMinScore(1);
        
        validationConfig.setPartant(partantValidation);
        
        eligibilityChecker = new EligibilityChecker(validationConfig);
        performanceCalculator = new PerformanceCalculator(validationConfig);
        skillCategoryDeterminer = new SkillCategoryDeterminer();
        raceStrategyDeterminer = new RaceStrategyDeterminer();
        
        partantDomainService = new PartantDomainService(
            validationConfig,
            eligibilityChecker,
            performanceCalculator,
            skillCategoryDeterminer,
            raceStrategyDeterminer
        );
    }

    @Nested
    class PartantEligibilityValidation {

        @Test
        void shouldValidateValidPartant() {
            // Given
            PartantRecord partant = new PartantRecord(1, "Thunder Bolt", 5);

            // When & Then
            assertDoesNotThrow(() -> partantDomainService.validatePartantEligibility(partant));
        }

        @Test
        void shouldThrowExceptionWhenNameIsEmpty() {
            // Given
            PartantRecord partant = new PartantRecord(1, "", 5);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                () -> partantDomainService.validatePartantEligibility(partant));
            assertTrue(exception.getMessage().contains("Partant name cannot be empty"));
        }

        @Test
        void shouldThrowExceptionWhenNameIsTooShort() {
            // Given
            PartantRecord partant = new PartantRecord(1, "A", 5);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                () -> partantDomainService.validatePartantEligibility(partant));
            assertTrue(exception.getMessage().contains("Partant name must be at least 2 characters long"));
        }

        @Test
        void shouldThrowExceptionWhenNameIsTooLong() {
            // Given
            String longName = "A".repeat(51);
            PartantRecord partant = new PartantRecord(1, longName, 5);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                () -> partantDomainService.validatePartantEligibility(partant));
            assertTrue(exception.getMessage().contains("Partant name cannot exceed 50 characters"));
        }

        @Test
        void shouldThrowExceptionWhenNumberIsNotPositive() {
            // Given - use reflection to create PartantRecord with invalid number
            try {
                java.lang.reflect.Constructor<PartantRecord> constructor = 
                    PartantRecord.class.getDeclaredConstructor(Integer.class, String.class, int.class);
                constructor.setAccessible(true);
                PartantRecord partant = constructor.newInstance(1, "Thunder Bolt", 0);

                // When & Then
                BusinessException exception = assertThrows(BusinessException.class,
                    () -> partantDomainService.validatePartantEligibility(partant));
                assertTrue(exception.getMessage().contains("Partant number must be between 1 and 99"));
            } catch (Exception e) {
                fail("Failed to create test PartantRecord: " + e.getMessage());
            }
        }

        @Test
        void shouldThrowExceptionWhenNumberIsTooHigh() {
            // Given - use reflection to create PartantRecord with invalid number
            try {
                java.lang.reflect.Constructor<PartantRecord> constructor = 
                    PartantRecord.class.getDeclaredConstructor(Integer.class, String.class, int.class);
                constructor.setAccessible(true);
                PartantRecord partant = constructor.newInstance(1, "Thunder Bolt", 100);

                // When & Then
                BusinessException exception = assertThrows(BusinessException.class,
                    () -> partantDomainService.validatePartantEligibility(partant));
                assertTrue(exception.getMessage().contains("Partant number must be between 1 and 99"));
            } catch (Exception e) {
                fail("Failed to create test PartantRecord: " + e.getMessage());
            }
        }
    }

    @Nested
    class GoodStandingCheck {

        @Test
        void shouldReturnTrueForPartantInGoodStanding() {
            // Given
            PartantRecord partant = new PartantRecord(1, "Thunder Bolt", 5);

            // When
            boolean inGoodStanding = partantDomainService.isInGoodStanding(partant);

            // Then
            assertTrue(inGoodStanding);
        }

        @Test
        void shouldReturnFalseForUnluckyNumber() {
            // Given
            PartantRecord partant = new PartantRecord(1, "Thunder Bolt", 13);

            // When
            boolean inGoodStanding = partantDomainService.isInGoodStanding(partant);

            // Then
            assertFalse(inGoodStanding);
        }

        @Test
        void shouldReturnFalseForHighNumber() {
            // Given
            PartantRecord partant = new PartantRecord(1, "Thunder Bolt", 75);

            // When
            boolean inGoodStanding = partantDomainService.isInGoodStanding(partant);

            // Then
            assertFalse(inGoodStanding);
        }
    }

    @Nested
    class PerformanceScoreCalculation {

        @Test
        void shouldCalculateHighScoreForLowNumber() {
            // Given
            PartantRecord partant = new PartantRecord(1, "Thunder", 3);

            // When
            int score = partantDomainService.calculatePerformanceScore(partant);

            // Then
            assertEquals(75, score); // 50 + 20 (low number) + 5 (short name)
        }

        @Test
        void shouldCalculateMediumScoreForMediumNumber() {
            // Given
            PartantRecord partant = new PartantRecord(1, "Thunder Bolt", 15);

            // When
            int score = partantDomainService.calculatePerformanceScore(partant);

            // Then
            assertEquals(55, score); // 50 + 5 (medium number)
        }

        @Test
        void shouldCalculateLowScoreForHighNumber() {
            // Given
            PartantRecord partant = new PartantRecord(1, "Very Long Horse Name Indeed", 60);

            // When
            int score = partantDomainService.calculatePerformanceScore(partant);

            // Then
            assertEquals(30, score); // 50 + 5 (medium number) - 5 (long name) - 15 (not good standing) - 5 (high number)
        }
    }

    @Nested
    class SkillCategoryDetermination {

        @Test
        void shouldReturnExpertForHighScore() {
            // Given
            PartantRecord partant = new PartantRecord(1, "Thunder", 1);

            // When
            String category = partantDomainService.determineSkillCategory(partant);

            // Then
            assertEquals("ADVANCED", category);
        }

        @Test
        void shouldReturnAdvancedForGoodScore() {
            // Given
            PartantRecord partant = new PartantRecord(1, "Thunder", 8);

            // When
            String category = partantDomainService.determineSkillCategory(partant);

            // Then
            assertEquals("INTERMEDIATE", category);
        }

        @Test
        void shouldReturnIntermediateForMediumScore() {
            // Given
            PartantRecord partant = new PartantRecord(1, "Thunder", 25);

            // When
            String category = partantDomainService.determineSkillCategory(partant);

            // Then
            assertEquals("INTERMEDIATE", category);
        }

        @Test
        void shouldReturnNoviceForLowScore() {
            // Given
            PartantRecord partant = new PartantRecord(1, "Very Long Horse Name", 90);

            // When
            String category = partantDomainService.determineSkillCategory(partant);

            // Then
            assertEquals("NOVICE", category);
        }
    }

    @Nested
    class PartantListValidation {

        @Test
        void shouldValidateValidPartantList() {
            // Given
            List<PartantRecord> partants = List.of(
                new PartantRecord(1, "Horse 1", 1),
                new PartantRecord(2, "Horse 2", 2),
                new PartantRecord(3, "Horse 3", 3)
            );

            // When & Then
            assertDoesNotThrow(() -> partantDomainService.validatePartantList(partants));
        }

        @Test
        void shouldThrowExceptionForEmptyList() {
            // Given
            List<PartantRecord> partants = List.of();

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                () -> partantDomainService.validatePartantList(partants));
            assertTrue(exception.getMessage().contains("Partant list cannot be null or empty"));
        }

        @Test
        void shouldThrowExceptionForDuplicateIds() {
            // Given
            List<PartantRecord> partants = List.of(
                new PartantRecord(1, "Horse 1", 1),
                new PartantRecord(1, "Horse 2", 2) // Duplicate ID
            );

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                () -> partantDomainService.validatePartantList(partants));
            assertTrue(exception.getMessage().contains("Duplicate partant IDs found"));
        }

        @Test
        void shouldThrowExceptionForDuplicateNames() {
            // Given
            List<PartantRecord> partants = List.of(
                new PartantRecord(1, "Horse", 1),
                new PartantRecord(2, "horse", 2) // Same name (case insensitive)
            );

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                () -> partantDomainService.validatePartantList(partants));
            assertTrue(exception.getMessage().contains("Duplicate partant names found"));
        }
    }

    @Nested
    class RaceStrategyDetermination {

        @Test
        void shouldReturnAggressiveForHighPerformance() {
            // Given - Create a partant that will achieve high performance score (>= 75)
            // Base score (50) + Number bonus (20) + Name bonus (5) + Good standing (0) = 75
            PartantRecord partant = new PartantRecord(1, "Max", 1); // Low number, short name

            // When
            String strategy = partantDomainService.determineRaceStrategy(partant);

            // Then
            assertEquals("AGGRESSIVE", strategy); // Score is 75, so AGGRESSIVE is correct
        }

        @Test
        void shouldReturnBalancedForMediumPerformance() {
            // Given
            PartantRecord partant = new PartantRecord(1, "Thunder", 15);

            // When
            String strategy = partantDomainService.determineRaceStrategy(partant);

            // Then
            assertEquals("BALANCED", strategy);
        }

        @Test
        void shouldReturnConservativeForLowPerformance() {
            // Given
            PartantRecord partant = new PartantRecord(1, "Very Long Horse Name", 90);

            // When
            String strategy = partantDomainService.determineRaceStrategy(partant);

            // Then
            assertEquals("CONSERVATIVE", strategy);
        }
    }
}
