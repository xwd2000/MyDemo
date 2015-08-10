package com.example.mydemos.net.downloadqueue;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;

import com.example.mydemos.net.downloadqueue.BeforeDownLoad.AfterFileLengthGeted;
import com.example.mydemos.net.downloadqueue.bean.Job;

public class DownloadService extends Service{
	private JobManager jobManage;
	@Override
	public void onCreate() {
		DownloadConfigure dc=new DownloadConfigure.Builder()
		.threadPoolSize(5)
		.threadNumPerJob(2)
		.threadPriority(Thread.NORM_PRIORITY-2)
		.savePathBase(Environment.getExternalStorageDirectory().toString())
		.build();
		jobManage=new JobManager(dc);
		final String url="http://10.20.34.109:8080/WebModule/photo.rar";
		new BeforeDownLoad(url, 
				new AfterFileLengthGeted() {
					@Override
					public void afterFileLengthGetted(int fileLength) {
						Job job=jobManage.genJob(fileLength,url);
						//job.setTaskNum(taskNum);
						jobManage.appendTask(job);
					}
				}).start();
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
		
		super.onDestroy();
	}
	
	public class DownLoadRunable implements Runnable{
		@Override
		public void run() {
			
		}
		
	}

}
