package com.hikvision.parentdotworry.costomui;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.hikvision.parentdotworry.R;
import com.hikvision.parentdotworry.bean.AdvertisementInfo;
import com.hikvision.parentdotworry.plug.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.hikvision.parentdotworry.plug.universalimageloader.core.DisplayImageOptions;
import com.hikvision.parentdotworry.plug.universalimageloader.core.ImageLoader;
import com.hikvision.parentdotworry.plug.universalimageloader.core.ImageLoaderConfiguration;
import com.hikvision.parentdotworry.plug.universalimageloader.core.assist.ImageScaleType;
import com.hikvision.parentdotworry.plug.universalimageloader.core.assist.QueueProcessingType;
import com.hikvision.parentdotworry.plug.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.hikvision.parentdotworry.utils.EmptyUtil;
import com.hikvision.parentdotworry.utils.ImageUtils;


/**
 * ViewPager实现的轮播图广告自定义视图，如京东首页的广告轮播图效果；
 * 既支持自动轮播页面也支持手势滑动切换页面
 * 
 *
 */

public class SlideShowView extends FrameLayout {
	
	// 使用universal-image-loader插件读取网络图片，需要工程导入universal-image-loader-1.8.6-with-sources.jar
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private static final int SILDING_RIGHT = 1;
	private static final int SILDING_LEFT = 2;
	
	private int silding_direction = SILDING_RIGHT;

    //轮播图图片数量
    //自动轮播的时间间隔
    private final static int TIME_INTERVAL = 10;
    //自动轮播启用开关
    private final static boolean isAutoPlay = true; 
    
    //自定义轮播图的资源
    List<AdvertisementInfo> adList;
    
    //放轮播图片的ImageView 的list
    private List<ImageView> imageViewsList;
    //放圆点的View的list
    private List<View> dotViewsList;

    private Bitmap bmDotBlur;

    private Bitmap bmDotFocus;
    
    private ViewPagerInToughView viewPager;
    //当前轮播页
    private int currentItem  = 0;
    
    private Context context;
    
    //Handler
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(viewPager!=null){
            	if(silding_direction == SILDING_RIGHT){
            		currentItem = (currentItem+1)%imageViewsList.size();
            	}else{
            		currentItem = (imageViewsList.size()+currentItem-1)%imageViewsList.size();
            	}
            	viewPager.setCurrentItem(currentItem);
            }
            sendEmptyMessageDelayed(0, TIME_INTERVAL*1000);
        }
        
    };
    
    public SlideShowView(Context context) {
        this(context,null);
    }
    public SlideShowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public SlideShowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;

		initImageLoader(context);
		
		init(new ArrayList<AdvertisementInfo>());
		
        if(isAutoPlay){
            startPlay();
        }
        
    }

  
    /**
     * 初始化相关Data
     */
    public void init(List<AdvertisementInfo> adList){
        imageViewsList = new ArrayList<ImageView>();
        dotViewsList = new ArrayList<View>();
        this.adList = adList;
        
        int dotSize=context.getResources().getDimensionPixelSize(R.dimen.message_list_adv_slide_dot_size);

        bmDotBlur = ImageUtils.generateCircleBitmap(dotSize/2, Color.argb(100, 255, 255, 255));
        bmDotFocus = ImageUtils.generateCircleBitmap(dotSize/2, Color.argb(255, 255, 255, 255));
        // 一步任务获取图片
        initUI(context);
    }
    /**
     * 初始化Views等UI
     */
    private void initUI(Context context){
    	if(EmptyUtil.isEmpty(adList))
    		return;
    	
        LayoutInflater.from(context).inflate(R.layout.layout_slideshow, this, true);
        
        LinearLayout dotLayout = (LinearLayout)findViewById(R.id.dotLayout);
        dotLayout.removeAllViews();
        
        // 热点个数与图片特殊相等
        for (int i = 0; i < adList.size(); i++) {
        	ImageView view =  new ImageView(context);
        	view.setTag(adList.get(i));
//        	if(i==0)//给一个默认图
//        		view.setBackgroundResource(R.drawable.appmain_subject_1);
        	view.setScaleType(ScaleType.FIT_XY);
        	imageViewsList.add(view);
        	
        	ImageView dotView =  new ImageView(context);
        	int dotSize=context.getResources().getDimensionPixelSize(R.dimen.message_list_adv_slide_dot_size);
        	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
        			dotSize,
        			dotSize);
        	params.leftMargin = dotSize/6;
			params.rightMargin = dotSize/6;
			
			dotView.setImageBitmap(bmDotBlur);
			if(i==0){
				dotView.setImageBitmap(bmDotFocus);
			}
			dotLayout.addView(dotView, params);
        	dotViewsList.add(dotView);
		}
        
        viewPager = (ViewPagerInToughView) findViewById(R.id.viewPager);
        viewPager.setFocusable(true);
        PagerAdapter pa = viewPager.getAdapter();
        if(pa==null){
        	pa=new MyPagerAdapter();
        	viewPager.setAdapter(pa);
        }
        viewPager.setOnPageChangeListener(new MyPageChangeListener());
        pa.notifyDataSetChanged();
    }
    
   
    /**
     * 填充ViewPager的页面适配器
     * 
     */
    private class MyPagerAdapter  extends PagerAdapter{

        @Override
        public void destroyItem(View container, int position, Object object) {
        	
        	Drawable drawable = ((ImageView)object).getDrawable();
        	 if (drawable != null) {
                 //解除drawable对view的引用
                 drawable.setCallback(null);
             }
            ((ViewPager)container).removeView((ImageView)object);
        }

        @Override
        public Object instantiateItem(View container, int position) {
        	ImageView imageView = imageViewsList.get(position);
        	final AdvertisementInfo ai = (AdvertisementInfo)imageView.getTag();
        	
        	DisplayImageOptions options;  
		    options = new DisplayImageOptions.Builder()  
		     //.showImageOnLoading(R.drawable.ic_launcher) //设置图片在下载期间显示的图片  
		     .showImageForEmptyUri(R.drawable.ic_launcher)//设置图片Uri为空或是错误的时候显示的图片  
		    .showImageOnFail(R.drawable.ic_launcher)  //设置图片加载/解码过程中错误时候显示的图片
		    .cacheInMemory(true)//设置下载的图片是否缓存在内存中  
		    .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中  
		    .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
		    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示  
		    .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//  
//		    .decodingOptions(
//		    		decodingOptions)//设置图片的解码配置  
		    //.delayBeforeLoading(int delayInMillis)//int delayInMillis为你设置的下载前的延迟时间
		    //设置图片加入缓存前，对bitmap进行设置  
		    .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位  
		    .displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间  
		    .build();//构建完成  
			imageLoader.displayImage(ai.getPicUrl() + "", imageView, options);
			
			imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent it = new Intent( Intent.ACTION_VIEW );
					it.setData( Uri.parse(ai.getContent()) );
					it = Intent.createChooser( it, null );
					context.startActivity( it );
				}
			});
            ((ViewPager)container).addView(imageViewsList.get(position));
            return imageViewsList.get(position);
        }

        @Override
        public int getCount() {
            
            return imageViewsList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            
            return arg0 == arg1;
        }
        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
            

        }

        @Override
        public Parcelable saveState() {
            
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
            

        }

        @Override
        public void finishUpdate(View arg0) {
            
            
        }
        
    }
    /**
     * ViewPager的监听器
     * 当ViewPager中页面的状态发生改变时调用
     * 
     */
    private class MyPageChangeListener implements OnPageChangeListener{

        @Override
        public void onPageScrollStateChanged(int arg0) {}

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        	
        }
      

        @Override
        public void onPageSelected(int pos) {
            
            
            currentItem = pos;
            for(int i=0;i < dotViewsList.size();i++){
                if(i == pos){
                    ((ImageView)dotViewsList.get(pos)).setImageBitmap(bmDotFocus);
                }else {
                    ((ImageView)dotViewsList.get(i)).setImageBitmap(bmDotBlur);
                }
            }
           
            if(pos == imageViewsList.size()-1){
            	silding_direction = SILDING_LEFT;
            }
            if(pos == 0){
            	silding_direction = SILDING_RIGHT;
            }
            stopPlay();
            startPlay();
        }
        
    }
    
    /**
     * 开始轮播图切换
     */
    public void startPlay(){
    	handler.sendEmptyMessageDelayed(0, TIME_INTERVAL*1000);
    }
    /**
     * 停止轮播图切换
     */
    public void stopPlay(){
    	 handler.removeMessages(0);
    }

    
    /**
     * 销毁ImageView资源，回收内存
     * 
     */
    public void destoryBitmaps() {
    	if(EmptyUtil.isEmpty(adList)){
    		return;
    	}
        for (int i = 0; i < adList.size(); i++) {
            ImageView imageView = imageViewsList.get(i);
            Drawable drawable = imageView.getDrawable();
            if (drawable != null) {
                //解除drawable对view的引用
                drawable.setCallback(null);
            }
        }
    }
 


	
	/**
	 * ImageLoader 图片组件初始化
	 * 
	 * @param context
	 */
	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration
				.Builder(context)
			.threadPriority(Thread.NORM_PRIORITY - 2)
			.denyCacheImageMultipleSizesInMemory()
			.diskCacheFileNameGenerator(new Md5FileNameGenerator())
			.tasksProcessingOrder(QueueProcessingType.LIFO)
			.writeDebugLogs() // Remove
																																																																								// for
																																																																								// release
																																																																								// app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
}