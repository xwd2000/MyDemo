package com.hikvision.parentdotworry.costomui;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

/**
 * 用反射修改pager的滑动时间
 * @author xuweidong
 *
 */
public class MessageViewPaper extends ViewPager {
	private static final String TAG = "MessageViewPaper";
	private int CURRENT_TOUGH_CHIL_INDEX = -1; //表示没有点到子view(这些view需要滑动事件)
	
	private int mScrollTime=500;
	private List<Rect> mChildTouchRectList;
	/**
	 * -1则没有点击到子view
	 */
	private int mTouchChildIndex = CURRENT_TOUGH_CHIL_INDEX;

	
	public MessageViewPaper(Context context, AttributeSet attrs) {
		super(context, attrs);
		mChildTouchRectList=new ArrayList<Rect>();
		setMyScroller();
	
	}
	/**
	 * 子View区域(相对于父View),这些区域会阻止此viewpager的事件
	 */
	public void addChildTouchRect(Rect rect){
		for(Rect rt:mChildTouchRectList){
			if(rect.intersect(rt)){
				StringBuilder logStr=new StringBuilder();
				logStr.append("添加的可触摸区域有重叠，已有(");
				logStr.append(rt.left);
				logStr.append(",");
				logStr.append(rt.top);
				logStr.append(",");
				logStr.append(rt.right);
				logStr.append(",");
				logStr.append(rt.bottom);
				logStr.append(")");
				logStr.append("新加入(");
				logStr.append(rect.left);
				logStr.append(",");
				logStr.append(rect.top);
				logStr.append(",");
				logStr.append(rect.right);
				logStr.append(",");
				logStr.append(rect.bottom);
				logStr.append(")");
				
				Log.d(TAG, logStr+"");
				return ;
			}
		}
		mChildTouchRectList.add(rect);
	}
	
	public void clearChildTouchRect(){
		mChildTouchRectList.clear();
	}

	private void setMyScroller() {
		try {
			Class<?> viewpager = ViewPager.class;
			Field scroller = viewpager.getDeclaredField("mScroller");
			scroller.setAccessible(true);
			scroller.set(this, new MyScroller(getContext()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	



	public void setScrollTime(int scrollTime) {
		this.mScrollTime = scrollTime;
	}

	@Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			for(int i=0,j=mChildTouchRectList.size();i<j;i++){
				if(mChildTouchRectList.get(i).contains((int)ev.getX(), (int)ev.getY())){
					setTouchChildIndex(i);
					return false;
				}
			}
			resetTouchChildIndex();
			break;
		case MotionEvent.ACTION_MOVE:
			if(getTouchChildIndex()!=CURRENT_TOUGH_CHIL_INDEX){
				return false;
			}
			break;
		case MotionEvent.ACTION_UP:
			if(getTouchChildIndex()!=CURRENT_TOUGH_CHIL_INDEX){
				resetTouchChildIndex();
				return false;
			}
			break;
		default:
			break;
		}
		
		
		return super.onInterceptTouchEvent(ev);
	}

	public class MyScroller extends Scroller {
		public MyScroller(Context context) {
			super(context, new DecelerateInterpolator());
		}

		@Override
		public void startScroll(int startX, int startY, int dx, int dy,
				int duration) {
			super.startScroll(startX, startY, dx, dy, mScrollTime);
		}
	}
	private void setTouchChildIndex(int index){
		mTouchChildIndex =index;
	}	
	private int getTouchChildIndex(){
		return mTouchChildIndex;
	}
	private void resetTouchChildIndex(){
		mTouchChildIndex =CURRENT_TOUGH_CHIL_INDEX;
	}
	 

}