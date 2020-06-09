package com.hackernews.api.model.ui;

public class Comment {

	private Integer id;
	private String userHandle;
	private Integer userAge;
	private String text;

	public Comment(Integer id, String userHandle, Integer userAge, String text) {
		this.id = id;
		this.userHandle = userHandle;
		this.userAge = userAge;
		this.text = text;
	}
	
	public Comment() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUserHandle() {
		return userHandle;
	}

	public void setUserHandle(String userHandle) {
		this.userHandle = userHandle;
	}

	public Integer getUserAge() {
		return userAge;
	}

	public void setUserAge(Integer userAge) {
		this.userAge = userAge;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
