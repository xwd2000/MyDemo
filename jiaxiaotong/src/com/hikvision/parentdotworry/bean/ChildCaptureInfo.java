package com.hikvision.parentdotworry.bean;

import java.util.Date;

import com.hikvision.parentdotworry.bean.interf.FromDb;

public class ChildCaptureInfo implements FromDb {
	private Integer id;
	private Integer childId;
	private Date captureTime;
	private String captureUrl;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getChildId() {
		return childId;
	}

	public void setChildId(Integer childId) {
		this.childId = childId;
	}

	public Date getCaptureTime() {
		return captureTime;
	}

	public void setCaptureTime(Date captureTime) {
		this.captureTime = captureTime;
	}

	public String getCaptureUrl() {
		return captureUrl;
	}

	public void setCaptureUrl(String captureUrl) {
		this.captureUrl = captureUrl;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append("id：")
				.append(id)
				.append(",childId：")
				.append(childId)
				.append(",captureTime：")
				.append(captureTime)
				.append(",captureUrl：")
				.append(captureUrl)
				.toString();

	}

}
