package com.hackernews.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
		return hackerNewsService.getLatestStories();
	}
	
	@GetMapping(path = "/top-stories")
	public Flux<Item> getTopStories() {
		return hackerNewsService.getTopStories();
	}
	
}