package com.hikvision.parentdotworry.receiver;

import java.util.List;

import com.hikvision.parentdotworry.utils.NetState;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
/**
 * 一、判断网络是否是wifi，在判断之前一定要进行的非空判断，如果没有任何网络
连接info ==null
info.getType() == ConnectivityManager.TYPE_WIFI
 
二、判断是否是手机网络
info !=null && info.getType() ==  ConnectivityManager.TYPE_MOBILE
 
手机网络进行详细区分：
 
info.getSubtype() 这里使用 getSubtype()，不是 getType()，getType()返回的
是0，或者1，是区分是手机网络还是wifi
 
info.getSubtype()取值列表如下：
 
         * NETWORK_TYPE_CDMA 网络类型为CDMA
         * NETWORK_TYPE_EDGE 网络类型为EDGE
         * NETWORK_TYPE_EVDO_0 网络类型为EVDO0
         * NETWORK_TYPE_EVDO_A 网络类型为EVDOA
         * NETWORK_TYPE_GPRS 网络类型为GPRS
         * NETWORK_TYPE_HSDPA 网络类型为HSDPA
         * NETWORK_TYPE_HSPA 网络类型为HSPA
         * NETWORK_TYPE_HSUPA 网络类型为HSUPA
         * NETWORK_TYPE_UMTS 网络类型为UMTS
 
联通的3G为UMTS或HSDPA，移动和联通的2G为GPRS或EDGE，电信的2G为CDMA，电信
的3G为EVDO
 * @author xuweidong
 */
public class NetWorkChangeDispatcher {
	private static NetWorkChangeDispatcher instance = new NetWorkChangeDispatcher();

	

	private NetWorkChangeDispatcher() {
	}

	public static NetWorkChangeDispatcher getInstance() {
		return instance;
	}

	public void dealNetWorkChange(Context context, Intent intent,
			List<? extends OnNetWorkChangeListener> oberserList) {
		

		int netWorkStatus = NetState.getCurrentNetType();
		for (int i = 0; i < oberserList.size(); i++) {
			OnNetWorkChangeListener onwcListener = oberserList.get(i);
			switch (netWorkStatus) {
			case NetState.NETSTATUS_WIFI:
				onwcListener.onWifiConnected();
				break;

			case NetState.NETSTATUS_MOBILE_2G:
			case NetState.NETSTATUS_MOBILE_3G:
			case NetState.NETSTATUS_MOBILE_4G:
				onwcListener.onMobileConnected();
				break;
			case NetState.NETSTATUS_NONE:
				onwcListener.onNetDisConnected();
				break;
			default:
				break;
			}
		}
	}
}
