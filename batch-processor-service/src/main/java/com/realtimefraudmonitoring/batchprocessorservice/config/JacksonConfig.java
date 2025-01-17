package com.realtimefraudmonitoring.batchprocessorservice.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.ACCEPT_FLOAT_AS_INT);
        objectMapper.disable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        objectMapper.disable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        objectMapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.enable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE);
        objectMapper.enable(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS);
        objectMapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);

        //to verify usage
        System.out.println("Custom ObjectMapper initialized");
        return objectMapper;
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        return new MappingJackson2HttpMessageConverter(objectMapper);
    }
}
