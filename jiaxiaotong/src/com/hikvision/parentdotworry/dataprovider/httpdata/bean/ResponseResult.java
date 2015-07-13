package com.hikvision.parentdotworry.dataprovider.httpdata.bean;

public class ResponseResult <T>{

	private T result;
	private ResponseStatus status;
	
	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}

	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}
	
}
