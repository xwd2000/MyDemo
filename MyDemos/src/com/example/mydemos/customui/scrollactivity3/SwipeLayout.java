package com.example.mydemos.customui.scrollactivity3;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.imid.swipebacklayout.lib.ViewDragHelper;
import me.imid.swipebacklayout.lib.SwipeBackLayout.SwipeListener;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class SwipeLayout extends ViewGroup{
	
    /**
     * Edge flag indicating that the left edge should be affected.
     */
    public static final int EDGE_LEFT = ViewDragHelper.EDGE_LEFT;

    /**
     * Edge flag indicating that the right edge should be affected.
     */
    public static final int EDGE_RIGHT = ViewDragHelper.EDGE_RIGHT;
	
    /**
     * Edge flag set indicating all edges should be affected.
     */
    public static final int EDGE_ALL = EDGE_LEFT | EDGE_RIGHT;

    /**
     * A view is not currently being dragged or animating as a result of a
     * fling/snap.
     */
    public static final int STATE_IDLE = ViewDragHelper.STATE_IDLE;

    /**
     * A view is currently being dragged. The position is currently changing as
     * a result of user input or simulated user input.
     */
    public static final int STATE_DRAGGING = ViewDragHelper.STATE_DRAGGING;

    /**
     * A view is currently settling into place as a result of a fling or
     * predefined non-interactive motion.
     */
    public static final int STATE_SETTLING = ViewDragHelper.STATE_SETTLING;
    /**
     * Default threshold of scroll
     */
    private static final float DEFAULT_SCROLL_THRESHOLD = 0.3f;
    
	private int mCurrentIndex;
	private ViewDragHelper mDragHelper;
	
    private int mEdgeFlag;
    /**
     * Edge being dragged
     */
    private int mTrackingEdge;
	
    /**
     * The set of listeners to be sent events through.
     */
    private List<SwipeListener> mListeners;
    private float mScrollPercent;
    
    private int mContentLeft;
    /**
     * Threshold of scroll, we will close the activity, when scrollPercent over
     * this value;
     */
    private float mScrollThreshold = DEFAULT_SCROLL_THRESHOLD;
    
	public SwipeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
		
		
	}

	public SwipeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SwipeLayout(Context context) {
		super(context);
		init();
		
	}
	
	private void init(){
		
		
		mDragHelper = ViewDragHelper.create(this, new ViewDragCallback());
		mCurrentIndex = 0;
	}

	
    /**
     * Enable edge tracking for the selected edges of the parent view. The
     * callback's
     * {@link me.imid.swipebacklayout.lib.ViewDragHelper.Callback#onEdgeTouched(int, int)}
     * and
     * {@link me.imid.swipebacklayout.lib.ViewDragHelper.Callback#onEdgeDragStarted(int, int)}
     * methods will only be invoked for edges for which edge tracking has been
     * enabled.
     *
     * @param edgeFlags Combination of edge flags describing the edges to watch
     * @see #EDGE_LEFT
     * @see #EDGE_RIGHT
     */
    public void setEdgeTrackingEnabled(int edgeFlags) {
        mEdgeFlag = edgeFlags;
        mDragHelper.setEdgeTrackingEnabled(mEdgeFlag);
    }
    
    /**
     * Set scroll threshold, we will close the activity, when scrollPercent over
     * this value
     *
     * @param threshold
     */
    public void setScrollThresHold(float threshold) {
        if (threshold >= 1.0f || threshold <= 0) {
            throw new IllegalArgumentException("Threshold value should be between 0 and 1.0");
        }
        mScrollThreshold = threshold;
    }
    
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int viewWidth = getMeasuredWidth();
		int viewHeight = getMeasuredHeight();
		
		
		for(int i=0,j=getChildCount();i<j;i++){
			View child = getChildAt(i);
			if (child.getVisibility() != GONE) {
				if(i<mCurrentIndex){
					child.layout(l*3/2-r/2, t, l*3/2+r/2, b);
				}else if(i==mCurrentIndex){
					child.layout(l+mContentLeft, t, r+mContentLeft, b);
				}else {
					child.layout(r, t, r+r-l, b);
				}
			}
		}
	}
	
    /**
     * Add a callback to be invoked when a swipe event is sent to this view.
     *
     * @param listener the swipe listener to attach to this view
     */
    public void addSwipeListener(SwipeListener listener) {
        if (mListeners == null) {
            mListeners = new ArrayList<SwipeListener>();
        }
        mListeners.add(listener);
    }

    /**
     * Removes a listener from the set of listeners
     *
     * @param listener
     */
    public void removeSwipeListener(SwipeListener listener) {
        if (mListeners == null) {
            return;
        }
        mListeners.remove(listener);
    }

    public static interface SwipeListener {
        /**
         * Invoke when state change
         *
         * @param state         flag to describe scroll state
         * @param scrollPercent scroll percent of this view
         * @see #STATE_IDLE
         * @see #STATE_DRAGGING
         * @see #STATE_SETTLING
         */
        public void onScrollStateChange(int state, float scrollPercent);

        /**
         * Invoke when edge touched
         *
         * @param edgeFlag edge flag describing the edge being touched
         * @see #EDGE_LEFT
         * @see #EDGE_RIGHT
         * @see #EDGE_BOTTOM
         */
        public void onEdgeTouch(int edgeFlag);

        /**
         * Invoke when scroll percent over the threshold for the first time
         */
        public void onScrollOverThreshold();
        
        /**
         * Invoke when scroll percent over the threshold for the first time
         */
        public void onScrollFinish();
    }

	
	
    
	 private class ViewDragCallback extends ViewDragHelper.Callback {
	        private boolean mIsScrollOverValid;

	        @Override
	        public boolean tryCaptureView(View view, int i) {
	            boolean ret = mDragHelper.isEdgeTouched(EDGE_ALL, i);
	            
	            if (ret) {
	                if (mDragHelper.isEdgeTouched(EDGE_LEFT, i)) {
	                    mTrackingEdge = ViewDragHelper.EDGE_LEFT;
	                } else if (mDragHelper.isEdgeTouched(EDGE_RIGHT, i)) {
	                    mTrackingEdge = EDGE_RIGHT;
	                }
	                if (mListeners != null && !mListeners.isEmpty()) {
	                    for (SwipeListener listener : mListeners) {
	                        listener.onEdgeTouch(mTrackingEdge);
	                    }
	                }
	                mIsScrollOverValid = true;
	            }
	            return ret;
	        }

	        @Override
	        public int getViewHorizontalDragRange(View child) {
	            return mEdgeFlag & (EDGE_LEFT | EDGE_RIGHT);
	        }

	        @Override
	        public int getViewVerticalDragRange(View child) {
	            return mEdgeFlag;
	        }

	        @Override
	        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
	            super.onViewPositionChanged(changedView, left, top, dx, dy);
	            if ((mTrackingEdge & EDGE_LEFT) != 0) {
	                mScrollPercent = Math.abs((float) left
	                        / (changedView.getWidth() /*+ mShadowLeft.getIntrinsicWidth()*/));
	            } else if ((mTrackingEdge & EDGE_RIGHT) != 0) {
	                mScrollPercent = Math.abs((float) left
	                        / (changedView.getWidth() /*+ mShadowRight.getIntrinsicWidth()*/));
	            }
	            
	            mContentLeft = left;
	            invalidate();
	            if (mScrollPercent < mScrollThreshold && !mIsScrollOverValid) {
	                mIsScrollOverValid = true;
	            }
	            if (mListeners != null && !mListeners.isEmpty()
	                    && mDragHelper.getViewDragState() == STATE_DRAGGING
	                    && mScrollPercent >= mScrollThreshold && mIsScrollOverValid) {
	                mIsScrollOverValid = false;
	                for (SwipeListener listener : mListeners) {
	                    listener.onScrollOverThreshold();
	                }
	            }

	            if (mScrollPercent >= 1) {
	            	for (SwipeListener listener : mListeners) {
	                    listener.onScrollFinish();
	                }
	            }
	        }

	        @Override
	        public void onViewReleased(View releasedChild, float xvel, float yvel) {
	            final int childWidth = releasedChild.getWidth();
	            final int childHeight = releasedChild.getHeight();

	            int left = 0, top = 0;
//	            if ((mTrackingEdge & EDGE_LEFT) != 0) {
//	                left = xvel > 0 || xvel == 0 && mScrollPercent > mScrollThreshold ? childWidth
//	                        + mShadowLeft.getIntrinsicWidth() + OVERSCROLL_DISTANCE : 0;
//	            } else if ((mTrackingEdge & EDGE_RIGHT) != 0) {
//	                left = xvel < 0 || xvel == 0 && mScrollPercent > mScrollThreshold ? -(childWidth
//	                        + mShadowLeft.getIntrinsicWidth() + OVERSCROLL_DISTANCE) : 0;
//	            }

	            mDragHelper.settleCapturedViewAt(left, top);
	            invalidate();
	        }

	        @Override
	        public int clampViewPositionHorizontal(View child, int left, int dx) {
	            int ret = 0;
	            if ((mTrackingEdge & EDGE_LEFT) != 0) {
	                ret = Math.min(child.getWidth(), Math.max(left, 0));
	            } else if ((mTrackingEdge & EDGE_RIGHT) != 0) {
	                ret = Math.min(0, Math.max(left, -child.getWidth()));
	            }
	            return ret;
	        }

	        @Override
	        public int clampViewPositionVertical(View child, int top, int dy) {
	            int ret = 0;
	            return ret;
	        }

	        @Override
	        public void onViewDragStateChanged(int state) {
	            super.onViewDragStateChanged(state);
	            if (mListeners != null && !mListeners.isEmpty()) {
	                for (SwipeListener listener : mListeners) {
	                    listener.onScrollStateChange(state, mScrollPercent);
	                }
	            }
	        }
	    }



}
