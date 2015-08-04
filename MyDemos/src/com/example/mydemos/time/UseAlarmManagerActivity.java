package com.example.mydemos.time;

import com.example.mydemos.R;
import com.example.mydemos.system.service.ServiceTest;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class UseAlarmManagerActivity extends Activity {
	private final String TAG="UseAlarmManagerActivity";
	
	private TextView tvAlarmNotice;

	private AlarmManager am;
	// 用来传递给服务的pendingItent
	private PendingIntent sender;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.use_alarm_manage);

		tvAlarmNotice = (TextView) findViewById(R.id.tv_alarm_notice);
		tvAlarmNotice.setText("正在定时启动");
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 获得闹钟管理器
		am = (AlarmManager) getSystemService(ALARM_SERVICE);
		sender = PendingIntent.getService(this, 0, new Intent(this,
				ServiceTest.class), PendingIntent.FLAG_ONE_SHOT);
		// 设置任务执行计划
		am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 5 * 1000,
				5 * 1000, sender);// 从firstTime才开始执行，每隔5秒
		Log.d(TAG,"已启动");
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (am == null || sender == null) {
			return;
		}
		am.cancel(sender);
		am=null;
		sender=null;
		Log.d(TAG,"已取消");
	}

}
