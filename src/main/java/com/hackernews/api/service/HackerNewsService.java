package com.hackernews.api.service;

import com.hackernews.api.model.Item;

import reactor.core.publisher.Flux;

public interface HackerNewsService {

	public Flux<Item> getLatestStories();
	
}
