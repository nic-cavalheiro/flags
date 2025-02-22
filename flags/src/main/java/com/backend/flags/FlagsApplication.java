package com.backend.flags;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.backend.flags.controllers")
public class FlagsApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlagsApplication.class, args);
	}

}
