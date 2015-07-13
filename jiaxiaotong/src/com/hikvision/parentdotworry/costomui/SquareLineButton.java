package com.hikvision.parentdotworry.costomui;

import java.util.Date;

import com.hikvision.parentdotworry.R;
import com.hikvision.parentdotworry.consts.AppConst;
import com.hikvision.parentdotworry.utils.DateUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SquareLineButton extends RelativeLayout {
	private static final String TAG = "SquareLineButton";
	private Button mBtButton;
	private ImageView mIvTopLine;
	private TextView mTvTime;

	@SuppressLint("NewApi")
	public SquareLineButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SquareLineButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(
				R.layout.square_line_button, this);
		mBtButton = (Button) relativeLayout
				.findViewById(R.id.bt_student_do_school);
		mIvTopLine = (ImageView) relativeLayout
				.findViewById(R.id.tv_top_v_line);
		mTvTime = (TextView) relativeLayout
				.findViewById(R.id.tv_square_button_time);
	}

	public SquareLineButton(Context context) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout linearLayout = (LinearLayout) inflater.inflate(
				R.layout.square_line_button, this);
		mBtButton = (Button) linearLayout
				.findViewById(R.id.bt_student_do_school);
		mIvTopLine = (ImageView) linearLayout.findViewById(R.id.tv_top_v_line);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);

		int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
		int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
		
		
		Log.d(TAG, "sizeWidth=" + sizeWidth + ";sizeHeight=" + sizeHeight);
	}

	public void setText(String text) {
		mBtButton.setText(text);
	}

	public void setText(int resid) {
		mBtButton.setText(resid);
	}

	public void setText(String text, View.OnClickListener onClickListener) {
		mBtButton.setText(text);
		mBtButton.setOnClickListener(onClickListener);
	}

	public void setText(int resid, View.OnClickListener onClickListener) {
		mBtButton.setText(resid);
		mBtButton.setOnClickListener(onClickListener);
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		mBtButton.setOnClickListener(l);
	}

	public void setTime(Date date) {
		mTvTime.setText(DateUtil.dateToString(date,
				AppConst.PATTERN_TIME_WITH_AM));
	}

	/**
	 * 设置顶端线的长度
	 * 
	 * @param height
	 */
	public void setTopLineHeight(int height) {
		mIvTopLine.setLayoutParams(setTopLineHeightParams(height));
	}

	/**
	 * 设置顶端线的长度
	 * 
	 * @param height
	 */
	public RelativeLayout.LayoutParams setTopLineHeightParams(int height) {
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mIvTopLine
				.getLayoutParams();
		lp.height = height;
		return lp;
	}

}
