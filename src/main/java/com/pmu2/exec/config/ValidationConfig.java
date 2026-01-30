package com.pmu2.exec.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration class for validation rules and business constraints.
 * This configuration provides centralized access to validation parameters
 * used throughout the application.
 */
@Configuration
@ConfigurationProperties(prefix = "pmu.validation")
@Validated
@Data
public class ValidationConfig {
    
    /**
     * Course validation rules.
     */
    private CourseValidation course = new CourseValidation();
    
    /**
     * Partant validation rules.
     */
    private PartantValidation partant = new PartantValidation();
    
    /**
     * Course-specific validation rules.
     */
    @Data
    public static class CourseValidation {
        private int nameMinLength = 2;
        private int nameMaxLength = 255;
        private int numberMin = 1;
        private int numberMax = 999;
        private int maxFutureMonths = 6;
        private int minPartants = 3;
        private int maxPartants = 20;
        private int minBettingPartants = 5;
        private int maxBettingPartants = 15;
        private int minBettingHours = 24;
        private int minModificationHours = 48;
    }
    
    /**
     * Partant-specific validation rules.
     */
    @Data
    public static class PartantValidation {
        private int nameMinLength = 2;
        private int nameMaxLength = 50;
        private int numberMin = 1;
        private int numberMax = 99;
        private int maxHighNumberThreshold = 50;
        private int unluckyNumber = 13;
        private int performanceBaseScore = 50;
        private int performanceMaxScore = 100;
        private int performanceMinScore = 1;
    }
}