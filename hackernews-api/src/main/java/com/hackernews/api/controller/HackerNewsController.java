package com.hackernews.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hackernews.api.model.Item;
import com.hackernews.api.model.ui.Comment;
import com.hackernews.api.model.ui.Story;
import com.hackernews.api.service.HackerNewsService;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/hacker-news")
public class HackerNewsController {

	private static final Logger LOGGER = LoggerFactory.getLogger(HackerNewsController.class);

	private HackerNewsService hackerNewsService;

	@Autowired
	public HackerNewsController(HackerNewsService hackerNewsService) {
		this.hackerNewsService = hackerNewsService;
	}

	@GetMapping(path = "/top-stories")
	public Flux<Story> getTopStories() {
		LOGGER.info("Fetching top stories...");
		return hackerNewsService.getTopStories();
	}

	@GetMapping(path = "/top-comments")
	public Flux<Comment> getTopComments(@RequestParam(name = "storyId") Integer storyId) {
		LOGGER.info("Fetching top comments...");
		return hackerNewsService.getTopComments(storyId);
	}

	@GetMapping(path = "/past-stories")
	public Flux<Story> getPastStories() {
		LOGGER.info("Fetching past stories...");
		return hackerNewsService.getPastStories();
	}

	@GetMapping(path = "/past-stories-paged")
	public Flux<Story> getPastStoriesPaginated(@RequestParam(name = "page", defaultValue = "0") Integer page,
			@RequestParam(name = "size", defaultValue = "10") Integer size) {
		LOGGER.info("Fetching past stories with pagination...");
		return hackerNewsService.getPastStories(page, size);
	}
	
	@GetMapping(path = "/latest-stories")
	public Flux<Story> getLatestStories() {
		LOGGER.info("Fetching latest stories...");
		return hackerNewsService.getLatestStories();
	}

}
