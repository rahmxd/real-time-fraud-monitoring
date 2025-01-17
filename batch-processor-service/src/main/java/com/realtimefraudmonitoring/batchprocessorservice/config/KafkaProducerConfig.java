package com.realtimefraudmonitoring.batchprocessorservice.config;

import com.realtimefraudmonitoring.avro.BatchEvent;
import com.realtimefraudmonitoring.avro.TransactionEvent;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

    @Bean
    public ProducerFactory<String, TransactionEvent> transactionProducerFactory() {
        Map<String, Object> configs = kafkaProperties.buildProducerProperties();
        configs.put("value.serializer", KafkaAvroSerializer.class);
        configs.put("schema.registry.url", "http://localhost:8081");
        return new DefaultKafkaProducerFactory<>(configs);
    }

    @Bean
    public KafkaTemplate<String, TransactionEvent> transactionKafkaTemplate() {
        return new KafkaTemplate<>(transactionProducerFactory());
    }

    @Bean
    public ProducerFactory<String, BatchEvent> batchProducerFactory() {
        Map<String, Object> configs = kafkaProperties.buildProducerProperties();
        configs.put("value.serializer", KafkaAvroSerializer.class);
        configs.put("schema.registry.url", "http://localhost:8081");
        return new DefaultKafkaProducerFactory<>(configs);
    }

    @Bean
    public KafkaTemplate<String, BatchEvent> batchKafkaTemplate() {
        return new KafkaTemplate<>(batchProducerFactory());
    }
}
