package com.hackernews.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.hackernews.api.dao.HackerNewsDAO;
import com.hackernews.api.helper.JsonResourceLoader;
import com.hackernews.api.model.Item;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest
@RunWith(JUnitPlatform.class)
class HackerNewsServiceImplTest {

	private HackerNewsServiceImpl hackerNewsServiceImpl;

	@MockBean
	private HackerNewsDAO hackerNewsDAO;

	@BeforeEach
	void setUp() {
		hackerNewsServiceImpl = new HackerNewsServiceImpl(hackerNewsDAO);
	}

	@Test
	void testMockDAO() {
		Mockito.when(hackerNewsDAO.getNewStories()).thenReturn(Flux.empty());
		Mockito.when(hackerNewsDAO.getStory(Mockito.anyInt())).thenReturn(Mono.empty());
		Flux<Item> itemList = hackerNewsServiceImpl.getLatestStories();
		assertEquals(0, itemList.toStream().count());
	}

	@Test
	void testSingleItemLessThan10Mins() {
		Item item = JsonResourceLoader.getItem("story.json");
		// less than 10 mins
		item.setTime(Instant.now().minusMillis(300000).getEpochSecond());
		Mockito.when(hackerNewsDAO.getNewStories()).thenReturn(Flux.just(23425780));
		Mockito.when(hackerNewsDAO.getStory(Mockito.anyInt())).thenReturn(Mono.just(item));
		Flux<Item> itemList = hackerNewsServiceImpl.getLatestStories();
		assertEquals("sanjanamane", itemList.blockFirst().getBy());
	}

	@Test
	void testSingleItemOlderThan10Mins() {
		Item item = JsonResourceLoader.getItem("story.json");
		// older than 10 mins
		item.setTime(Instant.now().minusMillis(700000).getEpochSecond());
		Mockito.when(hackerNewsDAO.getNewStories()).thenReturn(Flux.just(23425780));
		Mockito.when(hackerNewsDAO.getStory(Mockito.anyInt())).thenReturn(Mono.just(item));
		Flux<Item> itemList = hackerNewsServiceImpl.getLatestStories();
		assertEquals(0, itemList.toStream().count());
	}

	@Test
	void testMultipleItemLessThan10Mins() {
		List<Item> itemList = JsonResourceLoader.getTopItems("top-stories.json");
		List<Integer> intList = itemList.stream().map(e -> e.getId()).collect(Collectors.toList());
		Mockito.when(hackerNewsDAO.getNewStories()).thenReturn(Flux.fromIterable(intList));
		for (int i = 0; i < itemList.size(); i++) {
			itemList.get(i).setTime(Instant.now().minusSeconds(600).getEpochSecond());
		}
		for (int i = 0; i < itemList.size(); i++) {
			Mockito.when(hackerNewsDAO.getStory(itemList.get(i).getId())).thenReturn(Mono.just(itemList.get(i)));
		}
		Flux<Item> topStories = hackerNewsServiceImpl.getLatestStories();
		assertEquals(itemList.size(), topStories.toStream().count());
	}

	@Test
	void testMultipleItemGreaterThan10Mins() {
		List<Item> itemList = JsonResourceLoader.getTopItems("top-stories.json");
		List<Integer> intList = itemList.stream().map(e -> e.getId()).collect(Collectors.toList());
		Mockito.when(hackerNewsDAO.getNewStories()).thenReturn(Flux.fromIterable(intList));
		for (int i = 0; i < itemList.size(); i++) {
			// Set to older than 10 minutes
			itemList.get(i).setTime(Instant.now().minusSeconds(661).getEpochSecond());
		}
		for (int i = 0; i < itemList.size(); i++) {
			Mockito.when(hackerNewsDAO.getStory(itemList.get(i).getId())).thenReturn(Mono.just(itemList.get(i)));
		}
		Flux<Item> topStories = hackerNewsServiceImpl.getLatestStories();
		assertEquals(0, topStories.toStream().count());
	}

	@Test
	void testMultipleItemGreaterAndLessThan10Mins() {
		List<Item> itemList = JsonResourceLoader.getTopItems("top-stories.json");

		int totalSize = itemList.size();
		int positiveTestSize = 3;

		List<Integer> intList = itemList.stream().map(e -> e.getId()).collect(Collectors.toList());
		Mockito.when(hackerNewsDAO.getNewStories()).thenReturn(Flux.fromIterable(intList));
		// 0 to < 8-4 = 0,1,2,3
		for (int i = 0; i < positiveTestSize; i++) {
			// Set to less than 10 minutes
			itemList.get(i).setTime(Instant.now().minusSeconds(600).getEpochSecond());
		}
		// 8-4 to < 8 = 4, 5, 6, 7
		for (int i = positiveTestSize; i < itemList.size(); i++) {
			// Set to older than 10 minutes
			itemList.get(i).setTime(Instant.now().minusSeconds(661).getEpochSecond());
		}
		for (int i = 0; i < itemList.size(); i++) {
			Mockito.when(hackerNewsDAO.getStory(itemList.get(i).getId())).thenReturn(Mono.just(itemList.get(i)));
		}
		Flux<Item> topStories = hackerNewsServiceImpl.getLatestStories();
		assertEquals(positiveTestSize, topStories.toStream().count());
	}

	@Test
	void testMultipleItemSomeNullMins() {
		List<Item> itemList = JsonResourceLoader.getTopItems("top-stories.json");

		int totalSize = itemList.size();
		int positiveTestSize = 3;

		List<Integer> intList = itemList.stream().map(e -> e.getId()).collect(Collectors.toList());
		Mockito.when(hackerNewsDAO.getNewStories()).thenReturn(Flux.fromIterable(intList));
		
		for (int i = 0; i < itemList.size(); i++) {
			// Set to less than 10 minutes
			itemList.get(i).setTime(Instant.now().minusSeconds(600).getEpochSecond());
		}

		//These have an item associated with itemId
		for (int i = 0; i < positiveTestSize; i++) {
			Mockito.when(hackerNewsDAO.getStory(itemList.get(i).getId())).thenReturn(Mono.just(itemList.get(i)));
		}

		//These do have an item associated with itemId
		for (int i = positiveTestSize; i < totalSize; i++) {
			Mockito.when(hackerNewsDAO.getStory(itemList.get(i).getId())).thenReturn(Mono.empty());
		}

		Flux<Item> topStories = hackerNewsServiceImpl.getLatestStories();
		assertEquals(positiveTestSize, topStories.toStream().count());
	}

}
