package com.hackernews.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hackernews.api.model.Item;
import com.hackernews.api.service.HackerNewsService;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/hacker-news")
public class HackerNewsController {

	private static final Logger LOGGER = LoggerFactory.getLogger(HackerNewsController.class);

	@Autowired
	private HackerNewsService hackerNewsService;

	@GetMapping(path = "/latest-stories")
	public Flux<Item> getLatestStories() {
		LOGGER.info("Fetching latest stories...");
		return hackerNewsService.getLatestStories();
	}

	@GetMapping(path = "/top-stories")
	public Flux<Item> getTopStories() {
		LOGGER.info("Fetching top stories...");
		return hackerNewsService.getTopStories();
	}

	@GetMapping(path = "/past-stories")
	public Flux<Item> getPastStories() {
		LOGGER.info("Fetching past stories...");
		return hackerNewsService.getPastStories();
	}

	@GetMapping(path = "/past-stories-paginated")
	public Flux<Item> getPastStoriesPaginated(@RequestParam(name = "page", defaultValue = "0") Integer page,
			@RequestParam(name = "size", defaultValue = "10") Integer size) {
		LOGGER.info("Fetching past stories with pagination...");
		return hackerNewsService.getPastStories(page, size);
	}

}
