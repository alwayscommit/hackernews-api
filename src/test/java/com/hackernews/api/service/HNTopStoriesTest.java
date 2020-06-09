package com.hackernews.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
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
class HNTopStoriesTest {

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
	void testMock() {
		when(hackerNewsCacheService.isCached(anyString())).thenReturn(false);
		when(hackerNewsDAO.getTopStories()).thenReturn(Flux.empty());
		when(itemRepository.saveAll(Mockito.any(Flux.class))).thenReturn(Flux.empty());

		Flux<Story> itemList = hackerNewsServiceImpl.getTopStories();

		verify(hackerNewsCacheService, times(1)).isCached(anyString());
		verify(hackerNewsDAO, times(1)).getTopStories();
		verify(itemRepository, times(1)).saveAll(Mockito.any(Flux.class));

		long actualSize = itemList.count().block();
		assertEquals(0, actualSize);
	}

	@Test
	void testSingleTopItem() {
		Item item = JsonResourceLoader.getItem("item.json");

		Mockito.when(hackerNewsDAO.getTopStories()).thenReturn(Flux.just(item));
		Mockito.when(hackerNewsCacheService.isCached(Mockito.anyString())).thenReturn(false);
		Mockito.when(itemRepository.saveAll(Mockito.any(Flux.class))).thenReturn(Flux.just(item));

		Flux<Story> itemList = hackerNewsServiceImpl.getTopStories();
		String actualUser = itemList.blockFirst().getSubmittedBy();
		assertEquals("peter_retief", actualUser);
	}

	@Test
	void testTopStoryCachedMock() {

		Mockito.when(hackerNewsCacheService.isCached(Mockito.anyString())).thenReturn(true);

		hackerNewsServiceImpl.getTopStories();

		verify(hackerNewsCacheService, times(1)).isCached(anyString());
		verify(hackerNewsDAO, times(0)).getTopStories();
		verify(itemRepository, times(0)).saveAll(Mockito.any(Flux.class));
	}

	@Test
	void testTopStoryCachedData() {

		List<Story> topStoryList = JsonResourceLoader.getStoryList("top-stories.json");

		Mockito.when(hackerNewsCacheService.isCached(Mockito.anyString())).thenReturn(true);
		Mockito.when(hackerNewsCacheService.getCachedList(Mockito.anyString())).thenReturn(topStoryList);

		Flux<Story> actualStoryList = hackerNewsServiceImpl.getTopStories();

		verify(hackerNewsCacheService, times(1)).isCached(anyString());
		verify(hackerNewsCacheService, times(1)).getCachedList(anyString());
		
		assertEquals(topStoryList.size(), actualStoryList.toStream().count());
	}
	
	@Test
	void testTopStoryNotCachedMock() {

		Mockito.when(hackerNewsCacheService.isCached(Mockito.anyString())).thenReturn(false);
		when(hackerNewsDAO.getTopStories()).thenReturn(Flux.empty());
		when(itemRepository.saveAll(Mockito.any(Flux.class))).thenReturn(Flux.empty());

		hackerNewsServiceImpl.getTopStories();

		verify(hackerNewsCacheService, times(1)).isCached(anyString());
		verify(hackerNewsCacheService, times(0)).getCachedList(anyString());
		verify(hackerNewsDAO, times(1)).getTopStories();
		verify(itemRepository, times(1)).saveAll(Mockito.any(Flux.class));
	}


	@Test
	void testTopStoryNotCachedData() {
		
		List<Item> topItemList = JsonResourceLoader.getItemList("top-items.json");

		Mockito.when(hackerNewsCacheService.isCached(Mockito.anyString())).thenReturn(false);
		when(hackerNewsDAO.getTopStories()).thenReturn(Flux.fromIterable(topItemList));
		when(itemRepository.saveAll(Mockito.any(Flux.class))).thenReturn(Flux.fromIterable(topItemList));

		Flux<Story> actualStoryList = hackerNewsServiceImpl.getTopStories();
		
		assertEquals(topItemList.size(), actualStoryList.toStream().count());
	}
}
