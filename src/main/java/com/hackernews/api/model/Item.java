package com.hackernews.api.model;

import java.util.List;

public class Item {

	private Integer id;
	private String by;
	private Integer descendants;
	private List<Long> kids;
	private Long parent;
	private Integer score;
	private Long time;
	private String title;
	private Type type;
	private String url;
	private String text;

	public Item() {
	}

	public Item(Integer id, String by, Integer descendants, List<Long> kids, Integer score, Long time, String title,
			Type type, String url, String text) {
		this.id = id;
		this.by = by;
		this.descendants = descendants;
		this.kids = kids;
		this.score = score;
		this.time = time;
		this.title = title;
		this.type = type;
		this.url = url;
		this.text = text;
	}

	public Long getParent() {
		return parent;
	}

	public void setParent(Long parent) {
		this.parent = parent;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getBy() {
		return by;
	}

	public void setBy(String by) {
		this.by = by;
	}

	public Integer getDescendants() {
		return descendants;
	}

	public void setDescendants(Integer descendants) {
		this.descendants = descendants;
	}

	public List<Long> getKids() {
		return kids;
	}

	public void setKids(List<Long> kids) {
		this.kids = kids;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
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

	@Override
	public String toString() {
		return "Item [id=" + id + ", by=" + by + ", descendants=" + descendants + ", kids=" + kids + ", score=" + score
				+ ", time=" + time + ", title=" + title + ", type=" + type + ", url=" + url + "]";
	}

}
