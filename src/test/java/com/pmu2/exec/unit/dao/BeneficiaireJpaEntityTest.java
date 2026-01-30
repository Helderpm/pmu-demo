package com.pmu2.exec.unit.dao;

import com.pmu2.exec.infrastructure.dao.BeneficiaireJpaEntity;
import com.pmu2.exec.infrastructure.dao.EntrepriseJpaEntity;
import com.pmu2.exec.infrastructure.dao.PersonnePhysiqueJpaEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BeneficiaireJpaEntityTest {

    @Test
    void shouldCreateBeneficiaireWithAllArgsConstructor() {
        // Given
        EntrepriseJpaEntity entrepriseMere = new EntrepriseJpaEntity();
        entrepriseMere.setEntrepriseId(1L);
        entrepriseMere.setNom("Entreprise Mère");
        
        PersonnePhysiqueJpaEntity personnePhysique = new PersonnePhysiqueJpaEntity();
        personnePhysique.setPersonneId(2L);
        personnePhysique.setNom("Dupont");
        personnePhysique.setPrenom("Jean");
        
        EntrepriseJpaEntity entrepriseFille = new EntrepriseJpaEntity();
        entrepriseFille.setEntrepriseId(3L);
        entrepriseFille.setNom("Entreprise Fille");
        
        // When
        BeneficiaireJpaEntity beneficiaire = new BeneficiaireJpaEntity(
            100L, entrepriseMere, personnePhysique, entrepriseFille, 75
        );
        
        // Then
        assertEquals(100L, beneficiaire.getBeneficiaireId());
        assertEquals(entrepriseMere, beneficiaire.getEntrepriseMere());
        assertEquals(personnePhysique, beneficiaire.getPersonnePhysique());
        assertEquals(entrepriseFille, beneficiaire.getEntrepriseFille());
        assertEquals(75, beneficiaire.getPourcentageDetention());
    }

    @Test
    void shouldCreateBeneficiaireWithNoArgsConstructor() {
        // When
        BeneficiaireJpaEntity beneficiaire = new BeneficiaireJpaEntity();
        
        // Then
        assertNull(beneficiaire.getBeneficiaireId());
        assertNull(beneficiaire.getEntrepriseMere());
        assertNull(beneficiaire.getPersonnePhysique());
        assertNull(beneficiaire.getEntrepriseFille());
        assertEquals(0, beneficiaire.getPourcentageDetention());
    }

    @Test
    void shouldTestEqualsWithSameId() {
        // Given
        BeneficiaireJpaEntity beneficiaire1 = new BeneficiaireJpaEntity();
        beneficiaire1.setBeneficiaireId(100L);
        
        BeneficiaireJpaEntity beneficiaire2 = new BeneficiaireJpaEntity();
        beneficiaire2.setBeneficiaireId(100L);
        
        // When & Then
        assertEquals(beneficiaire1, beneficiaire2);
        assertEquals(beneficiaire1.hashCode(), beneficiaire2.hashCode());
    }

    @Test
    void shouldTestEqualsWithDifferentId() {
        // Given
        BeneficiaireJpaEntity beneficiaire1 = new BeneficiaireJpaEntity();
        beneficiaire1.setBeneficiaireId(100L);
        
        BeneficiaireJpaEntity beneficiaire2 = new BeneficiaireJpaEntity();
        beneficiaire2.setBeneficiaireId(200L);
        
        // When & Then
        assertNotEquals(beneficiaire1, beneficiaire2);
    }

    @Test
    void shouldTestEqualsForTransientEntities() {
        // Given
        EntrepriseJpaEntity entrepriseMere = new EntrepriseJpaEntity();
        entrepriseMere.setNom("Entreprise Mère");
        
        PersonnePhysiqueJpaEntity personnePhysique = new PersonnePhysiqueJpaEntity();
        personnePhysique.setNom("Dupont");
        personnePhysique.setPrenom("Jean");
        
        BeneficiaireJpaEntity beneficiaire1 = new BeneficiaireJpaEntity();
        beneficiaire1.setEntrepriseMere(entrepriseMere);
        beneficiaire1.setPersonnePhysique(personnePhysique);
        beneficiaire1.setPourcentageDetention(75);
        
        BeneficiaireJpaEntity beneficiaire2 = new BeneficiaireJpaEntity();
        beneficiaire2.setEntrepriseMere(entrepriseMere);
        beneficiaire2.setPersonnePhysique(personnePhysique);
        beneficiaire2.setPourcentageDetention(75);
        
        // When & Then
        assertEquals(beneficiaire1, beneficiaire2);
        assertEquals(beneficiaire1.hashCode(), beneficiaire2.hashCode());
    }

    @Test
    void shouldTestEqualsWithNull() {
        // Given
        BeneficiaireJpaEntity beneficiaire = new BeneficiaireJpaEntity();
        
        // When & Then
        assertNotEquals(null, beneficiaire);
    }

    @Test
    void shouldTestEqualsWithDifferentClass() {
        // Given
        BeneficiaireJpaEntity beneficiaire = new BeneficiaireJpaEntity();
        
        // When & Then
        assertNotEquals("string", beneficiaire);
    }

    @Test
    void shouldTestEqualsWithSameObject() {
        // Given
        BeneficiaireJpaEntity beneficiaire = new BeneficiaireJpaEntity();
        
        // When & Then
        assertEquals(beneficiaire, beneficiaire);
    }

    @Test
    void shouldTestHashCodeForTransientEntities() {
        // Given
        BeneficiaireJpaEntity beneficiaire = new BeneficiaireJpaEntity();
        beneficiaire.setPourcentageDetention(50);
        
        // When
        int hashCode = beneficiaire.hashCode();
        
        // Then
        assertNotEquals(0, hashCode);
    }

    @Test
    void shouldTestSettersAndGetters() {
        // Given
        BeneficiaireJpaEntity beneficiaire = new BeneficiaireJpaEntity();
        EntrepriseJpaEntity entreprise = new EntrepriseJpaEntity();
        PersonnePhysiqueJpaEntity personne = new PersonnePhysiqueJpaEntity();
        
        // When
        beneficiaire.setBeneficiaireId(123L);
        beneficiaire.setEntrepriseMere(entreprise);
        beneficiaire.setPersonnePhysique(personne);
        beneficiaire.setEntrepriseFille(entreprise);
        beneficiaire.setPourcentageDetention(25);
        
        // Then
        assertEquals(123L, beneficiaire.getBeneficiaireId());
        assertEquals(entreprise, beneficiaire.getEntrepriseMere());
        assertEquals(personne, beneficiaire.getPersonnePhysique());
        assertEquals(entreprise, beneficiaire.getEntrepriseFille());
        assertEquals(25, beneficiaire.getPourcentageDetention());
    }
}
