package com.pmu2.exec.infrastrure.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class AutoCreateTopicConfig {

    @Value("${spring.kafka.topic.name}")
    private String topicName;

    @Value("${spring.kafka.topic.partition-count}")
    public Integer partitions;

    @Value("${spring.kafka.topic.replication-factor}")
    private short replicationFactor;

    @Value("${spring.kafka.topic.retention.ms}")
    private Long retentionMs;

    @Value("${spring.kafka.topic.cleanup.policy}")
    private String cleanupPolicy;

    @Bean
    public NewTopic inventoryEvents() {
        return TopicBuilder.name(topicName)
                .partitions(partitions)
                .replicas(replicationFactor)
                .config(TopicConfig.RETENTION_MS_CONFIG, retentionMs.toString())
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, cleanupPolicy)
                .build();
    }

}
