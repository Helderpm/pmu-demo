package com.pmu2.exec.domain.service.strategy;

import com.pmu2.exec.config.ValidationConfig;
import com.pmu2.exec.domain.PartantRecord;
import com.pmu2.exec.util.MathUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Strategy for calculating partant performance scores.
 */
@Component
@RequiredArgsConstructor
public class PerformanceCalculator {
    
    private static final Logger log = LoggerFactory.getLogger(PerformanceCalculator.class);
    
    private final ValidationConfig validationConfig;
    
    /**
     * Calculates the partant's performance score based on their characteristics.
     * 
     * @param partant the partant to analyze
     * @return performance score (1-100, where 100 is best)
     */
    public int calculateScore(PartantRecord partant) {
        log.debug("Calculating performance score for partant: {}", partant.name());
        
        int baseScore = validationConfig.getPartant().getPerformanceBaseScore();
        
        // Factor 1: Partant number (lower numbers might indicate better performance)
        baseScore += calculateNumberBonus(partant.number());
        
        // Factor 2: Name length (shorter names might be more professional)
        baseScore += calculateNameBonus(partant.name());
        
        // Factor 3: Check if in good standing
        baseScore += calculateGoodStandingBonus(partant);
        
        // Ensure score is within configured bounds
        int score = MathUtil.clamp(baseScore, validationConfig.getPartant().getPerformanceMinScore(), 
                               validationConfig.getPartant().getPerformanceMaxScore());
        
        log.debug("Partant {} performance score calculated as: {}", partant.name(), score);
        return score;
    }
    
    private int calculateNumberBonus(int number) {
        if (number <= 5) {
            return 20;
        } else if (number <= 10) {
            return 10;
        } else if (number <= 20) {
            return 5;
        }
        return 0;
    }
    
    private int calculateNameBonus(String name) {
        int nameLength = name.trim().length();
        if (nameLength <= 10) {
            return 5;
        } else if (nameLength > 25) {
            return -5;
        }
        return 0;
    }
    
    private int calculateGoodStandingBonus(PartantRecord partant) {
        // Business Rule: Partants with unlucky numbers are not in good standing
        if (partant.number() % 100 == validationConfig.getPartant().getUnluckyNumber()) {
            return -15;
        }
        
        // Business Rule: Partants with very high numbers might be inexperienced
        if (partant.number() > validationConfig.getPartant().getMaxHighNumberThreshold()) {
            return -15;
        }
        
        return 0;
    }
}

