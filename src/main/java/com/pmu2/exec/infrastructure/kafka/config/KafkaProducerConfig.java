package com.pmu2.exec.infrastructure.kafka.config;

import com.pmu2.exec.domain.CourseRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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

@Configuration
class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private List<String> bootstrapAddress;

    /**
     * Creates common Kafka producer configuration properties.
     *
     * @return Map containing the shared configuration properties
     */
    private Map<String, Object> createCommonProducerConfig() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
          ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(
          ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        configProps.put(
          ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return configProps;
    }

    @Bean
    public ProducerFactory<Integer, CourseRecord> producerFactory() {
        return new DefaultKafkaProducerFactory<>(createCommonProducerConfig());
    }

    @Bean
    @Primary
    public KafkaTemplate<Integer, CourseRecord> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ProducerFactory<Integer, Object> genericProducerFactory() {
        return new DefaultKafkaProducerFactory<>(createCommonProducerConfig());
    }

    @Bean
    public KafkaTemplate<Integer, Object> genericKafkaTemplate() {
        return new KafkaTemplate<>(genericProducerFactory());
    }

    //Enable routingTemplate: If you need to route messages to different topics based on their content,
    //uncomment the routingTemplate method and configure it according to your needs.
    //@Bean
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

