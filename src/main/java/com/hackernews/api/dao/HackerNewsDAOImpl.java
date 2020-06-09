package com.hackernews.api.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger LOGGER = LoggerFactory.getLogger(HackerNewsDAOImpl.class);

	@Override
	public Flux<Item> getNewStories() {
		Flux<Integer> newStoryIds = WebClient.create().get().uri(Constants.URL_NEW_STORIES).retrieve()
				.bodyToFlux(Integer.class).onErrorResume(error -> {
					LOGGER.error("Error occurred while fetching New Stories. Error Message :: ", error.getMessage());
					return Flux.empty();
				});
		return newStoryIds.flatMap(storyId -> getItem(storyId));
	}

	@Override
	public Mono<Item> getItem(Integer itemId) {
		return WebClient.create().get().uri(String.format(Constants.URL_ITEM, itemId)).retrieve().bodyToMono(Item.class)
				.onErrorResume(error -> {
					LOGGER.error("Error occurred on fetching an Item. Error Message :: ", error.getMessage());
					return Mono.empty();
				});
	}

	@Override
	public Flux<Item> getTopStories() {
		Flux<Integer> topStoryIds = WebClient.create().get().uri(Constants.URL_TOP_STORIES).retrieve()
				.bodyToFlux(Integer.class).onErrorResume(error -> {
					LOGGER.error("Error occurred while fetching Top Items. Error Message :: ", error.getMessage());
					return Mono.empty();
				});
		return topStoryIds.flatMap(storyId -> getItem(storyId)).filter(item -> item.getType().equals(Type.STORY));
	}

	@Override
	public Mono<User> getUser(String userHandle) {
		return WebClient.create().get().uri(String.format(Constants.URL_USER, userHandle)).retrieve()
				.bodyToMono(User.class).onErrorResume(error -> {
					LOGGER.error("Error occurred while fetching user. Error Message :: ", error.getMessage());
					return Mono.empty();
				});
	}

}
