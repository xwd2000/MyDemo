package com.example.mydemos.customui.pulltorefresh;

public class PullBean {
	private String  title;
	private String content;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	@Override
	public String toString() {
		return "title="+title+";content="+content;
	}
}
