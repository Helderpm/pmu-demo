package com.pmu2.exec.infrastrure.kafka.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.core.RoutingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Configuration class for Kafka producer.
 * This class provides beans for Kafka producer factory and Kafka template.
 * It also includes a method to create a routing Kafka template for routing messages to different topics.
 */
@Configuration
public class KafkaProducerConfig {

    /**
     * List of bootstrap servers for Kafka.
     * This value is read from the application properties file using Spring's @Value annotation.
     */
    @Value("${spring.kafka.bootstrap-servers}")
    private List<String> bootstrapAddress;

    /**
     * Creates a Kafka producer factory.
     *
     * @return A Kafka producer factory with specified configurations.
     */
    @Bean
    public ProducerFactory<?, ?> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Creates a Kafka template.
     *
     * @return A Kafka template using the producer factory created in the previous method.
     */
    @Bean
    public KafkaTemplate<?, ?> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * Creates a routing Kafka template.
     * This template routes messages to different topics based on the provided pattern.
     *
     * @param context The application context.
     * @param pf The default producer factory.
     * @return A routing Kafka template.
     */
    @Bean
    public RoutingKafkaTemplate routingTemplate(GenericApplicationContext context,
                                                ProducerFactory<Object, Object> pf) {

        Map<String, Object> configs = new HashMap<>(pf.getConfigurationProperties());
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
        DefaultKafkaProducerFactory<Object, Object> bytesPF = new DefaultKafkaProducerFactory<>(configs);
        context.registerBean(DefaultKafkaProducerFactory.class, "bytesPF", bytesPF);

        Map<Pattern, ProducerFactory<Object, Object>> map = new LinkedHashMap<>();
        map.put(Pattern.compile("default-topic"), bytesPF);
        map.put(Pattern.compile("pmu-events"), pf); // Default PF with StringSerializer
        return new RoutingKafkaTemplate(map);
    }

}