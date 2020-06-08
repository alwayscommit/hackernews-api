package com.hackernews.api.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.hackernews.api.Constants;
import com.hackernews.api.dao.HackerNewsDAO;
import com.hackernews.api.model.Item;
import com.hackernews.api.model.Type;
import com.hackernews.api.model.User;
import com.hackernews.api.model.ui.Comment;
import com.hackernews.api.model.ui.Story;
import com.hackernews.api.repository.ItemRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class HackerNewsServiceImpl implements HackerNewsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(HackerNewsServiceImpl.class);

	private HackerNewsDAO hackerNewsDAO;

	private ItemRepository itemRepository;

	private HackerNewsCacheService hackerNewsCacheService;

	private static final String SORT_FIELD = "score";

	@Autowired
	public HackerNewsServiceImpl(HackerNewsDAO hackerNewsDAO, ItemRepository itemRepository,
			HackerNewsCacheService hackerNewsCacheService) {
		this.hackerNewsDAO = hackerNewsDAO;
		this.itemRepository = itemRepository;
		this.hackerNewsCacheService = hackerNewsCacheService;
	}

	@Override
	public Flux<Story> getTopStories() {

		if (hackerNewsCacheService.isCached(Constants.KEY_TOP_STORY)) {
			LOGGER.info("Fetching Top Stories from Local Cache...");
			return Flux.fromIterable(hackerNewsCacheService.getCachedList(Constants.KEY_TOP_STORY));
		} else {
			LOGGER.info("Fetching Top Stories from Hacker-News API...");

//			Flux<Integer> topStoryIds = hackerNewsDAO.getTopStories();

//			TODO REMOVE LATER
			Flux<Integer> topStoryIds = Flux.fromStream(hackerNewsDAO.getTopStories().toStream().limit(20));

			Flux<Item> sortedStoryItems = topStoryIds.flatMap(storyId -> hackerNewsDAO.getItem(storyId))
					.filter(item -> item.getType().equals(Type.STORY)).sort(compareItemScore());

			List<Item> top10StoriesList = sortedStoryItems.toStream().limit(10).collect(Collectors.toList());

			saveStoriesInDB(top10StoriesList);

			Flux<Story> top10Stories = Flux.fromIterable(top10StoriesList).flatMap(item -> mapToStory(item));

			hackerNewsCacheService.cache(Constants.KEY_TOP_STORY, top10Stories.toStream().collect(Collectors.toList()));

			return top10Stories;
		}
	}

	private Comparator<Item> compareItemScore() {
		return Comparator.comparingInt(Item::getScore).reversed();
	}

	@Override
	public Flux<Comment> getTopComments(Integer storyId) {
		LOGGER.info(String.format("Fetching top comments for storyId %s", storyId));

		Flux<Item> storyComments = getComment(storyId).flatMap(this::getInnerComments);
		Flux<Item> childComments = storyComments.filter(comment -> !comment.getId().equals(storyId));

		Flux<Item> topComments = childComments.filter(comment -> comment.getKids() != null).sort((comment,
				nextComment) -> Integer.valueOf(nextComment.getKids().size()).compareTo(comment.getKids().size()))
				.take(10);

		return topComments.flatMap(child -> getCommentUserDetails(child));
	}

	private Flux<Item> getComment(Integer commentId) {
		return Flux.from(hackerNewsDAO.getItem(commentId));
	}

	private Flux<Item> getInnerComments(Item parentComment) {

		if (parentComment.getKids() != null) {

			return Flux.merge(Flux.just(parentComment), Flux.fromIterable(parentComment.getKids())
					.flatMap(childId -> getComment(childId)).flatMap(childComment -> getInnerComments(childComment)));

		}

		return Flux.just(parentComment);
	}

	@Override
	public Flux<Story> getPastStories() {
		LOGGER.info("Fetching past stories...");
		return itemRepository.findAll(Sort.by(SORT_FIELD).descending()).flatMap(item -> mapToStory(item));
	}

	@Override
	public Flux<Story> getPastStories(Integer page, Integer size) {
		LOGGER.info(String.format("Fetching past stories with page %s and size %s", page, size));
		return itemRepository.findAll(Sort.by(SORT_FIELD).descending()).skip(page * size).take(size)
				.flatMap(item -> mapToStory(item));
	}

	@Override
	public Flux<Story> getLatestStories() {

		if (hackerNewsCacheService.isCached(Constants.KEY_LATEST_STORY)) {
			LOGGER.info("Fetching Latest Stories from Local Cache...");
			return Flux.fromIterable(hackerNewsCacheService.getCachedList(Constants.KEY_LATEST_STORY));
		} else {
			LOGGER.info("Fetching Latest Stories from Hacker-News API...");

//			Flux<Integer> newStoryIds = hackerNewsDAO.getNewStories();

//			TODO REMOVE LATER
			Flux<Integer> newStoryIds = Flux.fromStream(hackerNewsDAO.getNewStories().toStream().limit(20));

			Flux<Item> itemList = newStoryIds.flatMap(storyId -> hackerNewsDAO.getItem(storyId));

			Flux<Item> storiesWithin10Mins = itemList
					.filter(story -> calculateElapsedMinutes(story.getTime()) <= Constants.CACHE_TIME);

			List<Item> latestStories = storiesWithin10Mins.toStream().collect(Collectors.toList());

			saveStoriesInDB(latestStories);

			Flux<Story> storyList = Flux.fromIterable(latestStories).flatMap(item -> mapToStory(item));

			hackerNewsCacheService.cache(Constants.KEY_LATEST_STORY, storyList.toStream().collect(Collectors.toList()));

			return storyList;

		}
	}

	private Mono<Story> mapToStory(Item item) {
		return Mono.just(new Story(item.getId(), item.getBy(), item.getScore(), getCreationTime(item.getTime()),
				item.getTitle(), item.getType(), item.getUrl()));
	}

	private void saveStoriesInDB(List<Item> latestStories) {
		itemRepository.saveAll(latestStories).subscribe();
	}

	private Mono<Comment> getCommentUserDetails(Item child) {
		return getUser(child.getBy()).map(userDetails -> {
			return new Comment(child.getId(), userDetails.getId(), getAge(userDetails.getCreated()), child.getText());
		});
	}

	private int getAge(Long creationEpoch) {
		return Period.between(getProfileCreationDate(creationEpoch), LocalDate.now()).getYears();
	}

	public LocalDate getProfileCreationDate(Long creationEpoch) {
		return Instant.ofEpochSecond(creationEpoch).atZone(ZoneId.systemDefault()).toLocalDate();
	}

	private Mono<User> getUser(String userHandle) {
		return hackerNewsDAO.getUser(userHandle);
	}

	private long calculateElapsedMinutes(Long storyTime) {
		return ((nowEpochSeconds() - storyTime) / Constants.MINUTES_DIVISOR);
	}

	private long nowEpochSeconds() {
		return (Instant.now().getEpochSecond());
	}

	private LocalDateTime getCreationTime(Long epochTime) {
		return Instant.ofEpochSecond(epochTime).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
}
