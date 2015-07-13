package com.hikvision.parentdotworry.base;

import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;

/**
 * 滑动基类，实现左右滑动时运行回调，
 * 需要实现onFlingLeft，onFlingLeftEnd,onFlingRight,onFlingRightEnd
 * @author xuweidong
 */
public abstract class BaseFlingActivity extends BaseActivity{
	// 侧滑到设置页面的铭感度 1-10，越低越敏感
	private static final int FLING_SENSITIVITY = 4;
	
	// 速度跟踪
	private VelocityTracker mVelocityTracker;

	// 滑动进入设置时判断是否已经触发startActivity,防止多次启动设置界面
	private boolean goingToSettingFlag = false;
	
	/**
	 * 左滑时运行(只会运行一次)
	 */
	public abstract void onFlingLeft();
	/**
	 * 右滑时运行(只会运行一次)
	 */
	public abstract void onFlingRight();
	/**
	 * 左滑后运行(手指松开后运行)
	 */
	public abstract void onFlingLeftEnd();
	/**
	 * 右滑后运行(手指松开后运行)
	 */
	public abstract void onFlingRightEnd();
	
	
	
	@Override
	protected void onStart() {
		// 初始化滑入设置页面的标记
		resetGoingToSettingFlag();
		super.onStart();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// getWindow().getDecorView().setOnTouchListener(l)

		final int action = event.getAction();
		// 实现左滑进入我的信息界面
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			//logi("onTouchEvent  ACTION_DOWN");
			acquireVelocityTrackerIfNotExist(); // 获取速度跟踪对象

			addMovement(event);// 添加event给VelocityTracker处理
			break;

		case MotionEvent.ACTION_MOVE:
			Point size = new Point();
			getWindowManager().getDefaultDisplay().getSize(size);
			int screenWidth = size.x; // 屏幕宽（像素，如：480px）
			//logd("screenWidth=" + screenWidth);
			addMovement(event);// 添加event给VelocityTracker处理

			if ((-getVelocityX()) > FLING_SENSITIVITY * screenWidth
					&& !getGoingToSettingFlag()
					&&Math.abs(getVelocityY())-Math.abs(getVelocityX())<0) {
				
				// 置flag
				markGoingToSettingFlag();
				
				onFlingLeft();
			}else if(getVelocityX()> FLING_SENSITIVITY * screenWidth
					&& !getGoingToSettingFlag()
					&&Math.abs(getVelocityY())-Math.abs(getVelocityX())<0){
				// 置flag
				markGoingToSettingFlag();
				
				onFlingRight();
			}
			break;

		case MotionEvent.ACTION_UP:
			Point size1 = new Point();
			getWindowManager().getDefaultDisplay().getSize(size1);
			int screenWidth1 = size1.x; // 屏幕宽（像素，如：480px）
			addMovement(event);// 添加event给VelocityTracker处理

			if ((-getVelocityX()) > FLING_SENSITIVITY * screenWidth1
					&& !getGoingToSettingFlag()) {
				
				// 置flag
				markGoingToSettingFlag();
				
				onFlingLeftEnd();
			}else if(getVelocityX()> FLING_SENSITIVITY * screenWidth1
					&& !getGoingToSettingFlag()){
				// 置flag
				markGoingToSettingFlag();
				
				onFlingRightEnd();
			}
			resetGoingToSettingFlag();
			break;
		}

		return super.onTouchEvent(event);
	};
	
	
	@Override
	protected void onStop() {
		// 释放滑动计算对象
		releaseVelocityTracker();
		super.onStop();
	}
	/**
	 * 
	 * @param event
	 *            向VelocityTracker添加MotionEvent
	 * @see android.view.VelocityTracker#obtain()
	 * @see android.view.VelocityTracker#addMovement(MotionEvent)
	 */
	public VelocityTracker acquireVelocityTrackerIfNotExist() {

		if (null == mVelocityTracker) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		return mVelocityTracker;
	}

	/**
	 * 释放VelocityTracker
	 * 
	 * @see android.view.VelocityTracker#clear()
	 * @see android.view.VelocityTracker#recycle()
	 */
	private void releaseVelocityTracker() {
		if (null != mVelocityTracker) {
			mVelocityTracker.clear();
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
	}

	private int getVelocityX() {
		if (null != mVelocityTracker) {
			mVelocityTracker.computeCurrentVelocity(1000);
			return (int) mVelocityTracker.getXVelocity();
		}
		return 0;
	}
	private int getVelocityY() {
		if (null != mVelocityTracker) {
			mVelocityTracker.computeCurrentVelocity(1000);
			return (int) mVelocityTracker.getYVelocity();
		}
		return 0;
	}

	private void addMovement(MotionEvent event) {
		if (null != mVelocityTracker) {
			mVelocityTracker.addMovement(event);
		}
	}
	
	
	private void resetGoingToSettingFlag() {
		goingToSettingFlag = false;
	}

	/**
	 * 设置后，表示正在滑动
	 */
	private void markGoingToSettingFlag() {
		goingToSettingFlag = true;
	}

	
	private boolean getGoingToSettingFlag() {
		return goingToSettingFlag;
	}
}
