package com.example.mydemos.system.service;

import com.example.mydemos.R;
import com.example.mydemos.system.broadcastreceiver.ServiceStartReceiver;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class ServiceTestActivity extends Activity implements
		View.OnClickListener {
	private final String TAG="ServiceTestActivity";
	private ServiceStartReceiver bootReceiver;
	
	//服务绑定的连接毁掉
	private ServiceConnection serviceConnection;
	
	
	private TextView tvShowServiceStatusMessage;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.service_test_activity);
		
		initViewInstance();
	}

	private void initViewInstance(){
		tvShowServiceStatusMessage=(TextView)findViewById(R.id.tv_show_service_status_message);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_start_Service:
			if(!startServiceTestService()){
				Log.d(TAG, "服务启动失败");
			}
			break;
		case R.id.bt_bind_Service:
			if(!bindServiceTestService()){
				Log.d(TAG, "服务绑定失败");
			}
			break;
		case R.id.bt_unbind_Service:
			unbindServiceTestService();
			break;
		case R.id.bt_stop_Service:
			if(!stopServiceTestService()){
				Log.d(TAG, "停止服务失败");
			}
			break;
		case R.id.bt_boot_up_start_service:
			
			bootReceiver = new ServiceStartReceiver();
			IntentFilter filter = new IntentFilter(
					"android.intent.action.BOOT_COMPLETED");
			filter.addCategory("android.intent.category.LAUNCHER");
			registerReceiver(bootReceiver, filter);
			Log.d(TAG, "注册启动接收器成功");
			break;
		case R.id.bt_cancel_boot_up_start_service:
			if(bootReceiver!=null){
				unregisterReceiver(bootReceiver);
				Log.d(TAG, "解除启动接收器成功");
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * 绑定服务
	 * @return
	 */
	private boolean bindServiceTestService(){
		Intent intentBind = new Intent(this, ServiceTest.class);
		serviceConnection=new ServiceConnection() {
			@Override
			public void onServiceDisconnected(ComponentName name) {
				Log.d(TAG, "断开服务连接");
			}
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				Log.d(TAG, "建立服务连接");
			}
		};
		return bindService(intentBind, serviceConnection, BIND_AUTO_CREATE);
	}
	
	/**
	 * 解绑服务
	 */
	private void unbindServiceTestService(){
		if(serviceConnection!=null){
			unbindService(serviceConnection);
			serviceConnection=null;
		}
	}
	
	/**
	 * 开始服务
	 * @return
	 */
	private boolean startServiceTestService(){
		Intent intentStart = new Intent(this, ServiceTest.class);
		if(null==startService(intentStart)){
			return false;
		}
		return true;
	
	}	
	
	/**
	 * 停止服务
	 * @return
	 */
	private boolean stopServiceTestService(){
		Intent intentStop = new Intent(this, ServiceTest.class);
		return stopService(intentStop);
	}
	
	

}
