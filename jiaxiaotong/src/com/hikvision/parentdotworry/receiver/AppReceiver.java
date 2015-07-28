package com.hikvision.parentdotworry.receiver;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.hikvision.parentdotworry.application.AppApplication;
import com.hikvision.parentdotworry.push.NotifierUtils;
import com.hikvision.parentdotworry.service.AppService;
import com.hikvision.parentdotworry.utils.EmptyUtil;
import com.videogo.androidpn.Constants;

public class AppReceiver extends BroadcastReceiver {
	private static String TAG = "AppReceiver";
	private static final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";  
	@Override
	public void onReceive(Context context, Intent intent) { 
		AppApplication app = (AppApplication) context.getApplicationContext();
		List<WeakReference<Activity>> wrActivityList = app
				.getmRuningActivityList();

		String action = intent.getAction();
		
		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			List<OnNetWorkChangeListener> activityList = new LinkedList<OnNetWorkChangeListener>();
			for (int i = wrActivityList.size() - 1; i >=0; i--) {
				Activity ac = wrActivityList.get(i).get();
				if (ac != null && (ac instanceof OnNetWorkChangeListener)) {
					activityList.add((OnNetWorkChangeListener)ac);
				}
			}
			if(!EmptyUtil.isEmpty(activityList)){
				NetWorkChangeDispatcher.getInstance().dealNetWorkChange(context,
						intent, activityList);
				
			}
		}
		else if (Constants.NOTIFICATION_RECEIVED_ACTION.equals(action)) {
            NotifierUtils.showNotification(context, intent);
        }
		else if ( ACTION_BOOT.equals(action)) {
			Intent service = new Intent(context,AppService.class);
            context.startService(service);
        }
	}

}
