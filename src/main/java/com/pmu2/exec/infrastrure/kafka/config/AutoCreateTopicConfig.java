package com.pmu2.exec.infrastrure.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * This class is responsible for configuring the auto-creation of Kafka topics.
 * It uses Spring's @Configuration annotation to mark it as a configuration class.
 * The class contains a method that creates a new Kafka topic using the provided topic name, partition count, and replica count.
 */
@Configuration
public class AutoCreateTopicConfig {

    /**
     * The name of the Kafka topic to be created.
     * This value is obtained from the application's properties file using Spring's @Value annotation.
     */
    @Value("${spring.kafka.topic.name}")
    private String topicName;

    /**
     * The number of partitions for the Kafka topic.
     * This value is obtained from the application's properties file using Spring's @Value annotation.
     */
    @Value("${spring.kafka.topic.partition-count}")
    public Integer partition;

    /**
     * The number of replicas for the Kafka topic.
     * This value is obtained from the application's properties file using Spring's @Value annotation.
     */
    @Value("${spring.kafka.topic.replica-count}")
    public Integer replicas;

    /**
     * Creates a new Kafka topic using the provided topic name, partition count, and replica count.
     *
     * @return a NewTopic object representing the Kafka topic to be created
     */
    @Bean
    public NewTopic inventoryEvents() {
        return TopicBuilder.name(topicName)
                .partitions(partition)
                .replicas(replicas)
                .build();
    }
}
