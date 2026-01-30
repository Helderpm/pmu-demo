package com.pmu2.exec.unit.db.sql;

import com.pmu2.exec.infrastructure.db.sql.CourseEntity;
import com.pmu2.exec.infrastructure.db.sql.PartantEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PartantEntityTest {

    @Test
    void shouldCreatePartantWithAllArgsConstructor() {
        // Given
        List<CourseEntity> courses = List.of(new CourseEntity("Course 1", 1, LocalDate.now()));
        
        // When
        PartantEntity partant = new PartantEntity(100L, "Test Horse", 5, courses);
        
        // Then
        assertEquals(100L, partant.getId());
        assertEquals("Test Horse", partant.getName());
        assertEquals(5, partant.getNumber());
        assertEquals(courses, partant.getCourses());
    }

    @Test
    void shouldCreatePartantWithNoArgsConstructor() {
        // When
        PartantEntity partant = new PartantEntity();
        
        // Then
        assertNull(partant.getId());
        assertNull(partant.getName());
        assertNull(partant.getCourses());
    }

    @Test
    void shouldCreatePartantWithNameAndNumber() {
        // Given
        String name = "Test Horse";
        int number = 5;
        
        // When
        PartantEntity partant = new PartantEntity(name, number);
        
        // Then
        assertEquals(name, partant.getName());
        assertEquals(number, partant.getNumber());
    }

    @Test
    void shouldTestEqualsWithSameId() {
        // Given
        PartantEntity partant1 = new PartantEntity();
        partant1.setId(100L);
        
        PartantEntity partant2 = new PartantEntity();
        partant2.setId(100L);
        
        // When & Then
        assertEquals(partant1, partant2);
        assertEquals(partant1.hashCode(), partant2.hashCode());
    }

    @Test
    void shouldTestEqualsWithDifferentId() {
        // Given
        PartantEntity partant1 = new PartantEntity();
        partant1.setId(100L);
        
        PartantEntity partant2 = new PartantEntity();
        partant2.setId(200L);
        
        // When & Then
        assertNotEquals(partant1, partant2);
    }

    @Test
    void shouldTestEqualsForTransientEntities() {
        // Given
        PartantEntity partant1 = new PartantEntity("Test Horse", 5);
        PartantEntity partant2 = new PartantEntity("Test Horse", 5);
        
        // When & Then
        assertEquals(partant1, partant2);
        assertEquals(partant1.hashCode(), partant2.hashCode());
    }

    @Test
    void shouldTestEqualsForTransientEntitiesWithDifferentName() {
        // Given
        PartantEntity partant1 = new PartantEntity("Horse A", 5);
        PartantEntity partant2 = new PartantEntity("Horse B", 5);
        
        // When & Then
        assertNotEquals(partant1, partant2);
    }

    @Test
    void shouldTestEqualsForTransientEntitiesWithDifferentNumber() {
        // Given
        PartantEntity partant1 = new PartantEntity("Test Horse", 5);
        PartantEntity partant2 = new PartantEntity("Test Horse", 6);
        
        // When & Then
        assertNotEquals(partant1, partant2);
    }

    @Test
    void shouldTestEqualsWithNull() {
        // Given
        PartantEntity partant = new PartantEntity();
        
        // When & Then
        assertNotEquals(null, partant);
    }

    @Test
    void shouldTestEqualsWithDifferentClass() {
        // Given
        PartantEntity partant = new PartantEntity();
        
        // When & Then
        assertNotEquals("string", partant);
    }

    @Test
    void shouldTestEqualsWithSameObject() {
        // Given
        PartantEntity partant = new PartantEntity();
        
        // When & Then
        assertEquals(partant, partant);
    }

    @Test
    void shouldTestHashCodeWithId() {
        // Given
        PartantEntity partant = new PartantEntity();
        partant.setId(100L);
        
        // When
        int hashCode = partant.hashCode();
        
        // Then
        assertEquals(131, hashCode);
    }

    @Test
    void shouldTestHashCodeForTransientEntities() {
        // Given
        PartantEntity partant = new PartantEntity("Test", 5);
        
        // When
        int hashCode = partant.hashCode();
        
        // Then
        assertNotEquals(0, hashCode);
    }

    @Test
    void shouldTestSettersAndGetters() {
        // Given
        PartantEntity partant = new PartantEntity();
        List<CourseEntity> courses = List.of(new CourseEntity("Course 1", 1, LocalDate.now()));
        
        // When
        partant.setId(123L);
        partant.setName("New Horse");
        partant.setNumber(10);
        partant.setCourses(courses);
        
        // Then
        assertEquals(123L, partant.getId());
        assertEquals("New Horse", partant.getName());
        assertEquals(10, partant.getNumber());
        assertEquals(courses, partant.getCourses());
    }
}
