package com.hikvision.parentdotworry.bean;

import com.hikvision.parentdotworry.bean.interf.FromDb;

public class NomalTime implements FromDb{
	/**
	 * 这里的id和childId相同
	 */
	private Integer id;
	private String startTime;
	private String endTime;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
}
