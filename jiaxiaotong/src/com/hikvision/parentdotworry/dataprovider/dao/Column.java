package com.hikvision.parentdotworry.dataprovider.dao;

public enum Column {
	// table_message_bean
	ID("id"),
	RELEASE_TIME("release_time"),
	CHILD_ID("child_id"),
	PROMULGATOR("promulgator"),
	PIC_URL("pic_url"),
	IS_NEW("is_new"),
	TITLE("title"),
	CONTENT("content");
	
	public final String columnName;
	private Column(String columnName){
		this.columnName=columnName;
	}
	
}
