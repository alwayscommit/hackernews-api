package com.hackernews.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;

import com.hackernews.api.Constants;
import com.hackernews.api.model.Item;

public class HackerNewsCacheServiceImpl implements HackerNewsCacheService{
	
	@Autowired
	private CacheManager cacheManager;
	
	public void cache(String key, List<Item> itemList) {
		cacheManager.getCache(Constants.CACHE_HACKER_NEWS).put(key, itemList);
	}

	private void clearCache() {
		cacheManager.getCache(Constants.CACHE_HACKER_NEWS).clear();
	}

	@SuppressWarnings("unchecked")
	public List<Item> getCachedList(String key) {
		return cacheManager.getCache(Constants.CACHE_HACKER_NEWS).get(key, List.class);
	}

}
