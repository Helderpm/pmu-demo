package com.pmu2.exec.infrastrure.kafka.producer;

import com.pmu2.exec.domain.CourseRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.RoutingKafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for producing messages to Kafka topics using a RoutingKafkaTemplate.
 * It provides methods to send messages to specific topics based on the provided parameters.
 */
@Slf4j
@Component
public class RoutingKafkaProducer {

    /**
     * The name of the Kafka topic to which messages will be sent.
     * This value is obtained from the application's configuration properties.
     */
    @Value("${spring.kafka.topic.name}")
    private String topicName;

    @Value("${spring.kafka.replication.factor:1}")
    private int replicationFactor;

    @Value("${spring.kafka.partition.number:1}")
    private int partitionNumber;

    /**
     * The RoutingKafkaTemplate instance used to send messages to Kafka topics.
     */
    private RoutingKafkaTemplate routingTemplate;

    /**
     * Sends a message to the "default-topic" Kafka topic.
     *
     * @param message The message to be sent.
     */
    public void sendDefaultTopic(String message) {
        routingTemplate.send("default-topic", message.getBytes());
    }

    /**
     * Sends a Course event message to the Kafka topic specified by the topicName property.
     *
     * @param courseEvent The Course event to be sent.
     */
    public void sendInventoryEvent(CourseRecord courseEvent) {
        routingTemplate.send(topicName, courseEvent);
    }
}
