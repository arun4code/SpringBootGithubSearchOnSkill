package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages={"com.controller", "com.service"})
public class GithubScrapperApplication {

	public static void main(String[] args) {
		SpringApplication.run(GithubScrapperApplication.class, args);
	}
}
