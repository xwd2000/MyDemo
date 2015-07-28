package com.hikvision.parentdotworry.dataprovider.httpdata.ezbean;

import java.util.Map;

public class EzRequestParam {

	private EzSystemParam system;
	private String method;
	private Map<String,Object> params;
	private Integer id;
	public EzSystemParam getSystem() {
		return system;
	}
	public void setSystem(EzSystemParam system) {
		this.system = system;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public Map<String, Object> getParams() {
		return params;
	}
	public void setParams(Map<String, Object> params) {
		this.params = params;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	
}
