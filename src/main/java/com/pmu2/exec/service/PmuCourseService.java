package com.pmu2.exec.service;

import com.pmu2.exec.domain.CourseRecord;
import com.pmu2.exec.infrastrure.db.sql.CourseEntity;
import com.pmu2.exec.infrastrure.db.sql.CourseJpaRepository;
import com.pmu2.exec.infrastrure.db.sql.PartantEntity;
import com.pmu2.exec.infrastrure.kafka.producer.PmuProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PmuCourseService {

    @Autowired
    private CourseJpaRepository courseJpaRepository;

    @Autowired
    private PmuProducerService pmuProducerService;

    public List<CourseEntity> findAll() {
        return courseJpaRepository.findAll();
    }

    public CourseEntity save(CourseEntity course) {
        var res = courseJpaRepository.save(course);

        var cc = new CourseRecord(Math.toIntExact(res.getCourseId()), course.getName(), course.getNumber(), course.getDate(),
               new ArrayList<>());
        pmuProducerService.sendMessage(cc);

        return res;
    }

    public void deleteById(Long id) {
        courseJpaRepository.deleteById(id);
    }

    public List<CourseEntity> findByName(String name) {
        return courseJpaRepository.findByName(name);
    }

    public List<PartantEntity> findPartantsByCourse(Long courseId) {
        Optional<CourseEntity> courseOptional = courseJpaRepository.findById(courseId);
        List<PartantEntity> partantsList = new ArrayList<>();

        courseOptional.ifPresent(courseEntity -> partantsList.addAll(courseEntity.getPartants()));

        return partantsList;
    }
}
