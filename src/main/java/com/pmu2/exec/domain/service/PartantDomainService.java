package com.pmu2.exec.domain.service;

import com.pmu2.exec.domain.PartantRecord;
import com.pmu2.exec.exception.PartantBusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Domain service for partant (participant) related business logic.
 * This service contains pure business rules without any infrastructure dependencies.
 * 
 * Key responsibilities:
 * - Partant eligibility validation
 * - Partant performance calculations
 * - Partant assignment logic
 * - Partant status determination
 */
@Service
@Slf4j
public class PartantDomainService {

    /**
     * Validates if a partant can participate in a course based on business rules.
     * 
     * @param partant the partant to validate
     * @throws PartantBusinessException if business rules are violated
     */
    public void validatePartantEligibility(PartantRecord partant) {
        log.debug("Validating partant eligibility for: {}", partant.name());
        
        // Business Rule: Partant name cannot be empty or just whitespace
        if (partant.name() == null || partant.name().trim().isEmpty()) {
            throw new PartantBusinessException("Partant name cannot be empty");
        }
        
        // Business Rule: Partant name must be at least 2 characters long
        if (partant.name().trim().length() < 2) {
            throw new PartantBusinessException("Partant name must be at least 2 characters long");
        }
        
        // Business Rule: Partant name cannot exceed 50 characters
        if (partant.name().length() > 50) {
            throw new PartantBusinessException("Partant name cannot exceed 50 characters");
        }
        
        // Business Rule: Partant number must be positive
        if (partant.number() <= 0) {
            throw new PartantBusinessException("Partant number must be positive");
        }
        
        // Business Rule: Partant number cannot exceed 99
        if (partant.number() > 99) {
            throw new PartantBusinessException("Partant number cannot exceed 99");
        }
        
        log.debug("Partant eligibility validation passed for: {}", partant.name());
    }
    
    /**
     * Determines if a partant is in good standing for participation.
     * This would typically check historical performance, penalties, etc.
     * For this example, we'll use simplified rules.
     * 
     * @param partant the partant to check
     * @return true if the partant is in good standing
     */
    public boolean isInGoodStanding(PartantRecord partant) {
        log.debug("Checking good standing for partant: {}", partant.name());
        
        // Business Rule: Partants with numbers ending in 13 are considered unlucky (example rule)
        if (partant.number() % 100 == 13) {
            log.debug("Partant {} not in good standing: unlucky number 13", partant.name());
            return false;
        }
        
        // Business Rule: Partants with very high numbers might be inexperienced
        if (partant.number() > 50) {
            log.debug("Partant {} not in good standing: high number indicates inexperience", partant.name());
            return false;
        }
        
        log.debug("Partant {} is in good standing", partant.name());
        return true;
    }
    
    /**
     * Calculates the partant's performance score based on their characteristics.
     * This is a simplified example - real-world scenarios would consider historical data.
     * 
     * @param partant the partant to analyze
     * @return performance score (1-100, where 100 is best)
     */
    public int calculatePerformanceScore(PartantRecord partant) {
        log.debug("Calculating performance score for partant: {}", partant.name());
        
        int baseScore = 50;
        
        // Factor 1: Partant number (lower numbers might indicate better performance)
        if (partant.number() <= 5) {
            baseScore += 20;
        } else if (partant.number() <= 10) {
            baseScore += 10;
        } else if (partant.number() <= 20) {
            baseScore += 5;
        }
        
        // Factor 2: Name length (shorter names might be more professional)
        int nameLength = partant.name().trim().length();
        if (nameLength <= 10) {
            baseScore += 5;
        } else if (nameLength > 25) {
            baseScore -= 5;
        }
        
        // Factor 3: Check if in good standing
        if (!isInGoodStanding(partant)) {
            baseScore -= 15;
        }
        
        // Ensure score is within bounds
        int score = Math.max(1, Math.min(100, baseScore));
        
        log.debug("Partant {} performance score calculated as: {}", partant.name(), score);
        return score;
    }
    
    /**
     * Determines the partant's skill category based on their characteristics.
     * 
     * @param partant the partant to categorize
     * @return skill category (NOVICE, INTERMEDIATE, ADVANCED, EXPERT)
     */
    public String determineSkillCategory(PartantRecord partant) {
        log.debug("Determining skill category for partant: {}", partant.name());
        
        int performanceScore = calculatePerformanceScore(partant);
        
        if (performanceScore >= 85) {
            return "EXPERT";
        } else if (performanceScore >= 70) {
            return "ADVANCED";
        } else if (performanceScore >= 50) {
            return "INTERMEDIATE";
        } else {
            return "NOVICE";
        }
    }
    
    /**
     * Validates a list of partants for course assignment.
     * 
     * @param partants the list of partants to validate
     * @throws PartantBusinessException if validation fails
     */
    public void validatePartantList(List<PartantRecord> partants) {
        log.debug("Validating partant list of size: {}", partants.size());
        
        if (partants == null || partants.isEmpty()) {
            throw new PartantBusinessException("Partant list cannot be null or empty");
        }
        
        // Validate each partant individually
        for (PartantRecord partant : partants) {
            validatePartantEligibility(partant);
        }
        
        // Business Rule: Check for duplicate partant IDs
        List<Integer> partantIds = partants.stream()
            .map(PartantRecord::id)
            .distinct()
            .collect(Collectors.toList());
            
        if (partantIds.size() != partants.size()) {
            throw new PartantBusinessException("Duplicate partant IDs found in the list");
        }
        
        // Business Rule: Check for duplicate partant names
        List<String> partantNames = partants.stream()
            .map(p -> p.name().trim().toLowerCase())
            .distinct()
            .collect(Collectors.toList());
            
        if (partantNames.size() != partants.size()) {
            throw new PartantBusinessException("Duplicate partant names found in the list");
        }
        
        log.debug("Partant list validation passed for {} partants", partants.size());
    }
    
    /**
     * Determines the optimal race strategy for a partant.
     * This is a simplified example - real-world scenarios would be much more complex.
     * 
     * @param partant the partant to analyze
     * @return recommended strategy (AGGRESSIVE, BALANCED, CONSERVATIVE)
     */
    public String determineRaceStrategy(PartantRecord partant) {
        log.debug("Determining race strategy for partant: {}", partant.name());
        
        int performanceScore = calculatePerformanceScore(partant);
        
        if (performanceScore >= 80) {
            return "AGGRESSIVE";
        } else if (performanceScore >= 60) {
            return "BALANCED";
        } else {
            return "CONSERVATIVE";
        }
    }
}
