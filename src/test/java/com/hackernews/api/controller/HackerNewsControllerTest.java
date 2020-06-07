package com.hackernews.api.controller;

import static org.mockito.Mockito.times;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.hackernews.api.helper.JsonResourceLoader;
import com.hackernews.api.model.Item;
import com.hackernews.api.model.ui.Story;
import com.hackernews.api.service.HackerNewsService;

import reactor.core.publisher.Flux;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = HackerNewsController.class)
class HackerNewsControllerTest {

	@MockBean
	HackerNewsService hackerNewsService;

	@Autowired
	private WebTestClient testClient;

	@Test
	void getLatestStoriesTest() {
		List<Story> latestStories = JsonResourceLoader.getStoryList("latest-stories.json");
		Mockito.when(hackerNewsService.getLatestStories()).thenReturn(Flux.fromIterable(latestStories));
		testClient.get().uri("/hacker-news/latest-stories").exchange().expectStatus().is2xxSuccessful();
		Mockito.verify(hackerNewsService, times(1)).getLatestStories();
	}

	@Test
	void getTopStoriesTest() {
		List<Story> topStories = JsonResourceLoader.getStoryList("top-stories.json");
		Mockito.when(hackerNewsService.getLatestStories()).thenReturn(Flux.fromIterable(topStories));
		testClient.get().uri("/hacker-news/top-stories").exchange().expectStatus().is2xxSuccessful();
		Mockito.verify(hackerNewsService, times(1)).getTopStories();
	}

	@Test
	void getPastStoriesTest() {
		List<Story> pastStories = JsonResourceLoader.getStoryList("past-stories.json");
		Mockito.when(hackerNewsService.getPastStories()).thenReturn(Flux.fromIterable(pastStories));
		testClient.get().uri("/hacker-news/past-stories").exchange().expectStatus().is2xxSuccessful();
		Mockito.verify(hackerNewsService, times(1)).getPastStories();
	}

	@Test
	void getPastStoriesPagedTest() {
		List<Story> pastStories = JsonResourceLoader.getStoryList("past-stories.json");
		Mockito.when(hackerNewsService.getPastStories(0, 10)).thenReturn(Flux.fromIterable(pastStories));
		testClient.get().uri("/hacker-news/past-stories-paged?page=0&size=10").exchange().expectStatus()
				.is2xxSuccessful();
		Mockito.verify(hackerNewsService, times(1)).getPastStories(0, 10);
	}

	@Test
	void latestStoriesCountTest() {
		List<Story> latestStories = JsonResourceLoader.getStoryList("latest-stories.json");
		Mockito.when(hackerNewsService.getLatestStories()).thenReturn(Flux.fromIterable(latestStories));
		testClient.get().uri("/hacker-news/latest-stories").exchange().expectStatus().isOk().expectBodyList(Story.class)
				.hasSize(latestStories.size());
	}

	@Test
	void topStoriesCountTest() {
		List<Story> topStories = JsonResourceLoader.getStoryList("top-stories.json");
		Mockito.when(hackerNewsService.getTopStories()).thenReturn(Flux.fromIterable(topStories));
		testClient.get().uri("/hacker-news/top-stories").exchange().expectStatus().isOk().expectBodyList(Story.class)
				.hasSize(topStories.size());
	}

	@Test
	void pastStoriesCountTest() {
		List<Story> pastStories = JsonResourceLoader.getStoryList("top-stories.json");
		Mockito.when(hackerNewsService.getPastStories()).thenReturn(Flux.fromIterable(pastStories));
		testClient.get().uri("/hacker-news/past-stories").exchange().expectStatus().isOk().expectBodyList(Story.class)
				.hasSize(pastStories.size());
	}

	@Test
	void pastStoriesPaginatedCountTest() {
		int takeSize = 5;
		List<Story> pastStories = JsonResourceLoader.getStoryList("top-stories.json");
		Mockito.when(hackerNewsService.getPastStories(0, 5)).thenReturn(Flux.fromIterable(pastStories).take(takeSize));
		testClient.get().uri("/hacker-news/past-stories-paged?page=0&size=5").exchange().expectStatus().isOk()
				.expectBodyList(Item.class).hasSize(takeSize);

	}

}
