package com.example.util;

import com.example.mydemos.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ApplicationPreference {
	private Context context;

	public ApplicationPreference(Context context) {
		this.context = context;
	}
	
	
	/**
	 * 使用方式，applicationPreference.getEditor().putString(key1,val1).putString(key2,val2).commit();
	 * @return Editor对象，存储多个的时候建议用这种方式
	 */
	public Editor getEditor(){
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				context.getResources().getString(R.string.app_name),
				Context.MODE_PRIVATE);
		return sharedPreferences.edit();// 获取编辑器
	}
	/**
	 * 设置单个属性的时候用
	 * @param key
	 * @param data
	 */
	public void setString(String key,String data) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				context.getResources().getString(R.string.app_name),
				Context.MODE_PRIVATE);

		Editor editor = sharedPreferences.edit();// 获取编辑器

		editor.putString(key, data);
		editor.commit();// 提交修改

	}

	/**
	 * 获取设置
	 * @param key 键值
	 * @return 偏好值
	 */
	public String getString(String key) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				context.getResources().getString(R.string.app_name),
				Context.MODE_PRIVATE);
		return sharedPreferences.getString(key, "");
	}	
	
	
	/**
	 * 获取设置
	 * @param key 键值
	 * @defaultValue 默认值
	 * @return 偏好值
	 */
	public String getString(String key,String defaultValue) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				context.getResources().getString(R.string.app_name),
				Context.MODE_PRIVATE);
		return sharedPreferences.getString(key, "");
	}
}
