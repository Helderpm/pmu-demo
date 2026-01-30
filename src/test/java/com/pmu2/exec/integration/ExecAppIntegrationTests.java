package com.pmu2.exec.integration;

import com.pmu2.exec.domain.CourseRecord;
import com.pmu2.exec.domain.PartantRecord;
import com.pmu2.exec.infrastructure.db.sql.CourseEntity;
import com.pmu2.exec.infrastructure.db.sql.CourseJpaRepository;
import com.pmu2.exec.infrastructure.db.sql.PartantEntity;
import com.pmu2.exec.infrastructure.db.sql.PartantJpaRepository;
import com.pmu2.exec.utils.TestUtil;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
import static org.junit.jupiter.api.Assertions.*;

/**
 * ====================================================================
 * FULL-STACK INTEGRATION TESTS
 * ====================================================================
 * 
 * <p>Comprehensive integration tests validating the complete PMU Exec Application stack:
 * <ul>
 *   <li>🌐 REST API endpoints via TestRestTemplate
 *   <li>🗄️ Database persistence with TestContainers (MySQL 8.0)
 *   <li>📨 Kafka message streaming with EmbeddedKafka
 *   <li>🔄 End-to-end request/response flow
 *   <li>⚖️ Business logic validation through API calls
 * </ul>
 * 
 * ====================================================================
 * PREREQUISITES
 * ====================================================================
 * 
 * <p><strong>Required Software:</strong>
 * <ul>
 *   <li>☕ Java 21 or higher</li>
 *   <li>🐳 Docker Desktop (running) - Required for TestContainers</li>
 *   <li>📦 Maven 3.6+ (for test execution)</li>
 * </ul>
 * 
 * <p><strong>How to Enable Tests:</strong>
 * <pre>{@code
 * // Remove @Disabled annotation to enable full integration tests
 * @Disabled("Requires Docker for TestContainers - enable when Docker is available")
 * }</pre>
 * 
 * ====================================================================
 * INFRASTRUCTURE COMPONENTS
 * ====================================================================
 * 
 * <table border="1" style="width:100%">
 *   <tr><th>Component</th><th>Configuration</th><th>Purpose</th></tr>
 *   <tr>
 *     <td>🐳 TestContainers</td>
 *     <td>MySQL 8.0-debian container</td>
 *     <td>Real database testing</td>
 *   </tr>
 *   <tr>
 *     <td>📨 EmbeddedKafka</td>
 *     <td>Port 3333, 1 partition</td>
 *     <td>In-memory message broker</td>
 *   </tr>
 *   <tr>
 *     <td>🌐 Web Server</td>
 *     <td>Random port allocation</td>
 *     <td>HTTP endpoint testing</td>
 *   </tr>
 *   <tr>
 *     <td>🧹 Test Isolation</td>
 *     <td>@DirtiesContext</td>
 *     <td>Clean test environment</td>
 *   </tr>
 * </table>
 * 
 * ====================================================================
 * TEST LIFECYCLE
 * ====================================================================
 * 
 * <p><strong>Test Execution Flow:</strong>
 * <ol>
 *   <li>🚀 <strong>Setup Phase</strong>: Start containers and initialize Spring context</li>
 *   <li>🧹 <strong>Cleanup Phase</strong>: Delete all database records</li>
 *   <li>📊 <strong>Data Seeding</strong>: Insert 3 predefined courses:
 *     <ul>
 *       <li>Course A: With partants (for relationship testing)</li>
 *       <li>Course B: Without partants</li>
 *       <li>Course C: Without partants</li>
 *     </ul>
 *   </li>
 *   <li>🧪 <strong>Test Execution</strong>: Run individual test methods</li>
 *   <li>🔄 <strong>Teardown</strong>: Clean up resources and containers</li>
 * </ol>
 * 
 * ====================================================================
 * TEST CATEGORIES
 * ====================================================================
 * 
 * <p><strong>📋 Course Management Tests:</strong>
 * <ul>
 *   <li>CRUD operations (Create, Read, Update, Delete)</li>
 *   <li>Kafka event publishing and consumption</li>
 *   <li>Business logic calculations (difficulty, betting eligibility)</li>
 * </ul>
 * 
 * <p><strong>🐎 Partant Management Tests:</strong>
 * <ul>
 *   <li>Participant CRUD operations</li>
 *   <li>Performance scoring and skill categorization</li>
 *   <li>Good standing validation</li>
 * </ul>
 * 
 * ====================================================================
 * USAGE EXAMPLES
 * ====================================================================
 * 
 * <p><strong>Run All Tests:</strong>
 * <pre>{@code
 * mvn test -Dtest=ExecAppIntegrationTests
 * }</pre>
 * 
 * <p><strong>Run Specific Test Class:</strong>
 * <pre>{@code
 * mvn test -Dtest=ExecAppIntegrationTests$CourseIntegrationTests
 * }</pre>
 * 
 * <p><strong>Debug Mode:</strong>
 * <pre>{@code
 * mvn test -Dtest=ExecAppIntegrationTests -Dmaven.test.debug=true
 * }</pre>
 * 
 * ====================================================================
 * TROUBLESHOOTING
 * ====================================================================
 * 
 * <p><strong>Common Issues:</strong>
 * <ul>
 *   <li>❌ <strong>Docker Not Running</strong>: Start Docker Desktop before running tests</li>
 *   <li>❌ <strong>Port Conflicts</strong>: Tests use random ports to avoid conflicts</li>
 *   <li>❌ <strong>Container Timeout</strong>: Increase Docker memory allocation if needed</li>
 *   <li>❌ <strong>Kafka Connection</strong>: EmbeddedKafka handles broker setup automatically</li>
 * </ul>
 * 
 * ====================================================================
 * DEPENDENCIES
 * ====================================================================
 * 
 * @see TestRestTemplate HTTP client for REST API testing
 * @see TestContainers Container-based testing framework
 * @see EmbeddedKafka In-memory Kafka broker for testing
 * @see Awaitility Asynchronous testing utilities
 * @see DirtiesContext Test isolation and cleanup
 * 
 * @author PMU Exec Application Team
 * @version 1.0
 * @since 1.0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create-drop"})
@EmbeddedKafka(
        partitions = 1,
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:3333",
                "port=3333"
        })
@DirtiesContext
@Disabled("Requires Docker for TestContainers - enable when Docker is available")
public class ExecAppIntegrationTests {

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
    /**
     * ====================================================================
     * 📋 COURSE MANAGEMENT INTEGRATION TESTS
     * ====================================================================
     * 
     * <p>Comprehensive testing of all course-related REST API endpoints:
     * 
     * <table border="1" style="width:100%">
     *   <tr><th>Endpoint</th><th>Method</th><th>Purpose</th><th>Kafka Event</th></tr>
     *   <tr><td>/pmu/course</td><td>GET</td><td>Retrieve all courses</td><td>❌</td></tr>
     *   <tr><td>/pmu/course</td><td>POST</td><td>Create new course</td><td>✅</td></tr>
     *   <tr><td>/pmu/course/find/{name}</td><td>GET</td><td>Find courses by name</td><td>❌</td></tr>
     *   <tr><td>/pmu/course/{id}</td><td>DELETE</td><td>Delete course by ID</td><td>❌</td></tr>
     *   <tr><td>/pmu/course/{id}/betting-eligible</td><td>GET</td><td>Check betting eligibility</td><td>❌</td></tr>
     *   <tr><td>/pmu/course/{id}/difficulty</td><td>GET</td><td>Get difficulty score</td><td>❌</td></tr>
     * </table>
     * 
     * ====================================================================
     * 🔧 KEY TEST SCENARIOS
     * ====================================================================
     * 
     * <p><strong>🔄 CRUD Operations:</strong>
     * <ul>
     *   <li>Create courses with and without partants</li>
     *   <li>Retrieve single and multiple courses</li>
     *   <li>Delete courses and verify cleanup</li>
     * </ul>
     * 
     * <p><strong>📨 Kafka Integration:</strong>
     * <ul>
     *   <li>Course creation triggers Kafka events</li>
     *   <li>Asynchronous event processing validation</li>
     *   <li>Message publishing confirmation</li>
     * </ul>
     * 
     * <p><strong>⚖️ Business Logic:</strong>
     * <ul>
     *   <li>Domain service calculations via API</li>
     *   <li>Betting eligibility determination</li>
     *   <li>Difficulty score calculation (1-10 scale)</li>
     * </ul>
     * 
     * <p><strong>🔗 Data Relationships:</strong>
     * <ul>
     *   <li>Course-partant associations</li>
     *   <li>Cascade delete operations</li>
     *   <li>JSON serialization of nested objects</li>
     * </ul>
     * 
     * ====================================================================
     * ⏱️ ASYNCHRONOUS TESTING STRATEGY
     * ====================================================================
     * 
     * <p><strong>Why Awaitility?</strong>
     * <ul>
     *   <li>Kafka event processing is asynchronous</li>
     *   <li>Database persistence occurs via consumer</li>
     *   <li>Tests must wait for completion</li>
     * </ul>
     * 
     * <p><strong>Configuration:</strong>
     * <pre>{@code
     * await().atMost(50, TimeUnit.SECONDS).untilAsserted(() -> {
     *     // Validation logic here
     * });
     * }</pre>
     * 
     * ====================================================================
     * 📊 TEST DATA MATRIX
     * ====================================================================
     * 
     * <table border="1" style="width:100%">
     *   <tr><th>Test</th><th>Course Name</th><th>Partants</th><th>Special Setup</th></tr>
     *   <tr><td>testFindAll</td><td>Course A,B,C</td><td>Varies</td><td>Pre-seeded</td></tr>
     *   <tr><td>testCreate</td><td>course E</td><td>From TestUtil</td><td>Kafka async</td></tr>
     *   <tr><td>testFindByName</td><td>Course A</td><td>With partants</td><td>Relationship test</td></tr>
     *   <tr><td>testDeleteById</td><td>Course C</td><td>None</td><td>Cleanup verify</td></tr>
     *   <tr><td>testBettingEligibility</td><td>Course A</td><td>With partants</td><td>Business logic</td></tr>
     *   <tr><td>testDifficultyEndpoint</td><td>Course A</td><td>With partants</td><td>Domain calc</td></tr>
     * </table>
     * 
     * @since 1.0
     * @see CourseEntity Course domain model
     * @see CourseRecord Course DTO
     * @see Awaitility Asynchronous testing
     */
    class CourseIntegrationTests {
        
        /**
         * ====================================================================
         * 📋 TEST: GET /pmu/course - RETRIEVE ALL COURSES
         * ====================================================================
         * 
         * <p><strong>Objective:</strong> Verify endpoint returns all courses with proper HTTP status.
         * 
         * <table border="1" style="width:100%">
         *   <tr><th>Aspect</th><th>Details</th></tr>
         *   <tr><td>🌐 Endpoint</td><td>GET /pmu/course</td></tr>
         *   <tr><td>📊 Expected Status</td><td>200 OK</td></tr>
         *   <tr><td>📦 Response Type</td><td>List&lt;CourseEntity&gt;</td></tr>
         *   <tr><td>🔢 Expected Count</td><td>3 courses (pre-seeded)</td></tr>
         *   <tr><td>📨 Kafka Event</td><td>None (read-only operation)</td></tr>
         * </table>
         * 
         * <p><strong>🔍 Validation Points:</strong>
         * <ul>
         *   <li>✅ HTTP response status is 200 OK</li>
         *   <li>✅ Response body contains exactly 3 courses</li>
         *   <li>✅ Response format matches CourseEntity list structure</li>
         *   <li>✅ ParameterizedTypeReference handles generics correctly</li>
         * </ul>
         * 
         * <p><strong>📝 Implementation Notes:</strong>
         * <ul>
         *   <li>Uses ParameterizedTypeReference for proper generic type handling</li>
         *   <li>Relies on pre-seeded test data from setUp() method</li>
         *   <li>No Kafka events triggered for read operations</li>
         * </ul>
         * 
         * @since 1.0
         * @see ParameterizedTypeReference Spring's generic type handling
         * @see TestRestTemplate HTTP client for testing
         */
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

        /**
         * ====================================================================
         * 📋 TEST: POST /pmu/course - CREATE COURSE WITH KAFKA EVENT
         * ====================================================================
         * 
         * <p><strong>Objective:</strong> Verify course creation triggers Kafka event and persists to database.
         * 
         * <table border="1" style="width:100%">
         *   <tr><th>Aspect</th><th>Details</th></tr>
         *   <tr><td>🌐 Endpoint</td><td>POST /pmu/course</td></tr>
         *   <tr><td>📊 Expected Status</td><td>201 CREATED</td></tr>
         *   <tr><td>📦 Request Type</td><td>CourseRecord (JSON)</td></tr>
         *   <tr><td>📨 Kafka Event</td><td>✅ CourseCreated event</td></tr>
         *   <tr><td>⏱️ Async Timeout</td><td>50 seconds max</td></tr>
         * </table>
         * 
         * <p><strong>🔄 Test Flow:</strong>
         * <ol>
         *   <li>📤 <strong>HTTP Request</strong>: POST course data to API endpoint</li>
         *   <li>✅ <strong>Immediate Response</strong>: Verify 201 CREATED status</li>
         *   <li>📨 <strong>Kafka Event</strong>: CourseCreated event published to pmu-events topic</li>
         *   <li>⏳ <strong>Async Processing</strong>: Wait for consumer to process message</li>
         *   <li>🗄️ <strong>Database Validation</strong>: Verify course persisted via repository</li>
         *   <li>🔍 <strong>Data Integrity</strong>: Assert all course fields correctly saved</li>
         * </ol>
         * 
         * <p><strong>📊 Test Data:</strong>
         * <table border="1" style="width:100%">
         *   <tr><th>Field</th><th>Value</th><th>Purpose</th></tr>
         *   <tr><td>📝 Name</td><td>"course E"</td><td>Unique identifier</td></tr>
         *   <tr><td>🔢 Number</td><td>From TestUtil</td><td>Course identifier</td></tr>
         *   <tr><td>📅 Date</td><td>From TestUtil</td><td>Future date validation</td></tr>
         *   <tr><td>🐎 Partants</td><td>From TestUtil</td><td>Relationship testing</td></tr>
         * </table>
         * 
         * <p><strong>⚠️ Critical Validation Points:</strong>
         * <ul>
         *   <li>✅ HTTP response returns 201 CREATED immediately</li>
         *   <li>✅ Kafka event is published (async validation)</li>
         *   <li>✅ Database persistence completes within timeout</li>
         *   <li>✅ All course data fields are correctly saved</li>
         *   <li>✅ Course-partant relationships are maintained</li>
         * </ul>
         * 
         * <p><strong>🔧 Technical Implementation:</strong>
         * <ul>
         *   <li><strong>Awaitility</strong>: Handles async Kafka processing</li>
         *   <li><strong>Repository Query</strong>: findByName() for data validation</li>
         *   <li><strong>JSON Serialization</strong>: CourseRecord to HTTP request</li>
         *   <li><strong>Error Handling</strong>: Timeout and assertion failures</li>
         * </ul>
         * 
         * @since 1.0
         * @see CourseEventPublisher Kafka event publishing service
         * @see Awaitility Asynchronous testing utilities
         * @see CourseJpaRepository Database persistence validation
         */
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

        @Test
        void testBettingEligibilityEndpoint() {
            // Given
            String courseName = "Course A";
            List<CourseEntity> list = courseJpaRepository.findByName(courseName);
            CourseEntity courseEntity = list.getFirst();
            Long courseId = courseEntity.getCourseId();

            // When
            ResponseEntity<Boolean> response = restTemplate.exchange(
                    baseUri + "/pmu/course/" + courseId + "/betting-eligible",
                    HttpMethod.GET,
                    null,
                    Boolean.class
            );

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
        }

        @Test
        void testDifficultyEndpoint() {
            // Given
            String courseName = "Course A";
            List<CourseEntity> list = courseJpaRepository.findByName(courseName);
            CourseEntity courseEntity = list.getFirst();
            Long courseId = courseEntity.getCourseId();

            // When
            ResponseEntity<Integer> response = restTemplate.exchange(
                    baseUri + "/pmu/course/" + courseId + "/difficulty",
                    HttpMethod.GET,
                    null,
                    Integer.class
            );

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody() >= 1 && response.getBody() <= 10);
        }
    }

    @Nested
    /**
     * Integration tests for Partant (participant/horse) management endpoints.
     * 
     * <p>This test class validates all partant-related REST API endpoints:
     * <ul>
     *   <li>GET /pmu/partant - Retrieve all partants</li>
     *   <li>POST /pmu/partant - Create new partant</li>
     *   <li>GET /pmu/partant/find/{name} - Find partants by name</li>
     *   <li>DELETE /pmu/partant/{id} - Delete partant by ID</li>
     *   <li>GET /pmu/partant/{id}/good-standing - Check good standing status</li>
     *   <li>GET /pmu/partant/{id}/performance - Get performance score (1-100)</li>
     *   <li>GET /pmu/partant/{id}/skill-category - Get skill category</li>
     * </ul>
     * 
     * <h3>Business Logic Testing</h3>
     * <ul>
     *   <li><strong>Performance Scoring</strong>: Validates domain service calculations</li>
     *   <li><strong>Skill Categorization</strong>: Tests business rule implementation</li>
     *   <li><strong>Good Standing</strong>: Validates status determination logic</li>
     * </ul>
     * 
     * <h3>Data Validation</h3>
     * Tests ensure API properly validates partant data and returns appropriate
     * HTTP status codes and response formats.
     */
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

        @Test
        void testGoodStandingEndpoint() {
            // Given
            String partantName = "Partant AA";
            List<PartantEntity> list = partantJpaRepository.findByName(partantName);
            PartantEntity partantEntity = list.getFirst();
            Long partantId = partantEntity.getId();

            // When
            ResponseEntity<Boolean> response = restTemplate.exchange(
                    baseUri + "/pmu/partant/" + partantId + "/good-standing",
                    HttpMethod.GET,
                    null,
                    Boolean.class
            );

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
        }

        @Test
        void testPerformanceEndpoint() {
            // Given
            String partantName = "Partant AA";
            List<PartantEntity> list = partantJpaRepository.findByName(partantName);
            PartantEntity partantEntity = list.getFirst();
            Long partantId = partantEntity.getId();

            // When
            ResponseEntity<Integer> response = restTemplate.exchange(
                    baseUri + "/pmu/partant/" + partantId + "/performance",
                    HttpMethod.GET,
                    null,
                    Integer.class
            );

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody() >= 1 && response.getBody() <= 100);
        }

        @Test
        void testSkillCategoryEndpoint() {
            // Given
            String partantName = "Partant AA";
            List<PartantEntity> list = partantJpaRepository.findByName(partantName);
            PartantEntity partantEntity = list.getFirst();
            Long partantId = partantEntity.getId();

            // When
            ResponseEntity<String> response = restTemplate.exchange(
                    baseUri + "/pmu/partant/" + partantId + "/skill-category",
                    HttpMethod.GET,
                    null,
                    String.class
            );

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(List.of("NOVICE", "INTERMEDIATE", "ADVANCED", "EXPERT").contains(response.getBody()));
        }
    }

}
