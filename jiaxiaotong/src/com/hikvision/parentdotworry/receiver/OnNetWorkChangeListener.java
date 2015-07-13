package com.hikvision.parentdotworry.receiver;

public interface OnNetWorkChangeListener {

	public void onWifiConnected();
	public void onMobileConnected();
	public void onNetDisConnected();
}
