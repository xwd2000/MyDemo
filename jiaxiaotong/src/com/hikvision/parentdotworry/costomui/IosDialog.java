package com.hikvision.parentdotworry.costomui;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hikvision.parentdotworry.R;

public class IosDialog extends Dialog {
	private TextView mTvTitle;
	private TextView mTvContent;
	private Button mBtLeft;
	private Button mBtRight;

	public IosDialog(Context context) {
		super(context);
		init(context);
	}

	public void init(Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(
				R.layout.ios_dialog_view, null);
		setContentView(relativeLayout);
		Window dialogWindow = getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
		lp.width = (int) (d.widthPixels * 0.6); // 宽度设置为屏幕的0.6
		
		dialogWindow.setLayout(lp.width, lp.height);
		
		dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);
		initInstance();
	}
	
	private void initInstance(){
		mTvTitle=(TextView) findViewById(R.id.tv_ios_dialog_title);
		mTvContent=(TextView) findViewById(R.id.tv_ios_dialog_content);
		mBtLeft=(Button) findViewById(R.id.bt_ios_dialog_left);
		mBtRight=(Button) findViewById(R.id.bt_ios_dialog_right);
	}
	
	public void setTitle(String title){
		mTvTitle.setText(title);
	}	
	public void setContent(String content){
		mTvContent.setText(content);
	}	
	public void setButtonLeft(String text,View.OnClickListener onClickListener){
		mBtLeft.setText(text);
		mBtLeft.setOnClickListener(onClickListener);
	}	
	public void setButtonLeft(String text){
		mBtLeft.setText(text);
	}
	public void setButtonLeft(View.OnClickListener onClickListener){
		mBtLeft.setOnClickListener(onClickListener);
	}
	public void setButtonRight(String text,View.OnClickListener onClickListener){
		mBtRight.setText(text);
		mBtRight.setOnClickListener(onClickListener);
	}
	

}
