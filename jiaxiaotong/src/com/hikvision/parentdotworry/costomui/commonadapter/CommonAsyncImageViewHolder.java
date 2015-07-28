package com.hikvision.parentdotworry.costomui.commonadapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hikvision.parentdotworry.costomui.commonadapter.util.ViewFinder;
import com.hikvision.parentdotworry.plug.universalimageloader.core.DisplayImageOptions;
import com.hikvision.parentdotworry.plug.universalimageloader.core.ImageLoader;
import com.hikvision.parentdotworry.plug.universalimageloader.core.ImageLoaderConfiguration;
import com.hikvision.parentdotworry.plug.universalimageloader.core.listener.ImageLoadingListener;

public class CommonAsyncImageViewHolder extends CommonViewHolder{
	
    private ImageLoader mImageLoader;
    
	/**
	 * TODO newFixedThreadPool=Constant.CPU_NUMS * Constant.POOL_SIZE)
	 * @param context
	 * @param parent
	 * @param layoutId
	 */
	protected CommonAsyncImageViewHolder(Context context, ViewGroup parent,
			int layoutId) {
		super(context, parent, layoutId);
		mImageLoader = ImageLoader.getInstance();
        if(mImageLoader.isInited()==false){
        	mImageLoader.init(ImageLoaderConfiguration.createDefault(context));
        }
        
	}
	
    /**
     * 获取CommonViewHolder，当convertView为空的时候从布局xml装载item view,
     * 并且将该CommonViewHolder设置为convertView的tag, 便于复用convertView.
     * 
     * @param context Context
     * @param convertView Item view
     * @param layoutId 布局资源id, 例如R.layout.my_listview_item.
     * @return 通用的CommonViewHolder实例
     */
    public static CommonAsyncImageViewHolder getViewHolder(Context context, View convertView,
            ViewGroup parent, int layoutId) {

        context = (context == null && parent != null) ? parent.getContext() : context;
        CommonAsyncImageViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new CommonAsyncImageViewHolder(context, parent, layoutId);
        } else {
            viewHolder = (CommonAsyncImageViewHolder) convertView.getTag();
        }

        // 将当前item view设置为ViewFinder要查找的root view, 这一步不能搞错，否则查找不到对象的view
        // ViewFinder.initContentView(viewHolder.getContentView());

        return viewHolder;
    }

    public void clearImageCache() {
        mImageLoader.clearMemoryCache();
    }
    
    /**
     * 为ImageView设置图片，该方法会使用imageLoader异步加载图片，建议destory的时候adapter清空缓存
     * @param imageViewId ImageView的id, 例如R.id.my_imageview
     * @param bmp Bitmap图片
     */
    public void setImageForView(int imageViewId, String uri) {
    	ImageLoadingListener ill = null;
    	setImageForView(imageViewId,uri,ill);
    }

    
    /**
     * 为ImageView设置图片，该方法会使用imageLoader异步加载图片，建议destory的时候adapter清空缓存
     * @param imageViewId imageviewId
     * @param uri 加载地址
     * @param mImgLoadingListener 加载图片回调
     */
    public void setImageForView(int imageViewId, String uri,ImageLoadingListener imgLoadingListener) {
      
            DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheInMemory(true)//设置下载的图片是否缓存在内存中  
            .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
            .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
            .build();//构建完成                              
            setImageForView(imageViewId,uri,options,imgLoadingListener);
    }
    
    /**
     * 为ImageView设置图片，该方法会使用imageLoader异步加载图片，建议destory的时候adapter清空缓存
     * @param imageViewId imageviewId
     * @param uri 加载地址
     * @param options 图片设置
     */
    
    public void setImageForView(int imageViewId, String uri, DisplayImageOptions options) {
            setImageForView(imageViewId,uri,options,null);
    }
 
    
    /**
     * 为ImageView设置图片，该方法会使用imageLoader异步加载图片，建议destory的时候adapter清空缓存
     * @param imageViewId imageviewId
     * @param uri 加载地址
     * @param mImgLoadingListener 加载图片回调
     */
    public void setImageForView(int imageViewId, String uri,DisplayImageOptions options,ImageLoadingListener imgLoadingListener) {
        ImageView imageView = ViewFinder.findViewById(getContentView(), imageViewId);
        if (imageView != null) {
            // 依次从内存和sd中获取，如果没有则网络下载
            if(imgLoadingListener==null){
            	mImageLoader.displayImage(uri, imageView, options);
            }else{
            	mImageLoader.displayImage(uri, imageView, options, imgLoadingListener);
            }
        }
    }
}
