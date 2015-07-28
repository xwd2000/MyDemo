package com.hikvision.parentdotworry.costomui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hikvision.parentdotworry.R;
import com.hikvision.parentdotworry.utils.ImageUtils;
import com.hikvision.parentdotworry.utils.ScreenUtil;
public class TitleBar extends RelativeLayout{
	private TextView mTvTitle;
	private ImageView mBtLeftButton;
	private ImageView mBtRightButton;
	private ImageView mHasNewBadge;
	
	public TitleBar(Context context) {
		super(context);
	}

	public TitleBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}


	public TitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        inflater.inflate(R.layout.title_bar_base, this);  
        mBtLeftButton=(ImageView) findViewById(R.id.iv_title_bar_left_button);  
        mBtRightButton=(ImageView) findViewById(R.id.iv_title_bar_right_button);  
        mTvTitle=(TextView)findViewById(R.id.tv_title_bar_title);  
      
	}

	/**
	 * 设置左边按钮
	 * @param id
	 * @param listener
	 */
	public ImageView setLeftButton(int id,View.OnClickListener listener ){
		mBtLeftButton.setImageResource(id);
		mBtLeftButton.setOnClickListener(listener);
		mBtLeftButton.setVisibility(View.VISIBLE);
		return mBtLeftButton;
	}
	
	public ImageView setRightButton(int id,View.OnClickListener listener ){
		mBtRightButton.setImageResource(id);
		mBtRightButton.setOnClickListener(listener);
		mBtRightButton.setVisibility(View.VISIBLE);
		return mBtRightButton;
	}
	public void setTitle(String text){
		mTvTitle.setText(text);
	}
	
	/**
	 * 设置右图片drawable会在方法中setBounds
	 * @param text
	 * @param drawable 
	 */
	public void setTitle(String text,Drawable drawable){
		mTvTitle.setText(text);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		mTvTitle.setCompoundDrawables(null,null,drawable,null);
	}
	/**
	 * 设置右侧图片
	 * @param drawable
	 */
	public void setTitleDrawable(Drawable drawable){
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		mTvTitle.setCompoundDrawables(null,null,drawable,null);
	}
	public void setTitleOnClickListener(OnClickListener onClickListener){
		mTvTitle.setOnClickListener(onClickListener);
	}
	
	public void showHasNewBadge(){
		ImageView iv = newOrGetBadge();
		iv.setVisibility(View.VISIBLE);
	}		
	
	public void hideHasNewBadge(){
		ImageView iv = newOrGetBadge();
		iv.setVisibility(View.GONE);
	}
	
	private ImageView newOrGetBadge(){
		if(mHasNewBadge==null){
			LayoutParams lp = (LayoutParams) mBtLeftButton.getLayoutParams();
			ViewParent parent = mBtLeftButton.getParent();
			FrameLayout container = new FrameLayout(getContext());
			
			// TODO verify that parent is indeed a ViewGroup
			ViewGroup group = (ViewGroup) parent; 
			int index = group.indexOfChild(mBtLeftButton);
			
			group.removeView(mBtLeftButton);
			group.addView(container, index, lp);
			
			container.addView(mBtLeftButton);
			
			mHasNewBadge=new ImageView(getContext());
			
			int circleWidth=ScreenUtil.dip2px(getContext(), 10);
			int circleHeight=circleWidth;
			int circleMarginRight=ScreenUtil.dip2px(getContext(), 8);
			int circleMarginTop=circleMarginRight;
			
			Bitmap bitmap = ImageUtils.generateCircleBitmap(circleWidth/2, Color.argb(255, 238, 78, 50));
			FrameLayout.LayoutParams lpCircle=new FrameLayout.LayoutParams(circleWidth, circleHeight);
			lpCircle.gravity = Gravity.RIGHT | Gravity.TOP;
			lpCircle.setMargins(0, circleMarginTop, circleMarginRight, 0);
			mHasNewBadge.setLayoutParams(lpCircle);
			mHasNewBadge.setImageBitmap(bitmap);
			container.addView(mHasNewBadge);
			
			return mHasNewBadge;
		}else{
			return mHasNewBadge;
		}
	}

	/**
	 * 返回textview
	 * @return
	 */
	public View getTitle(){
		return mTvTitle;
	}
	
}
