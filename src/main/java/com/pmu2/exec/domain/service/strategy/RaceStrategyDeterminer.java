package com.pmu2.exec.domain.service.strategy;

import com.pmu2.exec.domain.PartantRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Strategy for determining optimal race strategies for partants.
 */
@Component
public class RaceStrategyDeterminer {
    
    private static final Logger log = LoggerFactory.getLogger(RaceStrategyDeterminer.class);
    
    private static final String AGGRESSIVE_STRATEGY = "AGGRESSIVE";
    private static final String BALANCED_STRATEGY = "BALANCED";
    private static final String CONSERVATIVE_STRATEGY = "CONSERVATIVE";
    
    /**
     * Determines the optimal race strategy for a partant based on performance score.
     * 
     * @param performanceScore the performance score (1-100)
     * @param partant the partant to analyze
     * @return recommended strategy (AGGRESSIVE, BALANCED, CONSERVATIVE)
     */
    public String determineStrategy(int performanceScore, PartantRecord partant) {
        log.debug("Determining race strategy for partant: {}", partant.name());
        
        if (performanceScore >= 75) {
            log.debug("High performance score ({}) suggests {} strategy for {}", performanceScore, AGGRESSIVE_STRATEGY, partant.name());
            return AGGRESSIVE_STRATEGY;
        } else if (performanceScore >= 50) {
            log.debug("Medium performance score ({}) suggests {} strategy for {}", performanceScore, BALANCED_STRATEGY, partant.name());
            return BALANCED_STRATEGY;
        } else {
            log.debug("Low performance score ({}) suggests {} strategy for {}", performanceScore, CONSERVATIVE_STRATEGY, partant.name());
            return CONSERVATIVE_STRATEGY;
        }
    }
}