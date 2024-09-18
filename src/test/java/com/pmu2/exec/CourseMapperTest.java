package com.pmu2.exec;

import com.pmu2.exec.domain.CourseRecord;
import com.pmu2.exec.infrastrure.db.sql.CourseEntity;
import com.pmu2.exec.infrastrure.db.sql.PartantEntity;
import com.pmu2.exec.service.mapper.CourseMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.List;

import static com.pmu2.exec.utils.TestUtil.newcourseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CourseMapperTest {
    private CourseMapper courseMapper;

    @BeforeEach
    void setUp() {
        courseMapper = Mappers.getMapper(CourseMapper.class);
    }

    private List<PartantEntity> getListParticipantA() {
        PartantEntity p1 = new PartantEntity("Partant AA", 909);
        PartantEntity p2 = new PartantEntity("Partant AB", 809);

        return List.of(p1, p2);
    }

    @Test
    void shouldMapCourseEntityToCourseRecordWithCorrespondingValues() {
        // given
        var course = newcourseEntity("Course A");
        course.setPartants(this.getListParticipantA());

        // when
        CourseRecord courseRecord = courseMapper.toRecord(course);

        // then
        assertEquals(course.getCourseId(), courseRecord.courseId().longValue());
        assertEquals(course.getName(), courseRecord.name());
        assertEquals(course.getNumber(), courseRecord.number());
        assertEquals(course.getDate(), courseRecord.date());
        assertEquals(course.getPartants().size(), courseRecord.partants().size());
    }

    @Test
    void shouldMapCourseEntityWithEmptyPartantEntityListToCourseRecordWithEmptyPartantList() {
        // given
        var course = newcourseEntity("Course A");

        // when
        CourseRecord courseRecord = courseMapper.toRecord(course);

        // then
        assertEquals(course.getCourseId(), courseRecord.courseId().longValue());
        assertEquals(course.getName(), courseRecord.name());
        assertEquals(course.getNumber(), courseRecord.number());
        assertEquals(course.getDate(), courseRecord.date());
        assertEquals(course.getPartants().size(), courseRecord.partants().size());
    }
}
