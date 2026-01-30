package com.pmu2.exec.unit.service;

import com.pmu2.exec.domain.CourseRecord;
import com.pmu2.exec.domain.PartantRecord;
import com.pmu2.exec.domain.service.CourseDomainService;
import com.pmu2.exec.infrastructure.db.sql.CourseEntity;
import com.pmu2.exec.infrastructure.db.sql.PartantEntity;
import com.pmu2.exec.infrastructure.db.sql.CourseJpaRepository;
import com.pmu2.exec.service.CourseEventPublisher;
import com.pmu2.exec.service.PmuCourseService;
import com.pmu2.exec.service.mapper.CourseMapper;
import com.pmu2.exec.validation.CourseValidator;
import com.pmu2.exec.validation.PartantValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PmuCourseServiceTest {

    @Mock
    private CourseJpaRepository courseJpaRepository;

    @Mock
    private CourseEventPublisher courseEventPublisher;

    @Mock
    private CourseMapper courseMapper;

    @Mock
    private CourseValidator courseValidator;

    @Mock
    private PartantValidator partantValidator;

    @Mock
    private CourseDomainService courseDomainService;

    @InjectMocks
    private PmuCourseService pmuCourseService;

    private CourseRecord testCourseRecord;
    private CourseEntity testCourseEntity;

    @BeforeEach
    void setUp() {
        List<PartantRecord> partants = List.of(
            new PartantRecord(1, "Horse 1", 1),
            new PartantRecord(2, "Horse 2", 2),
            new PartantRecord(3, "Horse 3", 3)
        );

        testCourseRecord = new CourseRecord(1, "Test Course", 100, LocalDate.now().plusDays(10), partants);
        testCourseEntity = new CourseEntity("Test Course", 100, LocalDate.now().plusDays(10));
        
        // Add partants to testCourseEntity
        List<PartantEntity> partantEntities = partants.stream().map(p -> {
            PartantEntity pe = new PartantEntity(p.name(), p.number());
            pe.setId((long) p.id());
            return pe;
        }).toList();
        testCourseEntity.setPartants(partantEntities);
        
        // Mock event publisher to return completed CompletableFuture
        when(courseEventPublisher.publishCourseCreated(any())).thenReturn(CompletableFuture.completedFuture(null));
        when(courseEventPublisher.publishCourseDeleted(any())).thenReturn(CompletableFuture.completedFuture(null));
    }

    @Test
    void shouldFindAllCourses() {
        // Given
        List<CourseEntity> courseEntities = List.of(testCourseEntity);
        List<CourseRecord> courseRecords = List.of(testCourseRecord);

        when(courseJpaRepository.findAll()).thenReturn(courseEntities);
        when(courseMapper.toRecordList(courseEntities)).thenReturn(courseRecords);

        // When
        List<CourseEntity> entities = pmuCourseService.findAll();
        List<CourseRecord> result = courseMapper.toRecordList(entities);

        // Then
        assertEquals(courseRecords, result);
        verify(courseJpaRepository).findAll();
        verify(courseMapper).toRecordList(courseEntities);
    }

    @Test
    void shouldSaveCourse() {
        // Given
        when(courseMapper.toEntity(testCourseRecord)).thenReturn(testCourseEntity);
        when(courseJpaRepository.save(testCourseEntity)).thenReturn(testCourseEntity);
        when(courseMapper.toRecord(testCourseEntity)).thenReturn(testCourseRecord);

        // When
        CourseEntity savedEntity = pmuCourseService.save(testCourseRecord);

        // Then
        assertEquals(testCourseEntity, savedEntity);
        verify(courseMapper).toEntity(testCourseRecord);
        verify(courseJpaRepository).save(testCourseEntity);
        verify(courseEventPublisher).publishCourseCreated(testCourseRecord);
    }

    @Test
    void shouldSaveEvent() {
        // Given
        when(courseMapper.toEntity(testCourseRecord)).thenReturn(testCourseEntity);
        when(courseJpaRepository.save(testCourseEntity)).thenReturn(testCourseEntity);
        when(courseMapper.toRecord(testCourseEntity)).thenReturn(testCourseRecord);

        // When
        CourseEntity savedEntity = pmuCourseService.saveEvent(testCourseRecord);

        // Then
        assertEquals(testCourseEntity, savedEntity);
        verify(courseMapper).toEntity(testCourseRecord);
        verify(courseJpaRepository).save(testCourseEntity);
        verify(courseEventPublisher).publishCourseCreated(testCourseRecord);
    }

    @Test
    void shouldDeleteCourseById() {
        // Given
        Long courseId = 1L;
        when(courseJpaRepository.existsById(courseId)).thenReturn(true);

        // When
        pmuCourseService.deleteById(courseId);

        // Then
        verify(courseJpaRepository).existsById(courseId);
        verify(courseJpaRepository).deleteById(courseId);
        verify(courseEventPublisher).publishCourseDeleted(courseId);
    }

    @Test
    void shouldFindCoursesByName() {
        // Given
        String courseName = "Test Course";
        List<CourseEntity> courseEntities = List.of(testCourseEntity);
        List<CourseRecord> courseRecords = List.of(testCourseRecord);

        when(courseJpaRepository.findByName(courseName)).thenReturn(courseEntities);
        when(courseMapper.toRecordList(courseEntities)).thenReturn(courseRecords);

        // When
        List<CourseEntity> entities = pmuCourseService.findByName(courseName);
        List<CourseRecord> result = courseMapper.toRecordList(entities);

        // Then
        assertEquals(courseRecords, result);
        verify(courseJpaRepository).findByName(courseName);
        verify(courseMapper).toRecordList(courseEntities);
    }

    @Test
    void shouldReturnTrueWhenCourseIsEligibleForBetting() {
        // Given
        Long courseId = 1L;
        // Create a course with 5+ partants to be eligible
        List<PartantRecord> eligiblePartants = List.of(
            new PartantRecord(1, "Horse 1", 1),
            new PartantRecord(2, "Horse 2", 2),
            new PartantRecord(3, "Horse 3", 3),
            new PartantRecord(4, "Horse 4", 4),
            new PartantRecord(5, "Horse 5", 5)
        );
        CourseEntity eligibleCourseEntity = new CourseEntity("Test Course", 100, LocalDate.now().plusDays(10));
        eligibleCourseEntity.setPartants(eligiblePartants.stream().map(p -> {
            PartantEntity pe = new PartantEntity(p.name(), p.number());
            pe.setId((long) p.id());
            return pe;
        }).toList());
        
        when(courseJpaRepository.findById(courseId)).thenReturn(Optional.of(eligibleCourseEntity));

        // When
        boolean result = pmuCourseService.isEligibleForBetting(courseId);

        // Then
        assertTrue(result);
        verify(courseJpaRepository).findById(courseId);
    }

    @Test
    void shouldReturnFalseWhenCourseIsNotEligibleForBetting() {
        // Given
        Long courseId = 1L;
        when(courseJpaRepository.findById(courseId)).thenReturn(Optional.of(testCourseEntity));

        // When
        boolean result = pmuCourseService.isEligibleForBetting(courseId);

        // Then
        assertFalse(result);
        verify(courseJpaRepository).findById(courseId);
    }

    @Test
    void shouldCalculateCourseDifficulty() {
        // Given
        Long courseId = 1L;
        int expectedDifficulty = 15; // 3 partants * 5 = 15
        when(courseJpaRepository.findById(courseId)).thenReturn(Optional.of(testCourseEntity));

        // When
        int result = pmuCourseService.calculateCourseDifficulty(courseId);

        // Then
        assertEquals(expectedDifficulty, result);
        verify(courseJpaRepository).findById(courseId);
    }

    @Test
    void shouldThrowExceptionWhenCourseNotFoundForEligibilityCheck() {
        // Given
        Long courseId = 1L;
        when(courseJpaRepository.findById(courseId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> pmuCourseService.isEligibleForBetting(courseId));
        assertEquals("Course with identifier '1' was not found", exception.getMessage());

        verify(courseJpaRepository).findById(courseId);
    }

    @Test
    void shouldThrowExceptionWhenCourseNotFoundForDifficultyCalculation() {
        // Given
        Long courseId = 1L;
        when(courseJpaRepository.findById(courseId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> pmuCourseService.calculateCourseDifficulty(courseId));
        assertEquals("Course with identifier '1' was not found", exception.getMessage());

        verify(courseJpaRepository).findById(courseId);
    }
}
