package com.pmu2.exec;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
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
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

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
    private PmuCourseConsumer pmuCourseConsumer;

    @MockBean
    private PmuCourseService pmuCourseService;

    @Captor
    ArgumentCaptor<CourseRecord> courseRecordArgumentCaptor;

    @Captor
    ArgumentCaptor<String> topicArgumentCaptor;

    @Captor
    ArgumentCaptor<Integer> partitionArgumentCaptor;

    @Captor
    ArgumentCaptor<Long> offsetArgumentCaptor;

    private ListAppender<ILoggingEvent> listAppender;

    @BeforeAll
    void setUp() {
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
        producer = new DefaultKafkaProducerFactory<>(configs, new StringSerializer(), new StringSerializer()).createProducer();

        Logger logger = (Logger) LoggerFactory.getLogger(PmuCourseConsumer.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @Test
    void testLogKafkaMessages() throws JsonProcessingException {

        CourseRecord newCourse = TestUtil.newCourseRecord("course E");
        String message = objectMapper.writeValueAsString(newCourse);
        producer.send(new ProducerRecord<>(topicName, 0, newCourse.courseId().toString(), message));
        producer.flush();

        // Read the message and assert its properties
        verify(pmuCourseConsumer, timeout(10000).times(1))
                .listenKafkaMessages(courseRecordArgumentCaptor.capture(), topicArgumentCaptor.capture(),
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

    @Test
    void whenMainConsumerSucceeds_thenNoDltMessage() throws Exception {
        CountDownLatch mainTopicCountDownLatch = new CountDownLatch(1);

        doAnswer(invocation -> {
            mainTopicCountDownLatch.countDown();
            return null;
        }).when(pmuCourseConsumer)
                .listenKafkaMessages(any(), any(), any(), any());
        CourseRecord newCourse = TestUtil.newCourseRecord("dlt-fail-main");
        String message = objectMapper.writeValueAsString(newCourse);
        producer.send(new ProducerRecord<>(topicName, 0, newCourse.courseId().toString(), message));
        producer.flush();

        assertThat(mainTopicCountDownLatch.await(5, TimeUnit.SECONDS)).isTrue();
        verify(pmuCourseConsumer, never()).handleDltCourseRecord(any(), any());
    }

    @Test
    void handleDltCourseRecordTest() {
        String pattern = "yyyy-MM-dd HH:mm";
        String topicDtlname = "test-topic-dlttopic";

        // Create a CourseRecord object for testing
        CourseRecord courseRecord = TestUtil.newCourseRecord("dlt-fail-main");
        // Create a Kafka message with headers

        Message<CourseRecord> message = MessageBuilder.withPayload(courseRecord)
                .setHeader(KafkaHeaders.RECEIVED_TOPIC, topicDtlname)
                .build();

        // Call the DLT handler method
        pmuCourseConsumer.handleDltCourseRecord(message.getPayload(), message.getHeaders().get(KafkaHeaders.RECEIVED_TOPIC, String.class));

        // Verify that the log message is printed (you might need to adjust this based on your logging configuration)
        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList).hasSize(1); // Assert that one log message was captured
        String logOutput = logsList.getFirst().getFormattedMessage();

        // This might be tricky to assert exactly
        assertThat(logOutput).contains("DTL TOPIC message : " + courseRecord)
                .contains("topic name : " + topicDtlname)
                .contains("at: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern)));

    }

}
