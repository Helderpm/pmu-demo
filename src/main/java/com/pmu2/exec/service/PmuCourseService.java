package com.pmu2.exec.service;

import com.pmu2.exec.infrastrure.inbound.CourseEntity;
import com.pmu2.exec.infrastrure.inbound.CourseJpaRepository;
import com.pmu2.exec.infrastrure.inbound.PartantEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PmuCourseService {

    @Autowired
    private CourseJpaRepository courseJpaRepository;

    public List<CourseEntity> findAll() {
        return courseJpaRepository.findAll();
    }

    public CourseEntity save(CourseEntity course) {
        return courseJpaRepository.save(course);
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
