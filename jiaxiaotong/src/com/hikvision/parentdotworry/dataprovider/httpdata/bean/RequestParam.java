package com.hikvision.parentdotworry.dataprovider.httpdata.bean;

import java.util.Map;

public class RequestParam {

	private SystemParam system;
	private Map<String,Object> params;
	
	
	public SystemParam getSystem() {
		return system;
	}
	public void setSystem(SystemParam system) {
		this.system = system;
	}
	public Map<String, Object> getParams() {
		return params;
	}
	public void setParams(Map<String, Object> params) {
		this.params = params;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("system:")
		.append(system)
		.append(",");

		if(params==null){
			sb.append("params:");
			sb.append(params);
		}else if(!params.isEmpty()){
			sb.append("params:{");
			for(String key:params.keySet()){
				sb.append(key);
				sb.append(":");
				sb.append(params.get(key));
				sb.append(",");
			}
			return sb.substring(0, sb.length()-1)+"}";
		}else{
			sb.append("params:\"\"");
		}
		return sb.toString();
	}

}
