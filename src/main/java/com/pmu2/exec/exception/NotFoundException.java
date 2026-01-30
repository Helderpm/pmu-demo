package com.pmu2.exec.exception;

/**
 * Simple exception for entity not found scenarios.
 * Replaces CourseNotFoundException, PartantNotFoundException, and ExistenceValidationException.
 */
public class NotFoundException extends RuntimeException {
    
    private final String entityType;
    private final String identifier;
    
    public NotFoundException(String message) {
        super(message);
        this.entityType = null;
        this.identifier = null;
    }
    
    public NotFoundException(String entityType, Object identifier) {
        super(String.format("%s with identifier '%s' was not found", entityType, identifier));
        this.entityType = entityType;
        this.identifier = String.valueOf(identifier);
    }
    
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.entityType = null;
        this.identifier = null;
    }
    
    // Getter methods for test compatibility
    public String getEntityType() {
        return entityType;
    }
    
    public String getIdentifier() {
        return identifier;
    }
    
    // Additional getters for specific entity types
    public String getCourseId() {
        return "Course".equals(entityType) ? identifier : null;
    }
    
    public String getPartantId() {
        return "Partant".equals(entityType) ? identifier : null;
    }
    
    public String getPartantName() {
        return "Partant".equals(entityType) ? identifier : null;
    }
}

