package com.hikvision.parentdotworry.utils;

import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;

import com.hikvision.parentdotworry.application.AppApplication;

public class SharePerferencesUtils {
	public String preferenceName;
	public Context context;
	
	public SharePerferencesUtils(String preferenceName,Context context) {
		super();
		this.preferenceName = preferenceName;
		this.context = context;
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
			put(editor,key,value);
		}
		editor.commit();
	}
	
	/**
	 * value支持格式Integer,String,Long,Float,Boolean,Set<String>
	 * @param key
	 * @param value
	 */
	public void put(String key,Object value){
		SharedPreferences settings = AppApplication.getApplication().getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		put(editor,key,value);
		editor.commit();
	}
	
	
	@SuppressWarnings("unchecked")
	/**
	 * value支持格式Integer,String,Long,Float,Boolean,Set<String>
	 * @param editor
	 * @param key
	 * @param value
	 */
	public void put(SharedPreferences.Editor editor,String key,Object value){
		if(value instanceof String){
			editor.putString(key, ""+value);
		}else if(value instanceof Integer){
			editor.putInt(key, (Integer)value);
		}else if(value instanceof Float){
			editor.putFloat(key, (Float)value);
		}else if(value instanceof Boolean){
			editor.putBoolean(key, (Boolean)value);
		}else if(value instanceof Long){
			editor.putLong(key, (Long)value);
		}else if(value instanceof Set<?>){
			editor.putStringSet(key, (Set<String>)value);
		}
	}
	
	public Integer getInt(String key,Integer defaults){
		SharedPreferences settings = AppApplication.getApplication().getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
		return settings.getInt(key, defaults);
	}

	public Float getFloat(String key,Float defaults){
		SharedPreferences settings = AppApplication.getApplication().getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
		return settings.getFloat(key, defaults);
	}
	public Boolean getBoolean(String key,Boolean defaults){
		SharedPreferences settings = AppApplication.getApplication().getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
		return settings.getBoolean(key, defaults);
	}
	public Long getLong(String key,Long defaults){
		SharedPreferences settings = AppApplication.getApplication().getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
		return settings.getLong(key, defaults);
	}
	public Set<String> getStringSet(String key,Set<String> defaults){
		SharedPreferences settings = AppApplication.getApplication().getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
		return settings.getStringSet(key, defaults);
	}
	
	public String getString(String key){
		return getString(key,"");
	}
	
	public String getString(String key,String defaults){
		SharedPreferences settings = AppApplication.getApplication().getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
		return settings.getString(key, defaults);
	}
}


