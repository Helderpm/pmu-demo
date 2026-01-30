package com.pmu2.exec.service;

import com.pmu2.exec.domain.PartantRecord;
import com.pmu2.exec.infrastructure.db.sql.PartantEntity;
import com.pmu2.exec.infrastructure.db.sql.PartantJpaRepository;
import com.pmu2.exec.service.mapper.PartantMapper;
import com.pmu2.exec.validation.PartantValidator;
import com.pmu2.exec.domain.service.PartantDomainService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Transactional
@Validated
public class PmuPartantService {

    public static final String PARTANT_NOT_FOUND = "Partant not found";
    private final PartantJpaRepository partantJpaRepository;
    private final PartantMapper partantMapper;
    private final PartantValidator partantValidator;
    private final PartantDomainService partantDomainService;

    public PmuPartantService(PartantJpaRepository partantJpaRepository, PartantMapper partantMapper, PartantValidator partantValidator, PartantDomainService partantDomainService) {
        this.partantJpaRepository = partantJpaRepository;
        this.partantMapper = partantMapper;
        this.partantValidator = partantValidator;
        this.partantDomainService = partantDomainService;
    }

    public List<PartantRecord> findAll() {
        return partantMapper.toRecordList(partantJpaRepository.findAll());
    }

    public PartantRecord save(PartantRecord partant) {
        // Validation chain: Format -> Integrity -> Business -> Existence
        partantDomainService.validatePartantEligibility(partant);
        partantValidator.validatePartantNameUnique(partant.name());
        
        PartantEntity partantEntity = partantMapper.toEntity(partant);
        partantValidator.validatePartantIntegrity(partantEntity);
        
        PartantEntity savedEntity = partantJpaRepository.save(partantEntity);
        return partantMapper.toRecord(savedEntity);
    }

    public void deleteById(Long id) {
        partantValidator.validatePartantExistsById(id);
        partantJpaRepository.deleteById(id);
    }

    public List<PartantRecord> findByName(String name) {
        partantValidator.validatePartantExistsByName(name);
        return partantMapper.toRecordList(partantJpaRepository.findByName(name));
    }

    /**
     * Checks if a partant is in good standing using domain service.
     * 
     * @param partantId the ID of the partant to check
     * @return true if in good standing
     */
    public boolean isInGoodStanding(Long partantId) {
        partantValidator.validatePartantExistsById(partantId);
        
        PartantRecord partant = partantMapper.toRecord(
            partantJpaRepository.findById(partantId)
                .orElseThrow(() -> new RuntimeException(PARTANT_NOT_FOUND))
        );
        
        return partantDomainService.isInGoodStanding(partant);
    }

    /**
     * Calculates performance score for a partant using domain service.
     * 
     * @param partantId the ID of the partant
     * @return performance score (1-100)
     */
    public int calculatePerformanceScore(Long partantId) {
        partantValidator.validatePartantExistsById(partantId);
        
        PartantRecord partant = partantMapper.toRecord(
            partantJpaRepository.findById(partantId)
                .orElseThrow(() -> new RuntimeException(PARTANT_NOT_FOUND))
        );
        
        return partantDomainService.calculatePerformanceScore(partant);
    }

    /**
     * Determines skill category for a partant using domain service.
     * 
     * @param partantId the ID of the partant
     * @return skill category (NOVICE, INTERMEDIATE, ADVANCED, EXPERT)
     */
    public String determineSkillCategory(Long partantId) {
        partantValidator.validatePartantExistsById(partantId);
        
        PartantRecord partant = partantMapper.toRecord(
            partantJpaRepository.findById(partantId)
                .orElseThrow(() -> new RuntimeException(PARTANT_NOT_FOUND))
        );
        
        return partantDomainService.determineSkillCategory(partant);
    }

}

