package com.hackernews.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
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
class HNLatestStoriesTest {

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
	void testMockDAO() {
		when(hackerNewsCacheService.isCached(anyString())).thenReturn(false);
		when(hackerNewsDAO.getNewStories()).thenReturn(Flux.empty());
		when(itemRepository.saveAll(Mockito.any(Flux.class))).thenReturn(Flux.empty());

		Flux<Story> itemList = hackerNewsServiceImpl.getLatestStories();

		verify(hackerNewsCacheService, times(1)).isCached(anyString());
		verify(hackerNewsDAO, times(1)).getNewStories();
		verify(itemRepository, times(1)).saveAll(Mockito.any(Flux.class));

		long actualSize = itemList.count().block();
		assertEquals(0, actualSize);
	}

	@Test
	void testCachedLatestStories() {
		
		List<Story> topStoryList = JsonResourceLoader.getStoryList("latest-stories.json");

		when(hackerNewsCacheService.isCached(anyString())).thenReturn(true);
		when(hackerNewsCacheService.getCachedList(Mockito.anyString())).thenReturn(topStoryList);

		hackerNewsServiceImpl.getLatestStories();

		verify(hackerNewsDAO, times(0)).getNewStories();
		verify(itemRepository, times(0)).saveAll(Mockito.any(Flux.class));
		
		verify(hackerNewsCacheService, times(1)).isCached(anyString());
		verify(hackerNewsCacheService, times(1)).getCachedList(anyString());
	}
	
	@Test
	void testNotCachedLatestStories() {
		
		List<Item> latestItemList = JsonResourceLoader.getItemList("latest-items.json");
		
		when(hackerNewsCacheService.isCached(anyString())).thenReturn(false);
		when(hackerNewsDAO.getNewStories()).thenReturn(Flux.fromIterable(latestItemList));
		when(itemRepository.saveAll(Mockito.any(Flux.class))).thenReturn(Flux.fromIterable(latestItemList));
		
		hackerNewsServiceImpl.getLatestStories();

		verify(hackerNewsDAO, times(1)).getNewStories();
		verify(itemRepository, times(1)).saveAll(Mockito.any(Flux.class));
		
		verify(hackerNewsCacheService, times(1)).isCached(anyString());
		verify(hackerNewsCacheService, times(0)).getCachedList(anyString());
	}
	
	@Test
	void testSingleItemLessThan10Mins() {
		Item item = JsonResourceLoader.getItem("item.json");
		// less than 10 mins
		item.setTime(Instant.now().minusMillis(300000).getEpochSecond());
		when(hackerNewsDAO.getNewStories()).thenReturn(Flux.just(item));
		when(hackerNewsCacheService.isCached(Mockito.anyString())).thenReturn(false);
		when(itemRepository.saveAll(Mockito.any(Flux.class))).thenReturn(Flux.just(item));

		Flux<Story> itemList = hackerNewsServiceImpl.getLatestStories();
		
		Mockito.verify(itemRepository, times(1)).saveAll(Mockito.any(Flux.class));
		
		String actualUser = itemList.blockFirst().getSubmittedBy();
		assertEquals("peter_retief", actualUser);
	}

	@Test
	void testSingleItemOlderThan10Mins() {
		Item item = JsonResourceLoader.getItem("item.json");
		// older than 10 mins
		item.setTime(Instant.now().minusMillis(700000).getEpochSecond());
		when(hackerNewsDAO.getNewStories()).thenReturn(Flux.just(item));
		when(itemRepository.saveAll(Mockito.any(Flux.class))).thenReturn(Flux.empty());
		Flux<Story> itemList = hackerNewsServiceImpl.getLatestStories();
		long actualSize = itemList.count().block();
		assertEquals(0, actualSize);
	}

	@Test
	void testItemsLessThan10Mins() {
		List<Item> itemList = JsonResourceLoader.getItemList("latest-items.json");
		when(hackerNewsDAO.getNewStories()).thenReturn(Flux.fromIterable(itemList));

		for (int i = 0; i < itemList.size(); i++) {
			itemList.get(i).setTime(Instant.now().minusSeconds(600).getEpochSecond());
		}
		
		when(itemRepository.saveAll(Mockito.any(Flux.class))).thenReturn(Flux.fromIterable(itemList));
		Flux<Story> topStories = hackerNewsServiceImpl.getLatestStories();
		
		verify(hackerNewsDAO, times(1)).getNewStories();
		verify(itemRepository, times(1)).saveAll(Mockito.any(Flux.class));
		verify(hackerNewsCacheService, times(1)).isCached(anyString());
		assertEquals(itemList.size(), topStories.toStream().count());
	}

	@Test
	void testItemsOlderThan10Mins() {
		List<Item> itemList = JsonResourceLoader.getItemList("top-items.json");
		when(hackerNewsDAO.getNewStories()).thenReturn(Flux.fromIterable(itemList));
		for (int i = 0; i < itemList.size(); i++) {
			// Set to older than 10 minutes
			itemList.get(i).setTime(Instant.now().minusSeconds(661).getEpochSecond());
		}
		when(itemRepository.saveAll(Mockito.any(Flux.class))).thenReturn(Flux.empty());
		Flux<Story> topStories = hackerNewsServiceImpl.getLatestStories();
		
		verify(hackerNewsDAO, times(1)).getNewStories();
		verify(itemRepository, times(1)).saveAll(Mockito.any(Flux.class));
		verify(hackerNewsCacheService, times(1)).isCached(anyString());
		assertEquals(0, topStories.toStream().count());
	}

}
