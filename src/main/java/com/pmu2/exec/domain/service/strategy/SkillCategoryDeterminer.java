package com.pmu2.exec.domain.service.strategy;

import com.pmu2.exec.domain.PartantRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Strategy for determining partant skill categories based on performance scores.
 */
@Component
public class SkillCategoryDeterminer {
    
    private static final Logger log = LoggerFactory.getLogger(SkillCategoryDeterminer.class);
    
    /**
     * Determines the partant's skill category based on their performance score.
     * 
     * @param performanceScore the performance score (1-100)
     * @param partant the partant to categorize
     * @return skill category (NOVICE, INTERMEDIATE, ADVANCED, EXPERT)
     */
    public String determineCategory(int performanceScore, PartantRecord partant) {
        log.debug("Determining skill category for partant: {}", partant.name());
        
        // Business Rule: Skill categories based on performance thresholds
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
}

