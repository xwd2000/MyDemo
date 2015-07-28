package com.hikvision.parentdotworry;

import java.lang.ref.WeakReference;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hikvision.parentdotworry.application.AppApplication;
import com.hikvision.parentdotworry.application.AppPerference;
import com.hikvision.parentdotworry.application.AppPerference.K;
import com.hikvision.parentdotworry.base.BaseFlingActivity;
import com.hikvision.parentdotworry.bean.ChildInfo;
import com.hikvision.parentdotworry.bean.Parent;
import com.hikvision.parentdotworry.costomui.TitleBar;
import com.hikvision.parentdotworry.splash.SplashScreen;
import com.hikvision.parentdotworry.utils.ScreenUtil;
import com.videogo.openapi.EzvizAPI;

public class SettingActivity2 extends BaseFlingActivity implements
		OnClickListener {

	// ===========================================================
	// Constants
	// ===========================================================
	public static final String CHILDLIST_KEY = "childList";
	public static final String PARENT_KEY = "phone";
	// ===========================================================
	// Fields
	// ===========================================================
	private EzvizAPI mEzvizAPI = EzvizAPI.getInstance();
	private List<ChildInfo> mChildList;
	private Parent mParent;
	private LayoutInflater mInflater;

	private TextView mTvName;
	private TextView mTvPhone;
	private TitleBar mTbTitleBar;

	private LinearLayout mLlSettingGroupContainer;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_activity2);

		@SuppressWarnings("unchecked")
		List<ChildInfo> serializableExtra = (List<ChildInfo>) getIntent()
				.getSerializableExtra(CHILDLIST_KEY);
		mChildList = serializableExtra;
		mParent = (Parent) getIntent().getSerializableExtra(PARENT_KEY);

		initUiInstance();

		initView();

		fillData(mParent, mChildList);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	public void onFlingLeft() {
	}

	@Override
	public void onFlingRight() {
		this.finish();
	}

	@Override
	public void onFlingLeftEnd() {
	}

	@Override
	public void onFlingRightEnd() {
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			onUserInteraction();
		}
		if (getWindow().superDispatchTouchEvent(ev)) {
			return onTouchEvent(ev);
		}
		return onTouchEvent(ev);
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_logout:
			logout();
			break;

		default:
			break;
		}

	}

	// ===========================================================
	// Methods
	// ===========================================================
	private void initUiInstance() {
		mTvName = (TextView) findViewById(R.id.tv_parent_name);
		mTvPhone = (TextView) findViewById(R.id.tv_parent_phone);
		mTbTitleBar = (TitleBar) findViewById(R.id.tb_title_bar);

		mLlSettingGroupContainer = (LinearLayout) findViewById(R.id.ll_setting_group_container);

		mInflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	private void initView() {
		mTbTitleBar.setTitle(getString(R.string.setting_title_my_info));
		mTbTitleBar.setLeftButton(R.drawable.arrow, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SettingActivity2.this.finish();
			}
		});

	}

	private void fillData(Parent parent, List<ChildInfo> childInfoList) {
		mTvName.setText(parent.getName());
		mTvPhone.setText(parent.getPhone());

		List<ChildInfo> childList = childInfoList;
		for (int i = 0, j = childList.size(); i < j; i++) {
			ChildInfo child = childList.get(i);

			LinearLayout llSettingChildGroupItem = (LinearLayout) mInflater
					.inflate(R.layout.setting_child_group_item2, null);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.bottomMargin = ScreenUtil.dip2px(SettingActivity2.this, 12f);
			llSettingChildGroupItem.setLayoutParams(lp);

			TextView tvChildName = (TextView) llSettingChildGroupItem
					.findViewById(R.id.tv_child_name);
			TextView tvChildStudentId = (TextView) llSettingChildGroupItem
					.findViewById(R.id.tv_child_student_id);
			TextView tvChildSchool = (TextView) llSettingChildGroupItem
					.findViewById(R.id.tv_child_school);
			TextView tvChildGradeClas = (TextView) llSettingChildGroupItem
					.findViewById(R.id.tv_child_class);

			tvChildName.setText(child.getName());
			tvChildStudentId.setText(child.getPersonCode() + "");
			tvChildSchool.setText(child.getSchoolName());
			tvChildGradeClas.setText(child.getGradeName()
					+ child.getClassName());
			mLlSettingGroupContainer.addView(llSettingChildGroupItem);
		}

		mLlSettingGroupContainer.setVisibility(View.VISIBLE);

	}

	private void logout() {
		new Thread() {
			public void run() {
				mEzvizAPI.logout();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						goToLoginPage();
					}
				});
			};

		}.start();
	}
	
	
	private void goToLoginPage() {
		Intent intent = new Intent(SettingActivity2.this, LoginActivity.class);
		
		Activity at=null;
		while((at = AppApplication.getApplication().popActivity())!=null){
			if(at!=this){
				at.finish();
			}
		}
		AppPerference.put(K.ACCESS_TOKEN_KEY, "");
		startActivity(intent);
		SettingActivity2.this.finish();
	}
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

}
