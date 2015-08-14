package com.example.mydemos.net.downloadqueue.bean;

import java.util.List;

public class Job {

	public static final int STATUS_CREATED=1;
	public static final int STATUS_RECREATED=2;
	public static final int STATUS_INITED=3;
	public static final int STATUS_STARTED=4;
	public static final int STATUS_STOP=5;
	public static final int STATUS_RUNNING=6;
	public static final int STATUS_ERROR=7;
	public static final int STATUS_FINISHED=8;
	
	@Override
	public boolean equals(Object o) {
		if(o.getClass()!=Job.class){
			return false;
		}
		Job newJob=(Job)o;
		return (url==null?newJob.getUrl()==null:url.equals(newJob.getUrl()))
				&&(savePath==null?newJob.getSavePath()==null:savePath.equals(newJob.getSavePath()));
	}
	
	private String url;
	private String savePath;
	private String fileName;
	private Integer status;
	private Integer totalSize;
	private Integer downloadedSize;
	private Integer taskNum;
	
	private List<Task> tasks;
	
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
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Integer getDownloadedSize() {
		return downloadedSize;
	}
	public void setDownloadedSize(Integer downloadedSize) {
		this.downloadedSize = downloadedSize;
	}
	public List<Task> getTasks() {
		return tasks;
	}
	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	
	
	
}
