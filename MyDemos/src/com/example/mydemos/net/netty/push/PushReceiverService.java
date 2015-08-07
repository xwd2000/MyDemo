package com.example.mydemos.net.netty.push;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

public class PushReceiverService extends Service{
	private Client pushClient;
	private String host;
	private int port;
	private Thread runningThread;
	@Override
	public void onCreate() {
		
		super.onCreate();
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Bundle b= intent.getExtras();
		String operate=b.getString("operate");
		if("startPush".equals(operate)){
			host = b.getString("serverIp");
			port = b.getInt("serverPort");
			startPush(host,port);
		}
		if("stopPush".equals(operate)){
		}
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	private void startPush(final String ip,final int port){
		runningThread = new Thread(){
			@Override
			public void run() {
				pushClient=new Client(ip, port);
				pushClient.run();
				super.run();
			}};
		runningThread.start();
		
		
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}

	

}
