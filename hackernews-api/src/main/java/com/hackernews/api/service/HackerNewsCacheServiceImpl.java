package com.hackernews.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.hackernews.api.Constants;
import com.hackernews.api.model.ui.Story;

@Service
public class HackerNewsCacheServiceImpl implements HackerNewsCacheService {

	@Autowired
	private CacheManager cacheManager;

	@Scheduled(fixedRateString = "${clear.cache.fixed.rate}", initialDelay = 0)
	public void clearCache() {
		cacheManager.getCache(Constants.CACHE_HACKER_NEWS).clear();
	}

	public void cache(String key, List<Story> storyList) {
		cacheManager.getCache(Constants.CACHE_HACKER_NEWS).put(key, storyList);
	}

	@SuppressWarnings("unchecked")
	public List<Story> getCachedList(String key) {
		return cacheManager.getCache(Constants.CACHE_HACKER_NEWS).get(key, List.class);
	}

	public boolean isCached(String key) {
		List<Story> itemList = getCachedList(key);
		return itemList != null && !itemList.isEmpty();
	}

}
