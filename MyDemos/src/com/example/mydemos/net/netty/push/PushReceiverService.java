package com.example.mydemos.net.netty.push;

import com.example.mydemos.AppApplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class PushReceiverService extends Service{
	private Client pushClient;
	private String host = AppApplication.PUSH_HOST;
	private int port = AppApplication.PUSH_PORT;
	private Thread runningThread;
	@Override
	public void onCreate() {
		runningThread = new Thread(){
			@Override
			public void run() {
				pushClient=new Client(host, port);
				pushClient.run();
				super.run();
			}};
		runningThread.start();
		
		
		super.onCreate();
	}
	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}

	

}
