package com.pmu2.exec.infrastrure.db.sql;


import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * A repository interface for managing {@link CourseEntity} entities in the database.
 * This interface extends {@link JpaRepository} and provides additional methods for querying {@link CourseEntity} entities.
 *
 * @author HelderPM
 */
@Repository
@Transactional
public interface CourseJpaRepository extends JpaRepository<CourseEntity, Long> {

    /**
     * Finds {@link CourseEntity} entities by their name.
     *
     * @param course The name of the course to search for.
     * @return A list of {@link CourseEntity} entities that match the given name.
     */
    List<CourseEntity> findByName(String course);

}
