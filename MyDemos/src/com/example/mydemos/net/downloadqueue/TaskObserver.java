package com.example.mydemos.net.downloadqueue;

import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.util.Log;

import com.example.mydemos.net.downloadqueue.JobManager.OnJobStatusChangeListener;
import com.example.mydemos.net.downloadqueue.bean.Job;
import com.example.mydemos.net.downloadqueue.bean.Task;
import com.example.mydemos.net.downloadqueue.downloader.AbsTaskDownloader;
import com.example.mydemos.net.downloadqueue.downloader.Downloader;
import com.example.mydemos.net.downloadqueue.downloader.HttpComponentTaskDownloader;
import com.example.mydemos.net.downloadqueue.statusstore.JobMemeryStore;
import com.example.mydemos.net.downloadqueue.statusstore.StatusStore;

public class TaskObserver implements Observer{
	private static final String TAG="TaskObserver";
	
	private StatusStore store;
	private JobMemeryStore jobMemery;
	private TaskManager taskManager;
	private Map<Job,OnJobStatusChangeListener> jobListenerMap;
	private Lock lock=new ReentrantLock();


	public TaskObserver(StatusStore store, TaskManager taskManager,JobMemeryStore jobMemery) {
		super();
		this.store = store;
		this.taskManager = taskManager;
		this.jobListenerMap=new ConcurrentHashMap<Job,OnJobStatusChangeListener>();
		this.jobMemery=jobMemery;
	}


	@Override
	public void update(Observable observable, Object data) {
		lock.lock();
		//long current=System.currentTimeMillis();
		Downloader downloader = (AbsTaskDownloader)observable;
		Task task = downloader.getTask();
		Job job=task.getJob();
		int getedSize=(Integer)data;
		OnJobStatusChangeListener obJobListener = jobListenerMap.get(job);
		
		switch (task.getStatus()) {
		case Task.STATUS_INITING:
			store.storeJob(job);
			store.storeTask(job, task);
			if(job.getStatus()==Job.STATUS_CREATED||job.getStatus()==Job.STATUS_CREATED||job.getStatus()==Job.STATUS_ERROR){
				if(obJobListener!=null){
					obJobListener.onJobInited(job);
				}
			}
			job.setStatus(Job.STATUS_INITED);
			break;
		case Task.STATUS_FINISHED:
			store.storeTask(job, task);
			job.setDownloadedSize(job.getDownloadedSize()+getedSize);
			Log.d(TAG,"job_"+job.getFileName()+" has "+job.getDownloadedSize()+"b downloaded");
			Log.i(TAG, "task finished url="+job.getUrl());
			store.removeTask(job, task);
			if(job.getDownloadedSize()>=job.getTotalSize()){
				Log.i(TAG,"job_"+job.getFileName()+" has finished,"+job.getDownloadedSize()+"byte downloaded");
				store.removeJobTasks(job);
				store.removeJob(job);
				jobMemery.remove(job);
				if(obJobListener!=null){
					obJobListener.onJobfinished(job);
				}
			}
			
			break;
		case Task.STATUS_RUNNING:
			if(job.getStatus()==Job.STATUS_INITED){
				job.setStatus(Job.STATUS_STARTED);
				if(obJobListener!=null){
					obJobListener.onJobStart(job);
				}
				job.setStatus(Job.STATUS_RUNNING);
			}
			int jobDownloaded=job.getDownloadedSize()+getedSize;
			job.setDownloadedSize(jobDownloaded);
			store.updateTask(job, task, task.getCurrentPos());
			store.updateJob(job, jobDownloaded);
			obJobListener.onJobProcessUpdated(job,jobDownloaded);
			Log.d(TAG,"job_"+job.getFileName()+" has "+job.getDownloadedSize()+"b downloaded");
			
			break;
		case Task.STATUS_STOP:
			store.storeJob(job);
			store.storeTask(job, task);
			boolean stoped=true;
			for(Task jobTask:job.getTasks()){
				if(jobTask.getStatus()==Task.STATUS_RUNNING){
					stoped=false;
					break;
				}
			}
			if(stoped){
				obJobListener.onJobStop(job);
				jobMemery.remove(job);
			}
			
			job.setStatus(Job.STATUS_INITED);
			break;
		case Task.STATUS_ERROR:
			Log.e(TAG, "task get Error，url="+job.getUrl());
			
			job.setStatus(Job.STATUS_ERROR);
			List<Task> jobTasks=store.getJobTasks(job);
			Task lastSuccessTask = null;
			for(Task taskTmp:jobTasks){
				if(task.getByteStart()==taskTmp.getByteStart()){
					lastSuccessTask=taskTmp;
					break;
				}
			}
			lastSuccessTask.setJob(job);
			taskManager.addTask(lastSuccessTask,this);//此时当前线程还没有结束，结束以后会运行
			
			break;

		default:
			break;
		}
		lock.unlock();
		//System.out.println("cost:"+(System.currentTimeMillis()-current));
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

	public void addJobListener(Job job,OnJobStatusChangeListener onJobStatusChange){
		jobListenerMap.put(job, onJobStatusChange);
	}


}
