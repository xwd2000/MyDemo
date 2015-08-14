package com.example.mydemos.net.downloadqueue.statusstore;

import java.util.List;

import com.example.mydemos.net.downloadqueue.bean.Job;
import com.example.mydemos.net.downloadqueue.bean.Task;

public interface StatusStore {

	public void storeTask(Job job,Task task);
	public void updateTask(Job job,Task task,int currentPos);
	public void updateJob(Job job,int currentPos);

	public void storeJob(Job job);
	
	public void removeJob(Job job);
	
	public void removeTask(Job job,Task task);

	public void removeJobTasks(Job job);

	public List<Job> getUnFinishJob();
	
	public List<Task> getJobTasks(Job job);
	
}
