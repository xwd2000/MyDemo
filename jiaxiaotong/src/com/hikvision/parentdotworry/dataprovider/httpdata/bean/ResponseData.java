package com.hikvision.parentdotworry.dataprovider.httpdata.bean;

public class ResponseData <T>{
	private T data;

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
