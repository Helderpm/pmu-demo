package com.pmu2.exec.unit.dao;

import com.pmu2.exec.infrastructure.dao.PersonnePhysiqueJpaEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PersonnePhysiqueJpaEntityTest {

    @Test
    void shouldCreatePersonnePhysiqueWithAllArgsConstructor() {
        // When
        PersonnePhysiqueJpaEntity personne = new PersonnePhysiqueJpaEntity(100L, "Dupont", "Jean");
        
        // Then
        assertEquals(100L, personne.getPersonneId());
        assertEquals("Dupont", personne.getNom());
        assertEquals("Jean", personne.getPrenom());
    }

    @Test
    void shouldCreatePersonnePhysiqueWithNoArgsConstructor() {
        // When
        PersonnePhysiqueJpaEntity personne = new PersonnePhysiqueJpaEntity();
        
        // Then
        assertNull(personne.getPersonneId());
        assertNull(personne.getNom());
        assertNull(personne.getPrenom());
    }

    @Test
    void shouldTestEqualsWithSameId() {
        // Given
        PersonnePhysiqueJpaEntity personne1 = new PersonnePhysiqueJpaEntity();
        personne1.setPersonneId(100L);
        
        PersonnePhysiqueJpaEntity personne2 = new PersonnePhysiqueJpaEntity();
        personne2.setPersonneId(100L);
        
        // When & Then
        assertEquals(personne1, personne2);
        assertEquals(personne1.hashCode(), personne2.hashCode());
    }

    @Test
    void shouldTestEqualsWithDifferentId() {
        // Given
        PersonnePhysiqueJpaEntity personne1 = new PersonnePhysiqueJpaEntity();
        personne1.setPersonneId(100L);
        
        PersonnePhysiqueJpaEntity personne2 = new PersonnePhysiqueJpaEntity();
        personne2.setPersonneId(200L);
        
        // When & Then
        assertNotEquals(personne1, personne2);
    }

    @Test
    void shouldTestEqualsForTransientEntities() {
        // Given
        PersonnePhysiqueJpaEntity personne1 = new PersonnePhysiqueJpaEntity();
        personne1.setNom("Dupont");
        personne1.setPrenom("Jean");
        
        PersonnePhysiqueJpaEntity personne2 = new PersonnePhysiqueJpaEntity();
        personne2.setNom("Dupont");
        personne2.setPrenom("Jean");
        
        // When & Then
        assertEquals(personne1, personne2);
        assertEquals(personne1.hashCode(), personne2.hashCode());
    }

    @Test
    void shouldTestEqualsForTransientEntitiesWithDifferentName() {
        // Given
        PersonnePhysiqueJpaEntity personne1 = new PersonnePhysiqueJpaEntity();
        personne1.setNom("Dupont");
        personne1.setPrenom("Jean");
        
        PersonnePhysiqueJpaEntity personne2 = new PersonnePhysiqueJpaEntity();
        personne2.setNom("Durand");
        personne2.setPrenom("Jean");
        
        // When & Then
        assertNotEquals(personne1, personne2);
    }

    @Test
    void shouldTestEqualsForTransientEntitiesWithDifferentPrenom() {
        // Given
        PersonnePhysiqueJpaEntity personne1 = new PersonnePhysiqueJpaEntity();
        personne1.setNom("Dupont");
        personne1.setPrenom("Jean");
        
        PersonnePhysiqueJpaEntity personne2 = new PersonnePhysiqueJpaEntity();
        personne2.setNom("Dupont");
        personne2.setPrenom("Pierre");
        
        // When & Then
        assertNotEquals(personne1, personne2);
    }

    @Test
    void shouldTestEqualsWithNull() {
        // Given
        PersonnePhysiqueJpaEntity personne = new PersonnePhysiqueJpaEntity();
        
        // When & Then
        assertNotEquals(null, personne);
    }

    @Test
    void shouldTestEqualsWithDifferentClass() {
        // Given
        PersonnePhysiqueJpaEntity personne = new PersonnePhysiqueJpaEntity();
        
        // When & Then
        assertNotEquals("string", personne);
    }

    @Test
    void shouldTestEqualsWithSameObject() {
        // Given
        PersonnePhysiqueJpaEntity personne = new PersonnePhysiqueJpaEntity();
        
        // When & Then
        assertEquals(personne, personne);
    }

    @Test
    void shouldTestHashCodeWithId() {
        // Given
        PersonnePhysiqueJpaEntity personne = new PersonnePhysiqueJpaEntity();
        personne.setPersonneId(100L);
        
        // When
        int hashCode = personne.hashCode();
        
        // Then
        assertEquals(131, hashCode);
    }

    @Test
    void shouldTestHashCodeForTransientEntities() {
        // Given
        PersonnePhysiqueJpaEntity personne = new PersonnePhysiqueJpaEntity();
        personne.setNom("Test");
        personne.setPrenom("Test");
        
        // When
        int hashCode = personne.hashCode();
        
        // Then
        assertNotEquals(0, hashCode);
    }

    @Test
    void shouldTestSettersAndGetters() {
        // Given
        PersonnePhysiqueJpaEntity personne = new PersonnePhysiqueJpaEntity();
        
        // When
        personne.setPersonneId(123L);
        personne.setNom("Martin");
        personne.setPrenom("Paul");
        
        // Then
        assertEquals(123L, personne.getPersonneId());
        assertEquals("Martin", personne.getNom());
        assertEquals("Paul", personne.getPrenom());
    }
}
