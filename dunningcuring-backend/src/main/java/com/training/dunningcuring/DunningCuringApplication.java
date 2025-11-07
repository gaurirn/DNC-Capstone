package com.training.dunningcuring;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling; // <-- IMPORT THIS

@SpringBootApplication
@EnableScheduling // <-- ADD THIS ANNOTATION
public class DunningCuringApplication {
	public static void main(String[] args) {
		SpringApplication.run(DunningCuringApplication.class, args);
	}
}
