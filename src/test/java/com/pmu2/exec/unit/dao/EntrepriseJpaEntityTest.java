package com.pmu2.exec.unit.dao;

import com.pmu2.exec.infrastructure.dao.EntrepriseJpaEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EntrepriseJpaEntityTest {

    @Test
    void shouldCreateEntrepriseWithAllArgsConstructor() {
        // When
        EntrepriseJpaEntity entreprise = new EntrepriseJpaEntity(100L, "Test Entreprise", "12345678901234");
        
        // Then
        assertEquals(100L, entreprise.getEntrepriseId());
        assertEquals("Test Entreprise", entreprise.getNom());
        assertEquals("12345678901234", entreprise.getSiret());
    }

    @Test
    void shouldCreateEntrepriseWithNoArgsConstructor() {
        // When
        EntrepriseJpaEntity entreprise = new EntrepriseJpaEntity();
        
        // Then
        assertNull(entreprise.getEntrepriseId());
        assertNull(entreprise.getNom());
        assertNull(entreprise.getSiret());
    }

    @Test
    void shouldTestEqualsWithSameId() {
        // Given
        EntrepriseJpaEntity entreprise1 = new EntrepriseJpaEntity();
        entreprise1.setEntrepriseId(100L);
        
        EntrepriseJpaEntity entreprise2 = new EntrepriseJpaEntity();
        entreprise2.setEntrepriseId(100L);
        
        // When & Then
        assertEquals(entreprise1, entreprise2);
        assertEquals(entreprise1.hashCode(), entreprise2.hashCode());
    }

    @Test
    void shouldTestEqualsWithDifferentId() {
        // Given
        EntrepriseJpaEntity entreprise1 = new EntrepriseJpaEntity();
        entreprise1.setEntrepriseId(100L);
        
        EntrepriseJpaEntity entreprise2 = new EntrepriseJpaEntity();
        entreprise2.setEntrepriseId(200L);
        
        // When & Then
        assertNotEquals(entreprise1, entreprise2);
    }

    @Test
    void shouldTestEqualsForTransientEntities() {
        // Given
        EntrepriseJpaEntity entreprise1 = new EntrepriseJpaEntity();
        entreprise1.setNom("Test Entreprise");
        entreprise1.setSiret("12345678901234");
        
        EntrepriseJpaEntity entreprise2 = new EntrepriseJpaEntity();
        entreprise2.setNom("Test Entreprise");
        entreprise2.setSiret("12345678901234");
        
        // When & Then
        assertEquals(entreprise1, entreprise2);
        assertEquals(entreprise1.hashCode(), entreprise2.hashCode());
    }

    @Test
    void shouldTestEqualsForTransientEntitiesWithDifferentSiret() {
        // Given
        EntrepriseJpaEntity entreprise1 = new EntrepriseJpaEntity();
        entreprise1.setNom("Test Entreprise");
        entreprise1.setSiret("12345678901234");
        
        EntrepriseJpaEntity entreprise2 = new EntrepriseJpaEntity();
        entreprise2.setNom("Test Entreprise");
        entreprise2.setSiret("98765432109876");
        
        // When & Then
        assertNotEquals(entreprise1, entreprise2);
    }

    @Test
    void shouldTestEqualsWithNull() {
        // Given
        EntrepriseJpaEntity entreprise = new EntrepriseJpaEntity();
        
        // When & Then
        assertNotEquals(null, entreprise);
    }

    @Test
    void shouldTestEqualsWithDifferentClass() {
        // Given
        EntrepriseJpaEntity entreprise = new EntrepriseJpaEntity();
        
        // When & Then
        assertNotEquals("string", entreprise);
    }

    @Test
    void shouldTestEqualsWithSameObject() {
        // Given
        EntrepriseJpaEntity entreprise = new EntrepriseJpaEntity();
        
        // When & Then
        assertEquals(entreprise, entreprise);
    }

    @Test
    void shouldTestHashCodeWithId() {
        // Given
        EntrepriseJpaEntity entreprise = new EntrepriseJpaEntity();
        entreprise.setEntrepriseId(100L);
        
        // When
        int hashCode = entreprise.hashCode();
        
        // Then
        assertEquals(131, hashCode);
    }

    @Test
    void shouldTestHashCodeForTransientEntities() {
        // Given
        EntrepriseJpaEntity entreprise = new EntrepriseJpaEntity();
        entreprise.setNom("Test");
        entreprise.setSiret("123");
        
        // When
        int hashCode = entreprise.hashCode();
        
        // Then
        assertNotEquals(0, hashCode);
    }

    @Test
    void shouldTestSettersAndGetters() {
        // Given
        EntrepriseJpaEntity entreprise = new EntrepriseJpaEntity();
        
        // When
        entreprise.setEntrepriseId(123L);
        entreprise.setNom("New Name");
        entreprise.setSiret("98765432109876");
        
        // Then
        assertEquals(123L, entreprise.getEntrepriseId());
        assertEquals("New Name", entreprise.getNom());
        assertEquals("98765432109876", entreprise.getSiret());
    }
}
