package com.realtimefraudmonitoring.transactionservice.config;

import com.realtimefraudmonitoring.avro.TransactionEvent;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    private final KafkaProperties kafkaProperties;

    public KafkaConsumerConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public ConsumerFactory<String, TransactionEvent> consumerFactory() {
        Map<String, Object> configs = kafkaProperties.buildConsumerProperties();
        configs.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        configs.put("value.deserializer", "io.confluent.kafka.serializers.KafkaAvroDeserializer");
        configs.put("schema.registry.url", "http://localhost:8081"); // Schema Registry URL
        configs.put("specific.avro.reader", "true"); // Enable specific Avro class deserialization
        return new DefaultKafkaConsumerFactory<>(configs);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TransactionEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TransactionEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
