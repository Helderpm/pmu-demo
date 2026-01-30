package com.pmu2.exec.validation;

import com.pmu2.exec.exception.NotFoundException;
import com.pmu2.exec.exception.SimpleValidationException;
import com.pmu2.exec.infrastructure.db.sql.PartantEntity;
import com.pmu2.exec.infrastructure.db.sql.PartantJpaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Component responsible for validating partant-related existence and integrity rules.
 */
@Component
@RequiredArgsConstructor
public class PartantValidator {

private static final Logger log = LoggerFactory.getLogger(PartantValidator.class);


    public static final String PARTANT_NAME = "partantName";
public static final String PARTANT = "Partant";
private final PartantJpaRepository partantJpaRepository;

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

    public void validatePartantExistsByName(String partantName) {
        if (partantName == null || partantName.trim().isEmpty()) {
            throw new SimpleValidationException("Partant name cannot be null or empty");
        }

        if (!partantJpaRepository.existsByName(partantName.trim())) {
            throw new NotFoundException(PARTANT, partantName);
        }

        log.debug("Partant '{}' exists in database", partantName);
    }

    public void validatePartantExistsById(Long partantId) {
        if (partantId == null) {
            throw new SimpleValidationException("partantId cannot be null");
        }

        if (!partantJpaRepository.existsById(partantId)) {
            throw new NotFoundException(PARTANT, partantId);
        }

        log.debug("Partant with ID {} exists in database", partantId);
    }

    public void validatePartantEntity(PartantEntity partant) {
        if (partant == null) {
            throw new SimpleValidationException("Partant entity cannot be null");
        }

        if (partant.getName() == null || partant.getName().trim().isEmpty()) {
            throw new SimpleValidationException("Partant name cannot be null or empty");
        }

        if (partant.getNumber() <= 0) {
            throw new SimpleValidationException("Partant number must be positive");
        }
    }

    public void validatePartantIntegrity(PartantEntity partant) {
        if (partant == null) {
            throw new SimpleValidationException("partant cannot be null");
        }

        log.debug("Partant integrity validation passed for: {}", partant.getName());
    }

    public void validatePartantNameUnique(String partantName) {
        if (partantName == null || partantName.trim().isEmpty()) {
            throw new SimpleValidationException(PARTANT_NAME);
        }

        boolean exists = partantJpaRepository.findByName(partantName).stream()
            .anyMatch(partant -> partant.getName().equalsIgnoreCase(partantName.trim()));
            
        if (exists) {
            throw new SimpleValidationException("Partant with name '" + partantName + "' already exists");
        }

        log.debug("Partant name uniqueness validation passed for: {}", partantName);
    }

    public void validatePartantNameUniqueForUpdate(String partantName, Long excludePartantId) {
        if (partantName == null || partantName.trim().isEmpty()) {
            throw new SimpleValidationException(PARTANT_NAME);
        }

        if (excludePartantId == null) {
            throw new SimpleValidationException("excludePartantId cannot be null");
        }

        boolean exists = partantJpaRepository.findByName(partantName).stream()
            .anyMatch(partant -> !partant.getId().equals(excludePartantId)
                && partant.getName().equalsIgnoreCase(partantName.trim()));
            
        if (exists) {
            throw new SimpleValidationException("Partant with name '" + partantName + "' already exists");
        }

        log.debug("Partant name uniqueness validation passed for: {} (excluding ID: {})", partantName, excludePartantId);
    }

    public void validatePartantNumbersUnique(List<PartantEntity> partants) {
        if (partants == null || partants.isEmpty()) {
            return;
        }

        List<Integer> numbers = partants.stream()
            .map(PartantEntity::getNumber)
            .sorted()
            .toList();
            
        for (int i = 1; i < numbers.size(); i++) {
            if (numbers.get(i).equals(numbers.get(i - 1))) {
                throw new SimpleValidationException("Duplicate partant number found: " + numbers.get(i));
            }
        }

        log.debug("Partant number uniqueness validation passed for {} partants", partants.size());
    }
}