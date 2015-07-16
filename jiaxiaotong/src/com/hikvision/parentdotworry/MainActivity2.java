package com.hikvision.parentdotworry;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.hikvision.parentdotworry.application.AppConfig;
import com.hikvision.parentdotworry.base.AsyncTaskBase;
import com.hikvision.parentdotworry.base.BaseFlingActivity;
import com.hikvision.parentdotworry.bean.ChildCaptureInfo;
import com.hikvision.parentdotworry.bean.ChildInfo;
import com.hikvision.parentdotworry.bean.MessageBean;
import com.hikvision.parentdotworry.bean.MessageDetail;
import com.hikvision.parentdotworry.bean.NomalTime;
import com.hikvision.parentdotworry.bean.Pagination;
import com.hikvision.parentdotworry.consts.AppConst;
import com.hikvision.parentdotworry.costomui.CommonPopupMenu;
import com.hikvision.parentdotworry.costomui.CommonPopupMenu.OnMenuItemClickListener;
import com.hikvision.parentdotworry.costomui.TimeBarV3;
import com.hikvision.parentdotworry.costomui.TitleBar;
import com.hikvision.parentdotworry.costomui.WaitDialog;
import com.hikvision.parentdotworry.dataprovider.dao.UseDatabase;
import com.hikvision.parentdotworry.dataprovider.httpdata.HttpDataProvider;
import com.hikvision.parentdotworry.exception.AppError;
import com.hikvision.parentdotworry.receiver.OnNetWorkChangeListener;
import com.hikvision.parentdotworry.utils.DateUtil;
import com.hikvision.parentdotworry.utils.EmptyUtil;
import com.hikvision.parentdotworry.utils.QEncodeUtil;
import com.hikvision.parentdotworry.utils.ScreenUtil;
import com.hikvision.parentdotworry.vo.ChildState;

public class MainActivity2 extends BaseFlingActivity implements
		OnNetWorkChangeListener {

	// ===========================================================
	// Constants
	// ===========================================================
	private static final String TAG = "MainActivity2";
	private static final int HANDLER_MESSAGE_UPDATE_TIME_WHAT = 0;

	private static final int CHILD_STATUS_UNKNOW = 0;
	// 您的小孩未到校
	private static final int CHILD_STATUS_1_NOT_IN_SCHOOL = 1;
	// 您的小孩已到校
	private static final int CHILD_STATUS_1_IN_SCHOOL = 2;
	// 您的小孩未到校
	private static final int CHILD_STATUS_2_NOT_IN_SCHOOL = 3;
	// 您的小孩按时到校
	private static final int CHILD_STATUS_2_IN_SCHOOL = 4;
	// 您的小孩迟到了
	private static final int CHILD_STATUS_2_LATE = 5;
	// 您的小孩未到校
	private static final int CHILD_STATUS_3_NOT_IN_SCHOOL = 6;
	// 您的小孩未离校
	private static final int CHILD_STATUS_3_IN_SCHOOL = 7;
	// 您的小孩已离校
	private static final int CHILD_STATUS_3_HAS_LEAVED = 8;
	// ===========================================================
	// Fields
	// ===========================================================

	// 标题栏
	private TitleBar mTbTitleBar;
	private RelativeLayout mRlMainColorBlock;
	private ImageView mIvMainChildStatus;
	private TextView mTvMainWarmingMessage;
	// 业务控件
	private TimeBarV3 mTimeBarV3;
	// 进度图标
	private ProgressBar mPbWaitMessage;
	// 无消息时显示的块
	private LinearLayout mLlNoMessageView;
	// 有消息时显示的内容
	private LinearLayout mLlMainMessageContainer;
	private TextView mTvCurrentTime;
	private TextView mTvMainTimeTextTail;
	// 这是menu
	private CommonPopupMenu mCpmChildMenu;

	private PullToRefreshScrollView mPtrsvMainScrollView;
	// 数据获取
	private HttpDataProvider mHttpDataProvider = HttpDataProvider.getInstance();
	private UseDatabase mUseDatabase = UseDatabase.getInstance();

	private List<ChildInfo> childInfoList;
	private int mCurrentChildId = 0;
	private String mParentPhone = "0";

	// 处理主页时钟
	private Handler mTimeUpdateHandler;

	private boolean mIsPressOnViewPager;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main2);

		childInfoList = mUseDatabase.findList("parent_phone=?",
				new String[] { mParentPhone }, ChildInfo.class);

		initUiInstance();
		initView();

		mTimeUpdateHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Date now = new Date();
				mTvCurrentTime.setText(DateUtil.dateToString(now,
						AppConst.PATTERN_COMMON_TIME12_WITHOUT_SECOND));
				int hour = DateUtil.getHour(DateUtil.getCalendar(now));
				String amOrpm = hour >= 12 ? "pm" : "am";
				if (!amOrpm.equals(mTvMainTimeTextTail.getText())) {
					mTvMainTimeTextTail.setText(amOrpm);
				}
				this.sendEmptyMessageDelayed(HANDLER_MESSAGE_UPDATE_TIME_WHAT,
						1000);
				super.handleMessage(msg);
			}

		};

		if (true) {// 判断token是否过期
			silenceLogin();
		}
		if (EmptyUtil.isEmpty(childInfoList) || childInfoList.size() == 1) {

			// 获取孩子信息
			new GetChildInfo(this).execute();
		}
	
	}

	@Override
	protected void onStart() {
		mTimeUpdateHandler.sendEmptyMessage(HANDLER_MESSAGE_UPDATE_TIME_WHAT);
		resetPressOnViewPagerFlag();
		super.onStart();
	}

	@Override
	protected void onStop() {
		mTimeUpdateHandler.removeMessages(HANDLER_MESSAGE_UPDATE_TIME_WHAT);
		super.onStop();
	}

	int dd = 0; // TODO 记住删除

	@Override
	public void onFlingLeft() {
		
		 goToInfoPage();
		// 测试状态变更
		
	
	}

	@Override
	public void onFlingRight() {
		// new IosDialog(this).show();
		// mTimeBarV3.removeAllPoint();

		mTimeBarV3.removeMakePoint(0);
	}

	@Override
	public void onFlingLeftEnd() {
	}

	@Override
	public void onFlingRightEnd() {
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		return super.onTouchEvent(event);

	}

	/**
	 * ACTIONDOWN事件的时候会调用
	 */
	public void onUserInteraction() {
		if (isMenuShowing()) {
			hideMenu();
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			onUserInteraction();
			if (mTimeBarV3 != null) {
				Rect rt = mTimeBarV3.getViewPagerRect();
				Rect scrollRt = mTimeBarV3.getScrollViewRect();
				if (rt.contains((int) ev.getX(), (int) ev.getY())
						|| scrollRt.contains((int) ev.getX(), (int) ev.getY())) {
					this.setPressOnViewPagerFlag();
				}
			}
		}

		if (ev.getAction() == MotionEvent.ACTION_UP) {
			this.resetPressOnViewPagerFlag();
		}
		if (getWindow().superDispatchTouchEvent(ev)) {
			if (this.isPressOnViewPagerFlag()) {
				return true;
			} else {
				return onTouchEvent(ev);
			}
		}
		return onTouchEvent(ev);
	};

	@Override
	public void onWifiConnected() {
		logd("onWifiConnected");
		toast("onWifiConnected");
//		mTimeBarV3
//				.addMarkPointView(DateUtil.stringToDate("16:00", "HH:mm"), "");
		mTimeBarV3.refresh();
	}

	@Override
	public void onMobileConnected() {
		logd("onMobileConnected");
		toast("onMobileConnected");

	}

	@Override
	public void onNetDisConnected() {
		logd("onNetDisConnected");
		toast("onNetDisConnected");

	}

	// ===========================================================
	// Methods
	// ===========================================================

	private void initUiInstance() {
		mTbTitleBar = (TitleBar) findViewById(R.id.tb_title_bar);
		mRlMainColorBlock = (RelativeLayout) findViewById(R.id.rl_main_color_block);
		mIvMainChildStatus = (ImageView) findViewById(R.id.iv_main_child_status);
		mTvMainWarmingMessage = (TextView) findViewById(R.id.tv_main_warming_message);
		mTimeBarV3 = (TimeBarV3) findViewById(R.id.tb_main_timebar);
		mLlMainMessageContainer = (LinearLayout) findViewById(R.id.ll_main_message_container);
		mPbWaitMessage = (ProgressBar) findViewById(R.id.pb_loadding);
		mLlNoMessageView = (LinearLayout) findViewById(R.id.ll_no_message_block);
		mCpmChildMenu = new CommonPopupMenu(this);
		mTvCurrentTime = (TextView) findViewById(R.id.tv_current_time);
		mTvMainTimeTextTail = (TextView) findViewById(R.id.tv_main_time_text_tail);
		mPtrsvMainScrollView = (PullToRefreshScrollView) findViewById(R.id.ptrsv_main_scroll_view);

	}

	private void initView() {

		createOrUpdateTitleBar(childInfoList);

		String schoolStart = AppConfig.getString("MainActivity.schoolStart",
				"8:00");
		String schoolEnd = AppConfig
				.getString("MainActivity.schoolEnd", "5:00");

		NomalTime nomalTime = mUseDatabase.findOne("id=?",
				new String[] { mCurrentChildId + "" }, NomalTime.class);
		if (nomalTime == null) {
			relayoutTimeBar(DateUtil.stringToDate(schoolStart, "HH:mm"),
					DateUtil.stringToDate(schoolEnd, "HH:mm"));
		} else {
			relayoutTimeBar(DateUtil.stringToDate(nomalTime.getStartTime(),
					AppConst.PATTERN_DATE_TIME_DB), DateUtil.stringToDate(
					nomalTime.getEndTime(), AppConst.PATTERN_DATE_TIME_DB));
		}

		// 弹出菜单,menu边框没有底边，每个item只有底边
		mCpmChildMenu.setMenuBackground(getDrawableById(R.drawable.pull_down));
		// 防止item遮住menu的边框！！
		// mCpmChildMenu.setMenuPadding(1, 1, 1, 0);
		// mCpmChildMenu.setMenuItemBackgroundId(R.drawable.menu_item_background_selector);
		mCpmChildMenu.setTextColor(Color.WHITE);
		mCpmChildMenu.setOnItemClickListener(new OnMenuItemClickListener() {
			@Override
			public void OnItemClick(int index, View v, Object tag) {
				ChildInfo child = (ChildInfo) tag;
				
				if (child != null) {
					logd(child.getName());
					mCurrentChildId = child.getId();
					new GetDataTask(MainActivity2.this,mCurrentChildId).execute();
					new FreshStatusTask(MainActivity2.this,mCurrentChildId).execute();
				}
			}
		});
		ILoadingLayout startLabels = mPtrsvMainScrollView
				.getLoadingLayoutProxy(true, false);
		startLabels.setPullLabel("下拉刷新...");// 刚下拉时，显示的提示
		startLabels.setRefreshingLabel("正在载入...");// 刷新时
		startLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示

		mPtrsvMainScrollView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh(PullToRefreshBase refreshView) {
				new GetDataTask(MainActivity2.this,mCurrentChildId).execute();
				new FreshStatusTask(MainActivity2.this,mCurrentChildId).execute();
			}

		});
		refreshStatus(0);
		new GetDataTask(this,mCurrentChildId).execute();
		new FreshStatusTask(this,mCurrentChildId).execute();
	}

	private void relayoutTimeBar(Date schoolStart, Date schoolEnd) {
		Date periodStart = schoolStart;
		Date periodEnd = schoolEnd;

		Date LimitStart = TimeBarV3.getTimeBarStartTimeByNormalTimeStart(periodStart, 2);
		
		Date LimitEnd = TimeBarV3.getTimeBarEndTimeByNormalTimeEnd(periodEnd, 2);
		
		mTimeBarV3.setLimitTime(LimitStart, LimitEnd);
		mTimeBarV3.addPeriod(0, periodStart, periodEnd);
		mTimeBarV3.refresh();
	}

	private void createOrUpdateTitleBar(final List<ChildInfo> childInfoList) {
		// 标题栏控制
		mTbTitleBar.setLeftButton(R.drawable.news_selector,
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (EmptyUtil.isEmpty(childInfoList)) {
							toast(AppError.USER_ERROR_NO_CHILD_INFO.message);
						} else {
							if (!EmptyUtil.isEmpty(childInfoList)) {
								goToMessageListPage(childInfoList);
							}
							// goToChangePasswordPage();
						}
					}
				});
		mTbTitleBar.setRightButton(R.drawable.video_selector,
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						goToVideoListPage();
					}
				});
		if (!EmptyUtil.isEmpty(childInfoList)) {
			updateTitle(childInfoList);
			
		}
	}

	private void updateTitle(final List<ChildInfo> childInfoList) {
		int tmpIndex = 0;
		for (int i = 0; i < childInfoList.size(); i++) {
			if (childInfoList.get(i) != null
					&& mCurrentChildId == childInfoList.get(i).getId()) {
				tmpIndex = i;
				break;
			}
		}
		mCurrentChildId = childInfoList.get(tmpIndex).getId();
		if (childInfoList.size() != 1) {// 是否显示下拉图标
			mTbTitleBar.setTitle(childInfoList.get(tmpIndex).getName(),
					getDrawableById(R.drawable.arrow_down));
		} else {
			mTbTitleBar.setTitle(childInfoList.get(tmpIndex).getName());
		}
		mTbTitleBar.setTitleOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleMenu(mTbTitleBar.getTitle());
			}
		});

	}

	private void showMessageProcessBar() {
		mPbWaitMessage.setVisibility(View.VISIBLE);
		mLlNoMessageView.setVisibility(View.GONE);
		mLlMainMessageContainer.setVisibility(View.GONE);
	}

	private void showEmptyBlock() {
		mPbWaitMessage.setVisibility(View.GONE);
		mLlNoMessageView.setVisibility(View.VISIBLE);
		mLlMainMessageContainer.setVisibility(View.GONE);
	}

	private void showMessageList() {
		mPbWaitMessage.setVisibility(View.GONE);
		mLlNoMessageView.setVisibility(View.GONE);
		mLlMainMessageContainer.setVisibility(View.VISIBLE);
	}

	private void goToInfoPage() {
		Intent intent = new Intent(MainActivity2.this, SettingActivity2.class);
		startActivity(intent);
	}

	private void goToMessageListPage(List<ChildInfo> childInfoList) {
		Intent intent = new Intent(MainActivity2.this,
				MessageListActivity.class);
		intent.putExtra(MessageListActivity.KEY_CHILD_INFO_LIST,
				(Serializable) childInfoList);
		startActivity(intent);
	}

	private void goToMessageDetailPage(MessageBean messageBean) {
		Intent intent = new Intent(MainActivity2.this,
				MessageDetailActivity.class);
		intent.putExtra(MessageDetailActivity.INTENT_KEY_MESSAGE_BEAN, messageBean);
		startActivity(intent);
	}

	private void goToVideoListPage() {
		Intent intent = new Intent(MainActivity2.this, VideoListActivity.class);
		startActivity(intent);
	}

	private void goToChangePasswordPage() {
		Intent intent = new Intent(MainActivity2.this,
				ChangePasswordActivity.class);
		startActivity(intent);
	}

	private ViewGroup fillMessageData(MessageBean bessageBean, ViewGroup parent) {
		TextView tvTitle = (TextView) parent
				.findViewById(R.id.tv_main_item_title);
		TextView tvTime = (TextView) parent
				.findViewById(R.id.tv_main_item_time);
		TextView tvDepartment = (TextView) parent
				.findViewById(R.id.tv_main_item_department);
		TextView tvDetail = (TextView) parent
				.findViewById(R.id.tv_main_item_detail);
		TextView tvIsnew = (TextView) parent
				.findViewById(R.id.tv_main_item_isnew);
		tvTitle.setText(bessageBean.getTitle());
		String messageDate = DateUtil.dateToString(DateUtil.stringToDate(
				bessageBean.getReleaseTime(), "yyyy-MM-dd"), "yyyy.M.d");
		tvTime.setText(messageDate);

		tvDepartment.setText(bessageBean.getPromulgator());
		tvDetail.setText(bessageBean.getContent());

		if (bessageBean.getIsNew() != null && bessageBean.getIsNew().equals(1)) {
			tvIsnew.setVisibility(View.VISIBLE);
		} else {
			tvIsnew.setVisibility(View.GONE);
		}
		return parent;
	}

	private void refreshStatus(int status) {
		AlphaAnimation fadeOutAnima=new AlphaAnimation(1,0);
		fadeOutAnima.setDuration(200);
		AlphaAnimation fadeInAnima=new AlphaAnimation(0,1);
		fadeInAnima.setDuration(200);
		switch (status) {
		case CHILD_STATUS_UNKNOW:
			mTvMainWarmingMessage
			.setText(R.string.main_warming_message_child_in_school);
			mTvMainWarmingMessage
			.setTextColor(getColorById(R.color.lightgrey));
			mRlMainColorBlock.setBackgroundResource(R.color.lightgrey);
			mTbTitleBar.setBackgroundResource(R.color.lightgrey);
			mIvMainChildStatus.setImageResource(R.drawable.main_in_school);
			break;
		case CHILD_STATUS_1_NOT_IN_SCHOOL:
			mTvMainWarmingMessage
					.setText(R.string.main_warming_message_child_not_in_school);
			mTvMainWarmingMessage
					.setTextColor(getColorById(R.color.main_status_yellow));
			mRlMainColorBlock.setBackgroundResource(R.color.main_status_yellow);
			mTbTitleBar.setBackgroundResource(R.color.main_status_yellow);
			mIvMainChildStatus.setImageResource(R.drawable.main_leave_school);
			break;
		case CHILD_STATUS_1_IN_SCHOOL:
			mTvMainWarmingMessage
					.setText(R.string.main_warming_message_child_in_school);
			mTvMainWarmingMessage
					.setTextColor(getColorById(R.color.main_status_green));
			mRlMainColorBlock.setBackgroundResource(R.color.main_status_green);
			mTbTitleBar.setBackgroundResource(R.color.main_status_green);
			mIvMainChildStatus.setImageResource(R.drawable.main_in_school);
			break;
		case CHILD_STATUS_2_NOT_IN_SCHOOL:
			mTvMainWarmingMessage
				.setText(R.string.main_warming_message_child_not_in_school);
			mTvMainWarmingMessage
					.setTextColor(getColorById(R.color.main_status_yellow));
			mRlMainColorBlock.setBackgroundResource(R.color.main_status_yellow);
			mTbTitleBar.setBackgroundResource(R.color.main_status_yellow);
			mIvMainChildStatus.setImageResource(R.drawable.main_leave_school);
			break;
		case CHILD_STATUS_2_IN_SCHOOL:
			mTvMainWarmingMessage
				.setText(R.string.main_warming_message_child_in_school_in_time);
			mTvMainWarmingMessage
					.setTextColor(getColorById(R.color.main_status_green));
			mRlMainColorBlock.setBackgroundResource(R.color.main_status_green);
			mTbTitleBar.setBackgroundResource(R.color.main_status_green);
			mIvMainChildStatus.setImageResource(R.drawable.main_in_school);
			break;
		case CHILD_STATUS_2_LATE:
			mTvMainWarmingMessage
					.setText(R.string.main_warming_message_child_late);
			mTvMainWarmingMessage
					.setTextColor(getColorById(R.color.main_status_red));
			mRlMainColorBlock.setBackgroundResource(R.color.main_status_red);
			mTbTitleBar.setBackgroundResource(R.color.main_status_red);
			mIvMainChildStatus.setImageResource(R.drawable.main_not_in_school);
			break;
		case CHILD_STATUS_3_NOT_IN_SCHOOL:
			mTvMainWarmingMessage
			.setText(R.string.main_warming_message_child_not_in_school);
			mTvMainWarmingMessage
					.setTextColor(getColorById(R.color.main_status_yellow));
			mRlMainColorBlock.setBackgroundResource(R.color.main_status_yellow);
			mTbTitleBar.setBackgroundResource(R.color.main_status_yellow);
			mIvMainChildStatus.setImageResource(R.drawable.main_leave_school);
			break;
		case CHILD_STATUS_3_IN_SCHOOL:
			mTvMainWarmingMessage
				.setText(R.string.main_warming_message_child_has_not_leave_school);
			mTvMainWarmingMessage
					.setTextColor(getColorById(R.color.main_status_green));
			mRlMainColorBlock.setBackgroundResource(R.color.main_status_green);
			mTbTitleBar.setBackgroundResource(R.color.main_status_green);
			mIvMainChildStatus.setImageResource(R.drawable.main_in_school);
			break;
		case CHILD_STATUS_3_HAS_LEAVED:
			mTvMainWarmingMessage
					.setText(R.string.main_warming_message_child_has_leave_school);
			mTvMainWarmingMessage
					.setTextColor(getColorById(R.color.main_status_green));
			mRlMainColorBlock.setBackgroundResource(R.color.main_status_green);
			mTbTitleBar.setBackgroundResource(R.color.main_status_green);
			mIvMainChildStatus.setImageResource(R.drawable.main_leave_school);
			break;
		default:
			break;
		}

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

	// 是否有menu
	private boolean isMenuShowing() {
		return mCpmChildMenu.isShowing();
	}

	// 隐藏menu
	private void showMenu(final View dropUnder) {
		mCpmChildMenu.clearItem();
		for (int i = 0, j = childInfoList.size(); i < j; i++) {
			ChildInfo c = childInfoList.get(i);
			mCpmChildMenu.addItem(c.getName(), c);
		}
		// mCpmChildMenu.showAsDropDown(dropUnder);
		Point p = ScreenUtil.getLocationOnScreen(dropUnder);
		mCpmChildMenu.showAtLocation(dropUnder, Gravity.CENTER_HORIZONTAL
				| Gravity.TOP, 0, p.y + dropUnder.getHeight());

		new GetChildInfo(this).execute();
	}

	private boolean isPressOnViewPagerFlag() {
		return mIsPressOnViewPager;
	}

	private void resetPressOnViewPagerFlag() {
		mIsPressOnViewPager = false;
	}

	private void setPressOnViewPagerFlag() {
		mIsPressOnViewPager = true;
	}

	private void silenceLogin() {

	}


	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	/**
	 * 获取列表数据
	 * 
	 * @author xuweidong
	 */
	public class GetDataTask extends
			AsyncTaskBase<Void, Void, List<MessageBean>> {
		private Integer childId;
		
		public GetDataTask(Context context,Integer childId) {
			super(context);
			this.childId=childId;
		}

		@Override
		protected void onPreExecute() {
			// showMessageProcessBar();
			List<MessageBean> messageList = mUseDatabase.findPageList(
					"child_id = ?", new String[] { childId + "" }, 1,
					1, "release_time", "desc", MessageBean.class);
			updateMessageBlock(messageList);
			super.onPreExecute();
		}

		@Override
		protected List<MessageBean> realDoInBackground(Void... params)
				throws Exception {
			if (childId == 0) {
				loge("获取消息列表时无法得到孩子id");
			}
			Pagination<MessageBean> messagePagination = null;
			messagePagination = mHttpDataProvider.getNoticeDetail(
					childId, mParentPhone, 1, 1);
			return messagePagination.getData();
			// return messagePagination.getDataList();
		}

		@Override
		protected void realOnPostExecute(List<MessageBean> result) {
			if (EmptyUtil.isEmpty(result)) {
				showEmptyBlock();
			} else {
				updateMessageBlock(result);
				for(MessageBean messageBean : result){
					messageBean.setChildId(childId);
					messageBean.setId(messageBean.getId()*100+childId);
				}
				mUseDatabase.insertOrUpdate(result);
			}
		}

		private void updateMessageBlock(List<MessageBean> result) {
			mLlMainMessageContainer.removeAllViews();
			LayoutInflater inflater = (LayoutInflater) MainActivity2.this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			for (MessageBean messageBean : result) {
				LinearLayout linearLayout = (LinearLayout) inflater.inflate(
						R.layout.main_message_item, null);
				fillMessageData(messageBean, linearLayout);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				lp.topMargin = MainActivity2.this.getResources()
						.getDimensionPixelSize(R.dimen.main_item_margin_top);// 设置上边距
				linearLayout.setLayoutParams(lp);
				linearLayout.setTag(messageBean);
				linearLayout.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						MessageBean mb = (MessageBean) v.getTag();
						goToMessageDetailPage(mb);
					}
				});
				mLlMainMessageContainer.addView(linearLayout);

			}
			showMessageList();
		}

		@Override
		protected void onError(Exception e) {
			MainActivity2.this.onError(e);
		}

	}

	/**
	 * 更新状态
	 * 
	 * @author xuweidong
	 */
	public class FreshStatusTask extends
			AsyncTaskBase<Void, Void, Map<String, Object>> {
		private static final String KEY_CHILDCAPTUREINFO = "ChildCaptureInfo";
		private static final String KEY_NOMALTIME = "nomalTime";
		private static final String KEY_CHILDSTATE = "keyChildstate";
		private Integer childId;
		public FreshStatusTask(Context context,Integer childId) {
			super(context);
			this.childId=childId;
		}

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
		}

		@Override
		protected Map<String, Object> realDoInBackground(Void... params)
				throws Exception {
			if (childId == 0) {
				loge("获取消息列表时无法得到孩子id");
			}
			Map<String, Object> result = new HashMap<String, Object>();
			NomalTime nomalTime = mHttpDataProvider
					.getNomalTime(childId);
			result.put(KEY_NOMALTIME, nomalTime);
			Date proidStart=DateUtil.stringToDate(nomalTime.getStartTime(),AppConst.PATTERN_DATE_TIME_FROM_NET);
			Date proidEnd=DateUtil.stringToDate(nomalTime.getEndTime(),AppConst.PATTERN_DATE_TIME_FROM_NET);

			Date limitedStart = TimeBarV3.getTimeBarStartTimeByNormalTimeStart(proidStart, 2);
			Date limitedEnd = TimeBarV3.getTimeBarEndTimeByNormalTimeEnd(proidEnd, 2);
			ChildState childState = mHttpDataProvider.getChildState(childId, mParentPhone);
			result.put(KEY_CHILDSTATE, childState);
			
			Pagination<ChildCaptureInfo> childCaptureInfoList = mHttpDataProvider
					.getChildRecord(childId, mParentPhone, 
							limitedStart, limitedEnd, Integer.MAX_VALUE, 1);
			result.put(KEY_CHILDCAPTUREINFO, childCaptureInfoList.getData());
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void realOnPostExecute(Map<String, Object> result) {
			mTimeBarV3.removeAllPoint();
			List<ChildCaptureInfo> childCaptureInfoList = (List<ChildCaptureInfo>) result
					.get(KEY_CHILDCAPTUREINFO);
			NomalTime nomalTime = (NomalTime) result.get(KEY_NOMALTIME);
			ChildState childState = (ChildState) result.get(KEY_CHILDSTATE);
			
			refreshStatus(childState.getState());
			
			nomalTime.setId(childId);
			mUseDatabase.insertOrUpdate(nomalTime);
			Date periodStart = DateUtil.stringToDate(nomalTime.getStartTime(),
					AppConst.PATTERN_DATE_TIME_FROM_NET);
			Date periodEnd = DateUtil.stringToDate(nomalTime.getEndTime(),
					AppConst.PATTERN_DATE_TIME_FROM_NET);
			relayoutTimeBar(periodStart, periodEnd);
			for (ChildCaptureInfo cci : childCaptureInfoList) {
				mTimeBarV3.addMarkPointView(cci.getTime(),
						cci.getPicUrl(),cci.getState());
			}

			if (mPtrsvMainScrollView != null
					&& mPtrsvMainScrollView.isRefreshing()) {
				mPtrsvMainScrollView.onRefreshComplete();
			}
			if (mTimeBarV3.getMarkPotinsCount() != 0) {
				mTimeBarV3
						.setCurrentMarkPoint(mTimeBarV3.getMarkPotinsCount() - 1);
			}

		}

		@Override
		protected void onError(Exception e) {
			MainActivity2.this.onError(e);
			if (mPtrsvMainScrollView != null
					&& mPtrsvMainScrollView.isRefreshing()) {
				mPtrsvMainScrollView.onRefreshComplete();
			}
		}
	}

	class GetChildInfo extends AsyncTaskBase<Void, Void, List<ChildInfo>> {
		public GetChildInfo(Context context) {
			super(context);
		}

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
		}

		@Override
		protected List<ChildInfo> realDoInBackground(Void... params)
				throws Exception {
			// login()
			childInfoList = mHttpDataProvider.getChildInfoList(mParentPhone);
			return childInfoList;
		}

		@Override
		protected void realOnPostExecute(List<ChildInfo> result) {
			if (EmptyUtil.isEmpty(result)) {
				toast(R.string.error_user_9001_no_child_info);
				return;
			}
			for (ChildInfo childInfo : result) {
				childInfo.setParentPhone(mParentPhone);
			}
			mUseDatabase.insertOrUpdate(result);
			updateTitle(result);
		}

		@Override
		protected void onError(Exception e) {
			MainActivity2.this.onError(e);
		}
	}

	
	// ===========================================================
	// Getter & Setter
	// ===========================================================

}
