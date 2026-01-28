package com.pmu2.exec.infrastrure.repository;

import com.pmu2.exec.infrastrure.dao.PersonnePhysiqueJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonnePhysiqueJpaRepository extends JpaRepository<PersonnePhysiqueJpaEntity, Long> {
}