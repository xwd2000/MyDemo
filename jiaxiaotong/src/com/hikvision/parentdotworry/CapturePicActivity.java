package com.hikvision.parentdotworry;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.hikvision.parentdotworry.base.BaseActivity;
import com.hikvision.parentdotworry.costomui.TitleBar;
import com.hikvision.parentdotworry.dataprovider.HttpDataProvider;
import com.hikvision.parentdotworry.plug.universalimageloader.core.ImageLoader;
import com.hikvision.parentdotworry.plug.universalimageloader.core.ImageLoaderConfiguration;
import com.hikvision.parentdotworry.plug.universalimageloader.core.assist.FailReason;
import com.hikvision.parentdotworry.plug.universalimageloader.core.listener.ImageLoadingListener;

public class CapturePicActivity extends BaseActivity{

	// ===========================================================
	// Constants
	// ===========================================================
	public static final String INTENT_KEY_PIC_TYPE_ENTER_SCHOOL="enter";
	public static final String INTENT_KEY_PIC_TYPE_LEAVE_SCHOOL="leave";
	private static final String TAG="CapturePicActivity";
	
	// ===========================================================
	// Fields
	// ===========================================================
	
	private TitleBar mTitleBar;
	private ImageView mImageView;
	
	private boolean mEnterOrLeave=true;
	
	private ImageLoader mImageLoader;
	private HttpDataProvider mHttpDataProvider = HttpDataProvider.getInstance();
	// ===========================================================
	// Constructors
	// ===========================================================



	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//只要有这个值，就是进学校
		mEnterOrLeave=getIntent().getBooleanExtra(INTENT_KEY_PIC_TYPE_ENTER_SCHOOL, false);
		
		setContentView(R.layout.capture_pic_activity);
		
		
		mImageLoader = ImageLoader.getInstance();
		
		
		initUiInstance();
		
		initView();
	}
	// ===========================================================
	// Methods
	// ===========================================================
	private void initUiInstance(){
		mTitleBar = (TitleBar)findViewById(R.id.tb_title_bar);
		mImageView=(ImageView)findViewById(R.id.iv_captured_pic);
	}
	private void initView(){
		mTitleBar.setLeftButton(R.drawable.backbtn_selector,new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CapturePicActivity.this.finish();
			}
		});
		ImageLoadingListener imageLoadingListener=new ImageLoadingListener(){

			@Override
			public void onLoadingStarted(String imageUri, View view) {
			}

			@Override
			public void onLoadingFailed(String imageUri, View view,
					FailReason failReason) {
			}

			@Override
			public void onLoadingComplete(String imageUri, View view,
					Bitmap loadedImage) {
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
			}};
		
		
		if(mEnterOrLeave==true){//进校门
			mTitleBar.setTitle(getString(R.string.capture_pic_student_enter_school_pic));
			String url=mHttpDataProvider.getCapturedEnterPic(0);
			mImageLoader.displayImage(url, mImageView, imageLoadingListener);
		}else{//出校门
			mTitleBar.setTitle(getString(R.string.capture_pic_student_leave_school_pic));
			String url=mHttpDataProvider.getCapturedLeavePic(0);
			mImageLoader.displayImage(url, mImageView, imageLoadingListener);
		}
		
		
		
	}
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	
	// ===========================================================
	// Getter & Setter
	// ===========================================================

}
