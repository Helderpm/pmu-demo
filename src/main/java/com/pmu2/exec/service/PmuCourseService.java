package com.pmu2.exec.service;

import com.pmu2.exec.domain.CourseRecord;
import com.pmu2.exec.exception.NotFoundException;
import com.pmu2.exec.infrastructure.db.sql.CourseEntity;
import com.pmu2.exec.infrastructure.db.sql.CourseJpaRepository;
import com.pmu2.exec.service.mapper.CourseMapper;
import com.pmu2.exec.validation.CourseValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Service for managing Course entities.
 * This service handles CRUD operations and business logic for courses.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PmuCourseService {

private static final Logger log = LoggerFactory.getLogger(PmuCourseService.class);
public static final String COURSE = "Course";

private final CourseJpaRepository courseJpaRepository;
    private final CourseMapper courseMapper;
    private final CourseEventPublisher courseEventPublisher;
    private final CourseValidator courseValidator;
    private final CoursePartantService coursePartantService;

    /**
     * Saves a course entity.
     *
     * @param courseRecord the course record to save
     * @return the saved course entity
     */
    public CourseEntity save(CourseRecord courseRecord) {
        log.debug("Saving course: {}", courseRecord.name());
        
        CourseEntity courseEntity = courseMapper.toEntity(courseRecord);
        CourseEntity savedCourse = courseJpaRepository.save(courseEntity);
        
        // Publish course created event
        CompletableFuture<Void> publishFuture = courseEventPublisher.publishCourseCreated(courseRecord);
        publishFuture.join(); // Wait for event to be published
        
        log.info("Successfully saved course with ID: {}", savedCourse.getCourseId());
        return savedCourse;
    }

    /**
     * Finds a course by its ID.
     *
     * @param courseId the ID of the course to find
     * @return an Optional containing the course if found, otherwise empty
     */
    public Optional<CourseEntity> findById(Long courseId) {
        log.debug("Finding course by ID: {}", courseId);
        return courseJpaRepository.findById(courseId);
    }

    /**
     * Finds all courses.
     *
     * @return a list of all courses
     */
    public List<CourseEntity> findAll() {
        log.debug("Finding all courses");
        return courseJpaRepository.findAll();
    }

    /**
     * Deletes a course by its ID.
     *
     * @param courseId the ID of the course to delete
     */
    public void deleteById(Long courseId) {
        log.debug("Deleting course by ID: {}", courseId);
        
        if (!courseJpaRepository.existsById(courseId)) {
            throw new NotFoundException(COURSE, courseId);
        }
        
        courseJpaRepository.deleteById(courseId);
        
        // Publish course deleted event
        CompletableFuture<Void> publishFuture = courseEventPublisher.publishCourseDeleted(courseId);
        publishFuture.join(); // Wait for event to be published
        
        log.info("Successfully deleted course with ID: {}", courseId);
    }

    /**
     * Updates an existing course.
     *
     * @param courseId the ID of the course to update
     * @param courseRecord the updated course data
     * @return the updated course entity
     */
    public CourseEntity update(Long courseId, CourseRecord courseRecord) {
        log.debug("Updating course with ID: {}", courseId);
        
        CourseEntity existingCourse = courseJpaRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException(COURSE, courseId));
        
        // Update fields
        existingCourse.setName(courseRecord.name());
        existingCourse.setNumber(courseRecord.number());
        existingCourse.setDate(courseRecord.date());
        existingCourse.setPartants(courseMapper.toPartantEntities(courseRecord.partants()));
        
        CourseEntity updatedCourse = courseJpaRepository.save(existingCourse);
        
        // Publish course updated event
        CompletableFuture<Void> publishFuture = courseEventPublisher.publishCourseUpdated(courseRecord);
        publishFuture.join(); // Wait for event to be published
        
        log.info("Successfully updated course with ID: {}", updatedCourse.getCourseId());
        return updatedCourse;
    }

    /**
     * Checks if a course exists by its ID.
     *
     * @param courseId the ID of the course to check
     * @return true if the course exists, false otherwise
     */
    public boolean existsById(Long courseId) {
        log.debug("Checking if course exists by ID: {}", courseId);
        return courseJpaRepository.existsById(courseId);
    }

    /**
     * Finds courses by name.
     *
     * @param name the name to search for
     * @return a list of courses with the given name
     */
    public List<CourseEntity> findByName(String name) {
        log.debug("Finding courses by name: {}", name);
        return courseJpaRepository.findByName(name);
    }

    /**
     * Counts the total number of courses.
     *
     * @return the total number of courses
     */
    public long count() {
        log.debug("Counting total courses");
        return courseJpaRepository.count();
    }
    
    /**
     * Saves a course event (alias for save method).
     *
     * @param courseRecord the course record to save
     * @return the saved course entity
     */
    public CourseEntity saveEvent(CourseRecord courseRecord) {
        log.debug("Saving course event: {}", courseRecord.name());
        return save(courseRecord);
    }
    
    /**
     * Checks if a course is eligible for betting.
     *
     * @param courseId the ID of the course to check
     * @return true if eligible for betting, false otherwise
     */
    public boolean isEligibleForBetting(Long courseId) {
        log.debug("Checking betting eligibility for course ID: {}", courseId);
        
        CourseEntity course = courseJpaRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException(COURSE, courseId));
        
        // Simple business logic - in real scenarios this would be more complex
        return course.getDate().isAfter(LocalDate.now().minusDays(1)) && 
               course.getPartants() != null && 
               course.getPartants().size() >= 5;
    }
    
    /**
     * Calculates the difficulty of a course.
     *
     * @param courseId the ID of the course to calculate difficulty for
     * @return difficulty score (1-100, where 100 is most difficult)
     */
    public int calculateCourseDifficulty(Long courseId) {
        log.debug("Calculating difficulty for course ID: {}", courseId);
        
        CourseEntity course = courseJpaRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException(COURSE, courseId));
        
        // Simple difficulty calculation based on number of partants
        // In real scenarios this would consider many more factors
        int partantCount = course.getPartants() != null ? course.getPartants().size() : 0;
        return Math.min(100, partantCount * 5); // 5 points per partant, max 100
    }
}