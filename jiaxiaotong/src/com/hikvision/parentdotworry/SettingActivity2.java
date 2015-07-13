package com.hikvision.parentdotworry;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.hikvision.parentdotworry.base.AsyncTaskBase;
import com.hikvision.parentdotworry.base.BaseActivity;
import com.hikvision.parentdotworry.base.BaseFlingActivity;
import com.hikvision.parentdotworry.bean.ChildInfo;
import com.hikvision.parentdotworry.bean.Parent;
import com.hikvision.parentdotworry.costomui.TitleBar;
import com.hikvision.parentdotworry.dataprovider.HttpDataProvider;
import com.hikvision.parentdotworry.utils.ScreenUtil;
import com.hikvision.parentdotworry.utils.MapUtil;
import com.videogo.exception.BaseException;

public class SettingActivity2 extends BaseFlingActivity {

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private HttpDataProvider mHttpDataProvider = HttpDataProvider.getInstance();
	// private List<Child> mChildList;
	// private Parent mParent;
	private LayoutInflater mInflater;

	private TextView mTvName;
	private TextView mTvPhone;
	private TitleBar mTbTitleBar;

	private PullToRefreshScrollView mSvSettingScroll;

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

		initUiInstance();

		initView();

		new DataObtainTask(SettingActivity2.this, true).execute();
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

	// ===========================================================
	// Methods
	// ===========================================================
	private void initUiInstance() {
		mTvName = (TextView) findViewById(R.id.tv_parent_name);
		mTvPhone = (TextView) findViewById(R.id.tv_parent_phone);
		mTbTitleBar = (TitleBar) findViewById(R.id.tb_title_bar);

		mLlSettingGroupContainer = (LinearLayout) findViewById(R.id.ll_setting_group_container);
		mSvSettingScroll = (PullToRefreshScrollView) findViewById(R.id.sv_setting_scroll);

		mInflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	private void initView() {
		mTbTitleBar.setTitle(getString(R.string.setting_title_my_info));
		mTbTitleBar.setLeftButton(R.drawable.arrow,
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						SettingActivity2.this.finish();
					}
				});

	}



	private Rect getScrollViewRect() {
		Rect rect = new Rect();
		mSvSettingScroll.getGlobalVisibleRect(rect);
		return rect;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	private class DataObtainTask extends
			AsyncTaskBase<Void, Void, HashMap<String, Object>> {

		public DataObtainTask(Context context, boolean needDialog) {
			super(context, needDialog);
		}

		public static final String PARENT_KEY = "parent";
		public static final String CHILD_LIST_KEY = "childList";

		@Override
		protected HashMap<String, Object> realDoInBackground(Void... params)
				throws BaseException {
			Parent parent = mHttpDataProvider.getParentInfo();
			List<ChildInfo> childList = mHttpDataProvider
					.getAgeSortedChildrenByParentId(parent.getId());

			return MapUtil.generateMap(PARENT_KEY, parent, CHILD_LIST_KEY,
					childList);
		}

		@Override
		protected void realOnPostExecute(HashMap<String, Object> result) {
			Parent parent = (Parent) result.get(PARENT_KEY);
			mTvName.setText(parent.getName());
			mTvPhone.setText(parent.getPhone());

			List<ChildInfo> childList = (List<ChildInfo>) result.get(CHILD_LIST_KEY);
			for (int i = 0, j = childList.size(); i < j; i++) {
				ChildInfo child = childList.get(i);

				LinearLayout llSettingChildGroupItem = (LinearLayout) mInflater
						.inflate(R.layout.setting_child_group_item2, null);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				lp.bottomMargin = ScreenUtil
						.dip2px(SettingActivity2.this, 12f);
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
				tvChildStudentId.setText(child.getId() + "");
				tvChildSchool.setText(child.getSchoolName());

				mLlSettingGroupContainer.addView(llSettingChildGroupItem);
			}

			mLlSettingGroupContainer.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onError(Exception e) {
		}
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

}
