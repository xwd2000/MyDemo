package com.hikvision.parentdotworry.application;

import com.hikvision.parentdotworry.utils.SharePerferencesUtils;

/**
 * 全部存储为字符串
 * @author xuweidong
 */
public class AppPerference {
	public static final SharePerferencesUtils spu;
	static{
		spu = new SharePerferencesUtils("jiaxiaotong_perferecnce",AppApplication.getApplication());
	}
	
	/**
	 * 这里添加所有的key
	 * @author xuweidong
	 */
	public enum K{
		ACCESS_TOKEN_KEY("access_token"),
		APP_VERSION_KEY("app_version");
		
		
		public String key;
		private K(String key){
			this.key=key;
		}
	}
	public class KeyVal{
		public K key;
		public String value;
		public KeyVal(K key, String value) {
			super();
			this.key = key;
			this.value = value;
		}
		
	}
	
	public static void put(K key,String value){
		spu.put(key.key,value);
	}
	
	public static void put(KeyVal... keyVal ){
		String[] strKeyVal = new String[keyVal.length*2];
		for(int i=0;i<keyVal.length*2;i++){
			strKeyVal[i]=keyVal[i/2].key.key;
			strKeyVal[i+1]=keyVal[i/2].value;
		}
		spu.put((Object[])strKeyVal);
	}
	
	public static String get(K key){
		return spu.getString(key.key);
	}
	
	public static String get(K key,String defaults){
		return spu.getString(key.key,defaults);
	}
}
