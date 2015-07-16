package com.hikvision.parentdotworry.dataprovider.httpdata.core;

import java.util.Map;

public class BaseServerSelector<Key> {
	private Map<Key,String> serverStore;
	private String defaultIp;
	
	public BaseServerSelector(Map<Key, String> serverStore) {
		super();
		this.serverStore = serverStore;
	}
	
	public BaseServerSelector(String defaultIp) {
		super();
		this.defaultIp = defaultIp;
	}
//	public String getServerIpByKey(Key key){
//		
//	}
//	public String getServerIpByKey(){
//		
//	}
}
