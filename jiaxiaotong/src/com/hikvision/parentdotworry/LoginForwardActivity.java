package com.hikvision.parentdotworry;

import android.os.Bundle;

import com.hikvision.parentdotworry.base.BaseActivity;
import com.videogo.openapi.EzvizAPI;

public class LoginForwardActivity extends BaseActivity{
	private EzvizAPI mEzvizAPI=EzvizAPI.getInstance();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_forward_activity);
		mEzvizAPI.gotoLoginPage(false);
	}

}
