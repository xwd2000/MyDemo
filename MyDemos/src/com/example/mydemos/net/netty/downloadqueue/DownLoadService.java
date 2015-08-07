package com.example.mydemos.net.netty.downloadqueue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DownLoadService extends Service{
	ExecutorService threadPool;
	private List<Task> taskList;
	
	@Override
	public void onCreate() {

		threadPool = Executors.newFixedThreadPool(4);
		taskList = new ArrayList<Task>();

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
		threadPool.shutdown();
		super.onDestroy();
	}
	
	public class DownLoadRunable implements Runnable{
		@Override
		public void run() {
			
		}
		
	}

}
