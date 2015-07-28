package com.hikvision.parentdotworry.bean;

import com.hikvision.parentdotworry.bean.interf.FromDb;

public class AdvertisementInfo implements FromDb{
	private String picUrl;
	private String content;
	private String title;
	private Integer id;
	private Integer childId;
	public AdvertisementInfo(){}
	
	public AdvertisementInfo( String picUrl,
			String content) {
		super();
		this.picUrl = picUrl;
		this.content = content;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

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

	
}
