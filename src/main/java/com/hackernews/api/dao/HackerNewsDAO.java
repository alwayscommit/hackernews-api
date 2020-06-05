package com.hackernews.api.dao;


import com.hackernews.api.model.Item;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface HackerNewsDAO {

	public Flux<Integer> getNewStories();

	public Mono<Item> getStory(Integer id);


}
