package com.pmu2.exec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pmu2.exec.domain.CourseRecord;
import com.pmu2.exec.infrastrure.kafka.producer.PmuProducerService;
import com.pmu2.exec.utils.TestUtil;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@EmbeddedKafka(topics = {"pmu-events-test"}, partitions = 2)
@SpringBootTest(properties = "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CourseProducerTest {

    private BlockingQueue<ConsumerRecord<String, String>> records;

    private KafkaMessageListenerContainer<String, String> container;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private PmuProducerService producer;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    void setUp() {
        DefaultKafkaConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(getConsumerProperties());
        ContainerProperties containerProperties = new ContainerProperties("pmu-events-test");
        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        records = new LinkedBlockingQueue<>();
        container.setupMessageListener((MessageListener<String, String>) records::add);
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    @Test
    void testWriteToKafkaWithCurse() throws InterruptedException, JsonProcessingException {
        // Create a new CourseEntity E
        CourseRecord newCourse = TestUtil.newCourseRecord("course E");
        producer.sendMessageToKafka(newCourse);

        // Read the message (John Wick user) with a test consumer from Kafka and assert its properties
        ConsumerRecord<String, String> message = records.poll(500, TimeUnit.MILLISECONDS);
        assertNotNull(message);
        CourseRecord result = objectMapper.readValue(message.value(), CourseRecord.class);
        assertNotNull(result);
        assertEquals(newCourse.courseId(), result.courseId());
        assertEquals(newCourse.name(), result.name());
        assertEquals(newCourse.number(), result.number());
        assertEquals(newCourse.date(), result.date());
        assertEquals(0, result.partants().size());
    }

    @Test
    void testWriteToKafkaWithCursePartant() throws InterruptedException, JsonProcessingException {
        // Create a new CourseEntity E
        CourseRecord newCourse = TestUtil.newCourseRecordwithParant("course E");
        producer.sendMessageToKafka(newCourse);

        // Read the message (John Wick user) with a test consumer from Kafka and assert its properties
        ConsumerRecord<String, String> message = records.poll(500, TimeUnit.MILLISECONDS);
        assertNotNull(message);
        CourseRecord result = objectMapper.readValue(message.value(), CourseRecord.class);
        assertNotNull(result);
        assertEquals(newCourse.courseId(), result.courseId());
        assertEquals(newCourse.name(), result.name());
        assertEquals(newCourse.number(), result.number());
        assertEquals(newCourse.date(), result.date());
        assertEquals(newCourse.partants().size(), result.partants().size());
    }

    private Map<String, Object> getConsumerProperties() {
        return Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString(),
                ConsumerConfig.GROUP_ID_CONFIG, "consumer",
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true",
                ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "10",
                ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "60000",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    }

    @AfterAll
    void tearDown() {
        container.stop();
    }
}
