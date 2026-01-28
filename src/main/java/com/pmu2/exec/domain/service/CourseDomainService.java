package com.pmu2.exec.domain.service;

import com.pmu2.exec.domain.CourseRecord;
import com.pmu2.exec.domain.PartantRecord;
import com.pmu2.exec.exception.CourseBusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Domain service for course-related business logic.
 * This service contains pure business rules without any infrastructure dependencies.
 * 
 * Key responsibilities:
 * - Course business rule validation
 * - Course eligibility calculations
 * - Course status determination
 * - Partant assignment logic
 */
@Service
@Slf4j
public class CourseDomainService {

    /**
     * Validates if a course can be created based on business rules.
     * 
     * @param course the course to validate
     * @throws CourseBusinessException if business rules are violated
     */
    public void validateCourseCreation(CourseRecord course) {
        log.debug("Validating course creation for: {}", course.name());
        
        // Business Rule: Course date cannot be more than 6 months in the future
        LocalDate maxFutureDate = LocalDate.now().plusMonths(6);
        if (course.date().isAfter(maxFutureDate)) {
            throw new CourseBusinessException(
                "Course date cannot be more than 6 months in the future. Provided date: " + course.date()
            );
        }
        
        // Business Rule: Course must have minimum 3 partants and maximum 20 partants
        int partantCount = course.partants().size();
        if (partantCount < 3) {
            throw new CourseBusinessException(
                "Course must have at least 3 partants. Current count: " + partantCount
            );
        }
        
        if (partantCount > 20) {
            throw new CourseBusinessException(
                "Course cannot have more than 20 partants. Current count: " + partantCount
            );
        }
        
        // Business Rule: All partant numbers must be unique within the course
        List<Integer> partantNumbers = course.partants().stream()
            .map(PartantRecord::number)
            .sorted()
            .collect(Collectors.toList());
            
        for (int i = 1; i < partantNumbers.size(); i++) {
            if (partantNumbers.get(i).equals(partantNumbers.get(i - 1))) {
                throw new CourseBusinessException(
                    "Duplicate partant number found: " + partantNumbers.get(i)
                );
            }
        }
        
        // Business Rule: Partant numbers must be sequential starting from 1
        for (int i = 0; i < partantNumbers.size(); i++) {
            if (partantNumbers.get(i) != i + 1) {
                throw new CourseBusinessException(
                    "Partant numbers must be sequential starting from 1. Expected " + (i + 1) + ", found " + partantNumbers.get(i)
                );
            }
        }
        
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
        
        // Business Rule: Course must be at least 24 hours in the future
        LocalDate minBettingDate = LocalDate.now().plusDays(1);
        if (course.date().isBefore(minBettingDate)) {
            log.debug("Course {} not eligible for betting: too soon", course.name());
            return false;
        }
        
        // Business Rule: Course must have between 5 and 15 partants for betting
        int partantCount = course.partants().size();
        if (partantCount < 5 || partantCount > 15) {
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
        int difficulty = Math.max(1, Math.min(10, baseDifficulty));
        
        log.debug("Course {} difficulty calculated as: {}", course.name(), difficulty);
        return difficulty;
    }
    
    /**
     * Determines the optimal starting positions for partants based on their numbers.
     * This is a simplified algorithm - real-world scenarios would be much more complex.
     * 
     * @param course the course with partants
     * @return list of partant IDs in optimal starting order
     */
    public List<Integer> determineStartingOrder(CourseRecord course) {
        log.debug("Determining starting order for course: {}", course.name());
        
        // Simple algorithm: sort by partant number (this would be more complex in reality)
        return course.partants().stream()
            .sorted((p1, p2) -> Integer.compare(p1.number(), p2.number()))
            .map(PartantRecord::id)
            .collect(Collectors.toList());
    }
    
    /**
     * Validates if a course can be modified based on business rules.
     * 
     * @param course the existing course
     * @param modifications the proposed modifications
     * @throws CourseBusinessException if modification is not allowed
     */
    public void validateCourseModification(CourseRecord course, CourseRecord modifications) {
        log.debug("Validating course modification for: {}", course.name());
        
        // Business Rule: Cannot modify course date if it's less than 48 hours away
        LocalDate cutoffDate = LocalDate.now().plusDays(2);
        if (course.date().isBefore(cutoffDate) && !course.date().equals(modifications.date())) {
            throw new CourseBusinessException(
                "Cannot modify course date when it's less than 48 hours away"
            );
        }
        
        // Business Rule: Cannot remove partants if course is less than 24 hours away
        LocalDate partantCutoffDate = LocalDate.now().plusDays(1);
        if (course.date().isBefore(partantCutoffDate) && 
            modifications.partants().size() < course.partants().size()) {
            throw new CourseBusinessException(
                "Cannot remove partants when course is less than 24 hours away"
            );
        }
        
        log.debug("Course modification validation passed for: {}", course.name());
    }
}
