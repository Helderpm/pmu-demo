package com.pmu2.exec.infrastructure.dao;

import jakarta.persistence.*;
import lombok.*;
import java.util.Objects;

@Entity
@Table(name = "personne_physique")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PersonnePhysiqueJpaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long personneId;
    
    private String nom;
    private String prenom;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        PersonnePhysiqueJpaEntity that = (PersonnePhysiqueJpaEntity) o;
        
        // Use business key for equality if possible, otherwise use ID with null safety
        if (personneId != null && that.personneId != null) {
            return Objects.equals(personneId, that.personneId);
        }
        
        // For transient entities, compare business fields
        return Objects.equals(nom, that.nom) &&
               Objects.equals(prenom, that.prenom);
    }

    @Override
    public int hashCode() {
        // Use business key for hash code if possible, otherwise use ID with null safety
        if (personneId != null) {
            return Objects.hash(personneId);
        }
        
        // For transient entities, hash business fields
        return Objects.hash(nom, prenom);
    }
}

