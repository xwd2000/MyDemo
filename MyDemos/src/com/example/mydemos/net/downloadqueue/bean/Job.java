package com.example.mydemos.net.downloadqueue.bean;

public class Job {
	private String url;
	private String savePath;
	private Integer status;
	private Integer totalSize;
	private Integer taskNum;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getSavePath() {
		return savePath;
	}
	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getTotalSize() {
		return totalSize;
	}
	public void setTotalSize(Integer totalSize) {
		this.totalSize = totalSize;
	}
	public Integer getTaskNum() {
		return taskNum;
	}
	public void setTaskNum(Integer taskNum) {
		this.taskNum = taskNum;
	}

	
	
	
}
