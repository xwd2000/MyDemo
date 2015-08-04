package com.example.mydemos.android.util;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class NetState {

	public static final int NETSTATUS_NONE = -1;

	/**
	 * The Default WIFI data connection. When active, all data traffic will use
	 * this connection by default.
	 */
	public static final int NETSTATUS_WIFI = 1;
	/**
	 * The Default Mobile data connection. When active, all data traffic will
	 * use this connection by default.
	 */
	public static final int NETSTATUS_MOBILE_2G = 2;
	public static final int NETSTATUS_MOBILE_3G = 3;
	public static final int NETSTATUS_MOBILE_4G = 4;

	public static boolean getNetWorkded(Context context) {
		boolean isNetWork = true;
		if (getCurrentNetType(context) == NETSTATUS_NONE) {
			isNetWork = false;
		} else {
			isNetWork = true;
		}
		return isNetWork;
	}

	/**
	 * 
	 * @return NETSTATUS_NONE,NETSTATUS_WIFI,NETSTATUS_MOBILE_2G,NETSTATUS_MOBILE_3G,NETSTATUS_MOBILE_4G
 	 */
	public static int getCurrentNetType(Context context) {
		ConnectivityManager connectMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo info = connectMgr.getActiveNetworkInfo();
		if (info == null || !info.isAvailable()) {
			return NETSTATUS_NONE;
		}
		if (info.getType() == ConnectivityManager.TYPE_WIFI) {
			return NETSTATUS_WIFI;
		} else {
			int subType = info.getSubtype();
			if (subType == TelephonyManager.NETWORK_TYPE_CDMA // ctnet
					|| subType == TelephonyManager.NETWORK_TYPE_GPRS // Un
					|| subType == TelephonyManager.NETWORK_TYPE_EDGE
					|| subType == TelephonyManager.NETWORK_TYPE_IDEN// mobile
			) {
				return NETSTATUS_MOBILE_2G;
			} else if (subType == TelephonyManager.NETWORK_TYPE_UMTS
					|| subType == TelephonyManager.NETWORK_TYPE_HSUPA // ctnet
					|| subType == TelephonyManager.NETWORK_TYPE_EVDO_0
					|| subType == TelephonyManager.NETWORK_TYPE_EVDO_A
					|| subType == TelephonyManager.NETWORK_TYPE_HSPA
					|| subType == TelephonyManager.NETWORK_TYPE_1xRTT
					|| subType == TelephonyManager.NETWORK_TYPE_HSDPA
					|| subType == TelephonyManager.NETWORK_TYPE_EVDO_B) {
				return NETSTATUS_MOBILE_3G;
			} else if (subType == TelephonyManager.NETWORK_TYPE_LTE
					|| subType == TelephonyManager.NETWORK_TYPE_EHRPD
					|| subType == TelephonyManager.NETWORK_TYPE_HSPAP) {
				return NETSTATUS_MOBILE_4G;
			}
		}
		return NETSTATUS_NONE;
	}
}
