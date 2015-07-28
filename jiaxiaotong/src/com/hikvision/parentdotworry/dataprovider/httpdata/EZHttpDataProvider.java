package com.hikvision.parentdotworry.dataprovider.httpdata;

import java.io.IOException;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;

import com.hikvision.parentdotworry.application.AppApplication;
import com.hikvision.parentdotworry.dataprovider.httpdata.core.HttpCore;
import com.hikvision.parentdotworry.dataprovider.httpdata.ezbean.EzResponseResult;
import com.hikvision.parentdotworry.dataprovider.httpdata.util.ParamUtils;
import com.hikvision.parentdotworry.utils.MapUtil;
import com.hikvision.parentdotworry.utils.QEncodeUtil;

public class EZHttpDataProvider {

	private static Logger logger = Logger.getLogger(HttpDataProvider.class);
	private String MAIN_EZ_SERVER_URL_BASE = AppApplication.NET_EZ_MAIN_SERVER_URL_BASE;

	public static final String NET_AES_PASSWORD = AppApplication.NET_AES_PASSWORD;
	private static EZHttpDataProvider instance = new EZHttpDataProvider();
	private static final String CLIENT_KEY="HttpDataProvider";
	
	
	
	
	private HttpCore httpCore;
	
	

	private EZHttpDataProvider() {
		//初始化连接服务器的httpclient
		httpCore=HttpCore.getInstance(CLIENT_KEY);
		httpCore.init(MAIN_EZ_SERVER_URL_BASE);
	}

	public static EZHttpDataProvider getInstance() {
		return instance;
	}




	public String post(String url, Map<String, Object> param)
			throws IOException {
		
		String encryptedParam = ParamUtils.generateEncryptedParam(param,
				NET_AES_PASSWORD,"");
		String orginResponse = httpCore.post(url, new StringEntity(encryptedParam, HTTP.UTF_8));
		try {
			String responseData = QEncodeUtil.decrypt(
					orginResponse, NET_AES_PASSWORD);
			logger.debug("解码后:" + responseData);
			logger.debug("----------------------------------------");
			return responseData;
			// return encryptedResponse;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	private static <T> T getResult(EzResponseResult<T> ezResponseResult){
//			throws AppBaseException {
//		if (ezResponseResult.getStatus() != null
//				&& ezResponseResult.getStatus().getStatusCode() == 0) {
//			return ezResponseResult.getResult();
//		} else {
//			ResponseStatus status = ezResponseResult.getStatus();
//			if (status == null) {
//				throw new AppSystemException(
//						AppError.NET_WORK_ERROR_NO_STATUS_RETURN);
//			} else if (status.getStatusCode() == ConstParam.ERROR_UNKNOW) {
//				throw new AppSystemException(AppError.NET_WORK_ERROR_UNKNOW,
//						status.getDescription());
//			}
//
//			else if (status.getStatusCode() == ConstParam.ARGUMENT_ERROR) {
//				throw new AppUserException(AppError.USER_ERROR_ARGUMENT,
//						status.getDescription());
//			}
//
//			throw new AppSystemException(AppError.NET_WORK_ERROR_UNKNOW);
//		}
		return ezResponseResult.getData();
	}

	public String  login(String userName,String password) throws IOException{
		String intf = "token/accessToken/get";
		Map<String,Object> params = MapUtil.generateMap();
		post(MAIN_EZ_SERVER_URL_BASE+intf,params);
		
		return "";
	}
}
