package com.realtimefraudmonitoring.batchprocessorservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BatchProcessorServiceApplication {

	public static void main(String[] args) {

		SpringApplication.run(BatchProcessorServiceApplication.class, args);
	}

}
