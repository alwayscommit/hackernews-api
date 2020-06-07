package com.hackernews.api.dao;


import com.hackernews.api.model.Item;
import com.hackernews.api.model.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface HackerNewsDAO {

	public Flux<Integer> getNewStories();

	public Mono<Item> getItem(Integer itemId);

	public Flux<Integer> getTopStories();

	public Mono<User> getUser(String userHandle);

}
