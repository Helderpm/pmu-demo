package com.pmu2.exec.infrastrure.inbound;


import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface CourseJpaRepository extends JpaRepository<CourseEntity, Long> {
    List<CourseEntity> findByName(String course);

}
