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
import android.app.Application;
import android.os.Build.VERSION;
import android.os.Environment;

import com.hikvision.parentdotworry.R;
import com.hikvision.parentdotworry.exception.CustomExceptionHandler;
import com.hikvision.parentdotworry.plug.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.hikvision.parentdotworry.plug.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.hikvision.parentdotworry.plug.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.hikvision.parentdotworry.plug.universalimageloader.core.DisplayImageOptions;
import com.hikvision.parentdotworry.plug.universalimageloader.core.ImageLoader;
import com.hikvision.parentdotworry.plug.universalimageloader.core.ImageLoaderConfiguration;
import com.hikvision.parentdotworry.plug.universalimageloader.core.assist.QueueProcessingType;
import com.hikvision.parentdotworry.plug.universalimageloader.utils.StorageUtils;
import com.hikvision.parentdotworry.utils.ApacheImageDownloader;
import com.hikvision.parentdotworry.utils.Args;
import com.videogo.constant.Config;
import com.videogo.openapi.EzvizAPI;

import de.mindpipe.android.logging.log4j.LogConfigurator;


/**
 * 自定义应用
 * @author xuweidong
 * @data 2014-7-12
 */
public class AppApplication extends Application {
    //开放平台申请的APP key & secret key
    //open
//    public static String APP_KEY = "8698d52f6ac34929b5286698fe7a10e8";
//    public static String SECRET_KEY = "32be2dea4158a84ef4294a126038c90f";
//    APP Key： d185b8544d81482f81094d45d53ecccd
//    Secret Key： 479cedc0383a101175647e7632d88aa2
    public static String APP_KEY = "d185b8544d81482f81094d45d53ecccd";
    public static String SECRET_KEY = "479cedc0383a101175647e7632d88aa2";
    private List<WeakReference<Activity>> mRuningActivityList=new LinkedList<WeakReference<Activity>>();
    
    
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
	 * 网络配置
	 */
	public static final String NET_MAIN_SERVER_URL_BASE="http://10.20.32.33/mobile/";
	public static final String NET_AES_PASSWORD="123456";
	
	/**
	 * 应用application常量
	 */
	/**
	 * 当前使用的sdk版本号
	 */
	public static final int SYSTEM_SDK_VERSION = VERSION.SDK_INT;
	
	private static Application instance;
	
	
    public static String API_URL = "https://open.ys7.com";
    public static String WEB_URL = "https://auth.ys7.com";
    
    
    
    @Override
    public void onCreate() {
        super.onCreate();
       
        initLog();
        
        Config.LOGGING = true;
		EzvizAPI.init(this, APP_KEY); 
        EzvizAPI.getInstance().setServerUrl(API_URL, WEB_URL);     
        Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(this));
        
        File cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(), CACHE_PATH);  
        
        //初始化imageloader
        ImageLoaderConfiguration config = new ImageLoaderConfiguration  
        	    .Builder(this)  
        	    .memoryCacheExtraOptions(480, 800) // max width, max height，即保存的每个缓存文件的最大长宽  
        	    //.discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75, null) // Can slow ImageLoader, use it carefully (Better don't use it)/设置缓存的详细信息，最好不要设置这个  
        	    .threadPoolSize(3)//线程池内加载的数量  
        	    .threadPriority(Thread.NORM_PRIORITY - 2)  
        	    .denyCacheImageMultipleSizesInMemory()  
        	    .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现  
        	    .memoryCacheSize(2 * 1024 * 1024)    
        	    .diskCacheSize(50 * 1024 * 1024)    
        	    .diskCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密  
        	    .tasksProcessingOrder(QueueProcessingType.LIFO)  
        	    .diskCacheFileCount(100) //缓存的文件数量  
        	    .diskCache(new UnlimitedDiskCache(cacheDir))//自定义缓存路径  
        	    .defaultDisplayImageOptions(DisplayImageOptions.createSimple())  
        	    .imageDownloader(new ApacheImageDownloader(this, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间  
        	    .writeDebugLogs() //TODO Remove for release app
        	    	
        	    .build();//开始构建  
        ImageLoader.getInstance().init(config);
        instance = this;
    }
    
    public void initLog(){
    	 LogConfigurator logConfigurator = new LogConfigurator();  
         logConfigurator.setFileName(
         		this.getFilesDir().getAbsolutePath()+
         		File.separator+"jiaxiaotong.log");  
         /*Environment.getExternalStorageDirectory()   
         + File.separator + "MyApp" + File.separator + "logs"  
         + File.separator + "log4j.txt"
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
    
    
    public void pushActivity(Activity activity){
    	
    	for(int i=mRuningActivityList.size()-1;i>=0;i--){
    		WeakReference<Activity> wrAcTmp = mRuningActivityList.get(i);
	    	if(wrAcTmp.get()!=null&&wrAcTmp.get()==activity){
	    		return;
	    	}
    	}
    	WeakReference<Activity> wrAc=new WeakReference<Activity>(activity);
    	mRuningActivityList.add(wrAc);
    }
    
    /**
     * 删除activity,和已经回收的activity
     * @param activity
     */
    public void popActivity(Activity activity){
    	Args.check(activity!=null, "activity是空");
    	for(int i=mRuningActivityList.size()-1;i>=0;i--){
    		WeakReference<Activity> wrAc = mRuningActivityList.get(i);
	    	if(wrAc.get()==null||wrAc.get()==activity){
	    		mRuningActivityList.remove(wrAc);
	    	}
    	}
    }


	public List<WeakReference<Activity>> getmRuningActivityList() {
		return mRuningActivityList;
	}

	/**
	 * 获取全局application
	 * @return
	 */
	public static Application getApplication(){
		return instance;
	}

    
}
