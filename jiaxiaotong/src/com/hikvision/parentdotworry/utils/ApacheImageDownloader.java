package com.hikvision.parentdotworry.utils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpGetHC4;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.util.Log;

import com.hikvision.parentdotworry.plug.universalimageloader.core.DisplayImageOptions;
import com.hikvision.parentdotworry.plug.universalimageloader.core.download.BaseImageDownloader;

public class ApacheImageDownloader extends BaseImageDownloader{

	public ApacheImageDownloader(Context context, int connectTimeout,
			int readTimeout) {
		super(context, connectTimeout, readTimeout);
	}

	public ApacheImageDownloader(Context context) {
		super(context);
	}
	
	/**
	 * Retrieves {@link InputStream} of image by URI (image is located in the network).
	 *
	 * @param imageUri Image URI
	 * @param extra    Auxiliary object which was passed to {@link DisplayImageOptions.Builder#extraForDownloader(Object)
	 *                 DisplayImageOptions.extraForDownloader(Object)}; can be null
	 * @return {@link InputStream} of image
	 * @throws IOException if some I/O error occurs during network request or if no InputStream could be created for
	 *                     URL.
	 */
	@Override
	protected InputStream getStreamFromNetwork(String imageUri, Object extra) throws IOException {
		 DefaultHttpClient httpclient = new DefaultHttpClient();  
         
		 HttpGetHC4 httpget = new HttpGetHC4(imageUri);  

         try {  
             HttpResponse resp = httpclient.execute(httpget);  
             //判断是否正确执行  
             if (HttpStatus.SC_OK == resp.getStatusLine().getStatusCode()) {  
                 //将返回内容转换为bitmap  
                 HttpEntity entity = resp.getEntity();  
                 BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(entity);
                 InputStream is = bufferedHttpEntity.getContent();
                 return is;
             }  
             Log.d("ApacheImageDownloader","HttpStatus = "+resp.getStatusLine().getStatusCode());
         } catch (Exception e) {  
             e.printStackTrace();
         } finally {  
             httpclient.getConnectionManager().shutdown();  
         }
		return null;  
         

		
	}



}
