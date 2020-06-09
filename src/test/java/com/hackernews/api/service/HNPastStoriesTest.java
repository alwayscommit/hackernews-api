package com.hackernews.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

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
import com.hackernews.api.model.ui.Story;
import com.hackernews.api.repository.ItemRepository;

import reactor.core.publisher.Flux;

@SuppressWarnings("unchecked")
@SpringBootTest
@RunWith(JUnitPlatform.class)
class HNPastStoriesTest {
	
	private HackerNewsServiceImpl hackerNewsServiceImpl;

	@MockBean
	private HackerNewsDAO hackerNewsDAO;

	@MockBean
	private ItemRepository itemRepository;

	@MockBean
	private HackerNewsCacheService hackerNewsCacheService;

	@BeforeEach
	void setUp() {
		hackerNewsServiceImpl = new HackerNewsServiceImpl(hackerNewsDAO, itemRepository, hackerNewsCacheService);
	}

	@Test
	void mockPastStoriesTest() {
		when(itemRepository.findAll(Mockito.any())).thenReturn(Flux.empty());

		hackerNewsServiceImpl.getPastStories();

		verify(itemRepository, times(1)).findAll(Mockito.any());

	}
	
	@Test
	void getPastStoriesTest() {
		List<Item> pastItems = JsonResourceLoader.getItemList("past-items.json");
		when(itemRepository.findAll(Mockito.any())).thenReturn(Flux.fromIterable(pastItems));

		Flux<Story> pastStories = hackerNewsServiceImpl.getPastStories();

		verify(itemRepository, times(1)).findAll(Mockito.any());
		
		int expectedSize = pastItems.size(); 
		long actualSize = pastStories.count().block();
		
		assertEquals(expectedSize, actualSize);
	}
	
	@Test
	void getPastStoriesDataTest() {
		List<Item> sortedPastItems = JsonResourceLoader.getItemList("past-items.json");
		when(itemRepository.findAll(Mockito.any())).thenReturn(Flux.fromIterable(sortedPastItems));

		Flux<Story> pastStories = hackerNewsServiceImpl.getPastStories();

		verify(itemRepository, times(1)).findAll(Mockito.any());
		
		Item expectedTopItem = sortedPastItems.get(0);
		Story actualTopItem = pastStories.blockFirst();
		
		assertEquals(expectedTopItem.getScore().intValue(), actualTopItem.getScore().intValue());
		assertEquals(expectedTopItem.getBy(), actualTopItem.getSubmittedBy());
	}
	
	@Test
	void mockPagedPastStoriesTest() {
		when(itemRepository.findAll(Mockito.any())).thenReturn(Flux.empty());

		hackerNewsServiceImpl.getPastStories(0, 3);

		verify(itemRepository, times(1)).findAll(Mockito.any());
	}
	
	@Test
	void getPagedPastStoriesTest() {
		int page = 0;
		int size = 3;
		List<Item> sortedPastItems = JsonResourceLoader.getItemList("past-items.json");
		when(itemRepository.findAll(Mockito.any())).thenReturn(Flux.fromIterable(sortedPastItems));

		Flux<Story> pastStories = hackerNewsServiceImpl.getPastStories(page, size);

		verify(itemRepository, times(1)).findAll(Mockito.any());
		
		long actualSize = pastStories.count().block();
		
		Item expectedTopItem = sortedPastItems.get(0);
		Story actualTopItem = pastStories.blockFirst();
		
		assertEquals(size, actualSize);
		assertEquals(expectedTopItem.getScore().intValue(), actualTopItem.getScore().intValue());
		assertEquals(expectedTopItem.getBy(), actualTopItem.getSubmittedBy());
	}
	
	@Test
	void getPagePastStoriesTest() {
		int page = 1;
		int size = 3;
		List<Item> sortedPastItems = JsonResourceLoader.getItemList("past-items.json");
		
		
		when(itemRepository.findAll(Mockito.any())).thenReturn(Flux.fromIterable(sortedPastItems));
		
		Flux<Story> pastStories = hackerNewsServiceImpl.getPastStories(page, size);

		verify(itemRepository, times(1)).findAll(Mockito.any());
		
		Story actualFourthStory = pastStories.blockFirst();
		Item expectedFourthItem = sortedPastItems.get(3);
		
		assertEquals(expectedFourthItem.getId().intValue(), actualFourthStory.getId().intValue());
	}

}
