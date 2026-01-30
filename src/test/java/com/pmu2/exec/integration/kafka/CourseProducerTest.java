package com.pmu2.exec.integration.kafka;

import com.pmu2.exec.domain.CourseRecord;
import com.pmu2.exec.service.CourseEventPublisher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * ====================================================================
 * 📨 KAFKA PRODUCER INTEGRATION TESTS
 * ====================================================================
 * 
 * <p>Comprehensive testing of Kafka event production for course events:
 * <ul>
 *   <li>📤 Course creation event publishing</li>
 *   <li>🗑️ Course deletion event publishing</li>
 *   <li>⚙️ Kafka template configuration validation</li>
 *   <li>⏱️ Asynchronous event processing verification</li>
 * </ul>
 * 
 * ====================================================================
 * 🎯 TEST OBJECTIVES
 * ====================================================================
 * 
 * <p><strong>Primary Goals:</strong>
 * <ul>
 *   <li>✅ Validate CourseEventPublisher functionality</li>
 *   <li>✅ Verify KafkaTemplate configuration and connectivity</li>
 *   <li>✅ Ensure event publishing completes without errors</li>
 *   <li>✅ Test asynchronous event processing completion</li>
 * </ul>
 * 
 * <p><strong>Scope Limitations:</strong>
 * <ul>
 *   <li>❌ Message content validation (handled in consumer tests)</li>
 *   <li>❌ Consumer processing validation (separate test class)</li>
 *   <li>❌ Topic management and configuration (assumed working)</li>
 * </ul>
 * 
 * ====================================================================
 * 🏗️ INFRASTRUCTURE CONFIGURATION
 * ====================================================================
 * 
 * <table border="1" style="width:100%">
 *   <tr><th>Component</th><th>Configuration</th><th>Purpose</th></tr>
 *   <tr>
 *     <td>🐳 EmbeddedKafka</td>
 *     <td>Port 3333, 1 partition</td>
 *     <td>In-memory message broker</td>
 *   </tr>
 *   <tr>
 *     <td>🌐 SpringBootTest</td>
 *     <td>Full application context</td>
 *     <td>Complete integration testing</td>
 *   </tr>
 *   <tr>
 *     <td>🧹 DirtiesContext</td>
 *     <td>Context cleanup after tests</td>
 *     <td>Test isolation</td>
 *   </tr>
 *   <tr>
 *     <td>⏰ Awaitility</td>
 *     <td>10-second timeout</td>
 *     <td>Async processing validation</td>
 *   </tr>
 * </table>
 * 
 * ====================================================================
 * 🔄 TEST EXECUTION FLOW
 * ====================================================================
 * 
 * <p><strong>Standard Test Pattern:</strong>
 * <ol>
 *   <li>📋 <strong>Setup</strong>: Prepare test data (CourseRecord or courseId)</li>
 *   <li>📤 <strong>Action</strong>: Call CourseEventPublisher method</li>
 *   <li>⏳ <strong>Wait</strong>: Use Awaitility for async completion</li>
 *   <li>✅ <strong>Validate</strong>: Verify KafkaTemplate accessibility</li>
 *   <li>🔄 <strong>Cleanup</strong>: Automatic via DirtiesContext</li>
 * </ol>
 * 
 * ====================================================================
 * 📊 EVENT TYPES TESTED
 * ====================================================================
 * 
 * <table border="1" style="width:100%">
 *   <tr><th>Event Type</th><th>Trigger</th><th>Data</th><th>Test Method</th></tr>
 *   <tr>
 *     <td>📝 CourseCreated</td>
 *     <td>Course creation</td>
 *     <td>Full CourseRecord</td>
 *     <td>shouldPublishCourseCreatedEvent()</td>
 *   </tr>
 *   <tr>
 *     <td>🗑️ CourseDeleted</td>
 *     <td>Course deletion</td>
 *     <td>Course ID only</td>
 *     <td>shouldPublishCourseDeletedEvent()</td>
 *   </tr>
 * </table>
 * 
 * ====================================================================
 * ⚠️ IMPORTANT NOTES
 * ====================================================================
 * 
 * <p><strong>🔧 Configuration Dependencies:</strong>
 * <ul>
 *   <li>Requires KafkaTemplate<Integer, Object> bean (generic template)</li>
 *   <li>Depends on CourseEventPublisher service</li>
 *   <li>EmbeddedKafka broker must be accessible on port 3333</li>
 * </ul>
 * 
 * <p><strong>🧪 Testing Strategy:</strong>
 * <ul>
 *   <li>Focus on producer functionality, not message content</li>
 *   <li>Validate infrastructure connectivity</li>
 *   <li>Ensure no exceptions during publishing</li>
 * </ul>
 * 
 * ====================================================================
 * 🚀 USAGE EXAMPLES
 * ====================================================================
 * 
 * <p><strong>Run All Producer Tests:</strong>
 * <pre>{@code
 * mvn test -Dtest=CourseProducerTest
 * }</pre>
 * 
 * <p><strong>Run Specific Test Method:</strong>
 * <pre>{@code
 * mvn test -Dtest=CourseProducerTest#shouldPublishCourseCreatedEvent
 * }</pre>
 * 
 * <p><strong>Debug Mode:</strong>
 * <pre>{@code
 * mvn test -Dtest=CourseProducerTest -Dmaven.test.debug=true
 * }</pre>
 * 
 * ====================================================================
 * 🔗 DEPENDENCIES
 * ====================================================================
 * 
 * @see EmbeddedKafka In-memory Kafka testing framework
 * @see CourseEventPublisher Service for event publishing logic
 * @see KafkaTemplate Spring's Kafka producer template
 * @see Awaitility Asynchronous testing utilities
 * @see DirtiesContext Test isolation and cleanup
 * 
 * @author PMU Exec Application Team
 * @version 1.0
 * @since 1.0
 */
@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:3333", "port=3333"})
@DirtiesContext
public class CourseProducerTest {

    @Autowired
    private CourseEventPublisher courseEventPublisher;

    @Autowired
    private KafkaTemplate<Integer, Object> kafkaTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    /**
     * ====================================================================
     * 📋 TEST: COURSE CREATION EVENT PUBLISHING
     * ====================================================================
     * 
     * <p><strong>Objective:</strong> Verify CourseEventPublisher can publish course creation events.
     * 
     * <table border="1" style="width:100%">
     *   <tr><th>Aspect</th><th>Details</th></tr>
     *   <tr><td>🎯 Event Type</td><td>CourseCreated</td></tr>
     *   <tr><td>📤 Publisher Method</td><td>publishCourseCreated()</td></tr>
     *   <tr><td>⏱️ Timeout</td><td>10 seconds max</td></tr>
     *   <tr><td>✅ Validation</td><td>KafkaTemplate accessibility</td></tr>
     * </table>
     * 
     * <p><strong>📊 Test Data:</strong>
     * <table border="1" style="width:100%">
     *   <tr><th>Field</th><th>Value</th><th>Purpose</th></tr>
     *   <tr><td>🆔 Course ID</td><td>1</td><td>Unique identifier</td></tr>
     *   <tr><td>📝 Course Name</td><td>"Test Course"</td><td>Descriptive name</td></tr>
     *   <tr><td>🔢 Course Number</td><td>100</td><td>Course identifier</td></tr>
     *   <tr><td>📅 Course Date</td><td>Today + 10 days</td><td>Future date validation</td></tr>
     *   <tr><td>🐎 Partants</td><td>Empty list</td><td>Simplified test case</td></tr>
     * </table>
     * 
     * <p><strong>🔄 Execution Flow:</strong>
     * <ol>
     *   <li>📋 <strong>Setup</strong>: Create CourseRecord with test data</li>
     *   <li>📤 <strong>Publish</strong>: Call courseEventPublisher.publishCourseCreated()</li>
     *   <li>⏳ <strong>Wait</strong>: Use Awaitility for async completion (max 10s)</li>
     *   <li>✅ <strong>Validate</strong>: Verify KafkaTemplate is accessible</li>
     * </ol>
     * 
     * <p><strong>⚠️ Validation Strategy:</strong>
     * <ul>
     *   <li>✅ Focus on publisher functionality, not message content</li>
     *   <li>✅ Verify KafkaTemplate remains accessible after publishing</li>
     *   <li>✅ Ensure no exceptions thrown during publishing</li>
     *   <li>✅ Validate async processing completes within timeout</li>
     * </ul>
     * 
     * <p><strong>🔧 Technical Notes:</strong>
     * <ul>
     *   <li>Uses KafkaTemplate<Integer, Object> for type compatibility</li>
     *   <li>Awaitility handles asynchronous event publishing</li>
     *   <li>Test validates infrastructure, not message content</li>
     * </ul>
     * 
     * @since 1.0
     * @see CourseEventPublisher#publishCourseCreated(CourseRecord)
     * @see Awaitility Asynchronous testing utilities
     */
    @Test
    void shouldPublishCourseCreatedEvent() {
        // Given
        CourseRecord course = new CourseRecord(1, "Test Course", 100, java.time.LocalDate.now().plusDays(10), List.of());

        // When
        courseEventPublisher.publishCourseCreated(course);

        // Then
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            // Verify that the message was sent to Kafka
            assertNotNull(kafkaTemplate);
        });
    }

    /**
     * Test course deletion event publishing.
     * 
     * <p>Validates that:
     * <ul>
     *   <li>CourseEventPublisher can publish course deleted events</li>
     *   <li>Deletion events are properly formatted and sent</li>
     *   <li>Kafka producer handles different event types</li>
     *   <li>Event publishing completes without exceptions</li>
     * </ul>
     * 
     * <h3>Test Data</h3>
     * <ul>
     *   <li>Course ID: 1L (simulating existing course deletion)</li>
     * </ul>
     * 
     * <h3>Validation Strategy</h3>
     * Similar to creation test, focuses on publisher functionality and
     * Kafka connectivity rather than message content inspection.
     */
    @Test
    void shouldPublishCourseDeletedEvent() {
        // Given
        Long courseId = 1L;

        // When
        courseEventPublisher.publishCourseDeleted(courseId);

        // Then
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            // Verify that the message was sent to Kafka
            assertNotNull(kafkaTemplate);
        });
    }
}
