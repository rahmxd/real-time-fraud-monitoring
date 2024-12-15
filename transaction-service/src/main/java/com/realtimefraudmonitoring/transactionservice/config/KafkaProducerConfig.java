package com.realtimefraudmonitoring.transactionservice.config;

import com.realtimefraudmonitoring.transactionservice.dto.TransactionEvent;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    private final KafkaProperties kafkaProperties;

    public KafkaProducerConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Profile("test")
    @Bean
    public ProducerFactory<String, String> producerFactoryString() {
        System.out.println("Active Profile: Using StringSerializer for DTO validation only.");
        Map<String, Object> configs = kafkaProperties.buildProducerProperties();
        configs.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        configs.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return new DefaultKafkaProducerFactory<>(configs);
    }

    @Profile("test")
    @Bean
    public KafkaTemplate<String, String> kafkaTemplateString() {
        System.out.println("Active Profile: KafkaTemplate<String, String> initialized for DTO validation.");
        return new KafkaTemplate<>(producerFactoryString());
    }

    @Profile("schema")
    @Bean
    public ProducerFactory<String, TransactionEvent> producerFactoryTransactionEvent() {
        System.out.println("Active Profile: Using Schema Registry with KafkaJsonSchemaSerializer.");
        Map<String, Object> configs = kafkaProperties.buildProducerProperties();
        configs.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        configs.put("value.serializer", "io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializer");
        configs.put("schema.registry.url", "http://localhost:8081");
        return new DefaultKafkaProducerFactory<>(configs);
    }

    @Profile("schema")
    @Bean
    public KafkaTemplate<String, TransactionEvent> kafkaTemplateTransactionEvent() {
        System.out.println("Active Profile: KafkaTemplate<String, TransactionEvent> initialized for Schema Registry.");
        return new KafkaTemplate<>(producerFactoryTransactionEvent());
    }
}
