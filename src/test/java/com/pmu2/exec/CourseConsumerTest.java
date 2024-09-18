package com.pmu2.exec;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pmu2.exec.domain.CourseRecord;
import com.pmu2.exec.infrastrure.kafka.consumer.PmuCourseConsumer;
import com.pmu2.exec.service.PmuCourseService;
import com.pmu2.exec.utils.TestUtil;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@EmbeddedKafka
@SpringBootTest(properties = "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CourseConsumerTest {

    /**
     * The name of the Kafka topic to send messages to.
     */
    @Value("${spring.kafka.topic.name}")
    private String topicName;

    private Producer<String, String> producer;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private ObjectMapper objectMapper;

    @SpyBean
    private PmuCourseConsumer userKafkaConsumer;

    @MockBean
    private PmuCourseService userService;

    @Captor
    ArgumentCaptor<CourseRecord> courseRecordArgumentCaptor;

    @Captor
    ArgumentCaptor<String> topicArgumentCaptor;

    @Captor
    ArgumentCaptor<Integer> partitionArgumentCaptor;

    @Captor
    ArgumentCaptor<Long> offsetArgumentCaptor;

    @BeforeAll
    void setUp() {
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
        producer = new DefaultKafkaProducerFactory<>(configs, new StringSerializer(), new StringSerializer()).createProducer();
    }

    @Test
    void testLogKafkaMessages() throws JsonProcessingException {

        CourseRecord newCourse = TestUtil.newCourseRecord("course E");
        String message = objectMapper.writeValueAsString(newCourse);
        producer.send(new ProducerRecord<>(topicName, 0, newCourse.courseId().toString(), message));
        producer.flush();

        // Read the message and assert its properties
        verify(userKafkaConsumer, timeout(10000).times(1))
                .logKafkaMessages(courseRecordArgumentCaptor.capture(), topicArgumentCaptor.capture(),
                        partitionArgumentCaptor.capture(), offsetArgumentCaptor.capture());

        CourseRecord captorValue = courseRecordArgumentCaptor.getValue();
        assertNotNull(captorValue);
        assertEquals(newCourse.courseId(), captorValue.courseId());
        assertEquals(newCourse.name(), captorValue.name());
        assertEquals(newCourse.number(), captorValue.number());
        assertEquals(newCourse.date(), captorValue.date());
        assertEquals(0, captorValue.partants().size());
        assertEquals(topicName, topicArgumentCaptor.getValue());
        assertEquals(0, partitionArgumentCaptor.getValue());
        assertEquals(0, offsetArgumentCaptor.getValue());
    }

    @AfterAll
    void shutdown() {
        producer.close();
    }
}
