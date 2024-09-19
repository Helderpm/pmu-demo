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

    @Bean
    public NewTopic inventoryEvents() {
        return TopicBuilder.name(topicName)
                .partitions(partition)
                .replicas(replicas)
                .build();
    }

}
