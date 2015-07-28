package com.hikvision.parentdotworry.costomui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.util.Asserts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hikvision.parentdotworry.R;
import com.hikvision.parentdotworry.application.AppApplication;
import com.hikvision.parentdotworry.plug.universalimageloader.core.DisplayImageOptions;
import com.hikvision.parentdotworry.plug.universalimageloader.core.ImageLoader;
import com.hikvision.parentdotworry.plug.universalimageloader.core.assist.ImageScaleType;
import com.hikvision.parentdotworry.plug.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.hikvision.parentdotworry.plug.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.hikvision.parentdotworry.plug.universalimageloader.core.process.BitmapProcessor;
import com.hikvision.parentdotworry.utils.Args;
import com.hikvision.parentdotworry.utils.DateUtil;
import com.hikvision.parentdotworry.utils.EmptyUtil;
import com.hikvision.parentdotworry.utils.ImageUtils;
import com.hikvision.parentdotworry.utils.ScreenUtil;

public class TimeBarV3 extends LinearLayout implements OnPageChangeListener,View.OnClickListener {

	// ===========================================================
	// Constants
	// ===========================================================
	private static final String TAG = "TimeBarV3";
	//private static final float PIC_WIDTH_SCREEN_RATIA = 3/4F;
	private static final float PIC_HEIGHT_SCREEN_RATIA = 1/3F;
	// ===========================================================
	// Fields
	// ===========================================================
	private ImageView mIvTimeLine;

	private RelativeLayout mRlLineContainer;
	private ViewPagerInToughView mVpMainPicShow;
	private HorizontalScrollView mHsvLineScrollView;
	private RelativeLayout mRlTimeTextContainer;
	private View mVScrollExtraHead;
	private View mVScrollExtraTail;
	private FrameLayout mMovingCircleFrame;
	private TextView mMovingCircleText;

	private List<MarkPointData> mMarkPointList;
	private Map<MarkPointData, MarkPointViews> mMarkPointViewMap;
	private PicPagerAdapter mPagerAdapter;

	private List<Period> mPeriodList;
	private List<ImageView> mPeriodViewList;

	private Period mLimitTime;
	private Context mContext;

	private int mCurrentItemIndex = 0;
	
	//原来移动点的位置,用作滑动圆的定位
	private int mOldCircleX=0;
	
	private int mMovingCircleWidth=0;
	// 使用universal-image-loader插件读取网络图片，需要工程导入universal-image-loader-1.8.6-with-sources.jar
	private ImageLoader mImageLoader = ImageLoader.getInstance();
	
	private OnPicClickListener mOnPicClickListener;

	// ===========================================================
	// Constructors
	// ===========================================================

	public TimeBarV3(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public TimeBarV3(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);

	}

	public TimeBarV3(Context context) {
		super(context);
		init(context);
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void onPageScrollStateChanged(int index) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int index) {
		setAllOtherPos(index);
	}
	
	@Override
	public void onClick(View v) {
		
		if(mOnPicClickListener!=null){
			if(v.getTag()!=null){
				MarkPointData mpd = (MarkPointData)v.getTag();
				mOnPicClickListener.onPicChick(mpd.getUrl());
			}
		}
	}


	// ===========================================================
	// Methods
	// ===========================================================
	public void init(Context context) {
		this.mContext = context;
		this.mMarkPointList = new ArrayList<TimeBarV3.MarkPointData>();
		this.mMarkPointViewMap = new HashMap<TimeBarV3.MarkPointData, TimeBarV3.MarkPointViews>();
		this.mPeriodList = new ArrayList<TimeBarV3.Period>();
		this.mPeriodViewList = new ArrayList<ImageView>();
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout linearLayout = (LinearLayout) inflater.inflate(
				R.layout.time_bar_v3, this);
		mIvTimeLine = (ImageView) linearLayout
				.findViewById(R.id.iv_main_time_line);

		mRlLineContainer = (RelativeLayout) linearLayout
				.findViewById(R.id.rl_line_container);
		mVpMainPicShow = (ViewPagerInToughView) linearLayout
				.findViewById(R.id.vp_main_pic_show);
		mHsvLineScrollView = (HorizontalScrollView) linearLayout
				.findViewById(R.id.hsv_line_scroll);
		mRlTimeTextContainer = (RelativeLayout) linearLayout
				.findViewById(R.id.rl_time_text_container);
		//屏幕宽度
		mHsvLineScrollView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		int screenWidth = ScreenUtil.getScreenMetrics(context).widthPixels;
		
		//设置container宽度为屏幕宽度
		LinearLayout.LayoutParams lpLineContainer = (LayoutParams) mRlLineContainer.getLayoutParams();
		lpLineContainer.width=screenWidth;
		mRlLineContainer.setLayoutParams(lpLineContainer);
		
		LinearLayout.LayoutParams lpTimeTextContainer = (LayoutParams) mRlTimeTextContainer.getLayoutParams();
		lpTimeTextContainer.width=screenWidth;
		mRlTimeTextContainer.setLayoutParams(lpTimeTextContainer);
		
		LinearLayout.LayoutParams lpViewPagerContainer = (LayoutParams) mVpMainPicShow.getLayoutParams();
		//宽度为屏幕宽度，滑动范围为屏幕宽度
		lpViewPagerContainer.width = LinearLayout.LayoutParams.MATCH_PARENT;
		lpViewPagerContainer.height=(int) (screenWidth*PIC_HEIGHT_SCREEN_RATIA);//viewpager高度定死为屏幕的宽度的PIC_HEIGHT_SCREEN_RATIA倍
		mVpMainPicShow.setLayoutParams(lpViewPagerContainer);
		
		
		// 设置额外区域的宽度，使scroll能够滑到最左端或最右端达到屏幕中间
		mVScrollExtraHead = (View) linearLayout
				.findViewById(R.id.v_scroll_extra_head);
		mVScrollExtraTail = (View) linearLayout
				.findViewById(R.id.v_scroll_extra_tail);
		LinearLayout.LayoutParams llHead = (LinearLayout.LayoutParams) mVScrollExtraHead
				.getLayoutParams();
		LinearLayout.LayoutParams llTail = (LinearLayout.LayoutParams) mVScrollExtraHead
				.getLayoutParams();
		int halfScreenWidth = screenWidth / 2;
		llHead.width = halfScreenWidth;
		llTail.width = halfScreenWidth;
		mVScrollExtraHead.setLayoutParams(llHead);
		mVScrollExtraTail.setLayoutParams(llTail);

		
		mPagerAdapter = new PicPagerAdapter();
		mVpMainPicShow.setAdapter(mPagerAdapter);
		mVpMainPicShow.setOnPageChangeListener(this);

		
	}

	public void setLimitTime(Date start, Date end) {
		if (mLimitTime == null) {
			mLimitTime = new Period();
			mLimitTime.setEnd(end);
			mLimitTime.setStart(start);

		} else {
			mLimitTime.setEnd(end);
			mLimitTime.setStart(start);
			refresh();
		}
	}

	/**
	 * 添加时间段
	 * 
	 * @param start
	 * @param end
	 */
	public void addPeriod(Date start, Date end) {
		addPeriod(Integer.MAX_VALUE,start,end);
	}
	/**
	 * 添加或修改时间段
	 * @param int index
	 * @param start
	 * @param end
	 */
	public void addPeriod(final int index,final Date start, final Date end){
		Asserts.check(mLimitTime != null, "addPeriod请先设置总时间");
		Asserts.check(mLimitTime.getStart().compareTo(start) <= 0
				&& mLimitTime.getEnd().compareTo(end) >= 0,
				"时间段必须在限制范围" + DateUtil.timeToString(mLimitTime.getStart())
						+ "-" + DateUtil.timeToString(mLimitTime.getEnd())
						+ "内" + "当前的时间设置为" + DateUtil.timeToString(start) + "-"
						+ DateUtil.timeToString(end));

		if(mIvTimeLine.getMeasuredWidth()==0||mIvTimeLine.getMeasuredHeight()==0){
			mIvTimeLine.getViewTreeObserver().addOnGlobalLayoutListener(
					new OnGlobalLayoutListener() {
						boolean isFirst = true;// 默认调用两次，这里只让它执行一次回调
	
						@SuppressLint("NewApi")
						@Override
						public void onGlobalLayout() {
							if(AppApplication.SYSTEM_SDK_VERSION>15){
								mIvTimeLine.getViewTreeObserver()
										.removeOnGlobalLayoutListener(this);
							}
							if (isFirst) {
								isFirst = false;
								// 现在布局全部完成，可以获取到任何View组件的宽度、高度、左边、右边等信息
								Log.i("TimeBarV2", "ViewTreeObserver CallBack W:"
										+ mIvTimeLine.getMeasuredWidth() + "  H:"
										+ mIvTimeLine.getMeasuredHeight());
								addPeriodView(index,start, end);
							}
						}
					});
			requestLayout();
		}else{
			addPeriodView(index,start, end);
		}
	}

	private void addPeriodView(int index,Date start, Date end) {
		
		Period newPeriod = new Period();
		newPeriod.setEnd(end);
		newPeriod.setStart(start);
		RelativeLayout.LayoutParams lpSrc = (RelativeLayout.LayoutParams) mIvTimeLine
				.getLayoutParams();
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				lpSrc.width, lpSrc.height);
		lp.setMargins(lpSrc.leftMargin, lpSrc.topMargin, 0, 0);
		lp.leftMargin = getXPosition(newPeriod.getStart());
		lp.width = getXPosition(newPeriod.getEnd()) - getXPosition(newPeriod.getStart());
		ImageView periodView = new ImageView(mContext);
		periodView.setImageResource(R.color.main_status_green);
		
		if(EmptyUtil.isEmpty(mPeriodList)||mPeriodList.size()<=index){
			mPeriodList.add(newPeriod);
			mPeriodViewList.add(periodView);
		}else{
			mPeriodList.remove(index);
			mPeriodList.add(index, newPeriod);
			mRlLineContainer.removeView(mPeriodViewList.remove(index));
			mPeriodViewList.add(index, periodView);
		}
		mRlLineContainer.addView(periodView, lp);
		
		mHsvLineScrollView.scrollTo(ScreenUtil.getScreenMetrics(mContext).widthPixels/2, 0);//设置初始位置
	};

	/**
	 * 设置一个抓图时间点，该方法可在view未初始化时(oncreate)中调用
	 * 
	 * @param time
	 *            抓图的时间
	 */
	public void addDefaultMarkPoint(final int id,final Date time,final String picUrl,final int enterOrLeave) {
		Asserts.check(mLimitTime != null, "addDefaultMarkPoint请先设置总时间");
		if (mLimitTime.getStart().compareTo(time) >= 0
				|| mLimitTime.getEnd().compareTo(time) <= 0) {
			Log.d(TAG,
					"超出时间范围，不显示在界面上" + "时间段必须在限制范围"
							+ DateUtil.timeToString(mLimitTime.getStart())
							+ "-" + DateUtil.timeToString(mLimitTime.getEnd())
							+ "内" + "当前时间" + DateUtil.timeToString(time));
			return;
		}
		mIvTimeLine.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					boolean isFirst = true;// 默认调用两次，这里只让它执行一次回调

					@SuppressLint("NewApi")
					@Override
					public void onGlobalLayout() {
						// TODO 这个函数15以下不可用
						if(AppApplication.SYSTEM_SDK_VERSION>15){
							mIvTimeLine.getViewTreeObserver()
									.removeOnGlobalLayoutListener(this);
						}
						if (isFirst) {
							isFirst = false;
							// 现在布局全部完成，可以获取到任何View组件的宽度、高度、左边、右边等信息
							Log.i("TimeBarV2", "ViewTreeObserver CallBack W:"
									+ mIvTimeLine.getMeasuredWidth() + "  H:"
									+ mIvTimeLine.getMeasuredHeight());
							addMarkPointView(id,time,picUrl,enterOrLeave);
						}
					}
				});
		requestLayout();
	}

	/**
	 * 设置一个抓图时间点ui
	 * 
	 * @param time
	 */
	public void addMarkPointView(int id,Date time,String picUrl,int enterOrLeave) {
		MarkPointData mpd = new MarkPointData();
		mpd.setxInParent(getXPosition(time));
		mpd.setMarkPointTime(time);
		mpd.setMarkPointDrawable(mContext.getResources().getDrawable(
				R.drawable.picture_box));
		mpd.setIsEnter(enterOrLeave);
		mpd.setUrl(picUrl);
		
		addMarkPointView(mpd);
	}

	public void addMarkPointView(MarkPointData data) {
		mMarkPointList.add(data);
		// 添加图片
		ImageView iv = new ImageView(mContext);
		// RelativeLayout.LayoutParams lp = new
		// RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		//iv.setImageDrawable(data.getMarkPointDrawable());
		// lp.leftMargin=data.getxInParent()-ImageUtils.drawableToBitmap(data.getMarkPointDrawable()).getWidth()/2;
		
		//圆和文字容器
		FrameLayout circleFrame = new FrameLayout(mContext);
		RelativeLayout.LayoutParams lpSrc = (RelativeLayout.LayoutParams) mIvTimeLine
				.getLayoutParams();
		//圆
		ImageView ivCircle = new ImageView(mContext);
		Drawable defaultCircle = mContext.getResources().getDrawable(
				R.drawable.main_tip_circle_small);
		Bitmap bm = ImageUtils.drawableToBitmap(defaultCircle);
		ivCircle.setImageResource(R.drawable.main_tip_circle_small);
		circleFrame.addView(ivCircle);
		
		//圆中的文本
		TextView tvCircleText = new TextView(mContext);
		if(1==data.getIsEnter()){
			tvCircleText.setText(R.string.main_circle_text_enter);
		}
		if(0==data.getIsEnter()){
			tvCircleText.setText(R.string.main_circle_text_leave);
		}
		tvCircleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		tvCircleText.setTextColor(mContext.getResources().getColor(android.R.color.white));
		FrameLayout.LayoutParams lpTextView = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,Gravity.CENTER);
		circleFrame.addView(tvCircleText,lpTextView);
		
		//整个圆的frame
		RelativeLayout.LayoutParams lpFrame = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lpFrame.setMargins(lpSrc.leftMargin, lpSrc.topMargin, 0, 0);
		lpFrame.leftMargin = data.getxInParent() - bm.getWidth() / 2;
		
		lpFrame.topMargin = lpSrc.topMargin - bm.getHeight() / 2
				+ mIvTimeLine.getHeight() / 2;// 必须加上线条的一半高度

		mRlLineContainer.addView(circleFrame,lpFrame);

		
		// 添加时间时间文本
		TextView ivTimeShow = new TextView(mContext);
		RelativeLayout.LayoutParams lpTimeShow = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lpTimeShow.leftMargin = data.getxInParent();
		ivTimeShow.setText(DateUtil.dateToString(data.getMarkPointTime(),
				"HH:mm"));
		ivTimeShow.setVisibility(View.GONE);
		mRlTimeTextContainer.addView(ivTimeShow, -1, lpTimeShow);

		MarkPointViews mpvViews = new MarkPointViews();
		mpvViews.setMarkPointCircle(ivCircle);
		mpvViews.setMarkPointCircleFrame(circleFrame);
		mpvViews.setMarkPointCircleText(tvCircleText);
		mpvViews.setMarkPointImageView(iv);
		mpvViews.setMarkPointTextView(ivTimeShow);
		mMarkPointViewMap.put(data, mpvViews);
		mPagerAdapter.notifyDataSetChanged();
		
		if(mMarkPointList.size()==1){//第一个截图，需要添加滑动图片
			addMovingCircle(data);
		}else{
			//将圆放到顶层(不被覆盖)
			circleFrame.bringToFront();
			mMovingCircleFrame.bringToFront();
		}
	}
	
	private void addMovingCircle(MarkPointData data){
		mMovingCircleFrame = new FrameLayout(mContext);
		RelativeLayout.LayoutParams lpSrc = (RelativeLayout.LayoutParams) mIvTimeLine
				.getLayoutParams();
		
		//创建圆
		ImageView ivCircle = new ImageView(mContext);
		Drawable defaultCircle = mContext.getResources().getDrawable(
				R.drawable.main_tip_circle);
		Bitmap bm = ImageUtils.drawableToBitmap(defaultCircle);
		ivCircle.setImageResource(R.drawable.main_tip_circle);
		mMovingCircleFrame.addView(ivCircle);
		//创建文本框
		TextView tvCircleText = new TextView(mContext);
		tvCircleText.setText("");
		tvCircleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		tvCircleText.setTextColor(mContext.getResources().getColor(android.R.color.white));
		mMovingCircleText = tvCircleText;
		FrameLayout.LayoutParams lpTextView = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,Gravity.CENTER);
		
		tvCircleText.setLayoutParams(lpTextView);
		mMovingCircleFrame.addView(tvCircleText);
		
		RelativeLayout.LayoutParams lpFrame = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lpFrame.setMargins(lpSrc.leftMargin, lpSrc.topMargin, 0, 0);
		lpFrame.leftMargin = data.getxInParent() - bm.getWidth() / 2;
		
		lpFrame.topMargin = lpSrc.topMargin - bm.getHeight() / 2
				+ mIvTimeLine.getHeight() / 2;// 必须加上线条的一半高度
		mMovingCircleWidth = bm.getHeight();
		mRlLineContainer.addView(mMovingCircleFrame, lpFrame);
		
		mOldCircleX = data.getxInParent()-mMovingCircleWidth/2-lpSrc.leftMargin;
	}

	/**
	 * 获取某个时间的view的x位置
	 * 
	 * @param time
	 * @return
	 */
	private int getXPosition(Date time) {
		Asserts.check(mLimitTime != null, "总时间未设置");
		int lineWidth = mIvTimeLine.getWidth();
		float posXWithoutMargin = 1.0f
				* lineWidth
				/ (DateUtil.getSecondInDay(mLimitTime.getEnd()) - DateUtil
						.getSecondInDay(mLimitTime.getStart()))
				* (DateUtil.getSecondInDay(time) - DateUtil
						.getSecondInDay(mLimitTime.getStart()));

		int margin = mIvTimeLine.getLeft();
		return (int) posXWithoutMargin + margin;
	}

	/**
	 * 获取viewpager的方形区域，可用于判断手势位置
	 * 
	 * @return
	 */
	public Rect getViewPagerRect() {
		Rect rect = new Rect();
		mVpMainPicShow.getGlobalVisibleRect(rect);
		return rect;
	}

	/**
	 * 获取的方形区域，可用于判断手势位置
	 * 
	 * @return
	 */
	public Rect getScrollViewRect() {
		Rect rect = new Rect();
		mHsvLineScrollView.getGlobalVisibleRect(rect);
		return rect;
	}

	/**
	 * 删除所有节点
	 */
	public void removeAllPoint() {
		while (mMarkPointList.size() != 0) {
			removeMakePoint(0);
		}
		requestLayout();
	}

	/**
	 * 删除某个节点
	 * @param index
	 */
	public MarkPointData removeMakePoint(int index) {
		Args.check(mMarkPointList.size()!=0&&mMarkPointList.size() > index, "移除的位置超出范围，当前列表大小为"
				+ mMarkPointList.size() + "移除位置为" + index);
		if(mCurrentItemIndex==index){
			if(mMarkPointList.size()!=1){
				//删除前滑动到其他节点
				if(mCurrentItemIndex>0){//向前划
					setCurrentMarkPoint(mCurrentItemIndex-1);
				}else{
					setCurrentMarkPoint(mCurrentItemIndex+1);
				}
			}
		}
		MarkPointData mpd = mMarkPointList.get(index);
		MarkPointViews mpv = mMarkPointViewMap.get(mpd);
		mRlLineContainer.removeView(mpv.getMarkPointCircleFrame());
		mRlTimeTextContainer.removeView(mpv.getMarkPointTextView());
		mMarkPointViewMap.remove(mMarkPointList.get(index));
		mMarkPointList.remove(index);
		if (mCurrentItemIndex > index) {
			mCurrentItemIndex--;
		}
		mPagerAdapter.notifyDataSetChanged();
	
		
		
		if(mMarkPointList.size()==0){
			//移除滑动圆
			mRlLineContainer.removeView(mMovingCircleFrame);
		}
		return mpd;
	}


	/**
	 * 根据当前是第几个图片，滑动时间轴位置，切换图片
	 * 
	 * @param index
	 */
	public void setCurrentMarkPoint(final int index) {
		Args.check(index < mMarkPointList.size(), "setCurrentTab所选tab超出已有tab数");
		// 设置当前的线条位置
		
		
		setAllOtherPos(index);
		// 设置当前的图片
		setPicPage(index);
		
		
	}

	/**
	 * 返回点的数量
	 * 
	 * @return
	 */
	public int getMarkPotinsCount() {
		return mMarkPointList.size();
	}
	
	/**
	 * 除了viewpager的其他一些移动
	 * @param index
	 */
	public void setAllOtherPos(int index){
		setLinePos(index);
		startCircleAnimate(index);
		startTextFadeIn(index);
		mCurrentItemIndex = index;
		
	}
	
	/**
	 * 大圆圈的动画
	 * @param toIndex
	 */
	public void startCircleAnimate(int toIndex){
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)mMovingCircleFrame.getLayoutParams();
		
		//int oldX=mMarkPointList.get(currentItemIndex).getxInParent()-mMovingCircleWidth/2-lp.leftMargin;
		MarkPointData newData =mMarkPointList.get(toIndex);
		int newX = newData.getxInParent()-mMovingCircleWidth/2-lp.leftMargin;// 记录的x位置
		
		Log.d(TAG,"newX="+newX);
		TranslateAnimation an = new TranslateAnimation(
				mOldCircleX, 
				newX,
				0,
				0);
		an.setFillAfter(true);
		an.setDuration(300);
		mMovingCircleFrame.startAnimation(an);
		if(newData.getIsEnter()==1){
			mMovingCircleText.setText(R.string.main_circle_text_enter);
		}else{
			mMovingCircleText.setText(R.string.main_circle_text_leave);
		}
		mOldCircleX = newX;
	}	
	/**
	 * 文字渐入动画
	 * @param toIndex
	 */
	public void startTextFadeIn(int toIndex){
		MarkPointViews views = mMarkPointViewMap.get(mMarkPointList.get(toIndex));
		if(mMarkPointList.size()>mCurrentItemIndex){
			//原文字消失
			MarkPointViews oldViews = mMarkPointViewMap.get(mMarkPointList.get(mCurrentItemIndex));
			final TextView oldTextView = oldViews.getMarkPointTextView();
			AlphaAnimation fadeOut = new AlphaAnimation(1,0);
			fadeOut.setDuration(200);
			fadeOut.setFillAfter(true);
			fadeOut.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {}
				
				@Override
				public void onAnimationRepeat(Animation animation) {}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					oldTextView.setVisibility(View.GONE);
				}
			});
			oldTextView.startAnimation(fadeOut);
		}
		
		final TextView newTextView = views.getMarkPointTextView();
		AlphaAnimation fadeIn = new AlphaAnimation(0,1);
		fadeIn.setDuration(300);
		fadeIn.setFillAfter(true);
		fadeIn.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
					newTextView.setVisibility(View.INVISIBLE);
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					newTextView.setVisibility(View.VISIBLE);
				}
			});
		newTextView.startAnimation(fadeIn);
	}
	

	/**
	 * 根据当前是第几个图片，滑动时间轴位置
	 * 
	 * @param toIndex
	 */
	public void setLinePos(int toIndex) {
		Log.d(TAG, "index=" + toIndex);
		int posX = mMarkPointList.get(toIndex).getxInParent();// 记录的x位置
		posX = posX - mHsvLineScrollView.getWidth() / 2;// 滑到中间
		posX = posX + mVScrollExtraHead.getWidth();// 加上额外区域
		mHsvLineScrollView.smoothScrollTo(posX, 0);
	}

	/**
	 * 根据当前是第几个图片，切换图片
	 * 
	 * @param index
	 */
	public void setPicPage(int index) {
		mVpMainPicShow.setCurrentItem(index);
	}
	
	public void refresh(){
		int currentIndex = mCurrentItemIndex;
		List<MarkPointData> newList=new ArrayList<MarkPointData>();
		while (mMarkPointList.size() != 0) {
			newList.add(removeMakePoint(0));
		}
		
		Collections.sort(newList,new Comparator<MarkPointData>() {
			@Override
			public int compare(MarkPointData lhs, MarkPointData rhs) {
				return lhs.getMarkPointTime().compareTo(rhs.getMarkPointTime());
			}
		});
		for(int i=0;i<newList.size();i++){
			addMarkPointView(newList.get(i));
		}
		if(mMarkPointList.size() != 0){
			setCurrentMarkPoint(currentIndex);
		}
		//setCurrentMarkPoint(mCurrentItemIndex);
	}
	
	/**
	 * 获取限制时间，用于设置bar的开始时间
	 * @param periodStart
	 * @param hourEarily
	 * @return
	 */
	public static Date getTimeBarStartTimeByNormalTimeStart(Date periodStart,int hourEarily){
		Date LimitStart = null;
		int hourS = DateUtil.getHour(DateUtil.getCalendar(periodStart));
		if (hourS > hourEarily) {
			LimitStart = DateUtil
					.addDate(periodStart, -hourEarily, Calendar.HOUR_OF_DAY);
		} else {
			LimitStart = DateUtil.dayStart(periodStart);
		}
		return LimitStart;
	}
	/**
	 * 获取限制时间，用于设置bar的结束时间
	 * @param periodStart
	 * @param hourLater
	 * @return
	 */
	public static Date getTimeBarEndTimeByNormalTimeEnd(Date periodEnd,int hourLater){
		Date LimitEnd = null;
		int hourE = DateUtil.getHour(DateUtil.getCalendar(periodEnd));
		if (hourE < (24-hourLater)) {
			LimitEnd = DateUtil.addDate(periodEnd, hourLater, Calendar.HOUR_OF_DAY);
		} else {
			LimitEnd = DateUtil.dayEnd(periodEnd);
		}
		return LimitEnd;
	}
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	class MarkPointData {
		private int id;
		public int xInParent;
		public Drawable markPointDrawable;
		public Date markPointTime;
		public String url;
		/**
		 * 0:leave  ;1:enter
		 */
		public int isEnter; 
		public int getxInParent() {
			return xInParent;
		}

		public void setxInParent(int xInParent) {
			this.xInParent = xInParent;
		}

		public Drawable getMarkPointDrawable() {
			return markPointDrawable;
		}

		public void setMarkPointDrawable(Drawable markPointDrawable) {
			this.markPointDrawable = markPointDrawable;
		}

		public Date getMarkPointTime() {
			return markPointTime;
		}

		public void setMarkPointTime(Date markPointTime) {
			this.markPointTime = markPointTime;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public int getIsEnter() {
			return isEnter;
		}

		public void setIsEnter(int isEnter) {
			this.isEnter = isEnter;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

	}

	class MarkPointViews {

		public View markPointCircle;
		public FrameLayout markPointCircleFrame;
		public TextView markPointCircleText;
		
		public TextView markPointTextView;
		public ImageView markPointImageView;

		public FrameLayout getMarkPointCircleFrame() {
			return markPointCircleFrame;
		}

		public void setMarkPointCircleFrame(FrameLayout markPointCircleFrame) {
			this.markPointCircleFrame = markPointCircleFrame;
		}

		public TextView getMarkPointCircleText() {
			return markPointCircleText;
		}

		public void setMarkPointCircleText(TextView markPointCircleText) {
			this.markPointCircleText = markPointCircleText;
		}

		public TextView getMarkPointTextView() {
			return markPointTextView;
		}

		public void setMarkPointTextView(TextView markPointTextView) {
			this.markPointTextView = markPointTextView;
		}

		public View getMarkPointCircle() {
			return markPointCircle;
		}

		public void setMarkPointCircle(View markPointCircle) {
			this.markPointCircle = markPointCircle;
		}

		public ImageView getMarkPointImageView() {
			return markPointImageView;
		}

		public void setMarkPointImageView(ImageView markPointImageView) {
			this.markPointImageView = markPointImageView;
		}

	}

	class Period {
		private Date start;
		private Date end;

		public Date getStart() {
			return start;
		}

		public void setStart(Date start) {
			this.start = start;
		}

		public Date getEnd() {
			return end;
		}

		public void setEnd(Date end) {
			this.end = end;
		}
	}

	
	/**
	 * 填充ViewPager的页面适配器
	 * 
	 */
	private class PicPagerAdapter extends PagerAdapter {
		private DisplayImageOptions options;
		public PicPagerAdapter(){
			options = new DisplayImageOptions.Builder()
			.cloneFrom(AppApplication.getApplication().getAppDefaultDisplayImageOptions())
			.showImageOnLoading(R.drawable.default_pic) //设置图片在下载期间显示的图片  
			.showImageForEmptyUri(R.drawable.default_pic)//设置图片Uri为空或是错误的时候显示的图片  
			.showImageOnFail(R.drawable.default_pic)  //设置图片加载/解码过程中错误时候显示的图片
//		    .decodingOptions(
//		    		decodingOptions)//设置图片的解码配置  
		    //.delayBeforeLoading(int delayInMillis)//int delayInMillis为你设置的下载前的延迟时间
		    //设置图片加入缓存前，对bitmap进行设置  
		    .postProcessor( new BitmapProcessor(){

				@Override
				public Bitmap process(Bitmap bitmap) {
					return ImageUtils.getRoundedCornerWithTrigBitmap(
							bitmap, 
							ScreenUtil.dip2px(mContext, 4), 
							ImageUtils.TRI_TOP, 
							bitmap.getWidth()/2,
							ScreenUtil.dip2px(mContext, 4));
				}
		    })  
		    .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位  
		    .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少  
		    .displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间  
		    .build();//构建完成  
		}
		@Override
		public void destroyItem(View container, int position, Object object) {
			// ((ViewPag.er)container).removeView((View)object);
			ImageView imageView = (ImageView)object;
			((ViewPager) container).removeView(imageView);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// 获取第I个图片
			MarkPointData data = mMarkPointList.get(position);
			ImageView imageView = new ImageView(mContext);
			mMarkPointViewMap.get(data).setMarkPointImageView(imageView);
			
			mImageLoader.displayImage(data.getUrl(), imageView, options);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);

			imageView.setLayoutParams(lp);
			imageView.setTag(data);
			imageView.setOnClickListener(TimeBarV3.this);
			
			container.addView(imageView);

			return imageView;
		}

		@Override
		public int getCount() {
			return mMarkPointList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public int getItemPosition(Object object) {
			//如果object在list中找不到，说明是删除的节点，返回POSITION_NONE刷新当前页
			for(int i=0;i<mMarkPointList.size();i++){
				if(object==mMarkPointViewMap.get(mMarkPointList.get(i)).getMarkPointImageView()){
					return i;
				}
			}
			return POSITION_NONE;
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}

		@Override
		public void finishUpdate(View arg0) {
		}

	}
	
	public interface OnPicClickListener{
		public void onPicChick(String picUrl);
	}
	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public int getCurrentItemIndex() {
		return mCurrentItemIndex;
	}

	public void setOnPicClickListener(OnPicClickListener onPicClickListener) {
		this.mOnPicClickListener = onPicClickListener;
	}

}
