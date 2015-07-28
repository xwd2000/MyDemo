package com.hikvision.parentdotworry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.hikvision.parentdotworry.base.BaseFlingActivity;
import com.hikvision.parentdotworry.bean.ChildInfo;
import com.hikvision.parentdotworry.costomui.CommonPopupMenu;
import com.hikvision.parentdotworry.costomui.CommonPopupMenu.OnMenuItemClickListener;
import com.hikvision.parentdotworry.costomui.TimeBar;
import com.hikvision.parentdotworry.costomui.TitleBar;
import com.hikvision.parentdotworry.dataprovider.HttpDataProvider;
import com.hikvision.parentdotworry.utils.DateUtil;
import com.hikvision.parentdotworry.utils.http.HttpUtil;

public class MainActivity extends BaseFlingActivity implements View.OnClickListener {
	// ===========================================================
	// Constants
	// ===========================================================
	// 主界面状态
	private static final int STATUS_NOT_ARRIVED = 0;
	private static final int STATUS_ARRIVED = 1;
	private static final int STATUS_LEAVED = 2;

	// ===========================================================
	// Fields
	// ===========================================================
	private HttpDataProvider mHttpDataProvider = HttpDataProvider.getInstance();

	private TitleBar mTbTitleBar;
	private TimeBar mTbSchoolDaily;
	private CommonPopupMenu mCpmChildMenu;

	private Button mBtSetting;
	private int mStatus = STATUS_NOT_ARRIVED;



	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initUiInstance();

		initView();
		(new Thread() {
			public void run() {
				try {
					HttpUtil.requestTest();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			};
		}).start();
		TextView tv = (TextView) findViewById(R.id.tv_warning_bar);
		tv.setVisibility(View.GONE);
		findViewById(R.id.bt_setting0).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						TextView tv = (TextView) findViewById(R.id.tv_warning_bar);
						tv.setVisibility(tv.getVisibility() == View.GONE ? View.VISIBLE
								: View.GONE);

					}
				});
		findViewById(R.id.bt_setting1).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {

						// mTbSchoolDaily.showEnterButton(DateUtil.stringToDate("16:50",
						// "HH:mm"));
						mTbSchoolDaily.hideLeaveButton();
					}
				});
	}

	@Override
	public void onClick(View v) {
		hideMenu();
		int viewId = v.getId();
		switch (viewId) {
		case R.id.iv_title_bar_left_button:
			goToMessageListPage();
			break;
		case R.id.iv_title_bar_right_button:
			Intent intentToVideoListActivity = new Intent(MainActivity.this,
					VideoListActivity.class);
			startActivity(intentToVideoListActivity);
			break;
		default:
			break;
		}

	}

	
	@Override
	public void onFlingLeft() {
		goToInfoPage();
	}

	@Override
	public void onFlingRight() {
		//goToMessageListPage();
	}

	@Override
	public void onFlingLeftEnd() {}

	@Override
	public void onFlingRightEnd() {}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		hideMenu();
		return super.onTouchEvent(event);
	}
	// ===========================================================
	// Methods
	// ===========================================================
	private void initUiInstance() {
		mTbTitleBar = (TitleBar) findViewById(R.id.tb_title_bar);
		mTbSchoolDaily = (TimeBar) findViewById(R.id.tb_school_daily_bar);
		mBtSetting = (Button) findViewById(R.id.bt_setting);
		mCpmChildMenu = new CommonPopupMenu(this);
	}

	private void initView() {
		// 标题栏控制
		mTbTitleBar.setLeftButton(R.drawable.ic_launcher, this);
		mTbTitleBar.setRightButton(R.drawable.ic_launcher, this);
		mBtSetting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				goToInfoPage();
			}
		});

		mTbTitleBar.setTitle("张大宝", getDrawableById(R.drawable.ic_launcher));
		mTbTitleBar.setTitleOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				toggleMenu(mTbTitleBar.getTitle());
			}
		});

		// 时间控件
		mTbSchoolDaily.init(DateUtil.stringToDate("8:00", "HH:mm"),
				DateUtil.stringToDate("16:00", "HH:mm"));

		mTbSchoolDaily.showEnterButton(DateUtil.stringToDate("6:00", "HH:mm"),
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(MainActivity.this,
								CapturePicActivity.class);
						intent.putExtra(
								CapturePicActivity.INTENT_KEY_PIC_TYPE_ENTER_SCHOOL,
								true);
						startActivity(intent);
					}
				});
		mTbSchoolDaily.showLeaveButton(DateUtil.stringToDate("16:20", "HH:mm"),
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(MainActivity.this,
								CapturePicActivity.class);
						intent.putExtra(
								CapturePicActivity.INTENT_KEY_PIC_TYPE_ENTER_SCHOOL,
								false);
						startActivity(intent);
					}
				});

		// 弹出菜单,menu边框没有底边，每个item只有底边
		mCpmChildMenu
				.setMenuBackground(getDrawableById(R.drawable.menu_no_bottom_background));
		// 防止item遮住menu的边框！！
		mCpmChildMenu.setMenuPadding(1, 1, 1, 0);
		mCpmChildMenu
				.setMenuItemBackgroundId(R.drawable.menu_item_background_selector);

		mCpmChildMenu.setOnItemClickListener(new OnMenuItemClickListener() {
			@Override
			public void OnItemClick(int index, View v, Object tag) {
				ChildInfo child = (ChildInfo) tag;
				if (child != null) {
					logd(child.getName());
				}
			}
		});
	}

	private void statusChange(int newStatus) {
		switch (newStatus) {
		case STATUS_NOT_ARRIVED:
			mTbSchoolDaily.hideEnterButton();
			mTbSchoolDaily.hideLeaveButton();
			break;
		case STATUS_ARRIVED:
			mTbSchoolDaily.showEnterButton(new Date());// 到时候时间c层获取
			// mTbSchoolDaily.showLeaveButton();
			break;
		case STATUS_LEAVED:
			mTbSchoolDaily.showLeaveButton(new Date());// 到时候时间c层获取
			break;
		}
	}

	private void changeChild() {

	}



	// 显示或隐藏menu
	private void toggleMenu(View dropUnder) {
		if (mCpmChildMenu.isShowing()) {
			mCpmChildMenu.dismiss();
		} else {
			showMenu(dropUnder);
		}
	}

	// 隐藏menu
	private void hideMenu() {
		mCpmChildMenu.dismiss();
	}

	// 隐藏menu
	private void showMenu(View dropUnder) {
		mCpmChildMenu.clearItem();
		List<ChildInfo> childList = new ArrayList<ChildInfo>();//mHttpDataProvider.getChildrenByParentId(0);
		for (int i = 0, j = childList.size(); i < j; i++) {
			ChildInfo c = childList.get(i);
			mCpmChildMenu.addItem(c.getName(), c);
		}
		mCpmChildMenu.showAsDropDown(dropUnder);
	}

	private void goToInfoPage(){
		Intent intent = new Intent(MainActivity.this,SettingActivity.class);
		startActivity(intent);
	}

	private void goToMessageListPage(){
		Intent intent = new Intent(MainActivity.this,MessageListActivity.class);
		startActivity(intent);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

}
