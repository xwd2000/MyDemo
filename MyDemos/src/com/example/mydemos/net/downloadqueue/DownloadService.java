package com.example.mydemos.net.downloadqueue;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;

import com.example.mydemos.net.downloadqueue.assist.statusstore.PerferenceStore;
import com.example.mydemos.net.downloadqueue.assist.statusstore.StatusStore;

public class DownloadService extends Service{
	private JobManager jobManage;
	private DownloadConfigure dc;
	@Override
	public void onCreate() {
		dc=new DownloadConfigure.Builder()
		.threadPoolSize(5)
		.threadNumPerJob(3)
		.threadPriority(Thread.NORM_PRIORITY-2)
		.savePathBase(Environment.getExternalStorageDirectory().toString())
		.build();
		StatusStore store = new PerferenceStore();
		jobManage=new JobManager(dc,store);
		final String url="http://10.20.34.109:8080/WebModule/photo.rar";
		final String url2="http://10.20.34.109:8080/WebModule/集体照 - 副本 (2).rar";
		jobManage.submitJob(url);
		jobManage.submitJob(url2);
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}
	
	
	

	@Override
	public void onDestroy() {
		dc.deBuild();
		super.onDestroy();
	}
	
	public class DownLoadRunable implements Runnable{
		@Override
		public void run() {
			
		}
		
	}

}
