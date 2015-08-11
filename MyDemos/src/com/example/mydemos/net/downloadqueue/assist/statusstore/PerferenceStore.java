package com.example.mydemos.net.downloadqueue.assist.statusstore;

import java.util.List;

import com.example.mydemos.net.downloadqueue.bean.Job;
import com.example.mydemos.net.downloadqueue.bean.Task;

public class PerferenceStore implements StatusStore{

	@Override
	public void storeTask(Job job, Task task) {
		
		
	}

	@Override
	public void removeTask(Job job, Task task) {
		
		
	}

	@Override
	public List<Job> getUnFinishJob() {
		
		return null;
	}

	@Override
	public List<Task> getJobTasks(Job job) {
		
		return null;
	}

}
