package com.hikvision.parentdotworry.dataprovider.httpdata;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hikvision.parentdotworry.application.AppApplication;
import com.hikvision.parentdotworry.bean.ChildInfo;
import com.hikvision.parentdotworry.bean.MessageBean;
import com.hikvision.parentdotworry.bean.NomalTime;
import com.hikvision.parentdotworry.bean.Pagination;
import com.hikvision.parentdotworry.dataprovider.httpdata.bean.Data;
import com.hikvision.parentdotworry.dataprovider.httpdata.bean.RequestParam;
import com.hikvision.parentdotworry.dataprovider.httpdata.bean.ResponseResult;
import com.hikvision.parentdotworry.dataprovider.httpdata.util.ParamUtils;
import com.hikvision.parentdotworry.exception.AppBaseException;
import com.hikvision.parentdotworry.exception.AppError;
import com.hikvision.parentdotworry.exception.AppUserException;
import com.hikvision.parentdotworry.exception.ServerResponseErrorException;
import com.hikvision.parentdotworry.utils.MapUtil;
import com.hikvision.parentdotworry.utils.QEncodeUtil;

public class HttpDataProvider {
	private static HttpDataProvider instance=new HttpDataProvider();
	private static Logger logger = Logger.getLogger(HttpDataProvider.class);
	private String MAIN_SERVER_URL_BASE = AppApplication.NET_MAIN_SERVER_URL_BASE;
	public static final String NET_AES_PASSWORD=AppApplication.NET_AES_PASSWORD;
	private static ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
        @Override
        public String handleResponse(
                final HttpResponse response) throws ClientProtocolException, IOException {
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                
                if(entity==null){
                	return null;
                }
                String encryptedResponse = EntityUtils.toString(entity);
                logger.debug("返回内容:"+encryptedResponse);
                	
                 try {
					//String responseData = QEncodeUtil.decrypt(encryptedResponse, NET_AES_PASSWORD);
					//return responseData;
					return encryptedResponse;
				} catch (Exception e) {
					logger.error(e.getMessage());
					return null;
				}
               
            } else {
            	logger.error("请求返回错误码: "+status);
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
        }
    };
    
	private HttpDataProvider(){}
	
	public static HttpDataProvider getInstance(){
		return instance;
	}
	

	public String post(String url,HttpEntity param) throws IOException{
		CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
        	HttpPost httpPost = new HttpPost(url);
        	httpPost.setEntity(param);
            String responseBody = httpclient.execute(httpPost, responseHandler);
            return responseBody;
        } finally {
            httpclient.close();
        }
	}
	public String post(String url,Map<String,Object> param) throws IOException{
		String encryptedParam = ParamUtils.generateEncryptedParam(
				param,NET_AES_PASSWORD);
		return post(url,new StringEntity(encryptedParam, HTTP.UTF_8));
	}
	
	private  static <T> T getResult(ResponseResult<T> responseResult) throws AppUserException{
//		  if(responseResult.getStatus()!=null&&responseResult.getStatus().getStatusCode()==0){
//	        	return responseResult.getResult();
//	        }else{
//	        	throw new AppUserException(AppError.USER_WORK_ERROR_NO_NOMAL_TIME);
//	        }
		  if(responseResult.getStatus()!=null&&responseResult.getStatus().getStatusCode()!=0){
			  throw new AppUserException(AppError.USER_WORK_ERROR_NO_NOMAL_TIME);
	        	
	        }else{
	        	return responseResult.getResult();
	        }
	}
	
	/**
	 * 获取孩子信息
	 * @param phone
	 * @return
	 * @throws IOException
	 * @throws AppUserException 
	 */
	public List<ChildInfo> getChildInfoList(String phone) throws IOException, AppUserException{
    	String url = MAIN_SERVER_URL_BASE+"getChildInfoList";
    	Map<String,Object> params =	MapUtil.generateMap("phone",phone);
        String responseBody=post(url,params);
        if(responseBody==null) {
        	return null;
        }
        ResponseResult<Data<List<ChildInfo>>> rr =new Gson().fromJson(responseBody,new TypeToken<ResponseResult<Data<List<ChildInfo>>>>(){}.getType());
        return getResult(rr).getData();
	}
	
	/**
	 * 获取公告消息
	 * @param childId
	 * @param phone
	 * @param pageSize
	 * @param pageStart
	 * @return
	 * @throws IOException
	 * @throws AppUserException 
	 */
	public Pagination<MessageBean> getNoticeDetail(Integer childId,String phone,Integer pageSize,Integer pageStart) throws IOException, AppUserException{
    	String url = MAIN_SERVER_URL_BASE+"getNoticeDetail";
    	Map<String,Object> params =	MapUtil.generateMap(
    			"childId",childId,
    			"phone",phone,
    			"pageSize",pageSize,
    			"pageStart",pageStart
    			);
        String responseBody=post(url,params);
        if(responseBody==null) {
        	return null;
        }
        ResponseResult<Pagination<MessageBean>> rr =new Gson().fromJson(responseBody,new TypeToken<ResponseResult<Pagination<MessageBean>>>(){}.getType());
        return getResult(rr);
	}
	
	/**
	 * 获取正常时间
	 * @param phone
	 * @return
	 * @throws IOException
	 * @throws AppUserException 
	 */
	public NomalTime getNomalTime(Integer childId) throws IOException, AppUserException{
    	String url = MAIN_SERVER_URL_BASE+"getNomalTime";
    	Map<String,Object> params =	MapUtil.generateMap(
    			"childId",childId);
        String responseBody=post(url,params);
        if(responseBody==null) {
        	return null;
        }
        ResponseResult<Data<NomalTime>> rr =new Gson().fromJson(responseBody,new TypeToken<ResponseResult<Data<NomalTime>>>(){}.getType());
       
        //return getResult(rr).getData();
        NomalTime t=new NomalTime();
        t.setStartTime("2015-7-10 8:00:00");
        t.setEndTime("2015-7-10 16:00:00");
        return t;
	}
	
}
