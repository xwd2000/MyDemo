package com.example.mydemos.net.downloadqueue;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;

import com.example.mydemos.net.downloadqueue.BeforeDownLoad.AfterFileLengthGeted;
import com.example.mydemos.net.downloadqueue.bean.Job;
import com.example.mydemos.net.downloadqueue.bean.Task;
import com.example.mydemos.net.downloadqueue.downloader.AbsTaskDownloader;
import com.example.mydemos.net.downloadqueue.downloader.HttpComponentTaskDownloader;
import com.example.mydemos.net.downloadqueue.statusstore.JobMemeryStore;
import com.example.mydemos.net.downloadqueue.statusstore.StatusStore;
import com.example.util.EmptyUtil;

public class JobManager {
	private TaskManager taskManager;
	private DownloadConfigure config;
	private TaskObserver observer;
	private StatusStore store;
	private JobMemeryStore jobInMemery;

	public JobManager(DownloadConfigure config,StatusStore store) {
		super();
		this.config = config;
		this.taskManager = new TaskManager(config.taskExecutor,config.downLoaderClass);
		this.store=store;
		jobInMemery=new JobMemeryStore();
		this.observer=new TaskObserver(store,taskManager,jobInMemery);
		
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
		job.setStatus(Job.STATUS_CREATED);
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
			task.setByteEnd(end-1);
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
	private void appendJob(Job job){
		List<Task> jobTasks = splitJobToTask(job);
		job.setTasks(jobTasks);
		store.storeJob(job);
		jobInMemery.add(job);
		for(Task task:jobTasks){
			taskManager.addTask(task, observer);
			store.storeTask(job, task);
		}
	}
	
	/**
	 * the parameter job mast has all fields complete
	 * @param job
	 */
	private void appFirstJob(Job job) {
		List<Task> jobTasks = splitJobToTask(job);
		job.setTasks(jobTasks);
		for(Task task:jobTasks){
			taskManager.addTask(task, observer);
		}
	}

	
	public Job newSubmitJob(final String url,final OnJobStatusChangeListener onJobStatusChange){
		final Job job=genJob(0,url);
		
		new BeforeDownLoad(url, 
				new AfterFileLengthGeted() {
					@Override
					public void afterFileLengthGetted(int fileLength) {
						job.setTotalSize(fileLength);
						job.setTaskNum(getJobTaskCountByJobFileSize(fileLength));
						//job.setTaskNum(taskNum);
						if(onJobStatusChange!=null){
							observer.addJobListener(job,onJobStatusChange);
						}
						appendJob(job);
						
					}
				}).start();
		
		return job;
	}
	public Job newSubmitJob(final String url){
		return newSubmitJob(url,null);
	}
	
	public void addJobListener(Job job,OnJobStatusChangeListener onJobStatusChange){
		observer.addJobListener(job,onJobStatusChange);
	}
	
	public JobMemeryStore getJobMemeryStore(){
		return jobInMemery;
	}
	
	public void removeJob(Job job){
		
	}
	
	public void recoveryJob(Job job,OnJobStatusChangeListener onJobStatusChange){
			job.setStatus(Job.STATUS_RECREATED);
			List<Task> jobTaskList=store.getJobTasks(job);
			if(EmptyUtil.isEmpty(jobTaskList)){
				newSubmitJob(job.getUrl(),onJobStatusChange);
			}else{
				for(int j=0;j<jobTaskList.size();j++){
					taskManager.addTask(jobTaskList.get(j), observer);
				}
				observer.addJobListener(job,onJobStatusChange);
			}
		
	}
	public void continueJob(Job job){
		job.setStatus(Job.STATUS_RECREATED);
		List<Task> jobTaskList=store.getJobTasks(job);
		for(int j=0;j<jobTaskList.size();j++){
			taskManager.addTask(jobTaskList.get(j), observer);
		}
	}
	
	
	public interface OnJobStatusChangeListener{
		public void onJobInited(Job job);
		public void onJobStart(Job job);
		public void onJobProcessUpdated(Job job,int process);
		public void onJobStop(Job job);
		public void onJobfinished(Job job);
		public void onJobError(Job job);
	}
}
