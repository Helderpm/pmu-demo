package com.pmu2.exec.infrastrure.inbound;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartantJpaRepository extends JpaRepository<PartantEntity, Long> {

    List<PartantEntity> findByName(String name);
}
