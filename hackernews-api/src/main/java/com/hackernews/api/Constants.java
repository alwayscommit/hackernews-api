package com.hackernews.api;

public interface Constants {

	public static final String URL_ITEM = "https://hacker-news.firebaseio.com/v0/item/%s.json";

	public static final String URL_TOP_STORIES = "https://hacker-news.firebaseio.com/v0/topstories.json";

	public static final String URL_NEW_STORIES = "https://hacker-news.firebaseio.com/v0/newstories.json";
	
	public static final String URL_USER = "https://hacker-news.firebaseio.com/v0/user/%s.json";

	public static final Integer CACHE_TIME = 10;

	public static final long MINUTES_DIVISOR = 60;

	public static final String CACHE_HACKER_NEWS = "hacker-news-cache";

	public static final String KEY_LATEST_STORY = "latestStories";

	public static final String KEY_TOP_STORY = "topStories";

}
