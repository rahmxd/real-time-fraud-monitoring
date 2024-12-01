package com.realtimefraudmonitoring.fraudscoringservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class FraudScoringServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FraudScoringServiceApplication.class, args);
	}

}
