package com.hackernews.api.repository;

import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;

import com.hackernews.api.model.Item;

@Repository
public interface ItemRepository extends ReactiveSortingRepository<Item, Integer>{

}
