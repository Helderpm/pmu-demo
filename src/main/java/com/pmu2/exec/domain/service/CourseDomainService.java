package com.pmu2.exec.domain.service;

import com.pmu2.exec.config.ValidationConfig;
import com.pmu2.exec.domain.CourseRecord;
import com.pmu2.exec.domain.PartantRecord;
import com.pmu2.exec.exception.BusinessException;
import com.pmu2.exec.util.MathUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Domain service for course-related business logic.
 * This service contains pure business rules without any infrastructure dependencies.
 * 
 * Key responsibilities:
 * - Course business rule validation
 * - Course eligibility calculations  
 * - Course status determination
 * - Complex cross-entity validation
 */
@Service
@RequiredArgsConstructor
public class CourseDomainService {

    private static final Logger log = LoggerFactory.getLogger(CourseDomainService.class);

    private final ValidationConfig validationConfig;

    /**
     * Validates if a course can be created based on business rules.
     * This method focuses on complex business logic that cannot be expressed
     * through Bean Validation annotations.
     * 
     * @param course the course to validate
     * @throws BusinessException if business rules are violated
     */
    public void validateCourseCreation(CourseRecord course) {
        log.debug("Validating course creation for: {}", course.name());
        
        // Business Rule: Course date cannot be more than configured months in the future
        LocalDate maxFutureDate = LocalDate.now().plusMonths(validationConfig.getCourse().getMaxFutureMonths());
        if (course.date().isAfter(maxFutureDate)) {
            throw new BusinessException(
                "Course date cannot be more than " + validationConfig.getCourse().getMaxFutureMonths() + " months in future"
            );
        }
        
        // Business Rule: Course must have minimum and maximum partants as configured
        int partantCount = course.partants().size();
        if (partantCount < validationConfig.getCourse().getMinPartants()) {
            throw new BusinessException(
                "Course must have at least " + validationConfig.getCourse().getMinPartants() + " partants"
            );
        }
        
        if (partantCount > validationConfig.getCourse().getMaxPartants()) {
            throw new BusinessException(
                "Course cannot have more than " + validationConfig.getCourse().getMaxPartants() + " partants"
            );
        }
        
        // Business Rule: All partant numbers must be unique within the course
        validatePartantNumbersUnique(course.partants());
        
        // Business Rule: Partant numbers must be sequential starting from 1
        validatePartantNumbersSequential(course.partants());
        
        log.debug("Course creation validation passed for: {}", course.name());
    }
    
    /**
     * Determines if a course is eligible for betting based on business rules.
     * 
     * @param course the course to check
     * @return true if the course is eligible for betting
     */
    public boolean isEligibleForBetting(CourseRecord course) {
        log.debug("Checking betting eligibility for course: {}", course.name());
        
        // Business Rule: Course must be at least configured hours in the future
        LocalDate minBettingDate = LocalDate.now().plusDays(validationConfig.getCourse().getMinBettingHours() / 24);
        if (course.date().isBefore(minBettingDate)) {
            log.debug("Course {} not eligible for betting: too soon", course.name());
            return false;
        }
        
        // Business Rule: Course must have between configured min/max partants for betting
        int partantCount = course.partants().size();
        if (partantCount < validationConfig.getCourse().getMinBettingPartants() || 
            partantCount > validationConfig.getCourse().getMaxBettingPartants()) {
            log.debug("Course {} not eligible for betting: invalid partant count ({})", course.name(), partantCount);
            return false;
        }
        
        log.debug("Course {} is eligible for betting", course.name());
        return true;
    }
    
    /**
     * Calculates the course difficulty based on partant characteristics.
     * This is a simplified example - in reality, this would consider many more factors.
     * 
     * @param course the course to analyze
     * @return difficulty score (1-10, where 10 is most difficult)
     */
    public int calculateCourseDifficulty(CourseRecord course) {
        log.debug("Calculating difficulty for course: {}", course.name());
        
        int baseDifficulty = 5;
        
        // Factor 1: Number of partants (more partants = more difficult)
        int partantCount = course.partants().size();
        if (partantCount > 10) {
            baseDifficulty += 2;
        } else if (partantCount < 5) {
            baseDifficulty -= 1;
        }
        
        // Factor 2: Course number (higher numbers might indicate higher stakes)
        if (course.number() > 100) {
            baseDifficulty += 1;
        }
        
        // Ensure difficulty is within bounds
        int difficulty = MathUtil.clamp(baseDifficulty, 1, 10);
        
        log.debug("Course {} difficulty calculated as: {}", course.name(), difficulty);
        return difficulty;
    }

    /**
     * Validates that partant numbers are unique within a course.
     *
     * @param partants list of partants to validate
     * @throws BusinessException if duplicate numbers are found
     */
    private void validatePartantNumbersUnique(List<PartantRecord> partants) {
        List<Integer> partantNumbers = partants.stream()
            .map(partant -> (Integer) partant.number())
            .sorted()
            .toList();
            
        for (int i = 1; i < partantNumbers.size(); i++) {
            if (partantNumbers.get(i).equals(partantNumbers.get(i - 1))) {
                throw new BusinessException(
                    "Duplicate partant number found: " + partantNumbers.get(i)
                );
            }
        }
    }

    /**
     * Validates that partant numbers are sequential starting from 1.
     *
     * @param partants list of partants to validate
     * @throws BusinessException if numbers are not sequential
     */
    private void validatePartantNumbersSequential(List<PartantRecord> partants) {
        List<Integer> partantNumbers = partants.stream()
            .map(partant -> (Integer) partant.number())
            .sorted()
            .toList();
            
        for (int i = 0; i < partantNumbers.size(); i++) {
            if (partantNumbers.get(i) != i + 1) {
                throw new BusinessException(
                    "Partant numbers must be sequential starting from 1. Found: " + partantNumbers.get(i) + ", expected: " + (i + 1)
                );
            }
        }
    }
}

