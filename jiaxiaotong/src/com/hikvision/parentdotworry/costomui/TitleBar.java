package com.hikvision.parentdotworry.costomui;

import com.hikvision.parentdotworry.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View;
public class TitleBar extends RelativeLayout{
	private TextView mTvTitle;
	private ImageView mBtLeftButton;
	private ImageView mBtRightButton;
	
	
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
	
	
	/**
	 * 返回textview
	 * @return
	 */
	public View getTitle(){
		return mTvTitle;
	}
	
}
