package com.pmu2.exec.domain.service;

import com.pmu2.exec.config.ValidationConfig;
import com.pmu2.exec.domain.PartantRecord;
import com.pmu2.exec.exception.BusinessException;
import com.pmu2.exec.domain.service.strategy.EligibilityChecker;
import com.pmu2.exec.domain.service.strategy.PerformanceCalculator;
import com.pmu2.exec.domain.service.strategy.SkillCategoryDeterminer;
import com.pmu2.exec.domain.service.strategy.RaceStrategyDeterminer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Domain service for partant (participant) related business logic.
 * This service contains pure business rules without any infrastructure dependencies.
 * 
 * Key responsibilities:
 * - Partant eligibility validation
 * - Partant performance calculations
 * - Partant assignment logic
 * - Complex cross-entity validation
 */
@Service
@RequiredArgsConstructor
public class PartantDomainService {

    private static final Logger log = LoggerFactory.getLogger(PartantDomainService.class);

    private final ValidationConfig validationConfig;
    private final EligibilityChecker eligibilityChecker;
    private final PerformanceCalculator performanceCalculator;
    private final SkillCategoryDeterminer skillCategoryDeterminer;
    private final RaceStrategyDeterminer raceStrategyDeterminer;

    /**
     * Validates if a partant can participate in a course based on business rules.
     * This method focuses on complex business logic that cannot be expressed
     * through Bean Validation annotations.
     * 
     * @param partant the partant to validate
     * @throws BusinessException if business rules are violated
     */
    public void validatePartantEligibility(PartantRecord partant) {
        eligibilityChecker.validateEligibility(partant);
    }
    
    /**
     * Determines if a partant is in good standing for participation.
     * This would typically check historical performance, penalties, etc.
     * For this example, we'll use simplified business rules.
     * 
     * @param partant the partant to check
     * @return true if the partant is in good standing
     */
    public boolean isInGoodStanding(PartantRecord partant) {
        log.debug("Checking good standing for partant: {}", partant.name());
        
        // Business Rule: Partants with unlucky numbers are not in good standing
        if (partant.number() % 100 == validationConfig.getPartant().getUnluckyNumber()) {
            log.debug("Partant {} not in good standing: unlucky number {}", partant.name(), validationConfig.getPartant().getUnluckyNumber());
            return false;
        }
        
        // Business Rule: Partants with very high numbers might be inexperienced
        if (partant.number() > validationConfig.getPartant().getMaxHighNumberThreshold()) {
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
        return performanceCalculator.calculateScore(partant);
    }
    
    /**
     * Determines the partant's skill category based on their characteristics.
     * 
     * @param partant the partant to categorize
     * @return skill category (NOVICE, INTERMEDIATE, ADVANCED, EXPERT)
     */
    public String determineSkillCategory(PartantRecord partant) {
        int performanceScore = calculatePerformanceScore(partant);
        return skillCategoryDeterminer.determineCategory(performanceScore, partant);
    }
    
    /**
     * Validates a list of partants for course assignment.
     * 
     * @param partants the list of partants to validate
     * @throws BusinessException if validation fails
     */
    public void validatePartantList(List<PartantRecord> partants) {
        if (partants == null || partants.isEmpty()) {
            throw new BusinessException("Partant list cannot be null or empty");
        }
        
        log.debug("Validating partant list of size: {}", partants.size());
        
        // Validate each partant individually
        for (PartantRecord partant : partants) {
            validatePartantEligibility(partant);
        }
        
        // Business Rule: Check for duplicate partant IDs
        List<Integer> partantIds = partants.stream()
            .map(PartantRecord::id)
            .distinct()
            .toList();
            
        if (partantIds.size() != partants.size()) {
            throw new BusinessException("Duplicate partant IDs found in the list");
        }
        
        // Business Rule: Check for duplicate partant names
        List<String> partantNames = partants.stream()
            .map(p -> p.name().trim().toLowerCase())
            .distinct()
            .toList();
            
        if (partantNames.size() != partants.size()) {
            throw new BusinessException("Duplicate partant names found in the list");
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
        int performanceScore = calculatePerformanceScore(partant);
        return raceStrategyDeterminer.determineStrategy(performanceScore, partant);
    }
    
}

