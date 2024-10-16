package com.pmu2.exec.service;

import com.pmu2.exec.domain.CourseRecord;
import com.pmu2.exec.exeption.AException;
import com.pmu2.exec.infrastrure.db.sql.CourseEntity;
import com.pmu2.exec.infrastrure.db.sql.CourseJpaRepository;
import com.pmu2.exec.infrastrure.db.sql.PartantEntity;
import com.pmu2.exec.infrastrure.kafka.producer.PmuProducerService;
import com.pmu2.exec.service.mapper.CourseMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PmuCourseService {

    private final CourseJpaRepository courseJpaRepository;
    private final PmuProducerService pmuProducerService;
    private final CourseMapper courseMapper;
    private final PmuPartantService pmuPartantService;

    public PmuCourseService(CourseJpaRepository courseJpaRepository, PmuProducerService pmuProducerService, CourseMapper courseMapper, PmuPartantService pmuPartantService) {
        this.courseJpaRepository = courseJpaRepository;
        this.pmuProducerService = pmuProducerService;
        this.courseMapper = courseMapper;
        this.pmuPartantService = pmuPartantService;
    }

    public List<CourseRecord> findAll() {
        return courseMapper.toRecordList(courseJpaRepository.findAll());
    }

    public CourseRecord saveEvent(CourseRecord course) {

        pmuProducerService.sendMessageToKafka(course);
        return course;
    }
    public CourseRecord save(CourseRecord course) {
        CourseEntity courseEntity = courseMapper.toEntity(course);

        pmuPartantService.existPartants(courseEntity.getPartants());
        var coursePersist = courseJpaRepository.save(courseEntity);
        return courseMapper.toRecord(coursePersist);
    }

    public void deleteById(Long id) {
        courseJpaRepository.deleteById(id);
    }

    public List<CourseRecord> findByName(String name) {

        return courseMapper.toRecordList(courseJpaRepository.findByName(name));
    }

    public List<PartantEntity> findPartantsByCourse(Long courseId) {
        Optional<CourseEntity> courseOptional = courseJpaRepository.findById(courseId);
        List<PartantEntity> partantsList = new ArrayList<>();
        courseOptional.ifPresent(courseEntity -> partantsList.addAll(courseEntity.getPartants()));

        return partantsList;
    }

    public void removePartant(Long id) {
        courseJpaRepository.findById(id).orElseThrow(() -> new AException("Course not found"))
               .getPartants()
               .removeIf(partantEntity -> partantEntity.getId().equals(id));
    }
}
