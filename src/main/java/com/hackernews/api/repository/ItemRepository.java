package com.hackernews.api.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.hackernews.api.model.Item;

public interface ItemRepository extends ReactiveCrudRepository<Item, Integer>{

}
