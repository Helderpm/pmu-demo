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
import java.util.Random;

@Service
public class PmuCourseService {

    @Autowired
    private CourseJpaRepository courseJpaRepository;

    @Autowired
    private PmuProducerService pmuProducerService;

    @Autowired
    private CourseMapper modelMapper;

    public List<CourseRecord> findAll() {

        return modelMapper.toRecordList(courseJpaRepository.findAll());
    }

    public CourseRecord saveEvent(CourseRecord course) {

        pmuProducerService.sendMessage(course);

//        var coursePersist = courseJpaRepository.save(getModelMapperEntity(course));
//
//        var courseEvent = getCourseEvent(coursePersist);

        return course;
    }
    public CourseRecord save(CourseRecord course) {
        var coursePersist = courseJpaRepository.save(getModelMapperEntity(course));

        List<CourseEntity> listE = courseJpaRepository.findAll();

        return new CourseRecord(Math.toIntExact(coursePersist.getCourseId()),
                coursePersist.getName(),
                coursePersist.getNumber(),
                coursePersist.getDate(),
                new ArrayList<>());
    }

    private static CourseRecord getCourseEvent(CourseEntity coursePersist) {
        return new CourseRecord(Math.toIntExact(coursePersist.getCourseId()),
                coursePersist.getName(),
                coursePersist.getNumber(),
                coursePersist.getDate(),
                new ArrayList<>());
    }

    private CourseEntity getModelMapperEntity(CourseRecord course) {
        return modelMapper.toEntity(course);
    }

//    public CourseRecord saveCourse(CourseRecord course) {
//        var coursePersist = courseJpaRepository.save(modelMapper.toEntity(course));
//        return new CourseRecord( Math.toIntExact(coursePersist.getCourseId()),
//                coursePersist.getName(),
//                coursePersist.getNumber(),
//                coursePersist.getDate(),
//                new ArrayList<>());
//    }

    public void deleteById(Long id) {
        courseJpaRepository.deleteById(id);
    }

    public List<CourseRecord> findByName(String name) {

        return modelMapper.toRecordList(courseJpaRepository.findByName(name));
    }

    public List<PartantEntity> findPartantsByCourse(Long courseId) {
        Optional<CourseEntity> courseOptional = courseJpaRepository.findById(courseId);
        List<PartantEntity> partantsList = new ArrayList<>();
        courseOptional.ifPresent(courseEntity -> partantsList.addAll(courseEntity.getPartants()));

        return partantsList;
    }
}
