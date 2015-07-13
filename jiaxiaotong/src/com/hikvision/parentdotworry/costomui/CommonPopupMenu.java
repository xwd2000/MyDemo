package com.hikvision.parentdotworry.costomui;

import java.util.ArrayList;
import java.util.List;

import com.hikvision.parentdotworry.MainActivity;
import com.hikvision.parentdotworry.R;
import com.hikvision.parentdotworry.utils.ScreenUtil;
import com.hikvision.parentdotworry.utils.ScreenUtil;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CommonPopupMenu extends PopupWindow implements
		android.view.View.OnClickListener {
	// 弹出的menu的宽度，单位dp
	private int mMenuWidth = 105;
	// 每个item的padding，单位dp
	private int mPaddingV = 10;
	private int mPaddingH = 16;

	private int mMenuItemBackgroundId;

	private List<View> mItemList = new ArrayList<View>();
	private Context mContext;
	private LinearLayout mLlContainer;

	private LinearLayout mLltransBackground;
	
	private OnMenuItemClickListener mOnMenuItemClickListener;

	/**
	 * 文本颜色
	 */
	private int mTextColor = Color.BLACK;
	
	public CommonPopupMenu(Context context) {
		super(context);
		this.mContext = context;
		init();

	}

	public void init() {
		
		
		mLlContainer = new LinearLayout(mContext);
		mLlContainer.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mLlContainer.setLayoutParams(params);

		mLltransBackground = new LinearLayout(mContext);
		mLltransBackground.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams paramsBackground = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mLltransBackground.setLayoutParams(params);
		
		
		this.setContentView(mLlContainer);
		this.setWidth(android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		this.setHeight(android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		this.setAnimationStyle(R.style.menu_animation);
	}

	public void addItem(View view) {
		mItemList.add(view);
		mLlContainer.addView(view);
	}

	public void addItem(String text) {
		addItem(text,null);
	}
	
	public void addItem(String text,Object tag) {
		TextView textItem = new TextView(mContext);
		textItem.setText(text);
		LayoutParams params = new LayoutParams(ScreenUtil.dip2px(mContext,
				mMenuWidth), LayoutParams.WRAP_CONTENT);
		textItem.setLayoutParams(params);
		if (mMenuItemBackgroundId != 0) {
			textItem.setBackgroundResource(mMenuItemBackgroundId);
		}
		textItem.setPadding(ScreenUtil.dip2px(mContext, mPaddingH),
				ScreenUtil.dip2px(mContext, mPaddingV),
				ScreenUtil.dip2px(mContext, mPaddingH),
				ScreenUtil.dip2px(mContext, mPaddingV));
		textItem.setTag(tag);
		textItem.setOnClickListener(this);
		textItem.setFocusable(true);
		textItem.setTextColor(mTextColor);
		addItem(textItem);
	}

	public void clearItem() {
		mLlContainer.removeAllViews();
		mItemList.clear();
	}

	@Override
	public void onClick(View v) {
		int clickIndex = 0;
		for (int i = 0; i < mItemList.size(); i++) {
			if (mItemList.get(i) == v) {
				clickIndex = i;
				break;
			}
		}
		if (mOnMenuItemClickListener != null) {
			mOnMenuItemClickListener.OnItemClick(clickIndex,v, v.getTag());
			this.dismiss();
		}
	}

	public void setOnItemClickListener(
			OnMenuItemClickListener onMenuItemClickListener) {
		this.mOnMenuItemClickListener = onMenuItemClickListener;
	}

	public void setMenuBackground(Drawable menuBackground) {
		setBackgroundDrawable(null);
		mLlContainer.setBackgroundDrawable(menuBackground);
	}
	
	/**
	 * 设置文本颜色
	 * @param color
	 */
	public void setTextColor(int color){
		if(color!=this.mTextColor){
			this.mTextColor = color;
			mLlContainer.requestLayout();
			mLlContainer.invalidate();
		}
	}

	/**
	 * 单位 dip
	 * 
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 */
	public void setMenuPadding(int left, int top, int right, int bottom) {
		mLlContainer.setPadding(
			ScreenUtil.dip2px(mContext, left),
			ScreenUtil.dip2px(mContext, top),
			ScreenUtil.dip2px(mContext, right),
			ScreenUtil.dip2px(mContext, bottom));
	}

	public void setMenuItemBackgroundId(int menuItemBackgroundId) {
		this.mMenuItemBackgroundId = menuItemBackgroundId;
		for (View item : mItemList) {
			item.setBackgroundResource(mMenuItemBackgroundId);
		}
	}

	public interface OnMenuItemClickListener {
		/**
		 * menu的回调
		 * @param index item的索引位置
		 * @param view 点击的view
		 * @param tag view的tag,可以自定义
		 */
		public void OnItemClick(int index,View view,Object tag);
	}

}
