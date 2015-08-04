package com.example.mydemos.system.broadcastreceiver;

import com.example.mydemos.system.service.ServiceTest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


public class ServiceStartReceiver extends BroadcastReceiver{
	private final String TAG = "ServiceStartReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		
		Toast.makeText(context, "myReceiver receive", Toast.LENGTH_SHORT)  
        .show();
		//后边的XXX.class就是要启动的服务  
        Intent service = new Intent(context,ServiceTest.class);  
        context.startService(service);  
        Log.v(TAG, "开机自动服务自动启动.....");
	}

}
