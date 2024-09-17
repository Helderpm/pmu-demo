package com.pmu2.exec.infrastrure.kafka.consumer;

import com.pmu2.exec.domain.CourseRecord;
import com.pmu2.exec.infrastrure.kafka.exeption.AException;
import com.pmu2.exec.service.PmuCourseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PmuCourseConsumer {

    private final PmuCourseService pmuCourseService;

    public PmuCourseConsumer(PmuCourseService pmuCourseService) {
        this.pmuCourseService = pmuCourseService;
    }

    @KafkaListener(topics = "${spring.kafka.topic.name}",
            concurrency = "${spring.kafka.consumer.level.concurrency:3}")
    @RetryableTopic(
            backoff = @Backoff(delay = 1000, multiplier = 1.0, maxDelay = 5000),
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            retryTopicSuffix = "-retrytopic",
            dltTopicSuffix = "-dlttopic",
            include = {AException.class}
    )
    public void logKafkaMessages(@Payload CourseRecord courseRecord,
                                 @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                 @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
                                 @Header(KafkaHeaders.OFFSET) Long offset) {
        log.info("Received a message contains a user information with id {}, from {} topic, " +
                "{} partition, and {} offset", courseRecord.courseId(), topic, partition, offset);
        try {
            pmuCourseService.save(courseRecord);
        } catch (Exception e) {
            throw new AException("Exception : " + courseRecord + " error !");
        }
    }

    @DltHandler
    public void dtl(
            String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("DTL TOPIC message : {}, topic name : {}", message, topic);
    }

}
