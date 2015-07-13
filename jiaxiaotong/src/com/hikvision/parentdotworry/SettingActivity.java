package com.hikvision.parentdotworry;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.hikvision.parentdotworry.base.AsyncTaskBase;
import com.hikvision.parentdotworry.base.BaseActivity;
import com.hikvision.parentdotworry.bean.ChildInfo;
import com.hikvision.parentdotworry.bean.Parent;
import com.hikvision.parentdotworry.costomui.TitleBar;
import com.hikvision.parentdotworry.dataprovider.HttpDataProvider;
import com.hikvision.parentdotworry.utils.MapUtil;
import com.videogo.exception.BaseException;

public class SettingActivity extends BaseActivity {

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private HttpDataProvider mHttpDataProvider = HttpDataProvider.getInstance();
	//private List<Child> mChildList;
	//private Parent mParent;
	private LayoutInflater mInflater;

	private TextView mTvName;
	private TextView mTvPhone;
	private TextView mTvUnDefine;
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
		setContentView(R.layout.setting_activity);

		initUiInstance();
		
		initView();
		
		
		new DataObtainTask(SettingActivity.this,true).execute();
	}
	
//	@Override
//	public void onFlingLeft() {}
//
//
//
//	@Override
//	public void onFlingRight() {
//		this.finish();
//	}
//
//
//
//	@Override
//	public void onFlingLeftEnd() {}
//
//
//
//	@Override
//	public void onFlingRightEnd() {}

	// ===========================================================
	// Methods
	// ===========================================================
	private void initUiInstance() {
		mTvName = (TextView) findViewById(R.id.tv_parent_name);
		mTvPhone = (TextView) findViewById(R.id.tv_parent_phone);
		mTvUnDefine = (TextView) findViewById(R.id.tv_parent_undefine);
		mTbTitleBar = (TitleBar)findViewById(R.id.tb_title_bar);
		
		mLlSettingGroupContainer = (LinearLayout) findViewById(R.id.ll_setting_group_container);
		mSvSettingScroll = (PullToRefreshScrollView)findViewById(R.id.sv_setting_scroll);
		
		mInflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	private void initView(){
		mTbTitleBar.setTitle(getString(R.string.setting_title_my_info));
		mTbTitleBar.setLeftButton(R.drawable.backbtn_selector, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SettingActivity.this.finish();
			}
		});

		mSvSettingScroll.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				toast("11111111111");
				return false;
			}
		});
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
			mTvUnDefine.setText(R.string.setting_undetermined_label);

			List<ChildInfo> childList = (List<ChildInfo>) result.get(CHILD_LIST_KEY);
			for (int i = 0, j = childList.size(); i < j; i++) {
				ChildInfo child=childList.get(i);
				
				LinearLayout llSettingChildGroupItem = (LinearLayout)mInflater.inflate(R.layout.setting_child_group_item , null);
				TextView mTvChildName =(TextView)llSettingChildGroupItem.findViewById(R.id.tv_child_name);
				TextView mTvChildStudentId =(TextView)llSettingChildGroupItem.findViewById(R.id.tv_child_student_id);
				TextView mTvChildUndefine =(TextView)llSettingChildGroupItem.findViewById(R.id.tv_child_undefine);

				mTvChildName.setText(child.getName());
				mTvChildStudentId.setText(child.getId()+"");
				mTvChildUndefine.setText(R.string.setting_undetermined_label);
				
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
