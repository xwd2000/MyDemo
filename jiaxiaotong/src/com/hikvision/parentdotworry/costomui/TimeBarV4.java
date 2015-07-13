package com.hikvision.parentdotworry.costomui;

import java.util.ArrayList;
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
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hikvision.parentdotworry.R;
import com.hikvision.parentdotworry.costomui.HorizontalScrollViewCanListener.OnScrollChangeListener;
import com.hikvision.parentdotworry.plug.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.hikvision.parentdotworry.plug.universalimageloader.core.DisplayImageOptions;
import com.hikvision.parentdotworry.plug.universalimageloader.core.ImageLoader;
import com.hikvision.parentdotworry.plug.universalimageloader.core.ImageLoaderConfiguration;
import com.hikvision.parentdotworry.plug.universalimageloader.core.assist.ImageScaleType;
import com.hikvision.parentdotworry.plug.universalimageloader.core.assist.QueueProcessingType;
import com.hikvision.parentdotworry.plug.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.hikvision.parentdotworry.plug.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.hikvision.parentdotworry.plug.universalimageloader.core.process.BitmapProcessor;
import com.hikvision.parentdotworry.utils.Args;
import com.hikvision.parentdotworry.utils.DateUtil;
import com.hikvision.parentdotworry.utils.ImageUtils;
import com.hikvision.parentdotworry.utils.ScreenUtil;

public class TimeBarV4 extends LinearLayout implements OnPageChangeListener {

	// ===========================================================
	// Constants
	// ===========================================================
	private static final String TAG = "TimeBarV4";
	private static final float PIC_WIDTH_SCREEN_RATIA = 2/7F;
	private static final float PIC_HEIGHT_SCREEN_RATIA = 2/7F;
	// ===========================================================
	// Fields
	// ===========================================================
	private ImageView mIvTimeLine;

	private RelativeLayout mRlLineContainer;
	private ViewPagerInToughView mVpMainPicShow;
	private ImageView ivMainCapturePic;
	private HorizontalScrollViewCanListener mHsvLineScrollView;
	private RelativeLayout mRlTimeTextContainer;
	private View mVScrollExtraHead;
	private View mVScrollExtraTail;
	private FrameLayout mMovingCircleFrame;

	private List<MarkPointData> mMarkPointList;
	private Map<MarkPointData, MarkPointViews> mMarkPointViewMap;
	private PicPagerAdapter mPagerAdapter;

	private List<Period> mPeriodList;

	private Period mLimitTime;
	private Context mContext;

	private int mCurrentItemIndex = 0;
	
	//原来移动点的位置,用作滑动圆的定位
	private int mOldCircleX=0;
	
	private int mMovingCircleWidth=0;
	// 使用universal-image-loader插件读取网络图片，需要工程导入universal-image-loader-1.8.6-with-sources.jar
	private ImageLoader mImageLoader = ImageLoader.getInstance();

	// ===========================================================
	// Constructors
	// ===========================================================

	public TimeBarV4(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public TimeBarV4(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);

	}

	public TimeBarV4(Context context) {
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
	


	// ===========================================================
	// Methods
	// ===========================================================
	public void init(Context context) {
		this.mContext = context;
		this.mMarkPointList = new ArrayList<TimeBarV4.MarkPointData>();
		this.mMarkPointViewMap = new HashMap<TimeBarV4.MarkPointData, TimeBarV4.MarkPointViews>();
		this.mPeriodList = new ArrayList<TimeBarV4.Period>();
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout linearLayout = (LinearLayout) inflater.inflate(
				R.layout.time_bar_v4, this);
		mIvTimeLine = (ImageView) linearLayout
				.findViewById(R.id.iv_main_time_line);

		mRlLineContainer = (RelativeLayout) linearLayout
				.findViewById(R.id.rl_line_container);
		mVpMainPicShow = (ViewPagerInToughView) linearLayout
				.findViewById(R.id.vp_main_pic_show);
		mHsvLineScrollView = (HorizontalScrollViewCanListener) linearLayout
				.findViewById(R.id.hsv_line_scroll);
		mRlTimeTextContainer = (RelativeLayout) linearLayout
				.findViewById(R.id.rl_time_text_container);
		ivMainCapturePic = (ImageView) linearLayout
				.findViewById(R.id.iv_main_capture_pic);
		//屏幕宽度
		
		
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
		
		//设置imageview的大小
		RelativeLayout.LayoutParams lpPic = (RelativeLayout.LayoutParams)ivMainCapturePic.getLayoutParams();
		lpPic.width = (int) (screenWidth*PIC_WIDTH_SCREEN_RATIA);
		lpPic.height = (int) (screenWidth*PIC_HEIGHT_SCREEN_RATIA);
		
		ivMainCapturePic.setLayoutParams(lpPic);
		ivMainCapturePic.setScaleType(ScaleType.FIT_XY);
		
		// 设置额外区域的宽度，使scroll能够滑到最左端或最右端达到屏幕中间
		mVScrollExtraHead = (View) linearLayout
				.findViewById(R.id.v_scroll_extra_head);
		mVScrollExtraTail = (View) linearLayout
				.findViewById(R.id.v_scroll_extra_tail);
		LinearLayout.LayoutParams llHead = (LinearLayout.LayoutParams) mVScrollExtraHead
				.getLayoutParams();
		LinearLayout.LayoutParams llTail = (LinearLayout.LayoutParams) mVScrollExtraHead
				.getLayoutParams();
		final int halfPicWidth = lpPic.width / 2;
		llHead.width = halfPicWidth;
		llTail.width = halfPicWidth;
		mVScrollExtraHead.setLayoutParams(llHead);mVScrollExtraHead.setBackgroundResource(R.color.azure);
		mVScrollExtraTail.setLayoutParams(llTail);mVScrollExtraTail.setBackgroundResource(R.color.azure);

		
		//initImageLoader(context);
		mPagerAdapter = new PicPagerAdapter();
		mVpMainPicShow.setAdapter(mPagerAdapter);
		mVpMainPicShow.setOnPageChangeListener(this);
		
		
		mHsvLineScrollView.setOnScrollChangerListener(
				new OnScrollChangeListener() {
			DisplayImageOptions dio = getDisplayImageOptions();
			int screenWidth = ScreenUtil.getScreenMetrics(mContext).widthPixels;
			double oldPercentPos=0;
			@Override
			public void onScrollChange(int l, int t, int oldl, int oldt) {
				
				int initMargin=mContext.getResources().getDimensionPixelSize(R.dimen.main_line_padding_left_right);
				//计算滑动的最右距离
				

				int maxScroll = halfPicWidth/*llHead.width*/+halfPicWidth/*llTail.width*/;//因为线条长度等于屏幕宽度
				
				//设置圆的位置
				RelativeLayout.LayoutParams lpCircle = (RelativeLayout.LayoutParams)mMovingCircleFrame.getLayoutParams();
				lpCircle.leftMargin = (int) (l*1.0f/maxScroll*mIvTimeLine.getWidth()+initMargin-mMovingCircleWidth/2);//mVScrollExtraHead.getWidth());
				mMovingCircleFrame.setLayoutParams(lpCircle);
				
				//图片透明度
				int nextIndex=0,preIndex=0;
				for(int i=0,j=mMarkPointList.size();i<j;i++){
					
					if(mMarkPointList.get(i).getxInParent()-(lpCircle.leftMargin+mMovingCircleWidth/2)>0){
						nextIndex=i;
						break;
					}
					preIndex=i;
				}
				MarkPointData oldData=mMarkPointList.get(preIndex);
				MarkPointData newData=mMarkPointList.get(nextIndex);
				int dalta = lpCircle.leftMargin+mMovingCircleWidth/2-mMarkPointList.get(preIndex).getxInParent();
				
				double percentPos = dalta*1.0/(newData.getxInParent()-oldData.getxInParent());
				double ratio=2*Math.abs(percentPos-0.5);
				ivMainCapturePic.setAlpha((int) (255*ratio));//0-256
				
				
				//设置图片位置
				RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)ivMainCapturePic.getLayoutParams();
				
				//lp.width=(int) (ratio*halfPicWidth*2);
				lp.leftMargin=(int) (l*(screenWidth*(1-PIC_WIDTH_SCREEN_RATIA)-2*initMargin)/
						maxScroll+initMargin+(halfPicWidth*2-lp.width)/2.0);
				//Log.d(TAG,"maxScroll="+maxScroll+"leftMargin="+lp.leftMargin+",l="+l);
				ivMainCapturePic.setLayoutParams(
						lp);
				if(percentPos>=0.5&&oldPercentPos<=0.5){
					mImageLoader.displayImage(newData.getUrl(), ivMainCapturePic, dio);
				}else if(percentPos<=0.5&&oldPercentPos>=0.5){
					mImageLoader.displayImage(oldData.getUrl(), ivMainCapturePic, dio);
				}
				oldPercentPos=percentPos;
				
				
			}
			
		});
	}

	public void setLimitTime(Date start, Date end) {
		if (mLimitTime == null) {
			mLimitTime = new Period();
			mLimitTime.setEnd(end);
			mLimitTime.setStart(start);

		} else {
			Asserts.check(false, "已经设置了一个时间段");
		}
	}

	/**
	 * 添加时间段
	 * 
	 * @param start
	 * @param end
	 */
	public void addPeriod(Date start, Date end) {
		Asserts.check(mLimitTime != null, "addPeriod请先设置总时间");
		Asserts.check(mLimitTime.getStart().compareTo(start) <= 0
				&& mLimitTime.getEnd().compareTo(end) >= 0,
				"时间段必须在限制范围" + DateUtil.timeToString(mLimitTime.getStart())
						+ "-" + DateUtil.timeToString(mLimitTime.getEnd())
						+ "内" + "当前的时间设置为" + DateUtil.timeToString(start) + "-"
						+ DateUtil.timeToString(end));

		final Period newPeriod = new Period();
		newPeriod.setEnd(end);
		newPeriod.setStart(start);
		mPeriodList.add(newPeriod);

		mIvTimeLine.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					boolean isFirst = true;// 默认调用两次，这里只让它执行一次回调

					@Override
					public void onGlobalLayout() {
						if (isFirst) {
							isFirst = false;
							// 现在布局全部完成，可以获取到任何View组件的宽度、高度、左边、右边等信息
							Log.i("TimeBarV2", "ViewTreeObserver CallBack W:"
									+ mIvTimeLine.getMeasuredWidth() + "  H:"
									+ mIvTimeLine.getMeasuredHeight());
							addPeriodView(newPeriod);
						}
					}
				});
		requestLayout();
	}

	private void addPeriodView(Period p) {
		RelativeLayout.LayoutParams lpSrc = (RelativeLayout.LayoutParams) mIvTimeLine
				.getLayoutParams();
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				lpSrc.width, lpSrc.height);
		lp.setMargins(lpSrc.leftMargin, lpSrc.topMargin, 0, 0);
		lp.leftMargin = getXPosition(p.getStart());
		lp.width = getXPosition(p.getEnd()) - getXPosition(p.getStart());
		ImageView periodView = new ImageView(mContext);
		periodView.setImageResource(R.color.main_status_green);
		mRlLineContainer.addView(periodView, lp);
	};

	/**
	 * 设置一个抓图时间点，该方法可在view未初始化时(oncreate)中调用
	 * 
	 * @param time
	 *            抓图的时间
	 */
	public void addDefaultMarkPoint(final Date time,final String picUrl) {
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
						if (isFirst) {
							isFirst = false;
							// 现在布局全部完成，可以获取到任何View组件的宽度、高度、左边、右边等信息
							Log.i("TimeBarV2", "ViewTreeObserver CallBack W:"
									+ mIvTimeLine.getMeasuredWidth() + "  H:"
									+ mIvTimeLine.getMeasuredHeight());
							addMarkPointView(time,picUrl);
							// TODO 这个函数15以下不可用
							mIvTimeLine.getViewTreeObserver()
									.removeOnGlobalLayoutListener(this);
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
	public void addMarkPointView(Date time,String picUrl) {
		MarkPointData mpd = new MarkPointData();
		mpd.setxInParent(getXPosition(time));
		mpd.setMarkPointTime(time);
		mpd.setMarkPointDrawable(mContext.getResources().getDrawable(
				R.drawable.picture_box));
		mpd.setUrl(picUrl);
		
		addMarkPointView(mpd);
	}

	public void addMarkPointView(MarkPointData data) {
		
		mMarkPointList.add(data);
		// 添加图片
		ImageView iv = new ImageView(mContext);
		// RelativeLayout.LayoutParams lp = new
		// RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		iv.setImageDrawable(data.getMarkPointDrawable());
		// lp.leftMargin=data.getxInParent()-ImageUtils.drawableToBitmap(data.getMarkPointDrawable()).getWidth()/2;

		// /添加默认图片
		RelativeLayout.LayoutParams lpSrc = (RelativeLayout.LayoutParams) mIvTimeLine
				.getLayoutParams();
		ImageView ivCircle = new ImageView(mContext);
		RelativeLayout.LayoutParams lpCircle = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lpCircle.setMargins(lpSrc.leftMargin, lpSrc.topMargin, 0, 0);
		Drawable defaultCircle = mContext.getResources().getDrawable(
				R.drawable.main_tip_circle_small);
		Bitmap bm = ImageUtils.drawableToBitmap(defaultCircle);
		lpCircle.leftMargin = data.getxInParent() - bm.getWidth() / 2;
		lpCircle.topMargin = lpSrc.topMargin - bm.getHeight() / 2
				+ mIvTimeLine.getHeight() / 2;// 必须加上线条的一半高度
		ivCircle.setImageResource(R.drawable.main_tip_circle_small);
		mRlLineContainer.addView(ivCircle, lpCircle);
		
		final int index = mMarkPointList.size()-1;
		ivCircle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setCurrentMarkPoint(index);
				
			}
		});
		
		// 添加时间文本
		TextView ivTimeShow = new TextView(mContext);
		RelativeLayout.LayoutParams lpTimeShow = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lpTimeShow.leftMargin = data.getxInParent();
		ivTimeShow.setText(DateUtil.dateToString(data.getMarkPointTime(),
				"HH:mm"));
		ivTimeShow.setVisibility(View.INVISIBLE);
		mRlTimeTextContainer.addView(ivTimeShow, -1, lpTimeShow);

		MarkPointViews mpvViews = new MarkPointViews();
		mpvViews.setMarkPointCircle(ivCircle);
		mpvViews.setMarkPointImageView(iv);
		mpvViews.setMarkPointTextView(ivTimeShow);
		mMarkPointViewMap.put(data, mpvViews);
		mPagerAdapter.notifyDataSetChanged();
		
		if(mMarkPointList.size()==1){//第一个截图，需要添加滑动图片
			addMovingCircle(data);
		}else{
			//将圆放到顶层(不被覆盖)
			mMovingCircleFrame.bringToFront();
		}
		DisplayImageOptions options = getDisplayImageOptions(); 
		mImageLoader.displayImage(data.getUrl(), ivMainCapturePic, options);
		
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
		tvCircleText.setText("来");
		tvCircleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		tvCircleText.setTextColor(mContext.getResources().getColor(android.R.color.white));
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
		mRlLineContainer.removeView(mpv.getMarkPointCircle());
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
		
		setPic(index);
		
		
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
		//startCircleAnimate(index);
		startTextFadeIn(index);
		mCurrentItemIndex = index;
	}
	public void setPic(int index){
		MarkPointData mpd = mMarkPointList.get(index);
		mImageLoader.displayImage(mpd.getUrl(), ivMainCapturePic, getDisplayImageOptions());
	}
	/**
	 * 大圆圈的动画
	 * @param toIndex
	 */
	public void startCircleAnimate(int toIndex){
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)mMovingCircleFrame.getLayoutParams();
		
		//int oldX=mMarkPointList.get(currentItemIndex).getxInParent()-mMovingCircleWidth/2-lp.leftMargin;
		int newX = mMarkPointList.get(toIndex).getxInParent()-mMovingCircleWidth/2-lp.leftMargin;// 记录的x位置
		
		Log.d(TAG,"newX="+newX);
		TranslateAnimation an = new TranslateAnimation(
				mOldCircleX, 
				newX,
				0,
				0);
		an.setFillAfter(true);
		an.setDuration(300);
		mMovingCircleFrame.startAnimation(an);
		
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
			AlphaAnimation fadeOut = new AlphaAnimation(1,0);
			fadeOut.setDuration(200);
			fadeOut.setFillAfter(true);
			oldViews.getMarkPointTextView().startAnimation(fadeOut);
		}
		
		
		AlphaAnimation fadeIn = new AlphaAnimation(0,1);
		fadeIn.setDuration(300);
		fadeIn.setFillAfter(true);
		views.getMarkPointTextView().startAnimation(fadeIn);
	}
	

	/**
	 * 根据当前是第几个图片，滑动时间轴位置
	 * 
	 * @param toIndex
	 */
	public void setLinePos(int toIndex) {
		
		int maxScroll = mVScrollExtraHead.getLayoutParams().width+mVScrollExtraTail.getLayoutParams().width;//因为线条长度等于屏幕宽度
		int lineWidth =  mIvTimeLine.getWidth();
		Log.d(TAG, "index=" + toIndex);
		int posX = mMarkPointList.get(toIndex).getxInParent();// 记录的x位置
		posX = posX - mContext.getResources().getDimensionPixelSize(R.dimen.main_line_padding_left_right);// 滑到中间
		int scrollX = (int) (posX*1.0/lineWidth*maxScroll);
		mHsvLineScrollView.smoothScrollTo(scrollX, 0);
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
		setCurrentMarkPoint(currentIndex);
		//setCurrentMarkPoint(mCurrentItemIndex);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	class MarkPointData {
		public int xInParent;
		public Drawable markPointDrawable;
		public Date markPointTime;
		public String url;
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

	}

	class MarkPointViews {

		public View markPointCircle;
		public TextView markPointTextView;
		public ImageView markPointImageView;

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
			ImageView imageView = mMarkPointViewMap.get(data).getMarkPointImageView();

		    DisplayImageOptions options = getDisplayImageOptions(); 
		    
			mImageLoader.displayImage(data.getUrl(), imageView, options);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);

			imageView.setLayoutParams(lp);
			
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
	
	private DisplayImageOptions getDisplayImageOptions(){
		return new DisplayImageOptions.Builder()  
	     //.showImageOnLoading(R.drawable.ic_launcher) //设置图片在下载期间显示的图片  
	     .showImageForEmptyUri(R.drawable.ic_launcher)//设置图片Uri为空或是错误的时候显示的图片  
	    .showImageOnFail(R.drawable.ic_launcher)  //设置图片加载/解码过程中错误时候显示的图片
	    .cacheInMemory(true)//设置下载的图片是否缓存在内存中  
	    .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中  
	    .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
	    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示  
	    .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//  
//	    .decodingOptions(
//	    		decodingOptions)//设置图片的解码配置  
	    //.delayBeforeLoading(int delayInMillis)//int delayInMillis为你设置的下载前的延迟时间
	    //设置图片加入缓存前，对bitmap进行设置  
	    .preProcessor( new BitmapProcessor(){

			@Override
			public Bitmap process(Bitmap bitmap) {
				int pxTriHeight =ScreenUtil.dip2px(mContext, 4);
				int roundPx = pxTriHeight;
				if(bitmap.getWidth()<pxTriHeight){
					pxTriHeight=0;
					roundPx=0;
				}
				return ImageUtils.getRoundedCornerWithTrigBitmap(
						bitmap, 
						roundPx, 
						ImageUtils.TRI_TOP, 
						bitmap.getWidth()/2,
						pxTriHeight);
			}
	    	
	    })  
	    .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位  
	    .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少  
	    .displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间  
	    .build();//构建完成  
	}

	/**
	 * ImageLoader 图片组件初始化
	 * 
	 * @param context
	 */
	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove
									// for
									// release
									// app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public int getCurrentItemIndex() {
		return mCurrentItemIndex;
	}

}
