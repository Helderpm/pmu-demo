package com.pmu2.exec.infrastrure.kafka.producer;

import com.pmu2.exec.domain.CourseRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
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

    /**
     * This method creates a new Kafka topic based on the provided configuration properties.
     * topicName         The name of the Kafka topic to be created.
     * partitionNumber   The number of partitions for the Kafka topic.
     * replicationFactor The replication factor for the Kafka topic.
     * @return A NewTopic instance representing the newly created Kafka topic.
     *
     * @Order(-1) - Indicates that this bean should be created before any other beans in the application context.
     * @Bean - Indicates that a method produces a bean to be managed by the Spring container.
     * @NewTopic - Indicates that a method produces a new Kafka topic.
     */
    @Bean
    @Order(-1)
    public NewTopic createNewTopic() {
        return new NewTopic(topicName, partitionNumber, (short) replicationFactor);
    }
}
