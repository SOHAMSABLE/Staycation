package com.staycation.Staycation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StaycationApplication {

	public static void main(String[] args) {
		SpringApplication.run(StaycationApplication.class, args);
	}

}
