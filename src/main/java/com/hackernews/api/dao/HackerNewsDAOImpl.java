package com.hackernews.api.dao;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.hackernews.api.Constants;
import com.hackernews.api.model.Item;
import com.hackernews.api.model.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class HackerNewsDAOImpl implements HackerNewsDAO {

	@Override
	public Flux<Integer> getNewStories() {
		return WebClient.create().get().uri(Constants.URL_NEW_STORIES).retrieve().bodyToFlux(Integer.class);
	}

	@Override
	public Mono<Item> getItem(Integer itemId) {
		return WebClient.create().get().uri(String.format(Constants.URL_ITEM, itemId)).retrieve()
				.bodyToMono(Item.class);
	}

	@Override
	public Flux<Integer> getTopStories() {
		return WebClient.create().get().uri(Constants.URL_TOP_STORIES).retrieve().bodyToFlux(Integer.class);
	}

	@Override
	public Mono<User> getUser(String userHandle) {
		return WebClient.create().get().uri(String.format(Constants.URL_USER, userHandle)).retrieve()
				.bodyToMono(User.class);
	}

}
