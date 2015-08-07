package com.example.mydemos.net.netty.downloadqueue;

public class Task {
	private Integer id;
	private String url;
	private long currentPos;
	private long byteStart;
	private long byteEnd;
	private Integer status;
	
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public long getByteStart() {
		return byteStart;
	}
	public void setByteStart(long byteStart) {
		this.byteStart = byteStart;
	}
	public long getByteEnd() {
		return byteEnd;
	}
	public void setByteEnd(long byteEnd) {
		this.byteEnd = byteEnd;
	}
	public long getCurrentPos() {
		return currentPos;
	}
	public void setCurrentPos(long currentPos) {
		this.currentPos = currentPos;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	

	

}
