package com.example.mydemos.net.downloadqueue;

import java.util.ArrayList;
import java.util.List;

import com.example.mydemos.net.downloadqueue.assist.threadpool.DequeThreadPoolExecutor;

public class TaskManager {
	private DequeThreadPoolExecutor executor;
	private List<Downloader> taskList;
	
	
	
	
	public TaskManager(DequeThreadPoolExecutor executor) {
		super();
		this.executor = executor;
		this.taskList = new ArrayList<Downloader>();
	}

	


	public void addDownloaderTask(Downloader downloader){
		taskList.add(downloader);
		executor.submit(downloader);
	}
	public void addFirstDownloaderTask(Downloader downloader){
		taskList.add(downloader);
		executor.submitFirst(downloader);
	}
}
