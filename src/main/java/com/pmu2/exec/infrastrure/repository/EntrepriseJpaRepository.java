package com.pmu2.exec.infrastrure.repository;


import com.pmu2.exec.infrastrure.dao.EntrepriseJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntrepriseJpaRepository extends JpaRepository<EntrepriseJpaEntity, Long> {
}
