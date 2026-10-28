package com.christophermicallef.joketellersender;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.christophermicallef.joketeller")
@EntityScan("com.christophermicallef.joketeller.entity")
@EnableJpaRepositories("com.christophermicallef.joketeller.repository")
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
