package com.hackernews.api.service;

import java.util.List;

import com.hackernews.api.model.Item;

public interface HackerNewsCacheService {

	public List<Item> getCachedList(String key);

	public void cache(String key, List<Item> itemList);

	public boolean isCached(String key);
}
