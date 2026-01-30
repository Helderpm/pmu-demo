package com.pmu2.exec.infrastructure.repository;

import com.pmu2.exec.infrastructure.dao.PersonnePhysiqueJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface PersonnePhysiqueJpaRepository extends JpaRepository<PersonnePhysiqueJpaEntity, Long> {
}

