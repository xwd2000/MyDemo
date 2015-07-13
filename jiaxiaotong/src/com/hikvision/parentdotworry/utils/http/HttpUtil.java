package com.hikvision.parentdotworry.utils.http;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class HttpUtil {
	
	public static void requestTest() throws ClientProtocolException, IOException{
		CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpget = new HttpGet("http://baidu.com/");

            System.out.println("Executing request " + httpget.getRequestLine());

            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                @Override
                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        //System.out.println(EntityUtils.toString(entity));
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }

            };
            String responseBody = httpclient.execute(httpget, responseHandler);
            System.out.println("----------------------------------------");
            System.out.println(responseBody);
        } finally {
            httpclient.close();
        }
	}
	
	public static Bitmap requestPic(String imageUri) throws ClientProtocolException, IOException{
		DefaultHttpClient httpclient = new DefaultHttpClient();  
         
         HttpGet httpget = new HttpGet(imageUri);  

         try {  
             HttpResponse resp = httpclient.execute(httpget);  
             //判断是否正确执行  
             if (HttpStatus.SC_OK == resp.getStatusLine().getStatusCode()) {  
                 //将返回内容转换为bitmap  
                 HttpEntity entity = resp.getEntity();  
                 BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(entity);
                 InputStream is = bufferedHttpEntity.getContent();
                 BitmapFactory.Options options = new BitmapFactory.Options();
                 options.inJustDecodeBounds = true;
                 BitmapFactory.decodeStream(is, null, options);
                 
                 float realWidth = options.outWidth;
                 float realHeight = options.outHeight;
                 System.out.println("真实图片高度：" + realHeight + "宽度:" + realWidth);
                 // 计算缩放比&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;
                 int scale = 1;
                 if(realWidth != -1f && realHeight!= -1f){
                	 scale = (int) ((realHeight > realWidth ? realHeight : realWidth) / 100);
	                 if (scale <= 0)
	                 {
	                     scale = 1;
	                 }
                 }
                 
                 options.inSampleSize = scale;
                 options.inJustDecodeBounds = false;
                 // 注意这次要把options.inJustDecodeBounds 设为 false,这次图片是要读取出来的。&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;
                 is.reset();
                 Bitmap bitmap = BitmapFactory.decodeStream(is);

                 return bitmap;
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
