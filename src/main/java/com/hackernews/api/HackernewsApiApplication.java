package com.hackernews.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HackernewsApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(HackernewsApiApplication.class, args);
	}
	
}
