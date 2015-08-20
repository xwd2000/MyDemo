package com.example.mydemos.system.perference;

import java.lang.reflect.Method;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePerferencesUtils {
	private String preferenceName;
	private Context context;
	private boolean isReflectOpen;
	private final String keyPrefix="#%"; 
	
	
	
	
	public SharePerferencesUtils(String preferenceName,Context context) {
		super();
		this.preferenceName = preferenceName;
		this.context = context;
		this.isReflectOpen=false;
	}
	public SharePerferencesUtils(String preferenceName,Context context,boolean isReflectOpen) {
		super();
		this.preferenceName = preferenceName;
		this.context = context;
		this.isReflectOpen=isReflectOpen;
	}
	/**
	 * value支持格式Integer,String,Long,Float,Boolean,Set<String>
	 * @param key
	 * @param value
	 */
	public void put(Object... keyAndValue){
		SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		for(int i=0;i<keyAndValue.length;i=i+2){
			String key=""+keyAndValue[i];
			Object value = keyAndValue[i+1];
			put(settings,editor,key,value);
		}
		editor.commit();
	}
	
	/**
	 * value支持格式Integer,String,Long,Float,Boolean,Set<String>
	 * @param key
	 * @param value
	 */
	public void put(String key,Object value){
		SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		put(settings,editor,key,value);
		editor.commit();
	}
	
	
	@SuppressWarnings("unchecked")
	/**
	 * value支持格式Integer,String,Long,Float,Boolean,Set<String>
	 * @param editor
	 * @param key
	 * @param value
	 */
	private void put(SharedPreferences settings,SharedPreferences.Editor editor,String key,Object value){
		String type=null;
		if(value instanceof String){
			editor.putString(key, ""+value);
			type="String";
		}else if(value instanceof Integer){
			editor.putInt(key, (Integer)value);
			type="Int";
		}else if(value instanceof Float){
			editor.putFloat(key, (Float)value);
			type="Float";
		}else if(value instanceof Boolean){
			editor.putBoolean(key, (Boolean)value);
			type="Boolean";
		}else if(value instanceof Long){
			editor.putLong(key, (Long)value);
			type="Long";
		}else if(value instanceof Set<?>){
			editor.putStringSet(key, (Set<String>)value);
			type="StringSet";
		}else{
			throw new IllegalArgumentException("value type unknow,please use String,Integer,Float,Boolean,Long,Set<String>");
		}
		if(type!=null&&isReflectOpen){
			String valueTypeKey=keyPrefix+key;
			String oldType=settings.getString(valueTypeKey, null);
			if(oldType!=null&&!oldType.equals(type)){
				try{
					throw new IllegalArgumentException("conflict value type,perference has store the "+key+" with another type value");
				}catch(IllegalArgumentException e){
					e.printStackTrace();
				}
			}
			if(type.equals(oldType)){
				return;
			}
			editor.putString(valueTypeKey, type);
		}
	}
	
	/**
	 * you should open isReflect and this method is use reflect
	 * @param key
	 * @param defaults
	 * @return
	 */
	public Object get(String key,Object defaults){
		if(!isReflectOpen){
			try{
				throw new IllegalArgumentException("can't use get method if reflect is not open");
			}catch(IllegalArgumentException e){
				e.printStackTrace();
				return null;
			}
		}
		SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
		String type = settings.getString( keyPrefix+key, null);
		if(type==null){
			return null;
		}
		
		Class<?> clazz = null;
		if(type.equals("String")){
			clazz=String.class;
		}else if(type.equals("Int")){
			clazz=int.class;
		}else if(type.equals("Float")){
			clazz=float.class;
		}else if(type.equals("Long")){
			clazz=long.class;
		}else if(type.equals("StringSet")){
			clazz=Set.class;
		}else if(type.equals("Boolean")){
			clazz=boolean.class;
		}
		
		try {
			Method mthod= SharedPreferences.class.getMethod("get"+type, String.class,clazz);
			return mthod.invoke(settings, key,defaults);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * you should open isReflect and this method is use reflect
	 * @param key
	 * @param defaults
	 * @return
	 */
	public Object get(String key){
		return get(key,null);
	}
	
	public Integer getInt(String key,Integer defaults){
		SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
		return settings.getInt(key, defaults);
	}

	public Float getFloat(String key,Float defaults){
		SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
		return settings.getFloat(key, defaults);
	}
	public Boolean getBoolean(String key,Boolean defaults){
		SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
		return settings.getBoolean(key, defaults);
	}
	public Long getLong(String key,Long defaults){
		SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
		return settings.getLong(key, defaults);
	}
	public Set<String> getStringSet(String key,Set<String> defaults){
		SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
		return settings.getStringSet(key, defaults);
	}
	
	public String getString(String key){
		return getString(key,"");
	}
	
	public String getString(String key,String defaults){
		SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
		return settings.getString(key, defaults);
	}
}


