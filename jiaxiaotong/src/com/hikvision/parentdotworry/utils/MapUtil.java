package com.hikvision.parentdotworry.utils;

import java.util.HashMap;

public class MapUtil {
	/**
	 * 简化map创建过程，使用方式 generateMap(key,value,key,value);
	 * @param keyAndVal 键值对
	 * @return 生成好的HashMap
	 */
	public static HashMap<String,Object> generateMap(Object ...  keyAndVal){
		Args.check(keyAndVal.length!=0&keyAndVal.length%2==0, "输入参数数量必须非0且为2的倍数");
		HashMap<String,Object> map=new HashMap<String,Object>();
		for(int i=0,j=keyAndVal.length;i<j;i=i+2){
			map.put(keyAndVal[i]+"", keyAndVal[i+1]);
		}
		return map;
	}
	
	/**
	 * 简化map创建过程，使用方式 generateMap(key,value,key,value);
	 * @param keyAndVal 键值对
	 * @return 生成好的HashMap
	 */
	@SuppressWarnings("unchecked")
	public static <T> HashMap<String,T> generateMap(Class<T> clazz,Object ...  keyAndVal){
		Args.check(keyAndVal.length!=0&keyAndVal.length%2==0, "输入参数数量必须非0且为2的倍数");
		HashMap<String,T> map=new HashMap<String,T>();
		for(int i=0,j=keyAndVal.length;i<j;i=i+2){
			map.put(keyAndVal[i]+"", (T)keyAndVal[i+1]);
		}
		return map;
	}
}
