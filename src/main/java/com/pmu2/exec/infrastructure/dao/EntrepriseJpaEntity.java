package com.pmu2.exec.infrastructure.dao;

import jakarta.persistence.*;
import lombok.*;
import java.util.Objects;


@Entity
@Table(name = "entreprise")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EntrepriseJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long entrepriseId;
    
    private String nom;
    private String siret;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        EntrepriseJpaEntity that = (EntrepriseJpaEntity) o;
        
        // Use business key for equality if possible, otherwise use ID with null safety
        if (entrepriseId != null && that.entrepriseId != null) {
            return Objects.equals(entrepriseId, that.entrepriseId);
        }
        
        // For transient entities, compare business fields (siret is a natural key)
        return Objects.equals(siret, that.siret) &&
               Objects.equals(nom, that.nom);
    }

    @Override
    public int hashCode() {
        // Use business key for hash code if possible, otherwise use ID with null safety
        if (entrepriseId != null) {
            return Objects.hash(entrepriseId);
        }
        
        // For transient entities, hash business fields (siret is a natural key)
        return Objects.hash(siret, nom);
    }
}

