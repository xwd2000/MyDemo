package com.example.mydemos;

import com.example.mydemos.net.netty.push.PushReceiverService;

import android.app.Application;
import android.content.Intent;

public class AppApplication extends Application{
	public static AppApplication context;

	public static final String PUSH_HOST="10.20.34.109";
	public static final int PUSH_PORT=7777;
	
	
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		context=this;
		Intent intent = new Intent(this,PushReceiverService.class);
		startService(intent);
	}
	
	public static AppApplication getContext(){
		return context;
	}


}
