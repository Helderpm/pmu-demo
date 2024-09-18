package com.pmu2.exec;

import com.pmu2.exec.infrastrure.db.sql.CourseEntity;
import com.pmu2.exec.infrastrure.db.sql.CourseJpaRepository;
import com.pmu2.exec.infrastrure.db.sql.PartantEntity;
import com.pmu2.exec.infrastrure.db.sql.PartantJpaRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.List;

import static com.pmu2.exec.utils.TestUtil.getParticipantEntityListA;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext
class RepositoryJpaTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Nested
    class CourseJpaRepositoryTests {
        @Autowired
        private CourseJpaRepository courseJpaRepository;

        @Test
        void testGetByFirstName() {
            //Given
            LocalDate localDateC1 = LocalDate.parse("2023-09-14");
            LocalDate localDateC2 = LocalDate.parse("2023-09-16");

            testEntityManager.persistAndFlush(stubCourse("C1", 2, localDateC1, getParticipantEntityListA()));
            testEntityManager.persistAndFlush(stubCourse("C1", 4, localDateC2, null));
            testEntityManager.persistAndFlush(stubCourse("P1", 6, localDateC2, null));
            //when
            List<CourseEntity> course2 = courseJpaRepository.findByName("C1");
            //then
            assertFalse(course2.isEmpty());
            assertEquals(2, course2.size());

            CourseEntity entityC1 = course2.getFirst();
            assertEquals("C1", entityC1.getName());
            assertEquals(2, entityC1.getNumber());
            assertEquals(localDateC1, entityC1.getDate());
            assertEquals(getParticipantEntityListA().size(), entityC1.getPartants().size());

            CourseEntity entityC2 = course2.get(1);
            assertEquals("C1", entityC2.getName());
            assertEquals(4, entityC2.getNumber());
            assertEquals(localDateC2, entityC2.getDate());
            assertNull(entityC2.getPartants());
        }

        private CourseEntity stubCourse(String name, int number, LocalDate date, List<PartantEntity> partant) {
            return new CourseEntity(name, number, date, partant);
        }
    }

    @Nested
    class PartantJpaRepositoryTests {

        @Autowired
        private PartantJpaRepository partantJpaRepository;

        @Test
        void testGetByFirstName() {
            //Given
            testEntityManager.persistAndFlush(stubPartant("Jonh", 2));
            testEntityManager.persistAndFlush(stubPartant("Jonh", 4));
            testEntityManager.persistAndFlush(stubPartant("Wick", 4));
            //when
            List<PartantEntity> partantEntityList = partantJpaRepository.findByName("Jonh");
            //then
            assertFalse(partantEntityList.isEmpty());
            assertEquals(2, partantEntityList.size());

            PartantEntity entityP1 = partantEntityList.getFirst();
            assertEquals("Jonh", entityP1.getName());
            assertEquals(2, entityP1.getNumber());

            PartantEntity entityP2 = partantEntityList.get(1);
            assertEquals("Jonh", entityP2.getName());
            assertEquals(4, entityP2.getNumber());
        }

        private PartantEntity stubPartant(String name, int number) {
            return new PartantEntity(name, number);
        }
    }

}

