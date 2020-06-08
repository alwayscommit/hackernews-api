package com.hackernews.api.dao;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.hackernews.api.Constants;
import com.hackernews.api.model.Item;
import com.hackernews.api.model.Type;
import com.hackernews.api.model.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class HackerNewsDAOImpl implements HackerNewsDAO {

	@Override
	public Flux<Item> getNewStories() {
		Flux<Integer> newStoryIds = WebClient.create().get().uri(Constants.URL_NEW_STORIES).retrieve().bodyToFlux(Integer.class);
		return newStoryIds.flatMap(storyId -> getItem(storyId));
	}

	@Override
	public Mono<Item> getItem(Integer itemId) {
		return WebClient.create().get().uri(String.format(Constants.URL_ITEM, itemId)).retrieve()
				.bodyToMono(Item.class);
	}

	@Override
	public Flux<Item> getTopStories() {
		Flux<Integer> topStoryIds = WebClient.create().get().uri(Constants.URL_TOP_STORIES).retrieve()
				.bodyToFlux(Integer.class);
		return topStoryIds.flatMap(storyId -> getItem(storyId)).filter(item -> item.getType().equals(Type.STORY));
	}

	@Override
	public Mono<User> getUser(String userHandle) {
		return WebClient.create().get().uri(String.format(Constants.URL_USER, userHandle)).retrieve()
				.bodyToMono(User.class);
	}

}
