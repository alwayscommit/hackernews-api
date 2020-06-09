package com.hackernews.api.service;

import com.hackernews.api.model.ui.Comment;
import com.hackernews.api.model.ui.Story;

import reactor.core.publisher.Flux;

public interface HackerNewsService {

	public Flux<Story> getLatestStories();

	public Flux<Story> getTopStories();

	Flux<Story> getPastStories();

	public Flux<Story> getPastStories(Integer page, Integer size);

	public Flux<Comment> getTopComments(Integer storyId);

}
