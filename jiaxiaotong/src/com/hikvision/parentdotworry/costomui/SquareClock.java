package com.hikvision.parentdotworry.costomui;

import java.util.Date;

import com.hikvision.parentdotworry.utils.DateUtil;
import com.hikvision.parentdotworry.utils.ScreenUtil;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class SquareClock extends View {
	// ===========================================================
	// Constants
	// ===========================================================
	
	// ===========================================================
	// Fields
	// ===========================================================
	private final String TAG = "SquareClock";
	
	//时间刻度三角形的底角
	private final float mTriangleAngle = (float) (45*Math.PI/180);
	
	private Paint mPaint;
	
	
	//整一条时间线显示的时间段
	
	private Date mSchoolEnterTime;
	private Date mSchoolLeaveTime;

	
	private int mSquareWidth = 0;
	private int mSquareHeight = 0;
	
	private int mTriangleSideLength=0;
	//线条默认1dp
	private int mLineWidth = ScreenUtil.dip2px(getContext(), 1f);
	// ===========================================================
	// Constructors
	// ===========================================================

	public SquareClock(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SquareClock(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		int darkerGray = getResources().getColor(android.R.color.darker_gray);
		mPaint.setColor(darkerGray);
		mPaint.setStrokeWidth(ScreenUtil.dip2px(getContext(), 1));
	}

	public SquareClock(Context context) {
		super(context);
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		int darkerGray = getResources().getColor(android.R.color.darker_gray);
		mPaint.setColor(darkerGray);
		mPaint.setStrokeWidth(ScreenUtil.dip2px(getContext(), 1));
	}

	
	
	
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mTriangleSideLength = getMeasuredWidth()/30;
		
		int halfLineWidth = mLineWidth / 2;// 可画区域最高点(边框为1dp)

		int width = getMeasuredWidth() - halfLineWidth;// 考虑到边框大小，实际可画区域-0.5dp(边框为1dp)
		int height = getMeasuredHeight() - halfLineWidth;

		
		Log.d(TAG,"SquareClockWidth="+width+"height="+height);
		
		double angle = 45 / 180.0 * Math.PI; // 底角大小
		int triangleSideLength = getTriangleSideLength();// 假设三角形斜边长为宽度除以30
		int triangleBottomSideLength = getTriangleBottomSideLength();// 三角形底边长
		
		// 大方块长宽，为了在measure时候就测出所画方块的大小
		mSquareWidth = width - triangleBottomSideLength;
		mSquareHeight = (int) (height - triangleSideLength
				* Math.sin(angle));
	}
	
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		int halfLineWidth = mLineWidth / 2;// 可画区域最高点(边框为1dp)

		int width = getMeasuredWidth() - halfLineWidth;// 考虑到边框大小，实际可画区域-0.5dp(边框为1dp)
		int height = getMeasuredHeight() - halfLineWidth;

		int secondInDay = DateUtil.getSecondInDay(new Date()); // 当前的时间点

		double angle = 45 / 180.0 * Math.PI; // 底角大小
		int triangleSideLength = getTriangleSideLength();// 假设三角形斜边长为宽度除以30
		int triangleBottomSideLength = getTriangleBottomSideLength();// 三角形底边长
		
		int squareMarginLift=getSquareMarginLeft();//方框离左边框位置

		// 大方块长宽
		mSquareWidth = width - triangleBottomSideLength;
		mSquareHeight = (int) (height - triangleSideLength
				* Math.sin(angle));

		mPaint.setStyle(Paint.Style.STROKE);
		// 绘制白色区域的 边框
		canvas.drawRect(triangleBottomSideLength / 2, halfLineWidth,
				triangleBottomSideLength / 2 + mSquareWidth, mSquareHeight, mPaint);

		if (mSchoolEnterTime != null && mSchoolLeaveTime != null) {
			mPaint.setStyle(Paint.Style.FILL);
			// 绘制灰色部分
			canvas.drawRect(calcMarginLeftByTime(mSquareWidth,squareMarginLift,mSchoolEnterTime)
					, halfLineWidth,
					calcMarginLeftByTime(mSquareWidth,squareMarginLift,mSchoolLeaveTime)
					, mSquareHeight,
					mPaint);
			//Log.d(TAG,"l"+calcMarginLeftByTime(squareWidth,squareMarginLift,schoolEnterTime)+";t"+halfLineWidth);
			//Log.d(TAG,"r"+calcMarginLeftByTime(squareWidth,squareMarginLift,schoolOutTime)+";b"+squareHeight);
		}

		float[] pts = new float[12];// 三角形描边的点
		pts[0] = mSquareWidth * secondInDay / 24f / 3600
				+ triangleBottomSideLength / 2f;
		pts[1] = mSquareHeight;
		pts[2] = (float) (pts[0] - triangleSideLength * Math.cos(angle));
		pts[3] = (float) (pts[1] + triangleSideLength * Math.sin(angle));

		pts[4] = pts[2];
		pts[5] = pts[3];
		pts[6] = (float) (pts[2] + 2 * triangleSideLength * Math.cos(angle));
		pts[7] = pts[3];

		pts[8] = pts[6];
		pts[9] = pts[7];
		pts[10] = pts[0];
		pts[11] = pts[1];
		canvas.drawLines(pts, mPaint);
		postInvalidateDelayed(1000, (int) pts[2] - halfLineWidth, (int) pts[1]
				+ halfLineWidth, (int) pts[6] + halfLineWidth, (int) pts[7]
				+ halfLineWidth);
	}

	// ===========================================================
	// Methods
	// ===========================================================
	/**
	 * 设置上学时间
	 * 
	 * @param time
	 */
	public void initSchoolEnterTime(Date time) {
		mSchoolEnterTime = time;
	}

	/**
	 * 设置放学时间
	 * 
	 * @param time
	 */
	public void initSchoolOutTime(Date time) {
		mSchoolLeaveTime = time;
	}
	/**
	 * 设置上学时间
	 * 
	 * @param time
	 */
	public void changeSchoolEnterTime(Date time) {
		mSchoolEnterTime = time;
		invalidate();
	}

	/**
	 * 设置放学时间
	 * 
	 * @param time
	 */
	public void changeSchoolOutTime(Date time) {
		mSchoolLeaveTime = time;
		invalidate();
	}
	/**
	 * 设置上学和放学时间
	 * 
	 * @param timeEnter
	 * @param timeOut
	 */
	public void changeSchoolTime(Date timeEnter, Date timeOut) {
		mSchoolEnterTime = timeEnter;
		mSchoolLeaveTime = timeOut;
		invalidate();
	}
	
	

	/**
	 * 获取时间刻度三角形的斜边长(px)
	 */
	public int getTriangleSideLength(){
		return mTriangleSideLength;
	}
	
	/**
	 * 获取时间刻度三角形的底边长(px)
	 */
	public int getTriangleBottomSideLength(){
		return (int) (2.0 * getTriangleSideLength() * Math
				.cos(mTriangleAngle));
	}
	
	/**
	 * 获取view边框到绘制的方形时间区域的距离(px)
	 * @return
	 */
	public int getSquareMarginLeft(){
		return getTriangleBottomSideLength()/2;
	}
	
	/**
	 * 获取view边框到绘制的方形时间的指定时间位置的距离(px)
	 * @return
	 */
	public int getSquareTimeMarginLeft(Date date){
		return calcMarginLeftByTime(mSquareWidth,getSquareMarginLeft(),date);
	}
	
	
	private int calcMarginLeftByTime(int squareWidth,int extraSpaceWidth,Date date){
		/*Log.d(TAG,"时间="+DateUtil.dateToString(date, "mm:ss")+
				";extraSpaceWidth="+extraSpaceWidth+
				";squareWidth="+squareWidth+
				";DateUtil.getSecondInDay(date)="+DateUtil.getSecondInDay(date)+
				";re="+(int) (extraSpaceWidth + squareWidth
						* DateUtil.getSecondInDay(date) / 24f / 3600));
		*/
		
		return (int) (extraSpaceWidth + squareWidth
		* DateUtil.getSecondInDay(date) / 24f / 3600);
	}
	
	/**
	 * 计算两个时间相减的秒数,注意，之计算时间相减，不计算天数相减
	 * @param start 开始时间
	 * @param end 结束时间
	 * @return
	 */
	private int calcSecond(Date start,Date end){
		return DateUtil.getSecondInDay(end)- DateUtil.getSecondInDay(start);
		
	}
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public int getSquareWidth() {
		return mSquareWidth;
	}


	public int getSquareHeight() {
		return mSquareHeight;
	}





}
