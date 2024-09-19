package com.pmu2.exec.infrastrure.kafka.producer;

import com.pmu2.exec.domain.CourseRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * This service is responsible for sending messages to a Kafka topic.
 * It uses a KafkaTemplate to send messages with a specified key and value.
 * The sendMessage method sends a message asynchronously and returns a CompletableFuture.
 * The method also handles success and failure scenarios by calling handleSuccess and handleFailure methods respectively.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PmuProducerService {

    /**
     * The name of the Kafka topic to send messages to.
     */
    @Value("${spring.kafka.topic.name}")
    private String topicName;

    /**
     * The KafkaTemplate used to send messages.
     */
    private final KafkaTemplate<Integer, CourseRecord> kafkaTemplate;

    /**
     * Sends a message to the Kafka topic asynchronously.
     *
     * @param message The message to send.
     * @return A CompletableFuture that completes when the message is sent or an error occurs.
     */
    public CompletableFuture<SendResult<Integer, CourseRecord>> sendMessageToKafka(CourseRecord message) {

        var key = message.courseId();

        var completableFuture = kafkaTemplate.send(topicName, key, message);

        return completableFuture.whenComplete(((sendResult, throwable) -> {
            if (throwable != null) {
                handleFailure(key, message, throwable);
            } else {
                handleSuccess(key, message, sendResult);
            }
        }));
    }

    /**
     * Handles the success scenario when a message is sent.
     *
     * @param key The key of the sent message.
     * @param value The value of the sent message.
     * @param sendResult The result of the send operation.
     */
    private void handleSuccess(Integer key, Object value, SendResult<Integer, CourseRecord> sendResult) {
        log.info("Message sent successfully for the key: {} and the value: {}, partition is: {}",
                key, value, sendResult.getRecordMetadata().partition());
    }

    /**
     * Handles the failure scenario when a message cannot be sent.
     *
     * @param key The key of the failed message.
     * @param value The value of the failed message.
     * @param throwable The exception that occurred during the send operation.
     */
    private void handleFailure(Integer key, Object value, Throwable throwable) {
        log.error("Error sending message for the key: {} and the value: {} and exception is {}", key, value, throwable.getMessage(), throwable);
    }

}
