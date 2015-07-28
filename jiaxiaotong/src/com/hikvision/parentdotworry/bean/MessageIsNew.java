package com.hikvision.parentdotworry.bean;

import com.hikvision.parentdotworry.bean.interf.FromDb;

public class MessageIsNew implements FromDb{
	private Integer id;
	private Integer isNew;


	public Integer getIsNew() {
		return isNew;
	}
	public void setIsNew(Integer isNew) {
		this.isNew = isNew;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	
}
