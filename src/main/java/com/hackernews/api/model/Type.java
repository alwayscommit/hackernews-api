package com.hackernews.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Type {

	@JsonProperty("story")
	STORY, 
	@JsonProperty("comment")
	COMMENT;

}
