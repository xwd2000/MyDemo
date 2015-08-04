package com.example.mydemos.system.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ServiceTest extends Service{
	private final String TAG = "ServiceTest";
	@Override
	public void onCreate() {
		Log.d(TAG, "服务启动");
		
		super.onCreate();
	}
	
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "服务绑定");
		return null;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(TAG, "解除服务绑定");
		return super.onUnbind(intent);
	}
	
	@Override
	public void onDestroy() {
		Log.d(TAG, "服务销毁");
		super.onDestroy();
	}

}
