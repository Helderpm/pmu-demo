package com.pmu2.exec.domain.service.strategy;

import com.pmu2.exec.config.ValidationConfig;
import com.pmu2.exec.domain.PartantRecord;
import com.pmu2.exec.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Strategy for checking partant eligibility.
 */
@Component
@RequiredArgsConstructor
public class EligibilityChecker {
    
    private static final Logger log = LoggerFactory.getLogger(EligibilityChecker.class);
    
    private final ValidationConfig validationConfig;
    
    /**
     * Validates if a partant can participate in a course based on business rules.
     * 
     * @param partant the partant to validate
     * @throws BusinessException if business rules are violated
     */
    public void validateEligibility(PartantRecord partant) {
        log.debug("Validating partant eligibility for: {}", partant.name());
        
        validateName(partant);
        validateNumber(partant);
        
        log.debug("Partant eligibility validation passed for: {}", partant.name());
    }
    
    private void validateName(PartantRecord partant) {
        // Business Rule: Partant name cannot be empty or just whitespace
        if (partant.name() == null || partant.name().trim().isEmpty()) {
            throw new BusinessException("Partant name cannot be empty");
        }
        
        // Business Rule: Partant name must meet minimum length requirement
        if (partant.name().trim().length() < validationConfig.getPartant().getNameMinLength()) {
            throw new BusinessException(
                "Partant name must be at least " + validationConfig.getPartant().getNameMinLength() + " characters long");
        }
        
        // Business Rule: Partant name cannot exceed maximum length
        if (partant.name().length() > validationConfig.getPartant().getNameMaxLength()) {
            throw new BusinessException(
                "Partant name cannot exceed " + validationConfig.getPartant().getNameMaxLength() + " characters");
        }
    }
    
    private void validateNumber(PartantRecord partant) {
        // Business Rule: Partant number must be within valid range
        if (partant.number() < validationConfig.getPartant().getNumberMin() || 
            partant.number() > validationConfig.getPartant().getNumberMax()) {
            throw new BusinessException(
                "Partant number must be between " + validationConfig.getPartant().getNumberMin() + 
                " and " + validationConfig.getPartant().getNumberMax());
        }
    }
}

