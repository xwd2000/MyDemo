package com.example.mydemos.net.downloadqueue.downloader;


import com.example.mydemos.net.downloadqueue.bean.Task;


public interface Downloader extends Runnable{
	public void pause();
	public void setTask(Task task);
	public Task getTask();
}
