package com.pmu2.exec.infrastrure.db.sql;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This interface represents a JPA repository for managing {@link PartantEntity} entities.
 * It extends Spring Data JPA's {@link JpaRepository} interface, providing basic CRUD operations
 * and additional query methods.
 *
 * @author HelderPM
 */
@Repository
public interface PartantJpaRepository extends JpaRepository<PartantEntity, Long> {

    /**
     * Finds all {@link PartantEntity} entities with the given name.
     *
     * @param name the name to search for
     * @return a list of {@link PartantEntity} entities that match the given name
     */
    List<PartantEntity> findByName(String name);
}
