package com.pmu2.exec.infrastrure.repository;


import com.pmu2.exec.infrastrure.dao.BeneficiaireJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BeneficiaireJpaRepository extends JpaRepository<BeneficiaireJpaEntity, Long> {

    @Query("SELECT b FROM BeneficiaireJpaEntity b WHERE b.entrepriseMere.entrepriseId = :entrepriseMereId")
    List<BeneficiaireJpaEntity> findByEntrepriseMere_EntrepriseId(@Param("entrepriseMereId") Long entrepriseMereId);
    
}
