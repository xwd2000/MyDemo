package com.hikvision.parentdotworry.service;

import org.apache.log4j.Logger;

import com.hikvision.parentdotworry.utils.CountUtil;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class AppService extends Service {
	private Logger logger=Logger.getLogger(AppService.class);
	private ServiceBinder mBinder;
	private CountUtil mCountSendSms;

	@Override
	public void onCreate() {
		super.onCreate();
		logger.debug("创建service");
		mBinder = new ServiceBinder();
	}

	@Override
	public IBinder onBind(Intent intent) {

		return mBinder;
	}

	public CountUtil getCountUtilSendSms() {
		return mCountSendSms;
	}

	public void setCountUtilSendSms(CountUtil countUtil) {
		if (mCountSendSms == countUtil) {
			return;
		}
		if (mCountSendSms != null) {
			mCountSendSms.stopCount();
			mCountSendSms = null;
		}
		this.mCountSendSms = countUtil;
	}

	@Override
	public void onDestroy() {
		logger.debug("销毁service");
		super.onDestroy();
	}

	public class ServiceBinder extends Binder {
		public AppService getService() {
			return AppService.this;
		}
	}
}
