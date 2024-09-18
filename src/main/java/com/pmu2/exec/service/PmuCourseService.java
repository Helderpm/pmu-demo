package com.pmu2.exec.service;

import com.pmu2.exec.domain.CourseRecord;
import com.pmu2.exec.infrastrure.db.sql.CourseEntity;
import com.pmu2.exec.infrastrure.db.sql.CourseJpaRepository;
import com.pmu2.exec.infrastrure.db.sql.PartantEntity;
import com.pmu2.exec.infrastrure.kafka.producer.PmuProducerService;
import com.pmu2.exec.service.mapper.CourseMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PmuCourseService {

    private final CourseJpaRepository courseJpaRepository;
    private final PmuProducerService pmuProducerService;
    private final CourseMapper courseMapper;

    public PmuCourseService(CourseJpaRepository courseJpaRepository, PmuProducerService pmuProducerService, CourseMapper courseMapper) {
        this.courseJpaRepository = courseJpaRepository;
        this.pmuProducerService = pmuProducerService;
        this.courseMapper = courseMapper;
    }

    public List<CourseRecord> findAll() {
        return courseMapper.toRecordList(courseJpaRepository.findAll());
    }

    public CourseRecord saveEvent(CourseRecord course) {

        pmuProducerService.sendMessage(course);
        return course;
    }
    public CourseRecord save(CourseRecord course) {
        var coursePersist = courseJpaRepository.save(courseMapper.toEntity(course));
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
}
