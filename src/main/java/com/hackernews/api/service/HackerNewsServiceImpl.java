package com.hackernews.api.service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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

	private HackerNewsCacheService hackerNewsCacheService;

	private static final String SORT_FIELD = "score";

	@Autowired
	public HackerNewsServiceImpl(HackerNewsDAO hackerNewsDAO, ItemRepository itemRepository,
			HackerNewsCacheService hackerNewsCacheService) {
		this.hackerNewsDAO = hackerNewsDAO;
		this.itemRepository = itemRepository;
		this.hackerNewsCacheService = hackerNewsCacheService;
	}

	public Flux<Item> getLatestStories() {

		if (hackerNewsCacheService.isCached(Constants.KEY_LATEST_STORY)) {
			LOGGER.info("Fetching Latest Stories from Local Cache...");
			return Flux.fromIterable(hackerNewsCacheService.getCachedList(Constants.KEY_LATEST_STORY));
		} else {
			LOGGER.info("Fetching Latest Stories from Hacker-News API...");

//			Flux<Integer> newStoryIds = hackerNewsDAO.getNewStories();

//			TODO REMOVE LATER
			Flux<Integer> newStoryIds = Flux.fromStream(hackerNewsDAO.getNewStories().toStream().limit(20));

			Flux<Item> storyList = newStoryIds.flatMap(storyId -> hackerNewsDAO.getStory(storyId))
					.filter(story -> calculateElapsedMinutes(story.getTime()) <= Constants.CACHE_TIME);

			List<Item> latestStories = storyList.toStream().collect(Collectors.toList());

			hackerNewsCacheService.cache(Constants.KEY_LATEST_STORY, latestStories);

			return itemRepository.saveAll(latestStories);

		}
	}

	public Flux<Item> getTopStories() {

		if (hackerNewsCacheService.isCached(Constants.KEY_TOP_STORY)) {
			LOGGER.info("Fetching Top Stories from Local Cache...");
			return Flux.fromIterable(hackerNewsCacheService.getCachedList(Constants.KEY_TOP_STORY));
		} else {
			LOGGER.info("Fetching Top Stories from Hacker-News API...");

//			Flux<Integer> topStoryIds = hackerNewsDAO.getTopStories();

//			TODO REMOVE LATER
			Flux<Integer> topStoryIds = Flux.fromStream(hackerNewsDAO.getTopStories().toStream().limit(20));

			Flux<Item> storyList = topStoryIds.flatMap(storyId -> hackerNewsDAO.getStory(storyId))
					.filter(item -> item.getType().equals(Type.STORY))
					.sort((story, nextSory) -> nextSory.getScore().compareTo(story.getScore()));

			List<Item> topStories = storyList.toStream().limit(10).collect(Collectors.toList());

			hackerNewsCacheService.cache(Constants.KEY_TOP_STORY, topStories);

			return itemRepository.saveAll(topStories);
		}
	}

	@Override
	public Flux<Item> getPastStories() {
		LOGGER.info("Fetching past stories...");
		return itemRepository.findAll(Sort.by(SORT_FIELD).descending());
	}

	@Override
	public Flux<Item> getPastStories(Integer page, Integer size) {
		LOGGER.info(String.format("Fetching past stories with page %s and size %s", page, size));
		return itemRepository.findAll(Sort.by(SORT_FIELD).descending()).skip(page * size).take(size);
	}

	private long calculateElapsedMinutes(Long storyTime) {
		return ((nowEpochSeconds() - storyTime) / Constants.MINUTES_DIVISOR);
	}

	private long nowEpochSeconds() {
		return (Instant.now().getEpochSecond());
	}

}
