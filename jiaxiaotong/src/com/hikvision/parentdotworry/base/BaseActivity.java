package com.hikvision.parentdotworry.base;


import org.apache.log4j.Logger;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.hikvision.parentdotworry.application.AppApplication;

public class BaseActivity extends FragmentActivity {
	private Logger logger;
	private String mClazzName;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mClazzName=this.getClass().getName();
		logger= Logger.getLogger(getClass());
		//记录activity
		AppApplication appAppli = (AppApplication)getApplication();
		appAppli.pushActivity(this);
	}
	
	@Override
	protected void onDestroy() {
		//移除activity
		AppApplication appAppli = (AppApplication)getApplication();
		appAppli.popActivity(this);
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		this.finish();
	}
	
	
	protected String getClassName(){
		if(mClazzName==null||"".equals(mClazzName)){
			return "";
		}else{
			return mClazzName.substring(mClazzName.lastIndexOf('.')+1);
		}
	}
	
	protected void toast(String text){
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
	}
	protected void toast(int textId){
		Toast.makeText(getApplicationContext(), textId, Toast.LENGTH_SHORT).show();
	}
	
	protected void logd(String message){
		//Log.d(getClassName(),message);
		logger.debug(message);
	}
	protected void logi(String message){
		//Log.i(getClassName(),message);
		logger.info(message);
	}	
	protected void loge(String message){
		//Log.e(getClassName(),message);
		logger.error(message);
	}
	/**
	 * 获取String
	 * @param id
	 * @return
	 */
	protected String getStringById(int id){
		return getResources().getString(id);
	}
	/**
	 * 获取drawable
	 * @param id
	 * @return
	 */
	protected Drawable getDrawableById(int id){
		return getResources().getDrawable(id);
	}
	
	/**
	 * 获取drawable
	 * @param id
	 * @return
	 */
	protected int getColorById(int id){
		return getResources().getColor(id);
	}
	


}
