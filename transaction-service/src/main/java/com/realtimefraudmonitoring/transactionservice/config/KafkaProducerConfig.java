package com.realtimefraudmonitoring.transactionservice.config;


import com.realtimefraudmonitoring.transactionservice.dto.TransactionEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;

import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    private final KafkaProperties kafkaProperties;

    //reads all kafka properties from application.yml into kafkaproperties bean
    public KafkaProducerConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

//    @Bean
//    public ProducerFactory<String, TransactionEvent> producerFactory() {
//        return new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties());
//    }
//
//    @Bean
//    public KafkaTemplate<String, TransactionEvent> kafkaTemplate() {
//        return new KafkaTemplate<>(producerFactory());
//    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configs = kafkaProperties.buildProducerProperties();
        configs.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        configs.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return new DefaultKafkaProducerFactory<>(configs);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
