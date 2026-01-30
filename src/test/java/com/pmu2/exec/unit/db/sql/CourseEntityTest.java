package com.pmu2.exec.unit.db.sql;

import com.pmu2.exec.infrastructure.db.sql.CourseEntity;
import com.pmu2.exec.infrastructure.db.sql.PartantEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CourseEntityTest {

    @Test
    void shouldCreateCourseWithAllArgsConstructor() {
        // Given
        List<PartantEntity> partants = List.of(new PartantEntity("Horse 1", 1));
        
        // When
        CourseEntity course = new CourseEntity(100L, "Test Course", 5, LocalDate.now(), partants);
        
        // Then
        assertEquals(100L, course.getCourseId());
        assertEquals("Test Course", course.getName());
        assertEquals(5, course.getNumber());
        assertEquals(LocalDate.now(), course.getDate());
        assertEquals(partants, course.getPartants());
    }

    @Test
    void shouldCreateCourseWithNoArgsConstructor() {
        // When
        CourseEntity course = new CourseEntity();
        
        // Then
        assertNull(course.getCourseId());
        assertNull(course.getName());
        assertNull(course.getDate());
        assertNull(course.getPartants());
    }

    @Test
    void shouldCreateCourseWithNameNumberDate() {
        // Given
        String name = "Test Course";
        int number = 5;
        LocalDate date = LocalDate.now();
        
        // When
        CourseEntity course = new CourseEntity(name, number, date);
        
        // Then
        assertEquals(name, course.getName());
        assertEquals(number, course.getNumber());
        assertEquals(date, course.getDate());
    }

    @Test
    void shouldCreateCourseWithNameNumberDateAndPartants() {
        // Given
        String name = "Test Course";
        int number = 5;
        LocalDate date = LocalDate.now();
        List<PartantEntity> partants = List.of(new PartantEntity("Horse 1", 1));
        
        // When
        CourseEntity course = new CourseEntity(name, number, date, partants);
        
        // Then
        assertEquals(name, course.getName());
        assertEquals(number, course.getNumber());
        assertEquals(date, course.getDate());
        assertEquals(partants, course.getPartants());
    }

    @Test
    void shouldTestEqualsWithSameId() {
        // Given
        CourseEntity course1 = new CourseEntity();
        course1.setCourseId(100L);
        
        CourseEntity course2 = new CourseEntity();
        course2.setCourseId(100L);
        
        // When & Then
        assertEquals(course1, course2);
        assertEquals(course1.hashCode(), course2.hashCode());
    }

    @Test
    void shouldTestEqualsWithDifferentId() {
        // Given
        CourseEntity course1 = new CourseEntity();
        course1.setCourseId(100L);
        
        CourseEntity course2 = new CourseEntity();
        course2.setCourseId(200L);
        
        // When & Then
        assertNotEquals(course1, course2);
    }

    @Test
    void shouldTestEqualsForTransientEntities() {
        // Given
        LocalDate date = LocalDate.now();
        CourseEntity course1 = new CourseEntity("Test Course", 5, date);
        CourseEntity course2 = new CourseEntity("Test Course", 5, date);
        
        // When & Then
        assertEquals(course1, course2);
        assertEquals(course1.hashCode(), course2.hashCode());
    }

    @Test
    void shouldTestEqualsForTransientEntitiesWithDifferentName() {
        // Given
        LocalDate date = LocalDate.now();
        CourseEntity course1 = new CourseEntity("Course A", 5, date);
        CourseEntity course2 = new CourseEntity("Course B", 5, date);
        
        // When & Then
        assertNotEquals(course1, course2);
    }

    @Test
    void shouldTestEqualsForTransientEntitiesWithDifferentNumber() {
        // Given
        LocalDate date = LocalDate.now();
        CourseEntity course1 = new CourseEntity("Test Course", 5, date);
        CourseEntity course2 = new CourseEntity("Test Course", 6, date);
        
        // When & Then
        assertNotEquals(course1, course2);
    }

    @Test
    void shouldTestEqualsForTransientEntitiesWithDifferentDate() {
        // Given
        CourseEntity course1 = new CourseEntity("Test Course", 5, LocalDate.now());
        CourseEntity course2 = new CourseEntity("Test Course", 5, LocalDate.now().plusDays(1));
        
        // When & Then
        assertNotEquals(course1, course2);
    }

    @Test
    void shouldTestEqualsWithNull() {
        // Given
        CourseEntity course = new CourseEntity();
        
        // When & Then
        assertNotEquals(null, course);
    }

    @Test
    void shouldTestEqualsWithDifferentClass() {
        // Given
        CourseEntity course = new CourseEntity();
        
        // When & Then
        assertNotEquals("string", course);
    }

    @Test
    void shouldTestEqualsWithSameObject() {
        // Given
        CourseEntity course = new CourseEntity();
        
        // When & Then
        assertEquals(course, course);
    }

    @Test
    void shouldTestHashCodeWithId() {
        // Given
        CourseEntity course = new CourseEntity();
        course.setCourseId(100L);
        
        // When
        int hashCode = course.hashCode();
        
        // Then
        assertEquals(131, hashCode);
    }

    @Test
    void shouldTestHashCodeForTransientEntities() {
        // Given
        CourseEntity course = new CourseEntity("Test", 5, LocalDate.now());
        
        // When
        int hashCode = course.hashCode();
        
        // Then
        assertNotEquals(0, hashCode);
    }

    @Test
    void shouldTestSettersAndGetters() {
        // Given
        CourseEntity course = new CourseEntity();
        List<PartantEntity> partants = List.of(new PartantEntity("Horse 1", 1));
        
        // When
        course.setCourseId(123L);
        course.setName("New Course");
        course.setNumber(10);
        course.setDate(LocalDate.now().plusDays(1));
        course.setPartants(partants);
        
        // Then
        assertEquals(123L, course.getCourseId());
        assertEquals("New Course", course.getName());
        assertEquals(10, course.getNumber());
        assertEquals(LocalDate.now().plusDays(1), course.getDate());
        assertEquals(partants, course.getPartants());
    }
}
