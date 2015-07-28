package com.hikvision.parentdotworry.costomui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hikvision.parentdotworry.utils.EmptyUtil;
import com.hikvision.parentdotworry.utils.ScreenUtil;

public class TabSelectContainer extends HorizontalScrollView {
	private static final int ITEM_WIDTH = LayoutParams.WRAP_CONTENT;// 每个item的宽度，默认单位dp

	private static final int ITEM_MARGIN_LEFT_RIGHT = 20;// 每个item左右外边距，默认单位dp
	private static final int ITEM_MARGIN_TOP = 28;// 每个item外边距，默认单位dp
	private static final int ITEM_MARGIN_BOTTOM = 26;// 每个item外边距，默认单位dp
	private static final int ITEM_TEXT_SIZE = 16;// 每个item字体大小，默认单位dp
	
	private static final int CONTAIN_PADDING_LEFT_RIGHT = 12;// 容器左右内边距，默认单位dp
	private static final int CONTAIN_HEIGHT = 44;// 整体高度，默认单位dp
	
	
	
	private static final String TAG = "TabSelectContainer";

	private LinearLayout mLlContainer;// 放按钮的容器
	private LinearLayout mLlTopContainer;
	private List<View> mTabViewList;
	private OnTabChangedListener mOnTabChangedListener;
	private int mCurrentTabIndex = 0;

	private ImageView mMovingItem;
	private AnimationSet as;
	
	private int mIndicatorPositionX = 0; // 记录滑块的X位置
	private float mIndicatorScale = 1f; // 记录滑块的大小

	private int mIndicatorAndTextColor;
	
	public TabSelectContainer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public TabSelectContainer(Context context, AttributeSet attrs) {
		super(context, attrs);

		mLlTopContainer = new LinearLayout(context);
		LinearLayout.LayoutParams rllp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				ScreenUtil.dip2px(context, CONTAIN_HEIGHT)); 
		mLlTopContainer.setPadding(
				ScreenUtil.dip2px(context, CONTAIN_PADDING_LEFT_RIGHT), 0, 
				ScreenUtil.dip2px(context, CONTAIN_PADDING_LEFT_RIGHT), 0);
		
		mLlTopContainer.setLayoutParams(rllp);
		mLlTopContainer.setOrientation(LinearLayout.VERTICAL);

		mLlContainer = new LinearLayout(context);
		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		mLlContainer.setLayoutParams(p);
		mLlContainer.setOrientation(LinearLayout.HORIZONTAL);

		

		mLlTopContainer.addView(mLlContainer);
		

		addView(mLlTopContainer);

		mTabViewList = new ArrayList<View>();
		
		mLlTopContainer.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					boolean isFirst = true;// 默认调用两次，这里只让它执行一次回调

					@Override
					public void onGlobalLayout() {
						if(isFirst == false ){
							return ;
						}
						isFirst=false;
						if(EmptyUtil.isEmpty(mTabViewList)){
							return;
						}
						int indicatorWidth = mTabViewList.get(0).getWidth();
						mMovingItem = new ImageView(getContext());
						LinearLayout.LayoutParams lpLayout = new LinearLayout.LayoutParams(
								indicatorWidth,
								ScreenUtil.dip2px(getContext(), 4));
						
						mMovingItem.setBackgroundColor(mIndicatorAndTextColor);
						if(!EmptyUtil.isEmpty(mTabViewList)){
							for(int i=0;i<mTabViewList.size();i++){
								View view = mTabViewList.get(i);
								if(mCurrentTabIndex==i){
									setItemTextColor(view,mIndicatorAndTextColor);
								}else{
									setItemTextColor(view,getContext().getResources().getColor(android.R.color.black));
								}
							}
						}
						mMovingItem.setLayoutParams(lpLayout);
						mLlTopContainer.addView(mMovingItem);// 添加滑动框
						
					}
				});
	}

	public TabSelectContainer(Context context) {
		super(context);
	}

	public void addTab(String name, Object tag) {
		Button tabButton = new Button(getContext());
		tabButton.setFocusable(true);
		tabButton.setText(name);
		LayoutParams lp = new LayoutParams(
				ITEM_WIDTH, LayoutParams.WRAP_CONTENT);
		lp.setMargins(
				ScreenUtil.dip2px(getContext(), ITEM_MARGIN_LEFT_RIGHT), 
				ScreenUtil.dip2px(getContext(), ITEM_MARGIN_TOP), 
				ScreenUtil.dip2px(getContext(), ITEM_MARGIN_LEFT_RIGHT), 
				ScreenUtil.dip2px(getContext(), ITEM_MARGIN_BOTTOM) );
		tabButton.setLayoutParams(lp);
		// 支持旧版本
		tabButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, ITEM_TEXT_SIZE);
		tabButton.setMaxLines(1);
		tabButton.setEllipsize(TextUtils.TruncateAt.END);
		tabButton.setBackgroundResource(android.R.color.white);
		tabButton.setTag(tag);// 把位置设置到tag里面
		addTab(tabButton);
	}

	public void addTab(String name) {
		addTab(name, null);
	}

	public void addTab(final View v) {
		
		mLlContainer.addView(v);
		if (EmptyUtil.isEmpty(mTabViewList)) {
			// 第一个view，默认选中
			v.setSelected(true);
			
		}
		mTabViewList.add(v);
		v.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 原选择位置
				int newIndex = mCurrentTabIndex;
				for (int i = 0; i < mTabViewList.size(); i++) {
					if (mTabViewList.get(i) == v) {
						newIndex = i;
						break;
					}
				}

				if (mOnTabChangedListener != null) {
					mOnTabChangedListener.onTabChanged(newIndex, v);
				}
				// setCurrentTab(newIndex);
			}
		});
	}

	public void setCurrentTab(final int index) {
		if (index > mTabViewList.size() - 1) {
			return;
		}
		View oldTabView=mTabViewList.get(mCurrentTabIndex);
		oldTabView.setSelected(false);
		setItemTextColor(oldTabView,getContext().getResources().getColor(android.R.color.black));

		int oldX = mIndicatorPositionX;
		
		View newTabView = mTabViewList.get(index);
		newTabView.setSelected(true);
		setItemTextColor(newTabView,mIndicatorAndTextColor);
		

		
		int totalWidth = mLlContainer.getWidth();
		int currentX = getScrollX();

		int scrollViewWidth = getWidth();// 这里不涉及padding，不然还要减去padding


		// 计算item在总长度中的位置
		mIndicatorPositionX=0;
		for(int i=0;i<index;i++){
			mIndicatorPositionX += mTabViewList.get(i).getWidth();
		}
		
		//mIndicatorPositionX = index * ScreenUtil.dip2px(getContext(), ITEM_WIDTH);

		int deltaX = mIndicatorPositionX - scrollViewWidth / 2 - currentX;
		if (deltaX != 0 && totalWidth> scrollViewWidth) {// tab按钮集需要滑动
			smoothScrollBy(deltaX, 0); // 滑动容器，当按钮的个数比较多，需要滑动scrollview显示按钮
		}
		if(mMovingItem==null){
			//一般初始化还没完成时调用，可直接返回
			return;
		}
		
		if(oldX!=mIndicatorPositionX){
			float[] rota = new float[] { oldX, mIndicatorPositionX, 0, 0 };
			if (as != null) {
				as.cancel();
			}
			
			//Interpolator i = 
			
			
		
			//计算大小变化比例
			float newIndicatorScale=mTabViewList.get(index).getWidth()*1.0f/mTabViewList.get(0).getWidth();
			//newIndicatorScale = (mIndicatorScale==1.5f?1.0f:1.5f);
			Log.d(TAG,"index="+index+"mCurrentTabIndex="+mCurrentTabIndex+"newIndicatorScale="+newIndicatorScale);
			ScaleAnimation mScalAnim = new ScaleAnimation(mIndicatorScale, newIndicatorScale, 1, 1,Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
			
			TranslateAnimation mTrAnim = new TranslateAnimation(rota[0],rota[1],rota[2], rota[3]);
			mIndicatorScale=newIndicatorScale;
			mScalAnim.setInterpolator(new AccelerateDecelerateInterpolator());
			mTrAnim.setInterpolator(new AccelerateDecelerateInterpolator());
			as = new AnimationSet(false);
			
			as.addAnimation(mScalAnim);
			as.addAnimation(mTrAnim);
			as.setDuration(200);
			as.setFillAfter(true);

			mMovingItem.startAnimation(as);
			
		}
		Log.d(TAG, "oldX=" + oldX + ";newX=" + mIndicatorPositionX + ";movingItem.getLeft()="
				+ mMovingItem.getLeft() + ";movingItem.getTranslationX()"
				+ mMovingItem.getTranslationX() + "" + "");
		
		mCurrentTabIndex = index;
	}

	/**
	 * 设置文本颜色
	 * @param view
	 */
	private void setItemTextColor(View view,int color){
		if(view instanceof TextView ){
			//文字可以设置颜色
			((TextView)view).setTextColor(color);
		}
		if(view instanceof Button){
			((Button)view).setTextColor(color);
		}
	}
	
	/**
	 * 设置以后item颜色会变成设置的颜色
	 */
	public void setItemColor(int color){
		this.mIndicatorAndTextColor = color;
		if(mMovingItem==null||mTabViewList.size()==0){
			return;
		}
		mMovingItem.setBackgroundColor(color);
		View newTabView = mTabViewList.get(mCurrentTabIndex);
		newTabView.setSelected(true);
		if(newTabView instanceof TextView ){
			//文字可以设置颜色
			((TextView)newTabView).setTextColor(color);
		}
		if(newTabView instanceof Button){
			((Button)newTabView).setTextColor(color);
		}
	}
	
	public void setOnTabChangedListener(
			OnTabChangedListener onTabChangedListener) {
		this.mOnTabChangedListener = onTabChangedListener;

	}

	public interface OnTabChangedListener {
		public void onTabChanged(int index, View view);
	}
	
	// 计算出该TextView中文字的长度(像素)
	public static float getTextViewLength(TextView textView,String text){
	  TextPaint paint = textView.getPaint();
	  // 得到使用该paint写上text的时候,像素为多少
	  float textLength = paint.measureText(text);
	  return textLength;
	}
}
