package com.pmu2.exec.infrastrure.dao;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "personne_physique")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "personneId")
public class PersonnePhysiqueJpaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long personneId;
    
    private String nom;
    private String prenom;
}
