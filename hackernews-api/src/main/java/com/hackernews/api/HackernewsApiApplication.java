package com.hackernews.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
public class HackernewsApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(HackernewsApiApplication.class, args);
	}

}
