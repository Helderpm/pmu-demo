package com.pmu2.exec.infrastrure.db.sql;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartantJpaRepository extends JpaRepository<PartantEntity, Long> {

    List<PartantEntity> findByName(String name);
}
