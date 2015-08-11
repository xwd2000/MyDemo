package com.example.mydemos.net.downloadqueue;


import com.example.mydemos.net.downloadqueue.assist.threadpool.DequeThreadPoolExecutor;
import com.example.mydemos.net.downloadqueue.downloader.AbsTaskDownloader;

public class TaskManager {
	private DequeThreadPoolExecutor executor;
	
	public TaskManager(DequeThreadPoolExecutor executor) {
		super();
		this.executor = executor;
	}

	


	public void addDownloaderTask(AbsTaskDownloader downloader){
		executor.submit(downloader);
	}
	public void addFirstDownloaderTask(AbsTaskDownloader downloader){
		executor.submitFirst(downloader);
	}
}
