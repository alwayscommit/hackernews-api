package com.hackernews.api.service;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hackernews.api.Constants;
import com.hackernews.api.dao.HackerNewsDAO;
import com.hackernews.api.model.Item;
import reactor.core.publisher.Flux;

@Service
public class HackerNewsServiceImpl implements HackerNewsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(HackerNewsServiceImpl.class);

	private HackerNewsDAO hackerNewsDAO;

	@Autowired
	public HackerNewsServiceImpl(HackerNewsDAO hackerNewsDAO) {
		this.hackerNewsDAO = hackerNewsDAO;
	}

	public Flux<Item> getLatestStories() {
		LOGGER.info("Fetching Top Stories from Hacker-News API...");

//		Get New Story Ids
		Flux<Integer> storyIdStream = hackerNewsDAO.getNewStories();

//		Get All Stories from Story Ids
		Flux<Item> storyList = storyIdStream.flatMap(storyId -> hackerNewsDAO.getStory(storyId))
				.filter(story -> story != null)
				.filter(story -> calculateMinutesFromNow(story.getTime()) <= Constants.CACHE_TIME);

		return storyList;
	}

	private long calculateMinutesFromNow(Long storyTime) {
		return ((Instant.now().getEpochSecond() - storyTime) / 60);
	}

}
