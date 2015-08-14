package com.example.mydemos.net.downloadqueue;


import java.util.Observer;

import com.example.mydemos.net.downloadqueue.assist.threadpool.DequeThreadPoolExecutor;
import com.example.mydemos.net.downloadqueue.bean.Task;
import com.example.mydemos.net.downloadqueue.downloader.AbsTaskDownloader;

public class TaskManager {
	private DequeThreadPoolExecutor executor;
	private Class<? extends AbsTaskDownloader> downloaderClass;
	
	public TaskManager(DequeThreadPoolExecutor executor,Class<? extends AbsTaskDownloader> downloaderClass) {
		super();
		this.executor = executor;
		this.downloaderClass=downloaderClass;
	}

	


	private void addDownloaderTask(AbsTaskDownloader downloader){
		executor.submit(downloader);
	}
	private void addFirstDownloaderTask(AbsTaskDownloader downloader){
		executor.submitFirst(downloader);
	}
	
	public void addTask(Task task,Observer observer){
		AbsTaskDownloader downloader;
		try {
			downloader = downloaderClass.newInstance();
			downloader.setTask(task);
			downloader.addObserver(observer);
			addDownloaderTask(downloader);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void addFirstTask(Task task,Observer observer){
		AbsTaskDownloader downloader;
		try {
			downloader = downloaderClass.newInstance();
			downloader.setTask(task);
			downloader.addObserver(observer);
			addFirstDownloaderTask(downloader);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
