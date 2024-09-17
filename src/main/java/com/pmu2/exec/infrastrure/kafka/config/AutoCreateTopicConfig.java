package com.pmu2.exec.infrastrure.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class AutoCreateTopicConfig {

    @Value("${spring.kafka.topic.name}")
    private String topicName;

    @Value("${spring.kafka.topic.partition-count}")
    public Integer partition;

    @Value("${spring.kafka.topic.replica-count}")
    public Integer replicas;

    private int partitionNumber;

    @Bean
    public NewTopic inventoryEvents() {
        return TopicBuilder.name(topicName)
                .partitions(partition)
                .replicas(replicas)
                .build();
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
//    @Bean
//    @Order(-1)
//    public NewTopic createNewTopic() {
//        return new NewTopic(topicName, partitionNumber, (short) replicationFactor);
//    }
}
