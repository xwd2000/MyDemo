package com.hikvision.parentdotworry;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.hikvision.parentdotworry.application.AppApplication;
import com.hikvision.parentdotworry.base.BaseActivity;
import com.hikvision.parentdotworry.bean.MessageBean;
import com.hikvision.parentdotworry.costomui.TitleBar;
import com.hikvision.parentdotworry.plug.universalimageloader.core.DisplayImageOptions;
import com.hikvision.parentdotworry.plug.universalimageloader.core.ImageLoader;
import com.hikvision.parentdotworry.plug.universalimageloader.core.assist.FailReason;
import com.hikvision.parentdotworry.plug.universalimageloader.core.listener.ImageLoadingListener;
import com.hikvision.parentdotworry.utils.Args;
import com.hikvision.parentdotworry.utils.EmptyUtil;
import com.hikvision.parentdotworry.utils.ScreenUtil;

public class MessageDetailActivity extends BaseActivity {

	// ===========================================================
	// Constants
	// ===========================================================
	public static final String INTENT_KEY_MESSAGE_BEAN = "messagebean";
	private static final float IMAGE_MIN_RATIO = 9/16f;
	// ===========================================================
	// Fields
	// ===========================================================
	private TitleBar mTbTitleBar;
	private TextView mTvMessageDetail;
	private TextView mTvMessageDetailTitle;
	private TextView mTvMessageDetailPageTime;
	private TextView mTvMessageDetailPageDepartment;
	private ImageView mIvMessageDetailImage;
	private ImageView mIvMessageDetailLine;

	private ImageLoader mImageLoader;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_detail_activity);
		Intent intent = getIntent();
		MessageBean messageBean = (MessageBean)intent.getSerializableExtra(INTENT_KEY_MESSAGE_BEAN);
		

		mImageLoader = ImageLoader.getInstance();
		initUiInstance();
		initView();

		addViewData(messageBean);
		//new GetDataTask(id).execute();
	}
	
	@Override
	protected void onDestroy() {
		mImageLoader.clearMemoryCache();
		super.onDestroy();
	}

	// ===========================================================
	// Methods
	// ===========================================================
	private void initUiInstance() {
		mTbTitleBar = (TitleBar) findViewById(R.id.tb_title_bar);
		mTvMessageDetail = (TextView) findViewById(R.id.tv_message_detail);
		mTvMessageDetailPageTime = (TextView) findViewById(R.id.tv_message_detail_page_time);
		mTvMessageDetailPageDepartment = (TextView) findViewById(R.id.tv_message_detail_page_department);
		mIvMessageDetailImage = (ImageView) findViewById(R.id.iv_message_detail_image);
		mIvMessageDetailLine = (ImageView) findViewById(R.id.iv_message_detail_line);
		mTvMessageDetailTitle = (TextView) findViewById(R.id.tv_message_detail_title);
	}

	private void initView() {

		mTbTitleBar.setLeftButton(R.drawable.arrow,
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						MessageDetailActivity.this.finish();
					}
				});
		mTbTitleBar.setTitle(getStringById(R.string.message_list_page_title));
		
	}

	private void addViewData(MessageBean messageDetail) {
		Args.notNull(messageDetail, "messageDetail");

		String largePicUrl = messageDetail.getPicUrl();
		if (!EmptyUtil.isEmpty(largePicUrl)) {
			mIvMessageDetailLine.setVisibility(View.GONE);
			mIvMessageDetailImage.setVisibility(View.VISIBLE);
			DisplayImageOptions options = AppApplication.getApplication().getAppDefaultDisplayImageOptions();
			// 依次从内存和sd中获取，如果没有则网络下载
			mImageLoader.displayImage(largePicUrl, mIvMessageDetailImage,
					options, new ImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							LayoutParams lp = view.getLayoutParams();
							DisplayMetrics dm = ScreenUtil.getScreenMetrics(MessageDetailActivity.this);
							int imageViewWidth = dm.widthPixels - 2*getResources().getDimensionPixelSize(R.dimen.message_detail_main_padding);
							lp.width = imageViewWidth;
							float heightMax = imageViewWidth*IMAGE_MIN_RATIO;
							lp.height = (int) heightMax;
							view.setLayoutParams(lp);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {

							LayoutParams lp = view.getLayoutParams();
							DisplayMetrics dm = ScreenUtil.getScreenMetrics(MessageDetailActivity.this);
							int imageViewWidth = dm.widthPixels - 2*getResources().getDimensionPixelSize(R.dimen.message_detail_main_padding);
							lp.width = imageViewWidth;
							float heightMax = imageViewWidth*IMAGE_MIN_RATIO;
							lp.height = (int) heightMax;
							view.setLayoutParams(lp);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							int bitmapWidth = loadedImage.getWidth();
							int bitmapHeight = loadedImage.getHeight();
							LayoutParams lp = view.getLayoutParams();
							DisplayMetrics dm = ScreenUtil.getScreenMetrics(MessageDetailActivity.this);
							int imageViewWidth = dm.widthPixels - 2*getResources().getDimensionPixelSize(R.dimen.message_detail_main_padding);
							lp.width = imageViewWidth;
							float heightMax = imageViewWidth*IMAGE_MIN_RATIO;
							float height = imageViewWidth*1.0f/bitmapWidth*bitmapHeight;
							lp.height = (int) (height>heightMax?heightMax:height);
							view.setLayoutParams(lp);
						}
						

						@Override
						public void onLoadingCancelled(String imageUri,
								View view) {

						}
					});
		}else{
			mIvMessageDetailLine.setVisibility(View.VISIBLE);
			mIvMessageDetailImage.setVisibility(View.GONE);
		}
		mTvMessageDetailPageTime.setText(messageDetail.getReleaseTime());
		mTvMessageDetailPageDepartment.setText(messageDetail.getPromulgator());
		mTvMessageDetailTitle.setText(messageDetail.getTitle());
		mTvMessageDetail.setText(messageDetail.getContent());
	}
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	
	// ===========================================================
	// Getter & Setter
	// ===========================================================

}
