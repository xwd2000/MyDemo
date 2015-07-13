package com.hikvision.parentdotworry.receiver;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import com.hikvision.parentdotworry.application.AppApplication;
import com.hikvision.parentdotworry.utils.EmptyUtil;
import com.hikvision.parentdotworry.utils.MapUtil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class AppReceiver extends BroadcastReceiver {
	private static String TAG = "AppReceiver";
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

	}

}
