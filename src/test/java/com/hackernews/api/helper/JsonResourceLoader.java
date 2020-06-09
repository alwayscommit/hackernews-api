package com.hackernews.api.helper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hackernews.api.model.Item;
import com.hackernews.api.model.User;
import com.hackernews.api.model.ui.Story;

public class JsonResourceLoader {

	private static final String TEST_RESOURCE_URL = "src/test/resources/";

	@SuppressWarnings("unchecked")
	public static <T> T getItem(String fileName) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JavaTimeModule());
			T item = (T) objectMapper.readValue(getFile(fileName), Item.class);
			return item;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<Item> getItemList(String fileName) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JavaTimeModule());
			List<Item> topItemList = Arrays.asList(objectMapper.readValue(getFile(fileName),Item[].class));
			return topItemList;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<Story> getStoryList(String fileName) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JavaTimeModule());
			List<Story> topItemList = Arrays.asList(objectMapper.readValue(getFile(fileName), Story[].class));
			return topItemList;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<User> getUserList(String fileName) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JavaTimeModule());
			List<User> userList = Arrays.asList(objectMapper.readValue(getFile(fileName), User[].class));
			return userList;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static File getFile(String fileName) {
		return new File(TEST_RESOURCE_URL + fileName);
	}

}
