package com.hikvision.parentdotworry.bean;

import java.util.Date;

import com.hikvision.parentdotworry.bean.interf.FromDb;

public class ChildCaptureInfo implements FromDb {
	private Integer id;
	private Integer childId;
	private Date time;
	private String picUrl;
	//1：进    2：出
	private Integer state;   //孩子是进还是出

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

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append("id：")
				.append(id)
				.append(",childId：")
				.append(childId)
				.append(",time：")
				.append(time)
				.append(",picUrl：")
				.append(picUrl)
				.append(",state：")
				.append(state)
				.toString();

	}

}
