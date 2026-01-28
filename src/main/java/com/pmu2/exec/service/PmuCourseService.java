package com.pmu2.exec.service;

import com.pmu2.exec.domain.CourseRecord;
import com.pmu2.exec.infrastrure.db.sql.CourseEntity;
import com.pmu2.exec.infrastrure.db.sql.CourseJpaRepository;
import com.pmu2.exec.infrastrure.db.sql.PartantEntity;
import com.pmu2.exec.service.mapper.CourseMapper;
import com.pmu2.exec.validation.CourseValidator;
import com.pmu2.exec.validation.PartantValidator;
import com.pmu2.exec.domain.service.CourseDomainService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class PmuCourseService {

    private final CourseJpaRepository courseJpaRepository;
    private final CourseEventPublisher courseEventPublisher;
    private final CourseMapper courseMapper;
    private final CoursePartantService coursePartantService;
    private final CourseValidator courseValidator;
    private final PartantValidator partantValidator;
    private final CourseDomainService courseDomainService;

    public PmuCourseService(CourseJpaRepository courseJpaRepository, CourseEventPublisher courseEventPublisher, CourseMapper courseMapper, CoursePartantService coursePartantService, CourseValidator courseValidator, PartantValidator partantValidator, CourseDomainService courseDomainService) {
        this.courseJpaRepository = courseJpaRepository;
        this.courseEventPublisher = courseEventPublisher;
        this.courseMapper = courseMapper;
        this.coursePartantService = coursePartantService;
        this.courseValidator = courseValidator;
        this.partantValidator = partantValidator;
        this.courseDomainService = courseDomainService;
    }

    public List<CourseRecord> findAll() {
        return courseMapper.toRecordList(courseJpaRepository.findAll());
    }

    public CourseRecord saveEvent(CourseRecord course) {
        courseValidator.validateCourseRecord(course);
        courseDomainService.validateCourseCreation(course);
        courseEventPublisher.publishCourseCreated(course);
        return course;
    }
    public CourseRecord save(CourseRecord course) {
        // Validate using both validation and domain services
        courseValidator.validateCourseRecord(course);
        courseDomainService.validateCourseCreation(course);
        
        CourseEntity courseEntity = courseMapper.toEntity(course);
        partantValidator.validatePartantsExist(courseEntity.getPartants());
        
        var coursePersist = courseJpaRepository.save(courseEntity);
        
        // Publish event after successful save
        courseEventPublisher.publishCourseCreated(courseMapper.toRecord(coursePersist));
        
        return courseMapper.toRecord(coursePersist);
    }

    public void deleteById(Long id) {
        courseValidator.validateCourseExistsById(id);
        
        courseEventPublisher.publishCourseDeleted(id);
        courseJpaRepository.deleteById(id);
    }

    public List<CourseRecord> findByName(String name) {
        courseValidator.validateCourseName(name);
        return courseMapper.toRecordList(courseJpaRepository.findByName(name));
    }

    /**
     * Checks if a course is eligible for betting using domain service.
     * 
     * @param courseId the ID of the course to check
     * @return true if eligible for betting
     */
    public boolean isEligibleForBetting(Long courseId) {
        courseValidator.validateCourseExistsById(courseId);
        
        CourseRecord course = courseMapper.toRecord(
            courseJpaRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"))
        );
        
        return courseDomainService.isEligibleForBetting(course);
    }

    /**
     * Calculates course difficulty using domain service.
     * 
     * @param courseId the ID of the course
     * @return difficulty score (1-10)
     */
    public int calculateCourseDifficulty(Long courseId) {
        courseValidator.validateCourseExistsById(courseId);
        
        CourseRecord course = courseMapper.toRecord(
            courseJpaRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"))
        );
        
        return courseDomainService.calculateCourseDifficulty(course);
    }

    public List<PartantEntity> findPartantsByCourse(Long courseId) {
        return coursePartantService.findPartantsByCourse(courseId);
    }

    public void removePartant(Long partantId) {
        coursePartantService.removePartantFromCourseAndDelete(partantId);
    }
}
