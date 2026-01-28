package com.pmu2.exec.validation;

import com.pmu2.exec.exception.PartantNotFoundException;
import com.pmu2.exec.infrastrure.db.sql.PartantEntity;
import com.pmu2.exec.infrastrure.db.sql.PartantJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Component responsible for validating partant-related business rules.
 * This component separates validation logic from service layer, following Single Responsibility Principle.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PartantValidator {

    private final PartantJpaRepository partantJpaRepository;

    /**
     * Validates that all partants exist in the database.
     * Throws PartantNotFoundException if any partant is not found.
     *
     * @param partants list of partants to validate
     * @throws PartantNotFoundException if any partant is not found
     */
    public void validatePartantsExist(List<PartantEntity> partants) {
        if (partants == null || partants.isEmpty()) {
            log.debug("No partants to validate");
            return;
        }

        log.debug("Validating {} partants", partants.size());
        
        for (PartantEntity partant : partants) {
            validatePartantExistsByName(partant.getName());
        }
        
        log.debug("All {} partants validated successfully", partants.size());
    }

    /**
     * Validates that a partant with the given name exists in the database.
     *
     * @param partantName the name of the partant to validate
     * @throws PartantNotFoundException if the partant is not found
     */
    public void validatePartantExistsByName(String partantName) {
        if (partantName == null || partantName.trim().isEmpty()) {
            throw new IllegalArgumentException("Partant name cannot be null or empty");
        }

        List<PartantEntity> existingPartants = partantJpaRepository.findByName(partantName);
        if (existingPartants.isEmpty()) {
            log.warn("Partant not found: {}", partantName);
            throw new PartantNotFoundException(partantName);
        }
        
        log.debug("Partant found: {}", partantName);
    }

    /**
     * Validates that a partant with the given ID exists in the database.
     *
     * @param partantId the ID of the partant to validate
     * @throws PartantNotFoundException if the partant is not found
     */
    public void validatePartantExistsById(Long partantId) {
        if (partantId == null) {
            throw new IllegalArgumentException("Partant ID cannot be null");
        }

        if (!partantJpaRepository.existsById(partantId)) {
            log.warn("Partant not found with ID: {}", partantId);
            throw new PartantNotFoundException(partantId);
        }
        
        log.debug("Partant found with ID: {}", partantId);
    }

    /**
     * Validates partant entity data integrity.
     *
     * @param partant the partant entity to validate
     * @throws IllegalArgumentException if validation fails
     */
    public void validatePartantEntity(PartantEntity partant) {
        if (partant == null) {
            throw new IllegalArgumentException("Partant entity cannot be null");
        }

        if (partant.getName() == null || partant.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Partant name cannot be null or empty");
        }

        if (partant.getNumber() <= 0) {
            throw new IllegalArgumentException("Partant number must be positive");
        }

        log.debug("Partant entity validation passed for: {}", partant.getName());
    }
}
