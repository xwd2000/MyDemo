package com.example.mydemos.net.downloadqueue.assist.statusstore;

import java.util.List;

import com.example.mydemos.net.downloadqueue.bean.Job;
import com.example.mydemos.net.downloadqueue.bean.Task;

public interface StatusStore {

	public void storeTask(Job job,Task task);
	
	public void removeTask(Job job,Task task);
	

	public List<Job> getUnFinishJob();
	
	public List<Task> getJobTasks(Job job);
	
}
