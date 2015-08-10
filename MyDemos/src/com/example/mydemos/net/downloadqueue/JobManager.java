package com.example.mydemos.net.downloadqueue;

import java.util.ArrayList;
import java.util.List;

import com.example.mydemos.net.downloadqueue.bean.Job;
import com.example.mydemos.net.downloadqueue.bean.Task;

public class JobManager {
	private TaskManager taskManager;
	private DownloadConfigure config;

	public JobManager(DownloadConfigure config) {
		super();
		this.config = config;
		taskManager = new TaskManager(config.taskExecutor);
	}

	public Job genJob(int totalSize,String url){
		int taskNum=getJobTaskCountByJobFileSize(totalSize);
		String pathBase=config.savePathBase;
		String fileName=null;
		try{
			fileName = url.substring(url.lastIndexOf('/'));
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		String saveFilePath=pathBase+fileName;
		
		Job job = new Job();
		job.setTotalSize(totalSize);
		job.setSavePath(saveFilePath);
		job.setTaskNum(taskNum);
		job.setUrl(url);
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
			taskList.add(task);
		}
		int start = nowStart;
		int end = job.getTotalSize();
		Task task = new Task();
		task.setByteStart(start);
		task.setByteEnd(end);
		task.setCurrentPos(start);
		task.setJob(job);
		taskList.add(task);
		return taskList;
	}

	public void appendTask(Job job){
		
		for(Task task:splitJobToTask(job)){
			Downloader dl = new CommonDownloader();
			dl.setTask(task);
			taskManager.addDownloaderTask(dl);
		}
	}

	public void appFirstTask(Job job) {
		for(Task task:splitJobToTask(job)){
			Downloader dl = new CommonDownloader();
			dl.setTask(task);
			taskManager.addFirstDownloaderTask(dl);
		}
	}
}
