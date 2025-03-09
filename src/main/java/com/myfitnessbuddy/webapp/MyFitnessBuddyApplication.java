package com.myfitnessbuddy.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.myfitnessbuddy.repository") // Ensure correct package path
public class MyFitnessBuddyApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyFitnessBuddyApplication.class, args);
	}

}
