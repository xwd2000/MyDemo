package com.hikvision.parentdotworry.costomui;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.hikvision.parentdotworry.R;
import com.hikvision.parentdotworry.application.AppApplication;
import com.hikvision.parentdotworry.plug.universalimageloader.core.DisplayImageOptions;
import com.hikvision.parentdotworry.plug.universalimageloader.core.ImageLoader;
import com.hikvision.parentdotworry.plug.universalimageloader.core.assist.ImageScaleType;
import com.hikvision.parentdotworry.plug.universalimageloader.core.display.FadeInBitmapDisplayer;

public class PicShowWindow{
	private ImageLoader mImageLoader = ImageLoader.getInstance();
	private PopupWindow popupWindow;
	private ImageView imageView;
	
	public PicShowWindow(Context context){
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
	
		FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.pic_show_popwindow, null);
		 
		popupWindow=new PopupWindow(layout,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		imageView = (ImageView) layout.findViewById(R.id.iv_capture_pic_large);
		layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
			}
		});
	}
	
	public void show(View parent,String uri){
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.cloneFrom(AppApplication.getApplication().getAppDefaultDisplayImageOptions())
		.build();// 构建完成
		
		mImageLoader.displayImage(uri, imageView, options);
		popupWindow.showAtLocation(parent, Gravity.LEFT, 0, 0);
	}
	public void dimiss(String uri){
		popupWindow.dismiss();
		imageView.setImageDrawable(null);
	}
}
