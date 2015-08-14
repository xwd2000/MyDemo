package com.example.mydemos.net.downloadqueue.statusstore;

import java.util.Deque;
import java.util.LinkedList;

import com.example.mydemos.net.downloadqueue.bean.Job;

public class JobMemeryStore{
	private Deque<Job> jobList;
	
	
	public JobMemeryStore() {
		jobList=new LinkedList<Job>();
	}
	
	public void add(Job job){
		jobList.add(job);
	}
	
	public void remove(Job job){
		jobList.remove(job);
	}
	
	public Deque<Job> getAllJobs(){
		return jobList;
	}
}
