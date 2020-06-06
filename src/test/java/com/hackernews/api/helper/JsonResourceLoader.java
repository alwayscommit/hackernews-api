package com.hackernews.api.helper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackernews.api.model.Item;

public class JsonResourceLoader {

	private static final String TEST_RESOURCE_URL = "src/test/resources/";

	public static Item getItem(String fileName) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			Item item = objectMapper.readValue(getFile(fileName), Item.class);
			return item;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<Item> getItemList(String fileName) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			List<Item> topStoryList = Arrays.asList(objectMapper.readValue(getFile(fileName), Item[].class));
			return topStoryList;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static File getFile(String fileName) {
		return new File(TEST_RESOURCE_URL + fileName);
	}

}
