package com.hackernews.api.service;

import static org.junit.Assert.assertTrue;
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
import com.hackernews.api.model.User;
import com.hackernews.api.model.ui.Comment;
import com.hackernews.api.repository.ItemRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SuppressWarnings("unchecked")
@SpringBootTest
@RunWith(JUnitPlatform.class)
class HNTopCommentsTest {

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
	void mockTopCommentsTest() {
		when(hackerNewsDAO.getItem(Mockito.anyInt())).thenReturn(Mono.empty());

		Flux<Comment> commentList = hackerNewsServiceImpl.getTopComments(Mockito.anyInt());

		verify(hackerNewsDAO, times(1)).getItem(Mockito.anyInt());

		long actualSize = commentList.count().block();
		assertEquals(0, actualSize);
	}

	@Test
	void getTopCommentsTest() {
		List<Item> commentItemList = JsonResourceLoader.getItemList("top-item-comments.json");
		int parentId = commentItemList.get(0).getId();
		List<User> userList = JsonResourceLoader.getUserList("users.json");
		
		for (int i = 0; i < commentItemList.size(); i++) {
			when(hackerNewsDAO.getItem(commentItemList.get(i).getId())).thenReturn(Mono.just(commentItemList.get(i)));
			when(hackerNewsDAO.getUser(userList.get(i).getId())).thenReturn(Mono.just(userList.get(i)));
		}

		Flux<Comment> commentList = hackerNewsServiceImpl.getTopComments(parentId);
		
		long expectedSize = commentItemList.stream().filter(item -> item.getKids()!=null).count(); 
		long expectedSizeMinusParent = expectedSize - 1;//minus parent
		long actualSize = commentList.count().block().longValue();
		
		verify(hackerNewsDAO, times(5)).getItem(Mockito.anyInt());
		assertEquals(expectedSizeMinusParent, actualSize);
	}

	@Test
	void getTopCommentsDataTest() {
		List<Item> commentItemList = JsonResourceLoader.getItemList("top-item-comments.json");
		int totalInvocations = commentItemList.size();
		int parentId = commentItemList.get(0).getId();
		List<User> userList = JsonResourceLoader.getUserList("users.json");
		
		for (int i = 0; i < commentItemList.size(); i++) {
			when(hackerNewsDAO.getItem(commentItemList.get(i).getId())).thenReturn(Mono.just(commentItemList.get(i)));
			when(hackerNewsDAO.getUser(userList.get(i).getId())).thenReturn(Mono.just(userList.get(i)));
		}

		Flux<Comment> commentList = hackerNewsServiceImpl.getTopComments(parentId);
		
		long expectedSize = commentItemList.stream().filter(item -> item.getKids()!=null).count(); 
		long expectedSizeMinusParent = expectedSize - 1;//minus parent
		long actualSize = commentList.count().block().longValue();
		
		verify(hackerNewsDAO, times(totalInvocations)).getItem(Mockito.anyInt());
		assertEquals(expectedSizeMinusParent, actualSize);
	}
	
	
	@Test
	void getTopCommentsMoreKidsDataTest() {
		List<Item> commentItemList = JsonResourceLoader.getItemList("top-item-comments.json");
		int totalInvocations = commentItemList.size();
		String topCommentUser = commentItemList.get(1).getBy();//since index 0 will be parent
		int parentId = commentItemList.get(0).getId();
		List<User> userList = JsonResourceLoader.getUserList("users.json");
		
		for (int i = 0; i < commentItemList.size(); i++) {
			when(hackerNewsDAO.getItem(commentItemList.get(i).getId())).thenReturn(Mono.just(commentItemList.get(i)));
			when(hackerNewsDAO.getUser(userList.get(i).getId())).thenReturn(Mono.just(userList.get(i)));
		}

		Flux<Comment> commentFlux = hackerNewsServiceImpl.getTopComments(parentId);
		
		List<Comment> commentList = commentFlux.collectList().block();
		
		verify(hackerNewsDAO, times(totalInvocations)).getItem(Mockito.anyInt());
		assertTrue(commentList.get(0).getUserHandle().equals(topCommentUser));
	}
}
