package com.hackernews.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
import com.hackernews.api.model.ui.Story;
import com.hackernews.api.repository.ItemRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
		Mockito.when(hackerNewsDAO.getNewStories()).thenReturn(Flux.empty());
		Mockito.when(hackerNewsDAO.getItem(Mockito.anyInt())).thenReturn(Mono.empty());
		Mockito.when(itemRepository.saveAll(Mockito.anyList())).thenReturn(Flux.empty());
		Mockito.when(hackerNewsCacheService.isCached(Mockito.anyString())).thenReturn(false);
		Flux<Story> itemList = hackerNewsServiceImpl.getLatestStories();
		long actualSize = itemList.count().block();
		assertEquals(0, actualSize);
	}

	@Test
	void testSingleItemLessThan10Mins() {
		Item item = JsonResourceLoader.getItem("item.json");
		// less than 10 mins
		item.setTime(Instant.now().minusMillis(300000).getEpochSecond());
		Mockito.when(hackerNewsDAO.getNewStories()).thenReturn(Flux.just(23425780));
		Mockito.when(hackerNewsDAO.getItem(Mockito.anyInt())).thenReturn(Mono.just(item));
		Mockito.when(hackerNewsCacheService.isCached(Mockito.anyString())).thenReturn(false);
		Mockito.when(itemRepository.saveAll(Mockito.anyList())).thenReturn(Flux.just(item));
		Flux<Story> itemList = hackerNewsServiceImpl.getLatestStories();
		String actualUser = itemList.blockFirst().getSubmittedBy();
		assertEquals("peter_retief", actualUser);
	}

	@Test
	void testSingleItemOlderThan10Mins() {
		Item item = JsonResourceLoader.getItem("item.json");
		// older than 10 mins
		item.setTime(Instant.now().minusMillis(700000).getEpochSecond());
		Mockito.when(hackerNewsDAO.getNewStories()).thenReturn(Flux.just(23425780));
		Mockito.when(hackerNewsDAO.getItem(Mockito.anyInt())).thenReturn(Mono.just(item));
		Mockito.when(itemRepository.saveAll(Mockito.anyList())).thenReturn(Flux.empty());
		Flux<Story> itemList = hackerNewsServiceImpl.getLatestStories();
		long actualSize = itemList.count().block();
		assertEquals(0, actualSize);
	}

	@Test
	void testItemsLessThan10Mins() {
		List<Item> itemList = JsonResourceLoader.getItemList("top-items.json");
		List<Integer> intList = itemList.stream().map(e -> e.getId()).collect(Collectors.toList());
		Mockito.when(hackerNewsDAO.getNewStories()).thenReturn(Flux.fromIterable(intList));
		
		for (int i = 0; i < itemList.size(); i++) {
			itemList.get(i).setTime(Instant.now().minusSeconds(600).getEpochSecond());
		}
		
		for (int i = 0; i < itemList.size(); i++) {
			Mockito.when(hackerNewsDAO.getItem(itemList.get(i).getId())).thenReturn(Mono.just(itemList.get(i)));
		}
		
		Mockito.when(itemRepository.saveAll(Mockito.anyList())).thenReturn(Flux.fromIterable(itemList));
		Flux<Story> topStories = hackerNewsServiceImpl.getLatestStories();
		assertEquals(itemList.size(), topStories.toStream().count());
	}

	@Test
	void testItemsOlderThan10Mins() {
		List<Item> itemList = JsonResourceLoader.getItemList("top-items.json");
		List<Integer> intList = itemList.stream().map(e -> e.getId()).collect(Collectors.toList());
		Mockito.when(hackerNewsDAO.getNewStories()).thenReturn(Flux.fromIterable(intList));
		for (int i = 0; i < itemList.size(); i++) {
			// Set to older than 10 minutes
			itemList.get(i).setTime(Instant.now().minusSeconds(661).getEpochSecond());
		}
		for (int i = 0; i < itemList.size(); i++) {
			Mockito.when(hackerNewsDAO.getItem(itemList.get(i).getId())).thenReturn(Mono.just(itemList.get(i)));
		}
		Mockito.when(itemRepository.saveAll(Mockito.anyList())).thenReturn(Flux.fromIterable(itemList));
		Flux<Story> topStories = hackerNewsServiceImpl.getLatestStories();
		assertEquals(0, topStories.toStream().count());
	}

	@Test
	void testMultipleItemGreaterAndLessThan10Mins() {
		List<Item> itemList = JsonResourceLoader.getItemList("top-items.json");

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
		for (int i = positiveTestSize; i < totalSize; i++) {
			// Set to older than 10 minutes
			itemList.get(i).setTime(Instant.now().minusSeconds(661).getEpochSecond());
		}
		for (int i = 0; i < itemList.size(); i++) {
			Mockito.when(hackerNewsDAO.getItem(itemList.get(i).getId())).thenReturn(Mono.just(itemList.get(i)));
		}
		Mockito.when(itemRepository.saveAll(Mockito.anyList())).thenReturn(Flux.fromIterable(itemList));
		Flux<Story> topStories = hackerNewsServiceImpl.getLatestStories();
		assertEquals(positiveTestSize, topStories.toStream().count());
	}

	@Test
	void testMultipleItemSomeNullMins() {
		List<Item> itemList = JsonResourceLoader.getItemList("top-items.json");

		int totalSize = itemList.size();
		int positiveTestSize = 3;

		List<Integer> intList = itemList.stream().map(e -> e.getId()).collect(Collectors.toList());
		Mockito.when(hackerNewsDAO.getNewStories()).thenReturn(Flux.fromIterable(intList));

		for (int i = 0; i < itemList.size(); i++) {
			// Set to less than 10 minutes
			itemList.get(i).setTime(Instant.now().minusSeconds(600).getEpochSecond());
		}

		// These have an item associated with itemId
		for (int i = 0; i < positiveTestSize; i++) {
			Mockito.when(hackerNewsDAO.getItem(itemList.get(i).getId())).thenReturn(Mono.just(itemList.get(i)));
		}

		// These do have an item associated with itemId
		for (int i = positiveTestSize; i < totalSize; i++) {
			Mockito.when(hackerNewsDAO.getItem(itemList.get(i).getId())).thenReturn(Mono.empty());
		}
		Mockito.when(itemRepository.saveAll(Mockito.anyList())).thenReturn(Flux.fromIterable(itemList));
		Flux<Story> topStories = hackerNewsServiceImpl.getLatestStories();
		assertEquals(positiveTestSize, topStories.toStream().count());
	}

}
