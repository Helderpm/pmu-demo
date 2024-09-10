package com.pmu2.exec;

import com.pmu2.exec.infrastrure.inbound.CourseEntity;
import com.pmu2.exec.infrastrure.inbound.CourseJpaRepository;
import com.pmu2.exec.infrastrure.inbound.PartantEntity;
import com.pmu2.exec.infrastrure.inbound.PartantJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testing with TestRestTemplate and @Testcontainers (image mysql:8.0-debian)
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// activate automatic startup and stop of containers
@Testcontainers
// JPA drop and create table, good for testing
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create-drop"})
class ExecAppIntegrationTests {

    @LocalServerPort
    private Integer port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String BASEURI;

    @Autowired
    CourseJpaRepository courseJpaRepository;

    @Autowired
    PartantJpaRepository partantJpaRepository;

    // static, all tests share this postgres container
    @Container
    @ServiceConnection
    static MySQLContainer<?> postgres = new MySQLContainer<>(
            "mysql:8.0-debian"
    );

    @BeforeEach
    void testSetUp() {

        BASEURI = "http://localhost:" + port;

        // Delete all records from the database before each test
        courseJpaRepository.deleteAll();
        partantJpaRepository.deleteAll();

        // Insert some test data
        CourseEntity b1 = new CourseEntity("Course A",
                99,
                LocalDate.of(2023, 8, 28));

        b1.setPartants(getListParticipantA());

        CourseEntity b2 = new CourseEntity("Course B",
                89,
                LocalDate.of(2023, 9, 29));
        CourseEntity b3 = new CourseEntity("Course C",
                79,
                LocalDate.of(2023, 7, 27));

        courseJpaRepository.saveAll(List.of(b1, b2, b3));
    }

    private List<PartantEntity> getListParticipantA() {
        PartantEntity p1 = new PartantEntity("Partant AA", 909);
        PartantEntity p2 = new PartantEntity("Partant AB", 809);
        PartantEntity p3 = new PartantEntity("Partant AC", 709);

        return List.of(p1, p2, p3);
    }

    @Test
    void testFindAll() {

        // find all Course and return List<CourseEntity>
        ParameterizedTypeReference<List<CourseEntity>> typeRef = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<List<CourseEntity>> response = restTemplate.exchange(
                BASEURI + "/pmu/course",
                HttpMethod.GET,
                null,
                typeRef
        );



        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    void testCreate() {

        // Create a new CourseEntity E
        String courseName = "Course E";
        CourseEntity newBook = new CourseEntity(courseName, 9, LocalDate.parse("2023-09-14"));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<CourseEntity> request = new HttpEntity<>(newBook, headers);

        // test POST save
        ResponseEntity<CourseEntity> responseEntity =
                restTemplate.postForEntity(BASEURI + "/pmu/course", request, CourseEntity.class);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        // find Course E
        List<CourseEntity> list = courseJpaRepository.findByName(courseName);

        // Test Course E details
        CourseEntity course = list.getFirst();
        assertEquals(courseName, course.getName());
        assertEquals(9, course.getNumber());
        assertEquals(LocalDate.of(2023, 9, 14), course.getDate());

    }

    @Test
    void testFindByName() {
        String courseName = "Course A";
        List<CourseEntity> list = courseJpaRepository.findByName(courseName);
        CourseEntity courseEntityA = list.isEmpty() ? null : list.getFirst();

        ParameterizedTypeReference<List<CourseEntity>> typeRef = new ParameterizedTypeReference<>() {
        };

        // find Course A
        ResponseEntity<List<CourseEntity>> response = restTemplate.exchange(
                BASEURI + "/pmu/course/find/" + courseName,
                HttpMethod.GET,
                null,
                typeRef
        );

        // test response code
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<CourseEntity> listResponse = response.getBody();
        assert listResponse != null;

        assertEquals(1, listResponse.size());

        // Test Course A details
        CourseEntity courseEntity = list.getFirst();
        assertEquals(courseName, courseEntity.getName());
        assertEquals(99, courseEntity.getNumber());
        assertEquals(LocalDate.of(2023, 8, 28), courseEntity.getDate());

        // Compare PartantEntity objects
        assertEquals(courseEntityA.getPartants().size(), listResponse.getFirst().getPartants().size());

    }

    @Test
    void testDeleteById() {

        String courseName = "Course C";
        List<CourseEntity> list = courseJpaRepository.findByName(courseName);
        CourseEntity courseEntityA = list.getFirst();

        // get course A id
        Long id = courseEntityA.getCourseId();

        // delete by id
        ResponseEntity<Void> response = restTemplate.exchange(
                BASEURI + "/pmu/course/" + id,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        // test 204
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        // find Book A again, ensure no result
        List<CourseEntity> listAgain = courseJpaRepository.findByName(courseName);
        assertEquals(0, listAgain.size());

    }

}
