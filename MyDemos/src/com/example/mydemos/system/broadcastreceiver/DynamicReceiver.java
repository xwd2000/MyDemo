package com.example.mydemos.system.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class DynamicReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Toast.makeText(context, "接收器接收到消息"+intent.getAction(), Toast.LENGTH_SHORT).show();
	}
	

}
