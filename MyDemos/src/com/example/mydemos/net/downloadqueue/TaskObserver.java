package com.example.mydemos.net.downloadqueue;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.util.Log;

import com.example.mydemos.net.downloadqueue.assist.statusstore.StatusStore;
import com.example.mydemos.net.downloadqueue.bean.Job;
import com.example.mydemos.net.downloadqueue.bean.Task;
import com.example.mydemos.net.downloadqueue.downloader.AbsTaskDownloader;
import com.example.mydemos.net.downloadqueue.downloader.Downloader;
import com.example.mydemos.net.downloadqueue.downloader.HttpComponentTaskDownloader;

public class TaskObserver implements Observer{
	private static final String TAG="TaskObserver";
	
	private StatusStore store;
	private TaskManager taskManager;
	
	


	public TaskObserver(StatusStore store, TaskManager taskManager) {
		super();
		this.store = store;
		this.taskManager = taskManager;
	}


	@Override
	public synchronized void update(Observable observable, Object data) {
		Downloader downloader = (AbsTaskDownloader)observable;
		Task task = downloader.getTask();
		Job job=task.getJob();
		int getedSize=(Integer)data;
		
		switch (task.getStatus()) {
		case Task.STATUS_FINISHED:
			store.storeTask(job, task);
			job.setDownloadedSize(job.getDownloadedSize()+getedSize);
			Log.d(TAG,"job_"+job.getFileName()+" has "+job.getDownloadedSize()+"b downloaded");
			Log.i(TAG, "task finished url="+job.getUrl());
			store.removeTask(job, task);
			if(job.getDownloadedSize()>=job.getTotalSize()){
				Log.i(TAG,"job_"+job.getFileName()+" has finished,"+job.getDownloadedSize()+"byte downloaded");
			}
			break;
		case Task.STATUS_RUNNING:
			store.storeTask(job, task);
			
			job.setDownloadedSize(job.getDownloadedSize()+getedSize);
			Log.d(TAG,"job_"+job.getFileName()+" has "+job.getDownloadedSize()+"b downloaded");
			
			break;
		case Task.STATUS_ERROR:
			Log.e(TAG, "task get Error，url="+job.getUrl());
			List<Task> jobTasks=store.getJobTasks(job);
			Task lastSuccessTask = null;
			for(Task taskTmp:jobTasks){
				if(task.getByteStart()==taskTmp.getByteStart()){
					lastSuccessTask=taskTmp;
				}
			}
			lastSuccessTask.setJob(job);
			AbsTaskDownloader dl = new HttpComponentTaskDownloader();
			dl.setTask(lastSuccessTask);
			taskManager.addFirstDownloaderTask(dl);//此时当前线程还没有结束，结束以后会运行
			
			break;

		default:
			break;
		}
		
	}
	
	
	public StatusStore getStore() {
		return store;
	}



	public void setStore(StatusStore store) {
		this.store = store;
	}



	public TaskManager getTaskManager() {
		return taskManager;
	}



	public void setTaskManager(TaskManager taskManager) {
		this.taskManager = taskManager;
	}



}
