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
import com.hikvision.parentdotworry.plug.universalimageloader.core.DisplayImageOptions;
import com.hikvision.parentdotworry.plug.universalimageloader.core.ImageLoader;
import com.hikvision.parentdotworry.plug.universalimageloader.core.assist.ImageScaleType;
import com.hikvision.parentdotworry.plug.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.hikvision.parentdotworry.plug.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.hikvision.parentdotworry.plug.universalimageloader.core.process.BitmapProcessor;
import com.hikvision.parentdotworry.utils.EmptyUtil;
import com.hikvision.parentdotworry.utils.ImageUtils;
import com.hikvision.parentdotworry.utils.ScreenUtil;
import com.videogo.openapi.EzvizAPI;

public class SplashScreen extends Activity {
	EzvizAPI mEzvizAPI=null;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        //初始化萤石实例
        initData();
        
        getWindow().setFormat(PixelFormat.RGBA_8888);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
        
        setContentView(R.layout.splash_screen);

        //Display the current version number
        PackageManager pm = getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo("org.wordpress.android", 0);
            TextView versionNumber = (TextView) findViewById(R.id.versionNumber);
            versionNumber.setText("Version " + (EmptyUtil.isEmpty(pi.versionName)?"":pi.versionName));
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        new Handler().postDelayed(new Runnable() {
            public void run() {
                /* Create an Intent that will start the Main WordPress Activity. */
            	//mEzvizAPI.gotoLoginPage(true);
            	Intent intent = new Intent(SplashScreen.this,LoginActivity.class);
            	startActivity(intent);
            	SplashScreen.this.finish();
            }
        }, 500); //2900 for release

    }
    
    
    private void initData() {
        mEzvizAPI = EzvizAPI.getInstance();
    }
}