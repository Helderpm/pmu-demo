package com.pmu2.exec.infrastructure.dao;

import jakarta.persistence.*;
import lombok.*;
import java.util.Objects;

@Entity
@Table(name = "beneficiaire")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BeneficiaireJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long beneficiaireId;
    
    @ManyToOne
    private EntrepriseJpaEntity entrepriseMere;
    
    @ManyToOne
    private PersonnePhysiqueJpaEntity personnePhysique;
    
    @ManyToOne
    private EntrepriseJpaEntity entrepriseFille;
    
    private int pourcentageDetention;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        BeneficiaireJpaEntity that = (BeneficiaireJpaEntity) o;
        
        // Use business key for equality if possible, otherwise use ID with null safety
        if (beneficiaireId != null && that.beneficiaireId != null) {
            return Objects.equals(beneficiaireId, that.beneficiaireId);
        }
        
        // For transient entities, compare business fields
        return pourcentageDetention == that.pourcentageDetention &&
               Objects.equals(entrepriseMere, that.entrepriseMere) &&
               Objects.equals(personnePhysique, that.personnePhysique) &&
               Objects.equals(entrepriseFille, that.entrepriseFille);
    }

    @Override
    public int hashCode() {
        // Use business key for hash code if possible, otherwise use ID with null safety
        if (beneficiaireId != null) {
            return Objects.hash(beneficiaireId);
        }
        
        // For transient entities, hash business fields
        return Objects.hash(pourcentageDetention, entrepriseMere, personnePhysique, entrepriseFille);
    }
}

