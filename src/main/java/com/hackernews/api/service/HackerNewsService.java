package com.hackernews.api.service;

import com.hackernews.api.model.Item;

import reactor.core.publisher.Flux;

public interface HackerNewsService {

	public Flux<Item> getLatestStories();

	public Flux<Item> getTopStories();

	Flux<Item> getPastStories();

	public Flux<Item> getPastStories(Integer page, Integer size);

}
