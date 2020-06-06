package com.hackernews.api.service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.hackernews.api.Constants;
import com.hackernews.api.dao.HackerNewsDAO;
import com.hackernews.api.model.Item;
import com.hackernews.api.model.Type;
import com.hackernews.api.repository.ItemRepository;

import reactor.core.publisher.Flux;

@Service
public class HackerNewsServiceImpl implements HackerNewsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(HackerNewsServiceImpl.class);

	private HackerNewsDAO hackerNewsDAO;

	private ItemRepository itemRepository;

	private static long lastRequest = 0L;

	@Autowired
	private HackerNewsCacheService hackerNewsCacheService;

	@Autowired
	public HackerNewsServiceImpl(HackerNewsDAO hackerNewsDAO, ItemRepository itemRepository) {
		this.hackerNewsDAO = hackerNewsDAO;
		this.itemRepository = itemRepository;
	}

	public Flux<Item> getLatestStories() {

		List<Item> latestStories = hackerNewsCacheService.getCachedList(Constants.KEY_LATEST_STORY);
		if (isCached(latestStories)) {
			LOGGER.info("Fetching Latest Stories from Local Cache...");
			return Flux.fromIterable(latestStories);
		} else {
			LOGGER.info("Fetching Latest Stories from Hacker-News API...");

			Flux<Integer> newStoryIds = hackerNewsDAO.getNewStories();

//			TODO REMOVE LATER
//			Flux<Integer> newStoryIds = Flux.fromStream(hackerNewsDAO.getNewStories().toStream().limit(20));

			Flux<Item> storyList = newStoryIds.flatMap(storyId -> hackerNewsDAO.getStory(storyId))
					.filter(story -> calculateElapsedMinutes(story.getTime()) <= Constants.CACHE_TIME);
			recordRequestTime();

			latestStories = storyList.toStream().collect(Collectors.toList());
			hackerNewsCacheService.cache(Constants.KEY_LATEST_STORY, latestStories);

			return itemRepository.saveAll(latestStories);

		}
	}

	public Flux<Item> getTopStories() {
		List<Item> topStories = hackerNewsCacheService.getCachedList(Constants.KEY_TOP_STORY);
		if (isCached(topStories)) {
			LOGGER.info("Fetching Top Stories from Local Cache...");
			return Flux.fromIterable(topStories);
		} else {
			LOGGER.info("Fetching Top Stories from Hacker-News API...");

			Flux<Integer> topStoryIds = hackerNewsDAO.getTopStories();

//			TODO REMOVE LATER
//			Flux<Integer> topStoryIds = Flux.fromStream(hackerNewsDAO.getTopStories().toStream().limit(20));

			Flux<Item> storyList = topStoryIds.flatMap(storyId -> hackerNewsDAO.getStory(storyId))
					.filter(item -> item.getType().equals(Type.STORY))
					.sort((story, nextSory) -> nextSory.getScore().compareTo(story.getScore()));
			recordRequestTime();

			topStories = storyList.toStream().limit(10).collect(Collectors.toList());

			hackerNewsCacheService.cache(Constants.KEY_TOP_STORY, topStories);

			return itemRepository.saveAll(topStories);
		}
	}

	private void recordRequestTime() {
		lastRequest = Instant.now().getEpochSecond();
	}

	private boolean isCached(List<Item> itemList) {

		if (requestLessThanCacheTime() && itemList != null && !itemList.isEmpty()) {
			return true;
		} else {
//			clearCache();
			return false;
		}

	}

	private boolean requestLessThanCacheTime() {
		return calculateElapsedMinutes(lastRequest) < Constants.CACHE_TIME;
	}

	private long calculateElapsedMinutes(Long storyTime) {
		return ((nowEpochSeconds() - storyTime) / Constants.MINUTES_DIVISOR);
	}

	private long nowEpochSeconds() {
		return (Instant.now().getEpochSecond());
	}

}
