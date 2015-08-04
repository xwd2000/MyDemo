package com.example.mydemos.customui.scrollactivity;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.example.mydemos.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class SliderBackActivityBase extends Activity{
	private View mMainView;
	private Drawable mBackActivityDrawable;
	private Drawable mForwardActivityDrawable;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		byte[] drawableBytes=getIntent().getByteArrayExtra("backPic");
		if(drawableBytes!=null){
			Bitmap bitmap=BitmapFactory.decodeByteArray(drawableBytes, 0, drawableBytes.length);  
			mBackActivityDrawable=new BitmapDrawable(bitmap);
		}
	}
	@Override
	protected void onStart() {
		super.onStart();
	}

public View proxyView(View view){
	LayoutInflater inflater = (LayoutInflater) this
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(
			R.layout.backable_activity_base, null);
	MyScrollLayout scl= (MyScrollLayout)relativeLayout.findViewById(R.id.msl_activity_proxy);
	int childCount=scl.getChildCount();
	if(childCount==3){
		View child0 = scl.getChildAt(0);
		child0.setBackgroundDrawable(mBackActivityDrawable);
		
		ViewGroup child1 = (ViewGroup)scl.getChildAt(1);
		child1.removeAllViews();
		child1.addView(view);
		
		View child2 = scl.getChildAt(2);
		child2.setBackgroundDrawable(mForwardActivityDrawable);
		
		
		return relativeLayout;
	}
	return view;
	
}
	@Override
	 public void setContentView(View view){
		mMainView=view;
		super.setContentView(proxyView(view));
	 }
	
	@Override
	 public void setContentView(View view, ViewGroup.LayoutParams params){
		mMainView=view;
		super.setContentView(proxyView(view),params);
	 }
	
	@Override
	 public void setContentView(int layoutResID) {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMainView = inflater.inflate(layoutResID, null);
		super.setContentView(proxyView(mMainView));
	 }
	
	
	public void cacheAndJumpToActivity(){
		
	}
	
	@SuppressLint("NewApi")
	@Override
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
		
		mMainView.setDrawingCacheEnabled(true);  
		mMainView.buildDrawingCache();  //启用DrawingCache并创建位图  
	    Bitmap bitmap = Bitmap.createBitmap(mMainView.getDrawingCache()); //创建一个DrawingCache的拷贝，因为DrawingCache得到的位图在禁用后会被回收  
	    mMainView.setDrawingCacheEnabled(false);  //禁用DrawingCahce否则会影响性能  
	    ImageView iv=(ImageView)findViewById(R.id.iv_pic);
	    Drawable dw = new BitmapDrawable(bitmap);
	    iv.setBackgroundDrawable(dw);
	    
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
	    
	    intent.putExtra("backPic",baos.toByteArray());
	    super.startActivityForResult(intent, requestCode, options);
    }
	
}
