/* 
 * @ProjectName VideoGoJar
 * @Copyright HangZhou Hikvision System Technology Co.,Ltd. All Right Reserved
 * 
 * @FileName EzvizApplication.java
 * @Description 这里对文件进行描述
 * 
 * @author chenxingyf1
 * @data 2014-7-12
 * 
 * @note 这里写本文件的详细功能描述和注释
 * @note 历史记录
 * 
 * @warning 这里写本文件的相关警告
 */
package com.hikvision.parentdotworry.application;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build.VERSION;

import com.hikvision.parentdotworry.R;
import com.hikvision.parentdotworry.dataprovider.httpdata.HttpDataProvider;
import com.hikvision.parentdotworry.exception.CustomExceptionHandler;
import com.hikvision.parentdotworry.plug.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.hikvision.parentdotworry.plug.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.hikvision.parentdotworry.plug.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.hikvision.parentdotworry.plug.universalimageloader.core.DisplayImageOptions;
import com.hikvision.parentdotworry.plug.universalimageloader.core.ImageLoader;
import com.hikvision.parentdotworry.plug.universalimageloader.core.ImageLoaderConfiguration;
import com.hikvision.parentdotworry.plug.universalimageloader.core.assist.ImageScaleType;
import com.hikvision.parentdotworry.plug.universalimageloader.core.assist.QueueProcessingType;
import com.hikvision.parentdotworry.plug.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.hikvision.parentdotworry.plug.universalimageloader.core.download.BaseImageDownloader;
import com.hikvision.parentdotworry.utils.Args;
import com.videogo.constant.Config;
import com.videogo.openapi.EzvizAPI;

import de.mindpipe.android.logging.log4j.LogConfigurator;

/**
 * 自定义应用
 * 
 * @author xuweidong
 * @data 2014-7-12
 */
public class AppApplication extends Application {
	/**
	 * 应用application常量
	 */
	


	/**
	 * 图片缓存地址
	 */
	public static final String CACHE_PATH = "imageloader/Cache";
	/**
	 * 数据库信息
	 */
	public static final String APP_DB_NAME = "parentdotworry";
	public static final int APP_DB_VERSION = 1;

	/**
	 * 网络配置平台
	 */
	public static final String NET_MAIN_SERVER_IP = "10.20.34.109";
	public static final int NET_MAIN_SERVER_PORT = 8080;
	public static final String NET_MAIN_SERVER_URL_BASE = "http://"+NET_MAIN_SERVER_IP+":"+NET_MAIN_SERVER_PORT+"/HikCMS/mobile/";
	public static final String NET_AES_PASSWORD = "123456";
	
	/**
	 * 网络配置萤石
	 */
	public static final String NET_EZ_MAIN_SERVER_IP = "open.ys7.com";
	public static final int NET_EZ_MAIN_SERVER_PORT = 80;
	public static final String NET_EZ_MAIN_SERVER_URL_BASE = "https://"+NET_MAIN_SERVER_IP+":"+NET_MAIN_SERVER_PORT+"/api/method/";
	public static final String NET_EZ_PROTOCOL_VERION = "1.0";//协议版本
//	 public static String APP_KEY = "8698d52f6ac34929b5286698fe7a10e8";
//	 public static String SECRET_KEY = "32be2dea4158a84ef4294a126038c90f";
	public static String NET_EZ_APP_KEY = "d185b8544d81482f81094d45d53ecccd";
	public static String NET_EZ_SECRET_KEY = "479cedc0383a101175647e7632d88aa2";
	

	
	/**
	 * 当前使用的sdk版本号
	 */
	public static final int SYSTEM_SDK_VERSION = VERSION.SDK_INT;

	/**
	 * 默认密码，当检测到用户使用默认密码登陆，则要求他修改
	 */
	public static final String DEFAUL_TPASSWORD_FOR_CHECK="111111";
	
	
	private static AppApplication instance;

	public static String API_URL = "https://open.ys7.com";
	public static String WEB_URL = "https://auth.ys7.com";

	

	private List<WeakReference<Activity>> mRuningActivityList = new LinkedList<WeakReference<Activity>>();
	private DisplayImageOptions appDefaultDisplayImageOptions;
	@Override
	public void onCreate() {
		super.onCreate();

		initLog();

		Config.LOGGING = true;
		EzvizAPI.init(this, NET_EZ_APP_KEY);
		EzvizAPI.getInstance().setServerUrl(API_URL, WEB_URL);
		Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(
				this));
		//初始化httpclient
		initNetwork();
		//初始化图片加载
		initImageLoader();
	

		instance = this;
	}
	
	
	private void initNetwork(){
		HttpDataProvider.getInstance();
	}
	
	private void initImageLoader() {
		String absoluteCachePath =getExternalCacheDir() + CACHE_PATH;

		
		appDefaultDisplayImageOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.default_pic) // 设置图片在下载期间显示的图片
				.showImageForEmptyUri(R.drawable.default_pic)// 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.default_pic) // 设置图片加载/解码过程中错误时候显示的图片
				.cacheInMemory(true)// 设置下载的图片是否缓存在内存中
				.cacheOnDisk(true)// 设置下载的图片是否缓存在SD卡中
				.considerExifParams(true) // 是否考虑JPEG图像EXIF参数（旋转，翻转）
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.ARGB_8888)// 设置图片的解码类型//
				.resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
				.displayer(new FadeInBitmapDisplayer(100))// 是否图片加载好后渐入的动画时间
				.build();// 构建完成

		// 初始化imageloader
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this)
				.memoryCacheExtraOptions(480, 800)
				// max width, max height，即保存的每个缓存文件的最大长宽
				.threadPoolSize(3)
				// 线程池内加载的数量
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
				// You can pass your own memory cache
				// implementation/你可以通过自己的内存缓存实现
				.memoryCacheSize(
						1024 * 1024 * ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE))
								.getMemoryClass() / 3)
				.diskCacheSize(50 * 1024 * 1024)
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				// 将保存的时候的URI名称用MD5 加密
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.diskCacheFileCount(100)
				// 缓存的文件数量
				.diskCache(new UnlimitedDiskCache(new File(absoluteCachePath)))
				// 自定义缓存路径
				.defaultDisplayImageOptions(appDefaultDisplayImageOptions)
				.imageDownloader(
						new BaseImageDownloader(this, 5 * 1000, 30 * 1000)) // 可以用，但是没有配置new ApacheImageDownloader(this, 5 * 1000, 30 * 1000))
																				//connectTimeout
																				// (5
																				// s),
																				// readTimeout
																				// (30
																				// s)超时时间
				.writeDebugLogs() // TODO Remove for release app
				.build();// 开始构建
		ImageLoader.getInstance().init(config);
	}

	private void initLog() {
		LogConfigurator logConfigurator = new LogConfigurator();
		logConfigurator.setFileName(this.getFilesDir().getAbsolutePath()
				+ File.separator + "jiaxiaotong.log");
		/*
		 * Environment.getExternalStorageDirectory() + File.separator + "MyApp"
		 * + File.separator + "logs" + File.separator + "log4j.txt"
		 */
		logConfigurator.setRootLevel(Level.DEBUG);
		logConfigurator.setLevel("org.apache", Level.ERROR);
		logConfigurator.setFilePattern("%d %-5p [%c{2}]-[%L] %m%n");
		logConfigurator.setMaxFileSize(1024 * 1024 * 5);
		logConfigurator.setImmediateFlush(true);
		logConfigurator.configure();
		Logger log = Logger.getLogger(AppApplication.class);
		log.info("Jiaxiaotong Application Created");
	}

	public void pushActivity(Activity activity) {

		for (int i = mRuningActivityList.size() - 1; i >= 0; i--) {
			WeakReference<Activity> wrAcTmp = mRuningActivityList.get(i);
			if (wrAcTmp.get() != null && wrAcTmp.get() == activity) {
				return;
			}
		}
		WeakReference<Activity> wrAc = new WeakReference<Activity>(activity);
		mRuningActivityList.add(wrAc);
	}

	/**
	 * 删除activity,和已经回收的activity
	 * 
	 * @param activity
	 */
	public void popActivity(Activity activity) {
		Args.check(activity != null, "activity是空");
		for (int i = mRuningActivityList.size() - 1; i >= 0; i--) {
			WeakReference<Activity> wrAc = mRuningActivityList.get(i);
			if (wrAc.get() == null || wrAc.get() == activity) {
				mRuningActivityList.remove(wrAc);
			}
		}
	}
	
	/**
	 * 获取最上端的activity，并从缓存中删除
	 * 
	 * @param activity
	 */
	public Activity popActivity() {
		for (int i = mRuningActivityList.size() - 1; i >= 0; i--) {
			WeakReference<Activity> wrAc = mRuningActivityList.get(i);
			if (wrAc.get() != null) {
				mRuningActivityList.remove(wrAc);
				return wrAc.get();
			}
		}
		return null;
	}

	public List<WeakReference<Activity>> getmRuningActivityList() {
		return mRuningActivityList;
	}
	


	/**
	 * 获取全局application
	 * 
	 * @return
	 */
	public static AppApplication getApplication() {
		return instance;
	}

	/**
	 * 获取应用默认的图片加载设置
	 * @return
	 */
	public DisplayImageOptions getAppDefaultDisplayImageOptions() {
		return appDefaultDisplayImageOptions;
	}
	
	

}
