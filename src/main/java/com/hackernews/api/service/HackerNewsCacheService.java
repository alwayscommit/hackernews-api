package com.hackernews.api.service;

import java.util.List;

import com.hackernews.api.model.ui.Story;

public interface HackerNewsCacheService {

	public List<Story> getCachedList(String key);

	public void cache(String key, List<Story> itemList);

	public boolean isCached(String key);
}
