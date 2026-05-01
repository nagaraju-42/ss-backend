package com.pastrypoint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PastryPointBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(PastryPointBackendApplication.class, args);
	}

}
