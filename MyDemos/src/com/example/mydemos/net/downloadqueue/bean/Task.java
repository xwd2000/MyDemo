package com.example.mydemos.net.downloadqueue.bean;

public class Task{

	public static final int STATUS_INITING=0;
	public static final int STATUS_RUNNING=1;
	public static final int STATUS_STOP=2;
	public static final int STATUS_FINISHED=3;
	public static final int STATUS_ERROR=4;
	
	
	private Integer currentPos;
	private Integer byteStart;
	private Integer byteEnd;
	private Integer status;
	private Job job;
 	



	public Integer getCurrentPos() {
		return currentPos;
	}
	public void setCurrentPos(Integer currentPos) {
		this.currentPos = currentPos;
	}
	public Integer getByteStart() {
		return byteStart;
	}
	public void setByteStart(Integer byteStart) {
		this.byteStart = byteStart;
	}
	public Integer getByteEnd() {
		return byteEnd;
	}
	public void setByteEnd(Integer byteEnd) {
		this.byteEnd = byteEnd;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}

	
	public Job getJob() {
		return job;
	}
	public void setJob(Job job) {
		this.job = job;
	}



	
	

	

}
