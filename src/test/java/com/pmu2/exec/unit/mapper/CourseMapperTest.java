package com.pmu2.exec.unit.mapper;

import com.pmu2.exec.domain.CourseRecord;
import com.pmu2.exec.domain.PartantRecord;
import com.pmu2.exec.infrastructure.db.sql.CourseEntity;
import com.pmu2.exec.infrastructure.db.sql.PartantEntity;
import com.pmu2.exec.service.mapper.CourseMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.List;

import static com.pmu2.exec.utils.TestUtil.newcourseEntity;
import static org.junit.jupiter.api.Assertions.*;

public class CourseMapperTest {
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

    @Test
    void shouldMapNullCourseEntityToNullRecord() {
        // Given
        CourseEntity courseEntity = null;

        // When
        CourseRecord courseRecord = courseMapper.toRecord(courseEntity);

        // Then
        assertNull(courseRecord);
    }

    @Test
    void shouldMapCourseRecordToCourseEntity() {
        // Given
        List<PartantRecord> partantRecords = List.of(
            new PartantRecord(1, "Horse 1", 1),
            new PartantRecord(2, "Horse 2", 2)
        );
        CourseRecord courseRecord = new CourseRecord(100, "Test Course", 5, LocalDate.now(), partantRecords);

        // When
        CourseEntity courseEntity = courseMapper.toEntity(courseRecord);

        // Then
        assertNotNull(courseEntity);
        assertEquals(100L, courseEntity.getCourseId());
        assertEquals("Test Course", courseEntity.getName());
        assertEquals(5, courseEntity.getNumber());
        assertEquals(LocalDate.now(), courseEntity.getDate());
        assertNotNull(courseEntity.getPartants());
        assertEquals(2, courseEntity.getPartants().size());
    }

    @Test
    void shouldMapNullCourseRecordToNullEntity() {
        // Given
        CourseRecord courseRecord = null;

        // When
        CourseEntity courseEntity = courseMapper.toEntity(courseRecord);

        // Then
        assertNull(courseEntity);
    }

    @Test
    void shouldMapCourseEntityListToCourseRecordList() {
        // Given
        CourseEntity course1 = newcourseEntity("Course 1");
        CourseEntity course2 = newcourseEntity("Course 2");
        List<CourseEntity> courseEntities = List.of(course1, course2);

        // When
        List<CourseRecord> courseRecords = courseMapper.toRecordList(courseEntities);

        // Then
        assertNotNull(courseRecords);
        assertEquals(2, courseRecords.size());
        
        CourseRecord record1 = courseRecords.get(0);
        assertEquals(course1.getCourseId(), record1.courseId().longValue());
        assertEquals(course1.getName(), record1.name());
        assertEquals(course1.getNumber(), record1.number());
        assertEquals(course1.getDate(), record1.date());

        CourseRecord record2 = courseRecords.get(1);
        assertEquals(course2.getCourseId(), record2.courseId().longValue());
        assertEquals(course2.getName(), record2.name());
        assertEquals(course2.getNumber(), record2.number());
        assertEquals(course2.getDate(), record2.date());
    }

    @Test
    void shouldMapEmptyCourseEntityListToEmptyRecordList() {
        // Given
        List<CourseEntity> courseEntities = List.of();

        // When
        List<CourseRecord> courseRecords = courseMapper.toRecordList(courseEntities);

        // Then
        assertNotNull(courseRecords);
        assertEquals(0, courseRecords.size());
    }

    @Test
    void shouldMapNullCourseEntityListToNullRecordList() {
        // Given
        List<CourseEntity> courseEntities = null;

        // When
        List<CourseRecord> courseRecords = courseMapper.toRecordList(courseEntities);

        // Then
        assertNull(courseRecords);
    }

    @Test
    void shouldMapCourseRecordListToCourseEntityList() {
        // Given
        List<PartantRecord> partants = List.of(new PartantRecord(1, "Horse 1", 1));
        CourseRecord record1 = new CourseRecord(1, "Course 1", 1, LocalDate.now(), partants);
        CourseRecord record2 = new CourseRecord(2, "Course 2", 2, LocalDate.now().plusDays(1), partants);
        List<CourseRecord> courseRecords = List.of(record1, record2);

        // When
        List<CourseEntity> courseEntities = courseMapper.toEntityList(courseRecords);

        // Then
        assertNotNull(courseEntities);
        assertEquals(2, courseEntities.size());

        CourseEntity entity1 = courseEntities.get(0);
        assertEquals(1L, entity1.getCourseId());
        assertEquals("Course 1", entity1.getName());
        assertEquals(1, entity1.getNumber());
        assertEquals(LocalDate.now(), entity1.getDate());

        CourseEntity entity2 = courseEntities.get(1);
        assertEquals(2L, entity2.getCourseId());
        assertEquals("Course 2", entity2.getName());
        assertEquals(2, entity2.getNumber());
        assertEquals(LocalDate.now().plusDays(1), entity2.getDate());
    }

    @Test
    void shouldMapEmptyCourseRecordListToEmptyEntityList() {
        // Given
        List<CourseRecord> courseRecords = List.of();

        // When
        List<CourseEntity> courseEntities = courseMapper.toEntityList(courseRecords);

        // Then
        assertNotNull(courseEntities);
        assertEquals(0, courseEntities.size());
    }

    @Test
    void shouldMapNullCourseRecordListToNullEntityList() {
        // Given
        List<CourseRecord> courseRecords = null;

        // When
        List<CourseEntity> courseEntities = courseMapper.toEntityList(courseRecords);

        // Then
        assertNull(courseEntities);
    }

    @Test
    void shouldMapPartantRecordsToPartantEntities() {
        // Given
        List<PartantRecord> partantRecords = List.of(
            new PartantRecord(1, "Horse 1", 1),
            new PartantRecord(2, "Horse 2", 2),
            new PartantRecord(3, "Horse 3", 3)
        );

        // When
        List<PartantEntity> partantEntities = courseMapper.toPartantEntities(partantRecords);

        // Then
        assertNotNull(partantEntities);
        assertEquals(3, partantEntities.size());

        PartantEntity entity1 = partantEntities.get(0);
        assertEquals(1L, entity1.getId());
        assertEquals("Horse 1", entity1.getName());
        assertEquals(1, entity1.getNumber());

        PartantEntity entity2 = partantEntities.get(1);
        assertEquals(2L, entity2.getId());
        assertEquals("Horse 2", entity2.getName());
        assertEquals(2, entity2.getNumber());

        PartantEntity entity3 = partantEntities.get(2);
        assertEquals(3L, entity3.getId());
        assertEquals("Horse 3", entity3.getName());
        assertEquals(3, entity3.getNumber());
    }

    @Test
    void shouldMapEmptyPartantRecordsToEmptyPartantEntities() {
        // Given
        List<PartantRecord> partantRecords = List.of();

        // When
        List<PartantEntity> partantEntities = courseMapper.toPartantEntities(partantRecords);

        // Then
        assertNotNull(partantEntities);
        assertEquals(0, partantEntities.size());
    }

    @Test
    void shouldMapNullPartantRecordsToNullPartantEntities() {
        // Given
        List<PartantRecord> partantRecords = null;

        // When
        List<PartantEntity> partantEntities = courseMapper.toPartantEntities(partantRecords);

        // Then
        assertNull(partantEntities);
    }

    @Test
    void shouldMapCourseEntityWithNullValues() {
        // Given
        CourseEntity courseEntity = new CourseEntity();
        courseEntity.setCourseId(null);
        courseEntity.setName(null);
        courseEntity.setNumber(0);
        courseEntity.setDate(null);
        courseEntity.setPartants(null);

        // When
        CourseRecord courseRecord = courseMapper.toRecord(courseEntity);

        // Then
        assertNotNull(courseRecord);
        assertNull(courseRecord.courseId());
        assertNull(courseRecord.name());
        assertEquals(0, courseRecord.number());
        assertNull(courseRecord.date());
        assertNull(courseRecord.partants());
    }

    @Test
    void shouldMapCourseRecordWithNullValues() {
        // Given
        CourseRecord courseRecord = new CourseRecord(null, null, 0, null, null);

        // When
        CourseEntity courseEntity = courseMapper.toEntity(courseRecord);

        // Then
        assertNotNull(courseEntity);
        assertNull(courseEntity.getCourseId());
        assertNull(courseEntity.getName());
        assertEquals(0, courseEntity.getNumber());
        assertNull(courseEntity.getDate());
        assertNull(courseEntity.getPartants());
    }
}
