package com.hikvision.parentdotworry.costomui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class HorizontalScrollViewCanListener extends HorizontalScrollView{
	private OnScrollChangeListener mOnScrollChangeListener;
	
	public HorizontalScrollViewCanListener(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public HorizontalScrollViewCanListener(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public HorizontalScrollViewCanListener(Context context) {
		super(context);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if(mOnScrollChangeListener!=null){
			mOnScrollChangeListener.onScrollChange(l, t, oldl, oldt);
		}
	}
	
	public void setOnScrollChangerListener(OnScrollChangeListener onScrollChangeListener){
		this.mOnScrollChangeListener = onScrollChangeListener;
	}
	
	
	public interface OnScrollChangeListener{
		public void onScrollChange(int l, int t, int oldl, int oldt);
	}
}
