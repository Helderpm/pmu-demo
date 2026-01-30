package com.pmu2.exec.unit.validation;

import com.pmu2.exec.exception.NotFoundException;
import com.pmu2.exec.exception.SimpleValidationException;
import com.pmu2.exec.infrastructure.db.sql.PartantEntity;
import com.pmu2.exec.infrastructure.db.sql.PartantJpaRepository;
import com.pmu2.exec.validation.PartantValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PartantValidatorTest {

    @Mock
    private PartantJpaRepository partantJpaRepository;

    @InjectMocks
    private PartantValidator partantValidator;

    private PartantEntity testPartantEntity;

    @BeforeEach
    void setUp() {
        testPartantEntity = new PartantEntity("Thunder Bolt", 5);
        testPartantEntity.setId(1L);
    }

    @Test
    void shouldValidateExistingPartantsList() {
        // Given
        List<PartantEntity> partants = List.of(testPartantEntity);
        when(partantJpaRepository.existsByName("Thunder Bolt")).thenReturn(true);

        // When & Then
        assertDoesNotThrow(() -> partantValidator.validatePartantsExist(partants));
        verify(partantJpaRepository).existsByName("Thunder Bolt");
    }

    @Test
    void shouldValidateEmptyPartantsList() {
        // Given
        List<PartantEntity> partants = List.of();

        // When & Then
        assertDoesNotThrow(() -> partantValidator.validatePartantsExist(partants));
        verifyNoInteractions(partantJpaRepository);
    }

    @Test
    void shouldValidateNullPartantsList() {
        // Given
        List<PartantEntity> partants = null;

        // When & Then
        assertDoesNotThrow(() -> partantValidator.validatePartantsExist(partants));
        verifyNoInteractions(partantJpaRepository);
    }

    @Test
    void shouldThrowExceptionWhenPartantNotFoundByName() {
        // Given
        List<PartantEntity> partants = List.of(testPartantEntity);
        when(partantJpaRepository.existsByName("Thunder Bolt")).thenReturn(false);

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> partantValidator.validatePartantsExist(partants));
        assertEquals("Thunder Bolt", exception.getPartantName());
        verify(partantJpaRepository).existsByName("Thunder Bolt");
    }

    @Test
    void shouldValidateExistingPartantByName() {
        // Given
        String partantName = "Thunder Bolt";
        when(partantJpaRepository.existsByName(partantName)).thenReturn(true);

        // When & Then
        assertDoesNotThrow(() -> partantValidator.validatePartantExistsByName(partantName));
        verify(partantJpaRepository).existsByName(partantName);
    }

    @Test
    void shouldThrowExceptionWhenPartantNameIsNull() {
        // Given
        String partantName = null;

        // When & Then
        SimpleValidationException exception = assertThrows(SimpleValidationException.class,
            () -> partantValidator.validatePartantExistsByName(partantName));
        assertEquals("Partant name cannot be null or empty", exception.getMessage());
        verifyNoInteractions(partantJpaRepository);
    }

    @Test
    void shouldThrowExceptionWhenPartantNameIsEmpty() {
        // Given
        String partantName = "   ";

        // When & Then
        SimpleValidationException exception = assertThrows(SimpleValidationException.class,
            () -> partantValidator.validatePartantExistsByName(partantName));
        assertEquals("Partant name cannot be null or empty", exception.getMessage());
        verifyNoInteractions(partantJpaRepository);
    }

    @Test
    void shouldThrowExceptionWhenPartantNotFoundByNameForValidation() {
        // Given
        String partantName = "Unknown Horse";
        when(partantJpaRepository.existsByName(partantName)).thenReturn(false);

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> partantValidator.validatePartantExistsByName(partantName));
        assertEquals(partantName, exception.getPartantName());
        verify(partantJpaRepository).existsByName(partantName);
    }

    @Test
    void shouldValidateExistingPartantById() {
        // Given
        Long partantId = 1L;
        when(partantJpaRepository.existsById(partantId)).thenReturn(true);

        // When & Then
        assertDoesNotThrow(() -> partantValidator.validatePartantExistsById(partantId));
        verify(partantJpaRepository).existsById(partantId);
    }

    @Test
    void shouldThrowExceptionWhenPartantIdIsNull() {
        // Given
        Long partantId = null;

        // When & Then
        SimpleValidationException exception = assertThrows(SimpleValidationException.class,
            () -> partantValidator.validatePartantExistsById(partantId));
        assertEquals("partantId cannot be null", exception.getMessage());
        verifyNoInteractions(partantJpaRepository);
    }

    @Test
    void shouldThrowExceptionWhenPartantNotFoundById() {
        // Given
        Long partantId = 1L;
        when(partantJpaRepository.existsById(partantId)).thenReturn(false);

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> partantValidator.validatePartantExistsById(partantId));
        assertEquals(String.valueOf(partantId), exception.getPartantId());
        verify(partantJpaRepository).existsById(partantId);
    }

    @Test
    void shouldValidateValidPartantEntity() {
        // Given
        PartantEntity partant = testPartantEntity;

        // When & Then
        assertDoesNotThrow(() -> partantValidator.validatePartantEntity(partant));
    }

    @Test
    void shouldThrowExceptionWhenPartantEntityIsNull() {
        // Given
        PartantEntity partant = null;

        // When & Then
        SimpleValidationException exception = assertThrows(SimpleValidationException.class,
            () -> partantValidator.validatePartantEntity(partant));
        assertEquals("Partant entity cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPartantEntityNameIsNull() {
        // Given
        PartantEntity partant = new PartantEntity(null, 5);

        // When & Then
        SimpleValidationException exception = assertThrows(SimpleValidationException.class,
            () -> partantValidator.validatePartantEntity(partant));
        assertEquals("Partant name cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPartantEntityNameIsEmpty() {
        // Given
        PartantEntity partant = new PartantEntity("   ", 5);

        // When & Then
        SimpleValidationException exception = assertThrows(SimpleValidationException.class,
            () -> partantValidator.validatePartantEntity(partant));
        assertEquals("Partant name cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPartantEntityNumberIsNotPositive() {
        // Given
        PartantEntity partant = new PartantEntity("Thunder Bolt", 0);

        // When & Then
        SimpleValidationException exception = assertThrows(SimpleValidationException.class,
            () -> partantValidator.validatePartantEntity(partant));
        assertEquals("Partant number must be positive", exception.getMessage());
    }

    @Test
    void shouldValidatePartantEntityWithPositiveNumber() {
        // Given
        PartantEntity partant = new PartantEntity("Thunder Bolt", 10);

        // When & Then
        assertDoesNotThrow(() -> partantValidator.validatePartantEntity(partant));
    }

    @Test
    void shouldValidatePartantIntegrity() {
        // Given
        PartantEntity partant = testPartantEntity;

        // When & Then
        assertDoesNotThrow(() -> partantValidator.validatePartantIntegrity(partant));
    }

    @Test
    void shouldThrowExceptionWhenPartantIntegrityIsNull() {
        // Given
        PartantEntity partant = null;

        // When & Then
        SimpleValidationException exception = assertThrows(SimpleValidationException.class,
            () -> partantValidator.validatePartantIntegrity(partant));
        assertEquals("partant cannot be null", exception.getMessage());
    }

    @Test
    void shouldValidateUniquePartantName() {
        // Given
        String partantName = "Unique Horse";
        when(partantJpaRepository.findByName(partantName)).thenReturn(List.of());

        // When & Then
        assertDoesNotThrow(() -> partantValidator.validatePartantNameUnique(partantName));
        verify(partantJpaRepository).findByName(partantName);
    }

    @Test
    void shouldThrowExceptionWhenPartantNameIsNotUnique() {
        // Given
        String partantName = "Existing Horse";
        PartantEntity existingPartant = new PartantEntity("Existing Horse", 1);
        existingPartant.setId(1L);
        when(partantJpaRepository.findByName(partantName)).thenReturn(List.of(existingPartant));

        // When & Then
        SimpleValidationException exception = assertThrows(SimpleValidationException.class,
            () -> partantValidator.validatePartantNameUnique(partantName));
        assertEquals("Partant with name '" + partantName + "' already exists", exception.getMessage());
        verify(partantJpaRepository).findByName(partantName);
    }

    @Test
    void shouldThrowExceptionWhenUniquePartantNameIsNull() {
        // Given
        String partantName = null;

        // When & Then
        SimpleValidationException exception = assertThrows(SimpleValidationException.class,
            () -> partantValidator.validatePartantNameUnique(partantName));
        assertEquals("partantName", exception.getMessage());
        verifyNoInteractions(partantJpaRepository);
    }

    @Test
    void shouldValidateUniquePartantNameForUpdate() {
        // Given
        String partantName = "Updated Horse";
        Long excludePartantId = 1L;
        PartantEntity otherPartant = new PartantEntity("Updated Horse", 1);
        otherPartant.setId(1L); // Same ID as excluded - should pass validation
        when(partantJpaRepository.findByName(partantName)).thenReturn(List.of(otherPartant));

        // When & Then
        assertDoesNotThrow(() -> partantValidator.validatePartantNameUniqueForUpdate(partantName, excludePartantId));
        verify(partantJpaRepository).findByName(partantName);
    }

    @Test
    void shouldThrowExceptionWhenPartantNameIsNotUniqueForUpdate() {
        // Given
        String partantName = "Existing Horse";
        Long excludePartantId = 1L;
        PartantEntity existingPartant = new PartantEntity("Existing Horse", 1);
        existingPartant.setId(3L); // Different ID
        when(partantJpaRepository.findByName(partantName)).thenReturn(List.of(existingPartant));

        // When & Then
        SimpleValidationException exception = assertThrows(SimpleValidationException.class,
            () -> partantValidator.validatePartantNameUniqueForUpdate(partantName, excludePartantId));
        assertEquals("Partant with name '" + partantName + "' already exists", exception.getMessage());
        verify(partantJpaRepository).findByName(partantName);
    }

    @Test
    void shouldThrowExceptionWhenUniquePartantNameForUpdateIsNull() {
        // Given
        String partantName = null;

        // When & Then
        SimpleValidationException exception = assertThrows(SimpleValidationException.class,
            () -> partantValidator.validatePartantNameUniqueForUpdate(partantName, 1L));
        assertEquals("partantName", exception.getMessage());
        verifyNoInteractions(partantJpaRepository);
    }

    @Test
    void shouldThrowExceptionWhenExcludePartantIdIsNull() {
        // Given
        String partantName = "Test Horse";
        Long excludePartantId = null;

        // When & Then
        SimpleValidationException exception = assertThrows(SimpleValidationException.class,
            () -> partantValidator.validatePartantNameUniqueForUpdate(partantName, excludePartantId));
        assertEquals("excludePartantId cannot be null", exception.getMessage());
        verifyNoInteractions(partantJpaRepository);
    }

    @Test
    void shouldValidateUniquePartantNumbers() {
        // Given
        PartantEntity partant1 = new PartantEntity("Horse 1", 1);
        PartantEntity partant2 = new PartantEntity("Horse 2", 2);
        PartantEntity partant3 = new PartantEntity("Horse 3", 3);
        List<PartantEntity> partants = List.of(partant1, partant2, partant3);

        // When & Then
        assertDoesNotThrow(() -> partantValidator.validatePartantNumbersUnique(partants));
    }

    @Test
    void shouldThrowExceptionWhenPartantNumbersAreNotUnique() {
        // Given
        PartantEntity partant1 = new PartantEntity("Horse 1", 1);
        PartantEntity partant2 = new PartantEntity("Horse 2", 2);
        PartantEntity partant3 = new PartantEntity("Horse 3", 1); // Duplicate number
        List<PartantEntity> partants = List.of(partant1, partant2, partant3);

        // When & Then
        SimpleValidationException exception = assertThrows(SimpleValidationException.class,
            () -> partantValidator.validatePartantNumbersUnique(partants));
        assertEquals("Duplicate partant number found: 1", exception.getMessage());
    }

    @Test
    void shouldValidateEmptyPartantNumbersList() {
        // Given
        List<PartantEntity> partants = List.of();

        // When & Then
        assertDoesNotThrow(() -> partantValidator.validatePartantNumbersUnique(partants));
    }

    @Test
    void shouldValidateNullPartantNumbersList() {
        // Given
        List<PartantEntity> partants = null;

        // When & Then
        assertDoesNotThrow(() -> partantValidator.validatePartantNumbersUnique(partants));
    }
}
