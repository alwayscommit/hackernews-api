package com.hackernews.api.model.ui;

import java.time.LocalDateTime;

import com.hackernews.api.model.Type;

public class Story {

	private Integer id;
	private String submittedBy;
	private Integer score;
	private LocalDateTime submissionTime;
	private String title;
	private Type type;
	private String url;

	public Story() {
	}

	public Story(Integer id, String submittedBy, Integer score, LocalDateTime submissionTime, String title, Type type,
			String url) {
		this.id = id;
		this.submittedBy = submittedBy;
		this.score = score;
		this.submissionTime = submissionTime;
		this.title = title;
		this.type = type;
		this.url = url;
	}

	public String getSubmittedBy() {
		return submittedBy;
	}

	public void setSubmittedBy(String submittedBy) {
		this.submittedBy = submittedBy;
	}

	public LocalDateTime getSubmissionTime() {
		return submissionTime;
	}

	public void setSubmissionTime(LocalDateTime submissionTime) {
		this.submissionTime = submissionTime;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
