package com.pmu2.exec;

import com.pmu2.exec.domain.CourseRecord;
import com.pmu2.exec.domain.PartantRecord;
import com.pmu2.exec.infrastrure.db.sql.CourseEntity;
import com.pmu2.exec.infrastrure.db.sql.CourseJpaRepository;
import com.pmu2.exec.infrastrure.db.sql.PartantEntity;
import com.pmu2.exec.infrastrure.db.sql.PartantJpaRepository;
import com.pmu2.exec.utils.TestUtil;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.pmu2.exec.utils.TestUtil.getParticipantEntityListA;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testing with TestRestTemplate and @Testcontainers (image mysql:8.0-debian)
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create-drop"})
@EmbeddedKafka(
        partitions = 1,
        controlledShutdown = false,
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:3333",
                "port=3333"
        })
@DirtiesContext
class ExecAppIntegrationTests {

    @LocalServerPort
    private Integer port;

    @Autowired
    KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUri;

    @Autowired
    CourseJpaRepository courseJpaRepository;

    @Autowired
    PartantJpaRepository partantJpaRepository;

    // static, all tests share this postgres container
    @Container
    private static final MySQLContainer<?> postgres =
            new MySQLContainer<>("mysql:8.0-debian");

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @BeforeEach
    void testSetUp() {
        baseUri = "http://localhost:" + port;

        // Delete all records from the database before each test
        courseJpaRepository.deleteAll();
        partantJpaRepository.deleteAll();

        // Insert some test data
        CourseEntity b1 = new CourseEntity("Course A",
                99,
                LocalDate.of(2023, 8, 28));

        b1.setPartants(getParticipantEntityListA());

        CourseEntity b2 = new CourseEntity("Course B",
                89,
                LocalDate.of(2023, 9, 29));
        CourseEntity b3 = new CourseEntity("Course C",
                79,
                LocalDate.of(2023, 7, 27));

        courseJpaRepository.saveAll(List.of(b1, b2, b3));
    }

    @Nested
    class CourseIntegrationTests {
        @Test
        void testFindAll() {

            // find all Course and return List<CourseEntity>
            ParameterizedTypeReference<List<CourseEntity>> typeRef = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<List<CourseEntity>> response = restTemplate.exchange(
                    baseUri + "/pmu/course",
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
            String courseE = "course E";
            CourseRecord newCourse = TestUtil.newCourseRecord(courseE);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");
            HttpEntity<CourseRecord> request = new HttpEntity<>(newCourse, headers);

            // test POST save
            ResponseEntity<CourseRecord> responseEntity =
                    restTemplate.postForEntity(baseUri + "/pmu/course", request, CourseRecord.class);

            assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

            // Replace sleep() with await() method
            // Use Awaitility to wait for the consumer to process the message
            Awaitility.await().atMost(50, TimeUnit.SECONDS).untilAsserted(() -> {
                List<CourseEntity> list = courseJpaRepository.findByName(courseE);
                assertThat(list).isNotEmpty();

                CourseEntity course = list.getFirst();
                assertEquals(newCourse.name(), course.getName());
                assertEquals(newCourse.number(), course.getNumber());
                assertEquals(newCourse.date(), course.getDate());
            });
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
                    baseUri + "/pmu/course/find/" + courseName,
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
            assert courseEntityA != null;
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
                    baseUri + "/pmu/course/" + id,
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );

            // test 204
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

            // find Course A again, ensure no result
            List<CourseEntity> listAgain = courseJpaRepository.findByName(courseName);
            assertEquals(0, listAgain.size());
        }
    }

    @Nested
    class PartantRecordIntegrationTests {
        @Test
        void testPartantFindAll() {

            // find all Partant and return List<PartantEntity>
            ParameterizedTypeReference<List<PartantRecord>> typeRef =
                    new ParameterizedTypeReference<>() {
            };
            ResponseEntity<List<PartantRecord>> response = restTemplate.exchange(
                    baseUri + "/pmu/partant",
                    HttpMethod.GET,
                    null,
                    typeRef
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(2, Objects.requireNonNull(response.getBody()).size());
        }

        @Test
        void testPartantCreate() {

            // Create a new CourseEntity E
            String partantName = "Partant EE";
            PartantRecord newPartant = new PartantRecord(12, partantName, 14);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");
            HttpEntity<PartantRecord> request = new HttpEntity<>(newPartant, headers);

            // test POST save
            ResponseEntity<PartantRecord> responseEntity =
                    restTemplate.postForEntity(baseUri + "/pmu/partant", request, PartantRecord.class);

            assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

            // find Course E
            List<PartantEntity> list = partantJpaRepository.findByName(partantName);

            // Test Course E details
            PartantEntity partant1 = list.getFirst();
            assertEquals(partantName, partant1.getName());
            assertEquals(14, partant1.getNumber());

        }

        @Test
        void testPartantFindByName() {
            String partantName = "Partant AA";
            List<PartantEntity> entityList = partantJpaRepository.findByName(partantName);

            ParameterizedTypeReference<List<PartantRecord>> typeRef =
                    new ParameterizedTypeReference<>() { };

            // find partant AA
            ResponseEntity<List<PartantRecord>> response = restTemplate.exchange(
                    baseUri + "/pmu/partant/find/" + partantName,
                    HttpMethod.GET,
                    null,
                    typeRef
            );

            // test response code
            assertEquals(HttpStatus.OK, response.getStatusCode());

            List<PartantRecord> listResponse = response.getBody();
            assert listResponse != null;

            assertEquals(1, listResponse.size());

            // Test partant A details
            PartantEntity partantEntity = entityList.getFirst();
            assertEquals(partantName, partantEntity.getName());
            assertEquals(909, partantEntity.getNumber());

        }

        @Test
        void testPartantDeleteById() {

            String partantName = "Partant AC";

            PartantEntity p3 = new PartantEntity(partantName, 709);
            partantJpaRepository.save(p3);
            List<PartantEntity> list = partantJpaRepository.findByName(partantName);

            // get partant A id
            Long id = list.getFirst().getId();

            // delete by id
            ResponseEntity<Void> response = restTemplate.exchange(
                    baseUri + "/pmu/partant/" + id,
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );

            // test 204
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

            // find partant A again, ensure no result
            List<PartantEntity> listAgain = partantJpaRepository.findByName(partantName);
            assertEquals(0, listAgain.size());

        }

    }

}
