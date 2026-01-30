package com.pmu2.exec.unit.domain;

import com.pmu2.exec.domain.CourseRecord;
import com.pmu2.exec.domain.PartantRecord;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CourseRecordBuilderTest {

    private List<PartantRecord> validPartants = List.of(
        new PartantRecord(1, "Horse 1", 1),
        new PartantRecord(2, "Horse 2", 2),
        new PartantRecord(3, "Horse 3", 3)
    );

    @Test
    void shouldBuildCourseRecordWithAllFields() {
        // Given
        Integer courseId = 100;
        String name = "Test Course";
        int number = 5;
        LocalDate date = LocalDate.now().plusDays(10);
        List<PartantRecord> partants = validPartants;

        // When
        CourseRecord courseRecord = CourseRecord.builder()
            .courseId(courseId)
            .name(name)
            .number(number)
            .date(date)
            .partants(partants)
            .build();

        // Then
        assertEquals(courseId, courseRecord.courseId());
        assertEquals(name, courseRecord.name());
        assertEquals(number, courseRecord.number());
        assertEquals(date, courseRecord.date());
        assertEquals(partants, courseRecord.partants());
    }

    @Test
    void shouldBuildCourseRecordWithPartialFields() {
        // Given
        String name = "Partial Course";
        LocalDate date = LocalDate.now().plusDays(5);

        // When
        CourseRecord courseRecord = CourseRecord.builder()
            .name(name)
            .date(date)
            .build();

        // Then
        assertNull(courseRecord.courseId());
        assertEquals(name, courseRecord.name());
        assertEquals(0, courseRecord.number()); // default int value
        assertEquals(date, courseRecord.date());
        assertNull(courseRecord.partants());
    }

    @Test
    void shouldBuildCourseRecordWithNullValues() {
        // When
        CourseRecord courseRecord = CourseRecord.builder()
            .courseId(null)
            .name(null)
            .number(0)
            .date(null)
            .partants(null)
            .build();

        // Then
        assertNull(courseRecord.courseId());
        assertNull(courseRecord.name());
        assertEquals(0, courseRecord.number());
        assertNull(courseRecord.date());
        assertNull(courseRecord.partants());
    }

    @Test
    void shouldBuildCourseRecordWithEmptyPartantsList() {
        // Given
        List<PartantRecord> emptyPartants = List.of();

        // When
        CourseRecord courseRecord = CourseRecord.builder()
            .courseId(100)
            .name("Empty Course")
            .number(1)
            .date(LocalDate.now().plusDays(1))
            .partants(emptyPartants)
            .build();

        // Then
        assertEquals(100, courseRecord.courseId());
        assertEquals("Empty Course", courseRecord.name());
        assertEquals(1, courseRecord.number());
        assertEquals(LocalDate.now().plusDays(1), courseRecord.date());
        assertEquals(emptyPartants, courseRecord.partants());
        assertTrue(courseRecord.partants().isEmpty());
    }

    @Test
    void shouldBuildCourseRecordWithSinglePartant() {
        // Given
        List<PartantRecord> singlePartant = List.of(
            new PartantRecord(1, "Single Horse", 1)
        );

        // When
        CourseRecord courseRecord = CourseRecord.builder()
            .courseId(200)
            .name("Single Partant Course")
            .number(2)
            .date(LocalDate.now().plusDays(2))
            .partants(singlePartant)
            .build();

        // Then
        assertEquals(200, courseRecord.courseId());
        assertEquals("Single Partant Course", courseRecord.name());
        assertEquals(2, courseRecord.number());
        assertEquals(LocalDate.now().plusDays(2), courseRecord.date());
        assertEquals(singlePartant, courseRecord.partants());
        assertEquals(1, courseRecord.partants().size());
    }

    @Test
    void shouldBuildCourseRecordWithMaxValues() {
        // Given
        List<PartantRecord> maxPartants = List.of(
            new PartantRecord(1, "Horse 1", 1),
            new PartantRecord(2, "Horse 2", 2),
            new PartantRecord(3, "Horse 3", 3),
            new PartantRecord(4, "Horse 4", 4),
            new PartantRecord(5, "Horse 5", 5),
            new PartantRecord(6, "Horse 6", 6),
            new PartantRecord(7, "Horse 7", 7),
            new PartantRecord(8, "Horse 8", 8),
            new PartantRecord(9, "Horse 9", 9),
            new PartantRecord(10, "Horse 10", 10),
            new PartantRecord(11, "Horse 11", 11),
            new PartantRecord(12, "Horse 12", 12),
            new PartantRecord(13, "Horse 13", 13),
            new PartantRecord(14, "Horse 14", 14),
            new PartantRecord(15, "Horse 15", 15),
            new PartantRecord(16, "Horse 16", 16),
            new PartantRecord(17, "Horse 17", 17),
            new PartantRecord(18, "Horse 18", 18),
            new PartantRecord(19, "Horse 19", 19),
            new PartantRecord(20, "Horse 20", 20)
        );

        // When
        CourseRecord courseRecord = CourseRecord.builder()
            .courseId(999)
            .name("Max Course")
            .number(999)
            .date(LocalDate.now().plusYears(1))
            .partants(maxPartants)
            .build();

        // Then
        assertEquals(999, courseRecord.courseId());
        assertEquals("Max Course", courseRecord.name());
        assertEquals(999, courseRecord.number());
        assertEquals(LocalDate.now().plusYears(1), courseRecord.date());
        assertEquals(maxPartants, courseRecord.partants());
        assertEquals(20, courseRecord.partants().size());
    }

    @Test
    void shouldBuildCourseRecordWithMinValues() {
        // Given
        List<PartantRecord> minPartants = List.of(
            new PartantRecord(1, "A", 1),
            new PartantRecord(2, "B", 2),
            new PartantRecord(3, "C", 3)
        );

        // When
        CourseRecord courseRecord = CourseRecord.builder()
            .courseId(1)
            .name("AB") // minimum 2 characters
            .number(1)
            .date(LocalDate.now().plusDays(1))
            .partants(minPartants)
            .build();

        // Then
        assertEquals(1, courseRecord.courseId());
        assertEquals("AB", courseRecord.name());
        assertEquals(1, courseRecord.number());
        assertEquals(LocalDate.now().plusDays(1), courseRecord.date());
        assertEquals(minPartants, courseRecord.partants());
        assertEquals(3, courseRecord.partants().size());
    }

    @Test
    void shouldCreateMultipleBuildersIndependently() {
        // Given
        CourseRecord.CourseRecordBuilder builder1 = CourseRecord.builder()
            .courseId(100)
            .name("Course 1");

        CourseRecord.CourseRecordBuilder builder2 = CourseRecord.builder()
            .courseId(200)
            .name("Course 2");

        // When
        CourseRecord course1 = builder1
            .number(1)
            .date(LocalDate.now().plusDays(1))
            .partants(validPartants)
            .build();

        CourseRecord course2 = builder2
            .number(2)
            .date(LocalDate.now().plusDays(2))
            .partants(validPartants)
            .build();

        // Then
        assertEquals(100, course1.courseId());
        assertEquals("Course 1", course1.name());
        assertEquals(1, course1.number());

        assertEquals(200, course2.courseId());
        assertEquals("Course 2", course2.name());
        assertEquals(2, course2.number());

        // Verify builders are independent
        assertNotEquals(course1, course2);
    }

    @Test
    void shouldReuseBuilderForMultipleRecords() {
        // Given
        CourseRecord.CourseRecordBuilder builder = CourseRecord.builder()
            .name("Base Course")
            .number(5)
            .partants(validPartants);

        // When
        CourseRecord course1 = builder
            .courseId(100)
            .date(LocalDate.now().plusDays(1))
            .build();

        CourseRecord course2 = builder
            .courseId(200)
            .date(LocalDate.now().plusDays(2))
            .build();

        // Then
        assertEquals(100, course1.courseId());
        assertEquals(LocalDate.now().plusDays(1), course1.date());

        assertEquals(200, course2.courseId());
        assertEquals(LocalDate.now().plusDays(2), course2.date());

        // Common fields should be the same
        assertEquals("Base Course", course1.name());
        assertEquals("Base Course", course2.name());
        assertEquals(5, course1.number());
        assertEquals(5, course2.number());
        assertEquals(validPartants, course1.partants());
        assertEquals(validPartants, course2.partants());
    }

    @Test
    void shouldHandleSpecialCharactersInName() {
        // Given
        String specialName = "Course éàü@#$%^&*()_+-=[]{}|;':\",./<>?";

        // When
        CourseRecord courseRecord = CourseRecord.builder()
            .courseId(300)
            .name(specialName)
            .number(10)
            .date(LocalDate.now().plusDays(10))
            .partants(validPartants)
            .build();

        // Then
        assertEquals(300, courseRecord.courseId());
        assertEquals(specialName, courseRecord.name());
        assertEquals(10, courseRecord.number());
        assertEquals(LocalDate.now().plusDays(10), courseRecord.date());
        assertEquals(validPartants, courseRecord.partants());
    }

    @Test
    void shouldTestBuilderToString() {
        // Given
        CourseRecord.CourseRecordBuilder builder = CourseRecord.builder()
            .courseId(100)
            .name("Test Course")
            .number(5)
            .date(LocalDate.now().plusDays(1))
            .partants(validPartants);

        // When
        String builderString = builder.toString();

        // Then
        assertNotNull(builderString);
        assertTrue(builderString.contains("CourseRecord.CourseRecordBuilder"));
    }

    @Test
    void shouldBuildCourseRecordWithChainedSetters() {
        // When
        CourseRecord courseRecord = CourseRecord.builder()
            .courseId(400)
            .name("Chained Course")
            .number(7)
            .date(LocalDate.now().plusWeeks(1))
            .partants(validPartants)
            .build();

        // Then
        assertEquals(400, courseRecord.courseId());
        assertEquals("Chained Course", courseRecord.name());
        assertEquals(7, courseRecord.number());
        assertEquals(LocalDate.now().plusWeeks(1), courseRecord.date());
        assertEquals(validPartants, courseRecord.partants());
    }

    @Test
    void shouldOverrideBuilderFields() {
        // Given
        CourseRecord.CourseRecordBuilder builder = CourseRecord.builder()
            .courseId(100)
            .name("Original Name")
            .number(1)
            .date(LocalDate.now().plusDays(1))
            .partants(validPartants);

        // When - Override fields
        builder.courseId(200)
              .name("Updated Name")
              .number(2)
              .date(LocalDate.now().plusDays(2));

        CourseRecord courseRecord = builder.build();

        // Then - Should have overridden values
        assertEquals(200, courseRecord.courseId());
        assertEquals("Updated Name", courseRecord.name());
        assertEquals(2, courseRecord.number());
        assertEquals(LocalDate.now().plusDays(2), courseRecord.date());
        assertEquals(validPartants, courseRecord.partants());
    }

    @Test
    void shouldBuildCourseRecordWithNullCourseId() {
        // When
        CourseRecord courseRecord = CourseRecord.builder()
            .courseId(null)
            .name("Null ID Course")
            .number(3)
            .date(LocalDate.now().plusDays(3))
            .partants(validPartants)
            .build();

        // Then
        assertNull(courseRecord.courseId());
        assertEquals("Null ID Course", courseRecord.name());
        assertEquals(3, courseRecord.number());
        assertEquals(LocalDate.now().plusDays(3), courseRecord.date());
        assertEquals(validPartants, courseRecord.partants());
    }

    @Test
    void shouldBuildCourseRecordWithZeroNumber() {
        // When
        CourseRecord courseRecord = CourseRecord.builder()
            .courseId(500)
            .name("Zero Number Course")
            .number(0)
            .date(LocalDate.now().plusDays(4))
            .partants(validPartants)
            .build();

        // Then
        assertEquals(500, courseRecord.courseId());
        assertEquals("Zero Number Course", courseRecord.name());
        assertEquals(0, courseRecord.number());
        assertEquals(LocalDate.now().plusDays(4), courseRecord.date());
        assertEquals(validPartants, courseRecord.partants());
    }
}
