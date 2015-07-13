package com.hikvision.parentdotworry.vo;

import java.util.List;

import com.hikvision.parentdotworry.bean.MessageBean;

public class ChildMessage {
	private int parentId;
	private String parentName;
	private int childId;
	private String ChildName;
	private List<MessageBean> messageList;
	
	
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public int getChildId() {
		return childId;
	}
	public void setChildId(int childId) {
		this.childId = childId;
	}
	public String getChildName() {
		return ChildName;
	}
	public void setChildName(String childName) {
		ChildName = childName;
	}
	public List<MessageBean> getMessageList() {
		return messageList;
	}
	public void setMessageList(List<MessageBean> messageList) {
		this.messageList = messageList;
	}
	

	
	
}
