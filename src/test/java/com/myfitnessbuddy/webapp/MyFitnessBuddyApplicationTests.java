package com.myfitnessbuddy.webapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootTest
@EnableJpaRepositories("com.myfitnessbuddy.repository")
class MyFitnessBuddyApplicationTests {

	@Test
	void contextLoads() {
	}

}
