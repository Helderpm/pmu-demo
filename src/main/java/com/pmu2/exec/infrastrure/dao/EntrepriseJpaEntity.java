package com.pmu2.exec.infrastrure.dao;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "entreprise")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "entrepriseId")
public class EntrepriseJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long entrepriseId;
    
    private String nom;
    private String siret;
}
