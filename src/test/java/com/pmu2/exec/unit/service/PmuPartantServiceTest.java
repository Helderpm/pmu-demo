package com.pmu2.exec.unit.service;

import com.pmu2.exec.domain.PartantRecord;
import com.pmu2.exec.domain.service.PartantDomainService;
import com.pmu2.exec.infrastructure.db.sql.PartantEntity;
import com.pmu2.exec.infrastructure.db.sql.PartantJpaRepository;
import com.pmu2.exec.service.PmuPartantService;
import com.pmu2.exec.service.mapper.PartantMapper;
import com.pmu2.exec.validation.PartantValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PmuPartantServiceTest {

    @Mock
    private PartantJpaRepository partantJpaRepository;

    @Mock
    private PartantMapper partantMapper;

    @Mock
    private PartantValidator partantValidator;

    @Mock
    private PartantDomainService partantDomainService;

    @InjectMocks
    private PmuPartantService pmuPartantService;

    private PartantRecord testPartantRecord;
    private PartantEntity testPartantEntity;

    @BeforeEach
    void setUp() {
        testPartantRecord = new PartantRecord(1, "Thunder Bolt", 5);
        testPartantEntity = new PartantEntity("Thunder Bolt", 5);
        testPartantEntity.setId(1L);
    }

    @Test
    void shouldFindAllPartants() {
        // Given
        List<PartantEntity> partantEntities = List.of(testPartantEntity);
        List<PartantRecord> partantRecords = List.of(testPartantRecord);

        when(partantJpaRepository.findAll()).thenReturn(partantEntities);
        when(partantMapper.toRecordList(partantEntities)).thenReturn(partantRecords);

        // When
        List<PartantRecord> result = pmuPartantService.findAll();

        // Then
        assertEquals(partantRecords, result);
        verify(partantJpaRepository).findAll();
        verify(partantMapper).toRecordList(partantEntities);
    }

    @Test
    void shouldSavePartant() {
        // Given
        when(partantMapper.toEntity(testPartantRecord)).thenReturn(testPartantEntity);
        when(partantJpaRepository.save(testPartantEntity)).thenReturn(testPartantEntity);
        when(partantMapper.toRecord(testPartantEntity)).thenReturn(testPartantRecord);

        // When
        PartantRecord result = pmuPartantService.save(testPartantRecord);

        // Then
        assertEquals(testPartantRecord, result);
        verify(partantDomainService).validatePartantEligibility(testPartantRecord);
        verify(partantValidator).validatePartantNameUnique(testPartantRecord.name());
        verify(partantValidator).validatePartantIntegrity(testPartantEntity);
        verify(partantJpaRepository).save(testPartantEntity);
    }

    @Test
    void shouldDeletePartantById() {
        // Given
        Long partantId = 1L;

        // When
        pmuPartantService.deleteById(partantId);

        // Then
        verify(partantValidator).validatePartantExistsById(partantId);
        verify(partantJpaRepository).deleteById(partantId);
    }

    @Test
    void shouldFindPartantsByName() {
        // Given
        String partantName = "Thunder Bolt";
        List<PartantEntity> partantEntities = List.of(testPartantEntity);
        List<PartantRecord> partantRecords = List.of(testPartantRecord);

        when(partantJpaRepository.findByName(partantName)).thenReturn(partantEntities);
        when(partantMapper.toRecordList(partantEntities)).thenReturn(partantRecords);

        // When
        List<PartantRecord> result = pmuPartantService.findByName(partantName);

        // Then
        assertEquals(partantRecords, result);
        verify(partantValidator).validatePartantExistsByName(partantName);
        verify(partantJpaRepository).findByName(partantName);
        verify(partantMapper).toRecordList(partantEntities);
    }

    @Test
    void shouldReturnTrueWhenPartantIsInGoodStanding() {
        // Given
        Long partantId = 1L;
        when(partantJpaRepository.findById(partantId)).thenReturn(Optional.of(testPartantEntity));
        when(partantMapper.toRecord(testPartantEntity)).thenReturn(testPartantRecord);
        when(partantDomainService.isInGoodStanding(testPartantRecord)).thenReturn(true);

        // When
        boolean result = pmuPartantService.isInGoodStanding(partantId);

        // Then
        assertTrue(result);
        verify(partantValidator).validatePartantExistsById(partantId);
        verify(partantJpaRepository).findById(partantId);
        verify(partantDomainService).isInGoodStanding(testPartantRecord);
    }

    @Test
    void shouldReturnFalseWhenPartantIsNotInGoodStanding() {
        // Given
        Long partantId = 1L;
        when(partantJpaRepository.findById(partantId)).thenReturn(Optional.of(testPartantEntity));
        when(partantMapper.toRecord(testPartantEntity)).thenReturn(testPartantRecord);
        when(partantDomainService.isInGoodStanding(testPartantRecord)).thenReturn(false);

        // When
        boolean result = pmuPartantService.isInGoodStanding(partantId);

        // Then
        assertFalse(result);
        verify(partantValidator).validatePartantExistsById(partantId);
        verify(partantJpaRepository).findById(partantId);
        verify(partantDomainService).isInGoodStanding(testPartantRecord);
    }

    @Test
    void shouldCalculatePerformanceScore() {
        // Given
        Long partantId = 1L;
        int expectedScore = 85;
        when(partantJpaRepository.findById(partantId)).thenReturn(Optional.of(testPartantEntity));
        when(partantMapper.toRecord(testPartantEntity)).thenReturn(testPartantRecord);
        when(partantDomainService.calculatePerformanceScore(testPartantRecord)).thenReturn(expectedScore);

        // When
        int result = pmuPartantService.calculatePerformanceScore(partantId);

        // Then
        assertEquals(expectedScore, result);
        verify(partantValidator).validatePartantExistsById(partantId);
        verify(partantJpaRepository).findById(partantId);
        verify(partantDomainService).calculatePerformanceScore(testPartantRecord);
    }

    @Test
    void shouldDetermineSkillCategory() {
        // Given
        Long partantId = 1L;
        String expectedCategory = "EXPERT";
        when(partantJpaRepository.findById(partantId)).thenReturn(Optional.of(testPartantEntity));
        when(partantMapper.toRecord(testPartantEntity)).thenReturn(testPartantRecord);
        when(partantDomainService.determineSkillCategory(testPartantRecord)).thenReturn(expectedCategory);

        // When
        String result = pmuPartantService.determineSkillCategory(partantId);

        // Then
        assertEquals(expectedCategory, result);
        verify(partantValidator).validatePartantExistsById(partantId);
        verify(partantJpaRepository).findById(partantId);
        verify(partantDomainService).determineSkillCategory(testPartantRecord);
    }

    @Test
    void shouldThrowExceptionWhenPartantNotFoundForGoodStandingCheck() {
        // Given
        Long partantId = 1L;
        when(partantJpaRepository.findById(partantId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> pmuPartantService.isInGoodStanding(partantId));
        assertEquals("Partant not found", exception.getMessage());

        verify(partantValidator).validatePartantExistsById(partantId);
        verify(partantJpaRepository).findById(partantId);
    }

    @Test
    void shouldThrowExceptionWhenPartantNotFoundForPerformanceScore() {
        // Given
        Long partantId = 1L;
        when(partantJpaRepository.findById(partantId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> pmuPartantService.calculatePerformanceScore(partantId));
        assertEquals("Partant not found", exception.getMessage());

        verify(partantValidator).validatePartantExistsById(partantId);
        verify(partantJpaRepository).findById(partantId);
    }

    @Test
    void shouldThrowExceptionWhenPartantNotFoundForSkillCategory() {
        // Given
        Long partantId = 1L;
        when(partantJpaRepository.findById(partantId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> pmuPartantService.determineSkillCategory(partantId));
        assertEquals("Partant not found", exception.getMessage());

        verify(partantValidator).validatePartantExistsById(partantId);
        verify(partantJpaRepository).findById(partantId);
    }
}
