package com.example.mydemos.customui.scrollactivity3;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewParent;

public class ViewPagerInToughView extends ViewPager{
	


	private boolean mIsBeingXDragged = false;
	private boolean mIsBeingYDragged = false;
	
		
	public ViewPagerInToughView(Context context, AttributeSet attrs) {
		super(context, attrs);
		final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
	}

	public ViewPagerInToughView(Context context) {
		super(context);
		final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
	}

	private int mLastMotionX;
	private int mLastMotionY;
    private int mTouchSlop;
	@Override
	public boolean onTouchEvent(MotionEvent ev) {

        final int action = ev.getAction();

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                if (getChildCount() == 0) {
                    return false;
                }
                
                // Remember where the motion event started
                mLastMotionX = (int) ev.getX();
                mLastMotionY = (int) ev.getY();
                break;
            }
            case MotionEvent.ACTION_MOVE:
             

                final int x = (int) ev.getX();
                final int y = (int) ev.getY();
                int deltaX = mLastMotionX - x;
                int deltaY = mLastMotionY - y;
                if ((!mIsBeingXDragged) && (!mIsBeingYDragged) && Math.abs(deltaX) > mTouchSlop) {
                	if(Math.abs(deltaX)>Math.abs(deltaY)){
	                    final ViewParent parent = getParent();
	                    if (parent != null) {
	                        parent.requestDisallowInterceptTouchEvent(true);
	                    }
	                    mIsBeingXDragged = true;
                	}
                }else if(mIsBeingXDragged){
                	final ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            	 if(mIsBeingXDragged){
                 	final ViewParent parent = getParent();
                     if (parent != null) {
                         parent.requestDisallowInterceptTouchEvent(true);
                     }
                 }
            	mIsBeingXDragged = false;
            	mIsBeingYDragged = false;
                break;
            case MotionEvent.ACTION_CANCEL:
              
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
        }
        return super.onTouchEvent(ev);
    }




}
