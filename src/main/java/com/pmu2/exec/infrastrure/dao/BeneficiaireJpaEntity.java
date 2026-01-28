package com.pmu2.exec.infrastrure.dao;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "beneficiaire")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "beneficiaireId")

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
}
