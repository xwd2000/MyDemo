package com.hikvision.parentdotworry.splash;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.hikvision.parentdotworry.LoginActivity;
import com.hikvision.parentdotworry.MainActivity;
import com.hikvision.parentdotworry.MainActivity2;
import com.hikvision.parentdotworry.R;
import com.hikvision.parentdotworry.application.AppApplication;
import com.hikvision.parentdotworry.application.AppPerference;
import com.hikvision.parentdotworry.application.AppPerference.K;
import com.hikvision.parentdotworry.plug.universalimageloader.core.DisplayImageOptions;
import com.hikvision.parentdotworry.plug.universalimageloader.core.ImageLoader;
import com.hikvision.parentdotworry.plug.universalimageloader.core.assist.ImageScaleType;
import com.hikvision.parentdotworry.plug.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.hikvision.parentdotworry.plug.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.hikvision.parentdotworry.plug.universalimageloader.core.process.BitmapProcessor;
import com.hikvision.parentdotworry.utils.EmptyUtil;
import com.hikvision.parentdotworry.utils.ImageUtils;
import com.hikvision.parentdotworry.utils.QEncodeUtil;
import com.hikvision.parentdotworry.utils.ScreenUtil;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EzvizAPI;

public class SplashScreen extends Activity {
	EzvizAPI mEzvizAPI = null;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		// 初始化萤石实例
		initData();

		getWindow().setFormat(PixelFormat.RGBA_8888);
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);

		setContentView(R.layout.splash_screen);
		String accessTokenStore = AppPerference.get(K.ACCESS_TOKEN_KEY);
		if (!EmptyUtil.isEmpty(accessTokenStore)) {
			String accessToken;
			try {
				accessToken = new StringBuilder(QEncodeUtil.decrypt(
						accessTokenStore, AppApplication.NET_AES_PASSWORD))
						.reverse().toString();
				mEzvizAPI.setAccessToken(accessToken);

				mEzvizAPI.getSquareVideoFavorite(1, 1);
				goToMainPageDelay(2000);
			} catch (BaseException e) {
				goToLoginPageDelay(2000);
				e.printStackTrace();
			} catch (Exception e) {
				goToLoginPageDelay(2000);
				e.printStackTrace();
			}
			//goToLoginPageDelay(2000);
		}else{
			goToLoginPageDelay(2000);
		}

	}

	private void goToMainPageDelay(final int time) {
		new Handler().postDelayed(new Runnable() {
			public void run() {
				goToMainPage();
			}
		}, time); // 2900 for release
	}
	private void goToLoginPageDelay(final int time) {
		new Handler().postDelayed(new Runnable() {
			public void run() {
				goToLoginPage();
			}
		}, time); // 2900 for release
	}

	private void goToMainPage() {
		Intent intent = new Intent(SplashScreen.this, MainActivity2.class);
		startActivity(intent);
		SplashScreen.this.finish();
	}

	private void goToLoginPage() {
		Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
		startActivity(intent);
		SplashScreen.this.finish();
	}

	private void initData() {
		mEzvizAPI = EzvizAPI.getInstance();
	}
}