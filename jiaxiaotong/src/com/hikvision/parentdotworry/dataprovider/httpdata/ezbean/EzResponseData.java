package com.hikvision.parentdotworry.dataprovider.httpdata.ezbean;

public class EzResponseData <T>{
	private Integer id;
	private EzResponseResult<T> result;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public EzResponseResult<T> getResult() {
		return result;
	}
	public void setResult(EzResponseResult<T> result) {
		this.result = result;
	}
	
}
