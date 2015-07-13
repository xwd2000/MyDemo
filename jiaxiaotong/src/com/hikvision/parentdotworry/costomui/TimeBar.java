package com.hikvision.parentdotworry.costomui;

import java.util.Date;

import com.hikvision.parentdotworry.R;
import com.hikvision.parentdotworry.utils.DateUtil;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class TimeBar extends RelativeLayout {
	private final String TAG = "TimeBar";

	private SquareLineButton mSlbButtonEnter;
	private SquareLineButton mSlbButtonLeave;
	private SquareClock mSquareClock;

	// private Date schoolEnterTime;
	// private Date schoolOutTime;
	//
	private Date mEnterTime;
	private Date mLeaveTime;

	public TimeBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	public TimeBar(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(
				R.layout.time_bar, this);
		mSlbButtonEnter = (SquareLineButton) relativeLayout
				.findViewById(R.id.slb_enter_button);
		mSlbButtonLeave = (SquareLineButton) relativeLayout
				.findViewById(R.id.slb_out_button);
		mSquareClock = (SquareClock) relativeLayout
				.findViewById(R.id.sc_bar_clock);

	}

	public TimeBar(Context context) {
		super(context);
	}

	public void init(Date schoolEnterTime, Date schoolOutTime) {
		mSquareClock.initSchoolEnterTime(schoolEnterTime);
		mSquareClock.initSchoolOutTime(schoolOutTime);

	}

	public void setSchoolEnterTime(Date time) {
		mSquareClock.changeSchoolEnterTime(time);
	}

	public void setSchoolOutTime(Date time) {
		mSquareClock.changeSchoolOutTime(time);
	}

	/**
	 * 显示进入按钮
	 * 
	 * @param time
	 */
	public void showEnterButton(Date time) {
		mEnterTime = time;
		mSlbButtonEnter.setTime(time);
		mSlbButtonEnter.setText(R.string.main_student_enter_school);
		addButton(mSlbButtonEnter, time);
	}

	/**
	 * 显示进入按钮
	 * 
	 * @param time
	 *            进入的时间
	 * @param onClickListener
	 */
	public void showEnterButton(Date time, View.OnClickListener onClickListener) {
		mEnterTime = time;
		mSlbButtonEnter.setTime(time);
		mSlbButtonEnter.setText(R.string.main_student_enter_school);
		mSlbButtonEnter.setOnClickListener(onClickListener);
		addButton(mSlbButtonEnter, time);

	}

	/**
	 * 显示出门按钮
	 * 
	 * @param time
	 */
	public void showLeaveButton(Date time) {
		mLeaveTime = time;
		mSlbButtonLeave.setTime(time);
		mSlbButtonLeave.setText(R.string.main_student_leave_school);
		addButton(mSlbButtonLeave, time);
	}

	/**
	 * 显示出门按钮
	 * 
	 * @param time
	 */
	public void showLeaveButton(Date time, View.OnClickListener onClickListener) {
		mLeaveTime = time;
		mSlbButtonLeave.setTime(time);
		mSlbButtonLeave.setText(R.string.main_student_leave_school);
		mSlbButtonLeave.setOnClickListener(onClickListener);
		addButton(mSlbButtonLeave, time);
	}

	/**
	 * 隐藏进入按钮
	 * 
	 */
	public void hideEnterButton() {
		hideButton(mSlbButtonEnter);
		mEnterTime = null;
	}

	/**
	 * 隐藏出门按钮
	 * 
	 */
	public void hideLeaveButton() {
		hideButton(mSlbButtonLeave);
		mLeaveTime = null;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		/**
		 * 获得此ViewGroup上级容器为其推荐的宽和高，以及计算模式 /** Measure specification mode: The
		 * parent has not imposed any constraint on the child. It can be
		 * whatever size it wants. public static final int UNSPECIFIED = 0 <<
		 * MODE_SHIFT; Measure specification mode: The parent has determined an
		 * exact size for the child. The child is going to be given those bounds
		 * regardless of how big it wants to be. public static final int EXACTLY
		 * = 1 << MODE_SHIFT; Measure specification mode: The child can be as
		 * large as it wants up to the specified size. public static final int
		 * AT_MOST = 2 << MODE_SHIFT;
		 */
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);

		int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
		int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
		Log.i(TAG, "sizeWidth=" + sizeWidth + ";sizeHeight=" + sizeHeight);

		int buttonWidth = getExtraWidthSpaceToSquareClock();

		measureChild(mSquareClock, MeasureSpec.makeMeasureSpec(sizeWidth - 2
				* buttonWidth, widthMode), MeasureSpec.makeMeasureSpec(
				sizeHeight / 5 * 2, heightMode));

		if (mEnterTime != null) {
			int marginLeft = mSquareClock.getSquareTimeMarginLeft(mEnterTime);
			LayoutParams lpEnterBt = (LayoutParams) mSlbButtonEnter
					.getLayoutParams();
			lpEnterBt.setMargins(marginLeft, 0, 0, 0);
			mSlbButtonEnter.setLayoutParams(lpEnterBt);
			mSlbButtonEnter.setTopLineHeightParams((int) (mSquareClock
					.getSquareHeight() / 3.0 * 4));
			mSlbButtonEnter.setVisibility(View.VISIBLE);
			measureChild(mSlbButtonEnter, widthMeasureSpec, heightMeasureSpec);
		}
		if (mLeaveTime != null) {
			int marginLeft = mSquareClock.getSquareTimeMarginLeft(mLeaveTime);
			LayoutParams lpEnterBt = (LayoutParams) mSlbButtonLeave
					.getLayoutParams();
			lpEnterBt.setMargins(marginLeft, 0, 0, 0);
			mSlbButtonLeave.setLayoutParams(lpEnterBt);
			mSlbButtonLeave.setTopLineHeightParams((int) (mSquareClock
					.getSquareHeight() / 3.0 * 4));
			mSlbButtonLeave.setVisibility(View.VISIBLE);
			measureChild(mSlbButtonLeave, widthMeasureSpec, heightMeasureSpec);
		}

		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int extraWidthSpaceToSquareClock = getExtraWidthSpaceToSquareClock();

		LayoutParams lp = (LayoutParams) mSquareClock.getLayoutParams();

		lp.setMargins(extraWidthSpaceToSquareClock, 0, 0, 0);

		Log.i(TAG, "(changed, l, t, r, b)=" + "(" + changed + "," + l + "," + t
				+ "," + r + "," + b + ")");
		super.onLayout(changed, l, t, r, b);
	}

	/**
	 * 获取外边框到SquareClock子View的距离
	 * 
	 * @return
	 */
	private int getExtraWidthSpaceToSquareClock() {
		int timerBarWidthExtraSpace = (mSlbButtonEnter.getMeasuredWidth() > mSlbButtonLeave
				.getMeasuredWidth()) ? mSlbButtonEnter.getMeasuredWidth()
				: mSlbButtonLeave.getMeasuredWidth();
		return timerBarWidthExtraSpace / 2;
	}

	/**
	 * 获取外边框到SquareClock子View的边框的距离
	 * 
	 * @return
	 */
	private int getExtraWidthSpaceToSquareClockSide() {
		int timerBarWidthExtraSpace = (mSlbButtonEnter.getMeasuredWidth() > mSlbButtonLeave
				.getMeasuredWidth()) ? mSlbButtonEnter.getMeasuredWidth()
				: mSlbButtonLeave.getMeasuredWidth();

		return timerBarWidthExtraSpace / 2 + mSquareClock.getSquareMarginLeft();
	}

	private void addButton(SquareLineButton button, Date time) {
		int marginLeft = mSquareClock.getSquareTimeMarginLeft(time);
		LayoutParams lpEnterBt = (LayoutParams) button.getLayoutParams();
		lpEnterBt.setMargins(marginLeft, 0, 0, 0);
		button.setLayoutParams(lpEnterBt);
		button.setTopLineHeight((int) (mSquareClock.getSquareHeight() / 3.0 * 4));
		Log.d(TAG, (int) (mSquareClock.getSquareHeight() / 3.0 * 4) + "");
		button.setVisibility(View.VISIBLE);
	}

	private void hideButton(SquareLineButton button) {
		button.setVisibility(View.GONE);
	}

}
