package com.example.mydemos.net.downloadqueue;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.example.mydemos.net.downloadqueue.BeforeDownLoad.AfterFileLengthGeted;
import com.example.mydemos.net.downloadqueue.assist.statusstore.StatusStore;
import com.example.mydemos.net.downloadqueue.bean.Job;
import com.example.mydemos.net.downloadqueue.bean.Task;
import com.example.mydemos.net.downloadqueue.downloader.AbsTaskDownloader;
import com.example.mydemos.net.downloadqueue.downloader.HttpComponentTaskDownloader;

public class JobManager {
	private TaskManager taskManager;
	private DownloadConfigure config;
	private TaskObserver observer;

	public JobManager(DownloadConfigure config,StatusStore store) {
		super();
		this.config = config;
		this.taskManager = new TaskManager(config.taskExecutor);
		this.observer=new TaskObserver(store,taskManager);
	}

	public Job genJob(int totalSize,String urlStr){
		int taskNum=getJobTaskCountByJobFileSize(totalSize);
		String pathBase=config.savePathBase;
		String fileName=null;
		try{
			fileName = urlStr.substring(urlStr.lastIndexOf("/")+1);
			urlStr=urlStr.substring(0, urlStr.lastIndexOf("/")+1)+URLEncoder.encode(fileName, "utf-8").replaceAll("\\+", "%20");
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		String saveFilePath=pathBase+File.separator+fileName;
		
		Job job = new Job();
		job.setTotalSize(totalSize);
		job.setSavePath(saveFilePath);
		job.setTaskNum(taskNum);
		job.setFileName(fileName);
		job.setDownloadedSize(0);
		job.setUrl(urlStr);
		return job;
	}
	
	
	public int getJobTaskCountByJobFileSize(int fileSize){
		int threadNumPerJob = config.threadNumPerJob;
		long hypotheticalDownloadSize = fileSize / threadNumPerJob;
		final int oneM = 1024 * 1024;
		
		long threadDownloadSize = hypotheticalDownloadSize > oneM ? hypotheticalDownloadSize
				: oneM;

		int threadCounter = (int) ((fileSize - 1)
				/ threadDownloadSize + 1);
		return threadCounter;
	}
	
	public List<Task> splitJobToTask(Job job) {
		
		List<Task> taskList = new ArrayList<Task>();
		int nowStart = 0;
		int threadCounter = job.getTaskNum();
		int hypotheticalDownloadSize = job.getTotalSize()/job.getTaskNum();
				final int oneM = 1024 * 1024;
		int threadDownloadSize = hypotheticalDownloadSize > oneM ? hypotheticalDownloadSize
				: oneM;
		
		while (--threadCounter > 0) {// 1M使用单线程下载

			int start = nowStart;
			int end = nowStart + threadDownloadSize;
			nowStart += threadDownloadSize;
			Task task = new Task();
			task.setByteStart(start);
			task.setByteEnd(end);
			task.setCurrentPos(start);
			task.setJob(job);
			task.setStatus(Task.STATUS_INITING);
			taskList.add(task);
		}
		int start = nowStart;
		int end = job.getTotalSize();
		Task task = new Task();
		task.setByteStart(start);
		task.setByteEnd(end);
		task.setCurrentPos(start);
		task.setJob(job);
		task.setStatus(Task.STATUS_INITING);
		taskList.add(task);
		return taskList;
	}
	
	/**
	 * the param job mast has all fields complete
	 * @param job
	 */
	public void appendJob(Job job){
		for(Task task:splitJobToTask(job)){
			AbsTaskDownloader dl = new HttpComponentTaskDownloader();
			dl.setTask(task);
			dl.addObserver(observer);
			taskManager.addDownloaderTask(dl);
		}
	}
	
	/**
	 * the param job mast has all fields complete
	 * @param job
	 */
	public void appFirstJob(Job job) {
		for(Task task:splitJobToTask(job)){
			AbsTaskDownloader dl = new HttpComponentTaskDownloader();
			dl.setTask(task);
			dl.addObserver(observer);
			taskManager.addFirstDownloaderTask(dl);
		}
	}
	
	
	public void submitJob(final String url){
		new BeforeDownLoad(url, 
				new AfterFileLengthGeted() {
					@Override
					public void afterFileLengthGetted(int fileLength) {
						Job job=genJob(fileLength,url);
						//job.setTaskNum(taskNum);
						appendJob(job);
					}
				}).start();
	}
}
