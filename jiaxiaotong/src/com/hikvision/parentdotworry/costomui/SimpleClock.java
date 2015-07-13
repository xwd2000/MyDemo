package com.hikvision.parentdotworry.costomui;

import java.util.Calendar;
import java.util.Date;

import com.hikvision.parentdotworry.R;
import com.hikvision.parentdotworry.utils.DateUtil;
import com.hikvision.parentdotworry.utils.ScreenUtil;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class SimpleClock extends View {
	private final String TAG = "SimpleClock";
	private final float mRatioMinHandToEdage = 6.0f / 7;
	private final float mRatioHourHandToMinHand = 6.0f / 7;

	private Paint mPaint;
	private float mHourHandLengthPix; // 单位 px
	private float mMinuteHandLengthPix; // 单位 px

	private float mViewWidth;
	
	//指针颜色
	private int mHandColor;

	public SimpleClock(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SimpleClock(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.SimpleClock);
		mMinuteHandLengthPix = ScreenUtil.dip2px(getContext(),
				ta.getDimension(R.styleable.SimpleClock_minute_hand_length, 0));
		if (mMinuteHandLengthPix == 0) {
			mHourHandLengthPix = 0;
		} else {
			// 根据比率计算出分针长度
			mHourHandLengthPix = mMinuteHandLengthPix * mRatioHourHandToMinHand;
		}
		mHandColor = ta.getColor(R.styleable.SimpleClock_hand_color, Color.BLACK);// 默认黑色
		ta.recycle();
	}

	public SimpleClock(Context context) {
		super(context);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthTmp = MeasureSpec.getSize(widthMeasureSpec);
		int heightTmp = MeasureSpec.getSize(heightMeasureSpec);

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		
		mViewWidth = widthTmp > heightTmp ? heightTmp : widthTmp;
		setMeasuredDimension(MeasureSpec.makeMeasureSpec(
				(int) Math.floor(mViewWidth), widthMode),
				MeasureSpec.makeMeasureSpec((int) Math.floor(mViewWidth),
						heightMode)); // 这里面是原始的大小，需要重新计算可以修改本行
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if(this.mPaint==null){
			return;
		}
		int measuredWidth=getMeasuredWidth();
		int measuredHeight=getMeasuredHeight();
		mViewWidth=measuredHeight>measuredWidth?measuredWidth:measuredHeight;
		
		if (mMinuteHandLengthPix == 0) {
			mMinuteHandLengthPix = mViewWidth / 2 * mRatioMinHandToEdage;
			mHourHandLengthPix = mMinuteHandLengthPix * mRatioHourHandToMinHand;
		}
		// 绘制内圆
		this.mPaint.setARGB(155, 167, 190, 206);
		this.mPaint.setStrokeWidth(0);

		canvas.drawCircle(measuredWidth / 2, measuredHeight / 2, mViewWidth / 2,
				this.mPaint);
		
		
		
		Calendar c=DateUtil.getCalendar();
		int hour = DateUtil.getHour(c);
		int minute = DateUtil.getMinute(c);
		int second = DateUtil.getSecond(c);
		
		//设置黑色
		this.mPaint.setColor(mHandColor);
		//画中心圆
		canvas.drawCircle(measuredWidth / 2, measuredHeight / 2, ScreenUtil.dip2px(getContext(), 4),
				this.mPaint);
		
		// 指针离边框的距离
		float extraSpaceHour = mViewWidth / 2 - mHourHandLengthPix;
		float extraSpaceMinute = (mViewWidth / 2 - mMinuteHandLengthPix);
		
		float xSecond = (float) (extraSpaceMinute + mMinuteHandLengthPix
				* (1 + Math.sin(second / 60.0 * 360 * Math.PI / 180)));
		float ySencond = (float) (extraSpaceMinute + mMinuteHandLengthPix
				* (1 + Math.cos((second / 60.0 * 360 + 180) * Math.PI / 180)));
		
		this.mPaint.setStrokeWidth(ScreenUtil.dip2px(getContext(), 2));
		canvas.drawLine(measuredWidth / 2 , measuredHeight / 2,
				xSecond, ySencond, this.mPaint);

		
		// 分针终点
		this.mPaint.setStrokeWidth(ScreenUtil.dip2px(getContext(), 3));
		float xminute = (float) (extraSpaceMinute + mMinuteHandLengthPix
				* (1 + Math.sin(minute / 60.0 * 360 * Math.PI / 180)));
		float yminute = (float) (extraSpaceMinute + mMinuteHandLengthPix
				* (1 + Math.cos((minute / 60.0 * 360 + 180) * Math.PI / 180)));
		
		this.mPaint.setStrokeWidth(ScreenUtil.dip2px(getContext(), 3));
		canvas.drawLine(measuredWidth / 2, measuredHeight / 2,
				xminute, yminute, this.mPaint);

		
		// 时针终点
		float xhour = (float) (extraSpaceHour + mHourHandLengthPix
				* (1 + Math.sin((minute/2.0+hour / 12.0 * 360) * Math.PI / 180)));
		//minute/2.0为分对应始终走的度数
		float yhour = (float) (extraSpaceHour + mHourHandLengthPix
				* (1 + Math.cos((minute/2.0+hour / 12.0 * 360 + 180) * Math.PI / 180)));

		this.mPaint.setStrokeWidth(ScreenUtil.dip2px(getContext(), 4));
		canvas.drawLine(measuredWidth / 2 , measuredHeight / 2,
				xhour, yhour, this.mPaint);
		postInvalidateDelayed(500);
		
		/*Log.d(TAG,"measuredWidth="+measuredWidth+";measuredHeight="+measuredHeight+";viewWidth="+viewWidth+
				";minuteHandLengthPix="+minuteHandLengthPix+";hourHandLengthPix="+hourHandLengthPix+";xminute="+xminute+";yminute="+yminute);
		*/
		super.onDraw(canvas);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		
		super.onLayout(changed, left, top, right, bottom);
	}

}
