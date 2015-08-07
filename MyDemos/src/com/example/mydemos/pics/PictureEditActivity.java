package com.example.mydemos.pics;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.mydemos.R;
import com.example.util.ImageUtils;

public class PictureEditActivity extends Activity{
	private ImageView ivPicEditImageview;
	private LinearLayout llPicEditButtonContainer;
	private Bitmap orgin;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.picture_edit_activity);
		ivPicEditImageview=(ImageView)findViewById(R.id.iv_pic_edit_imageview);
		llPicEditButtonContainer = (LinearLayout)findViewById(R.id.ll_pic_edit_button_container);
		
	
		Drawable da=getResources().getDrawable(R.drawable.picture_edit);
		orgin=ImageUtils.drawableToBitmap(da);
		ivPicEditImageview.setImageDrawable(da);
		
		addRoundedCornerButton(llPicEditButtonContainer);
		
		addRoundedCornerButtonWithTrig(llPicEditButtonContainer);
		
		addScalButton(llPicEditButtonContainer);
		super.onCreate(savedInstanceState);
	}
	
	
	private void addRoundedCornerButton(ViewGroup container){
		Button bt = genButton("圆角");
		container.addView(bt);
		bt.setOnClickListener(
				new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bitmap bm=ImageUtils.getRoundedCornerBitmap(orgin, 20f);
				
				ivPicEditImageview.setImageBitmap(bm);
			}
		});
	}
	
	
	private void addRoundedCornerButtonWithTrig(ViewGroup container){
		Button bt = genButton("圆角气泡");
		container.addView(bt);
		bt.setOnClickListener(
				new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bitmap bm=ImageUtils.getRoundedCornerWithTrigBitmap(orgin, 20f, ImageUtils.TRI_TOP, orgin.getHeight()/3, 20);
				
				ivPicEditImageview.setImageBitmap(bm);
			}
		});
	}
	private void addScalButton(ViewGroup container){
		Button bt = genButton("缩放");
		container.addView(bt);
		bt.setOnClickListener(
				new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bitmap bm=ImageUtils.zoomBitmap(orgin,orgin.getWidth()*2,orgin.getHeight()*2);
				ivPicEditImageview.setImageBitmap(bm);
			}
		});
	}
	
	public Button genButton(String text){
		Button button=new Button(this);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		button.setLayoutParams(lp);
		button.setText(text);
		return button;
	}
	
	
}
