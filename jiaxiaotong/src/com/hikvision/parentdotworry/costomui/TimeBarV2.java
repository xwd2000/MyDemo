package com.hikvision.parentdotworry.costomui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.util.Asserts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hikvision.parentdotworry.R;
import com.hikvision.parentdotworry.utils.DateUtil;
import com.hikvision.parentdotworry.utils.ImageUtils;

public class TimeBarV2 extends LinearLayout {

	// ===========================================================
	// Constants
	// ===========================================================


	// ===========================================================
	// Fields
	// ===========================================================
	private ImageView mIvTimeLine;

	private RelativeLayout mRlLineContainer;
	private RelativeLayout mRlTimePicContainer;
	private RelativeLayout mRlTimeTextContainer;

	private List<MarkPointData> mMarkPointList;

	private List<Period> mPeriodList;

	private Period mLimitTime;
	private Context mContext;

	// ===========================================================
	// Constructors
	// ===========================================================

	public TimeBarV2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public TimeBarV2(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);

	}

	public TimeBarV2(Context context) {
		super(context);
		init(context);
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
	}

	// ===========================================================
	// Methods
	// ===========================================================
	public void init(Context context) {
		this.mContext = context;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout linearLayout = (LinearLayout) inflater.inflate(
				R.layout.time_bar_v2, this);
		mIvTimeLine = (ImageView) linearLayout
				.findViewById(R.id.iv_main_time_line);


		mRlLineContainer = (RelativeLayout) findViewById(R.id.rl_line_container);
		mRlTimePicContainer = (RelativeLayout) findViewById(R.id.rl_time_pic_container);
		mRlTimeTextContainer = (RelativeLayout) findViewById(R.id.rl_time_text_container);
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
		final Period newPeriod = new Period();
		newPeriod.setEnd(end);
		newPeriod.setStart(start);
		if (mPeriodList == null)
			mPeriodList = new ArrayList<Period>();
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
		lp.setMargins(lpSrc.leftMargin, lpSrc.topMargin, 0,
				0);
		lp.leftMargin = getXPosition(p.getStart());
		lp.width = getXPosition(p.getEnd()) - getXPosition(p.getStart());
		ImageView periodView = new ImageView(mContext);
		periodView.setImageResource(R.color.main_status_green);
		mRlLineContainer.addView(periodView,lp);
	};

	public void addDefaultMarkPoint(final Date time) {

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
							addMarkPointView(time);
							//TODO 这个函数15以下不可用
							mIvTimeLine.getViewTreeObserver().removeOnGlobalLayoutListener(this);
						}
					}
				});
		requestLayout();
	}

	public void addMarkPointView(Date time) {
		MarkPointData mpd = new MarkPointData();
		mpd.setxInParent(getXPosition(time));
		mpd.setMarkPointTime(time);
		mpd.setMarkPointDrawable(mContext.getResources().getDrawable(
				R.drawable.picture_box));
				if(mMarkPointList==null){
				mMarkPointList=new ArrayList<MarkPointData>();
				}
				mMarkPointList.add(mpd);
		addMarkPointView(mpd);
	}

	public void addMarkPointView(MarkPointData data) {
		
		//添加图片
		ImageView iv=new ImageView(mContext);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		iv.setImageDrawable(data.getMarkPointDrawable());
		lp.leftMargin=data.getxInParent()-ImageUtils.drawableToBitmap(data.getMarkPointDrawable()).getWidth()/2;
		mRlTimePicContainer.addView(iv,lp);
	
		///添加默认图片
		RelativeLayout.LayoutParams lpSrc = (RelativeLayout.LayoutParams) mIvTimeLine
				.getLayoutParams();
		ImageView ivCircle=new ImageView(mContext);
		RelativeLayout.LayoutParams lpCircle = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		lpCircle.setMargins(lpSrc.leftMargin, lpSrc.topMargin, 0,
				0);
		Drawable defaultCircle=mContext.getResources().getDrawable(R.drawable.main_tip_circle_small);
		Bitmap bm=ImageUtils.drawableToBitmap(defaultCircle);
		lpCircle.leftMargin=data.getxInParent()-bm.getWidth()/2;
		lpCircle.topMargin=lpSrc.topMargin-bm.getHeight()/2+mIvTimeLine.getHeight()/2;//必须加上线条的一半高度
		ivCircle.setImageResource(R.drawable.main_tip_circle_small);
		mRlLineContainer.addView(ivCircle,lpCircle);
		
		//添加时间文本
		TextView ivTimeShow=new TextView(mContext);
		RelativeLayout.LayoutParams lpTimeShow = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		lpTimeShow.leftMargin=data.getxInParent();
		ivTimeShow.setText(DateUtil.dateToString(data.getMarkPointTime(),"HH:mm"));
		mRlTimeTextContainer.addView(ivTimeShow,-1,lpTimeShow);
	}

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

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	class MarkPointData {
		public int xInParent;
		public Drawable markPointDrawable;
		public Date markPointTime;

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
	// ===========================================================
	// Getter & Setter
	// ===========================================================

}
