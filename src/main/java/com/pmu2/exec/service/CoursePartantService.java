package com.pmu2.exec.service;

import com.pmu2.exec.exception.CourseNotFoundException;
import com.pmu2.exec.exception.PartantNotFoundException;
import com.pmu2.exec.infrastrure.db.sql.CourseEntity;
import com.pmu2.exec.infrastrure.db.sql.CourseJpaRepository;
import com.pmu2.exec.infrastrure.db.sql.PartantEntity;
import com.pmu2.exec.infrastrure.db.sql.PartantJpaRepository;
import com.pmu2.exec.validation.CourseValidator;
import com.pmu2.exec.validation.PartantValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service responsible for managing relationships between courses and partants.
 * This service removes circular dependencies by handling course-partant operations centrally.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CoursePartantService {

    private final CourseJpaRepository courseJpaRepository;
    private final PartantJpaRepository partantJpaRepository;
    private final CourseValidator courseValidator;
    private final PartantValidator partantValidator;

    /**
     * Validates that all partants exist in the database.
     *
     * @param partants list of partants to validate
     * @throws PartantNotFoundException if any partant is not found
     */
    public void validatePartantsExist(List<PartantEntity> partants) {
        partantValidator.validatePartantsExist(partants);
    }

    /**
     * Removes a partant from its associated course and deletes the partant.
     *
     * @param partantId the ID of the partant to remove
     * @throws PartantNotFoundException if the partant is not found
     */
    public void removePartantFromCourseAndDelete(Long partantId) {
        partantValidator.validatePartantExistsById(partantId);
        
        PartantEntity partant = partantJpaRepository.findById(partantId)
                .orElseThrow(() -> new PartantNotFoundException(partantId));
        
        // Find all courses that reference this partant and remove the reference
        List<CourseEntity> courses = courseJpaRepository.findAll();
        for (CourseEntity course : courses) {
            if (course.getPartants() != null) {
                course.getPartants().removeIf(p -> p.getId().equals(partantId));
                courseJpaRepository.save(course);
            }
        }

        // Delete the partant
        partantJpaRepository.deleteById(partantId);
        log.info("Successfully removed partant with ID: {} from all courses and deleted it", partantId);
    }

    /**
     * Finds all partants associated with a specific course.
     *
     * @param courseId the ID of the course
     * @return list of partants for the course
     * @throws CourseNotFoundException if the course is not found
     */
    public List<PartantEntity> findPartantsByCourse(Long courseId) {
        courseValidator.validateCourseExistsById(courseId);
        
        CourseEntity course = courseJpaRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));
        
        return course.getPartants() != null ? course.getPartants() : List.of();
    }
}
