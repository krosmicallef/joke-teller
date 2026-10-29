package com.christophermicallef.joketeller.sender;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {
		"com.christophermicallef.joketeller.services",
		"com.christophermicallef.joketeller.sender"
})
@EnableScheduling
@EntityScan("com.christophermicallef.joketeller.services.entity")
@EnableJpaRepositories("com.christophermicallef.joketeller.services.repository")
public class JokeTellerSenderApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(JokeTellerSenderApplication.class);
		application.setWebApplicationType(WebApplicationType.NONE);
		application.run(args);
	}

	@Override
	public void run(String... args) {
		// Add sender startup behavior here.
	}
}
