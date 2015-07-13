package com.hikvision.parentdotworry;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.hikvision.parentdotworry.R;
import com.hikvision.parentdotworry.base.BaseActivity;
import com.hikvision.parentdotworry.costomui.CommonPopupMenu;
import com.hikvision.parentdotworry.costomui.TimeBarV3;
import com.hikvision.parentdotworry.costomui.TitleBar;

public class ChangePasswordActivity extends BaseActivity{
	private TitleBar mTbTitleBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_password_activity);
		initUiInstance();
		initView();
		
	}
	private void initUiInstance() {
		mTbTitleBar = (TitleBar) findViewById(R.id.tb_title_bar);

	}

	private void initView() {
		// 标题栏控制
		mTbTitleBar.setLeftButton(R.drawable.arrow,
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						
					}
				});
		mTbTitleBar.setTitle(getStringById(R.string.change_password_title_text));
	}
}
