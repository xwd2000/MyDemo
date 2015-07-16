package com.hikvision.parentdotworry.bean;

import java.io.Serializable;

import com.hikvision.parentdotworry.bean.interf.FromDb;

public class MessageBean implements FromDb,Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String title;
	private String picUrl;
	private String promulgator;
	private String releaseTime;
	private Integer isNew;
	private Integer childId;
	private String content;
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public String getPromulgator() {
		return promulgator;
	}

	public void setPromulgator(String promulgator) {
		this.promulgator = promulgator;
	}

	public String getReleaseTime() {
		return releaseTime;
	}

	public void setReleaseTime(String releaseTime) {
		this.releaseTime = releaseTime;
	}

	public Integer getIsNew() {
		return isNew;
	}

	public void setIsNew(Integer isNew) {
		this.isNew = isNew;
	}

	public Integer getChildId() {
		return childId;
	}

	public void setChildId(Integer childId) {
		this.childId = childId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return new StringBuilder()
		.append("id：")
		.append(id)
		.append(",title：")
		.append(title)
		.append(",picUrl：")
		.append(picUrl)
		.append(",promulgator：")
		.append(promulgator)
		.append(",releaseTime：")
		.append(releaseTime)
		.append(",isNew：")
		.append(isNew)
		.append(",childId：")
		.append(childId)
		.append(",content：")
		.append(content)
		.toString();

	}
	
	
}
