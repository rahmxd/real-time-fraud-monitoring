package com.realtimefraudmonitoring.batchprocessorservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Bean(name = "batchProcessorExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); //min number of threads
        executor.setMaxPoolSize(10); //max num of threads
        executor.setQueueCapacity(500); //queue cap before rejecting tasks
        executor.setThreadNamePrefix("BatchProcessor-"); //thread name
        executor.initialize();
        return executor;
    }
}
