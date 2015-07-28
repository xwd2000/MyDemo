package com.hikvision.parentdotworry.dataprovider.httpdata;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hikvision.parentdotworry.application.AppApplication;
import com.hikvision.parentdotworry.bean.AdvertisementInfo;
import com.hikvision.parentdotworry.bean.ChildCaptureInfo;
import com.hikvision.parentdotworry.bean.ChildInfo;
import com.hikvision.parentdotworry.bean.MessageBean;
import com.hikvision.parentdotworry.bean.NomalTime;
import com.hikvision.parentdotworry.bean.Pagination;
import com.hikvision.parentdotworry.consts.AppConst;
import com.hikvision.parentdotworry.dataprovider.httpdata.bean.Data;
import com.hikvision.parentdotworry.dataprovider.httpdata.bean.ResponseResult;
import com.hikvision.parentdotworry.dataprovider.httpdata.bean.ResponseStatus;
import com.hikvision.parentdotworry.dataprovider.httpdata.core.ConstParam;
import com.hikvision.parentdotworry.dataprovider.httpdata.core.HttpCore;
import com.hikvision.parentdotworry.dataprovider.httpdata.util.ParamUtils;
import com.hikvision.parentdotworry.exception.AppBaseException;
import com.hikvision.parentdotworry.exception.AppError;
import com.hikvision.parentdotworry.exception.AppSystemException;
import com.hikvision.parentdotworry.exception.AppUserException;
import com.hikvision.parentdotworry.utils.DateUtil;
import com.hikvision.parentdotworry.utils.MapUtil;
import com.hikvision.parentdotworry.utils.QEncodeUtil;
import com.hikvision.parentdotworry.vo.ChildState;

public class HttpDataProvider {

	private static Logger logger = Logger.getLogger(HttpDataProvider.class);
	private String MAIN_SERVER_URL_BASE = AppApplication.NET_MAIN_SERVER_URL_BASE;

	public static final String NET_AES_PASSWORD = AppApplication.NET_AES_PASSWORD;
	private static HttpDataProvider instance = new HttpDataProvider();
	private static final String CLIENT_KEY="HttpDataProvider";
	
	private HttpCore httpCore;
	
	

	private HttpDataProvider() {
		//初始化连接服务器的httpclient
		httpCore=HttpCore.getInstance(CLIENT_KEY);
		httpCore.init(MAIN_SERVER_URL_BASE);
	}

	public static HttpDataProvider getInstance() {
		return instance;

	}



	public String post(String url, Map<String, Object> param)
			throws IOException {
		String method=url.substring(url.lastIndexOf('/')+1);
		String encryptedParam = ParamUtils.generateEncryptedParam(param,
				NET_AES_PASSWORD,method);
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

	private static <T> T getResult(ResponseResult<T> responseResult)
			throws AppBaseException {
		if (responseResult.getStatus() != null
				&& responseResult.getStatus().getStatusCode() == 0) {
			return responseResult.getResult();
		} else {
			ResponseStatus status = responseResult.getStatus();
			if (status == null) {
				throw new AppSystemException(
						AppError.NET_WORK_ERROR_NO_STATUS_RETURN);
			} else if (status.getStatusCode() == ConstParam.ERROR_UNKNOW) {
				throw new AppSystemException(AppError.NET_WORK_ERROR_UNKNOW,
						status.getDescription());
			}

			else if (status.getStatusCode() == ConstParam.ARGUMENT_ERROR) {
				throw new AppUserException(AppError.USER_ERROR_ARGUMENT,
						status.getDescription());
			}else if (status.getStatusCode() == ConstParam.SIGN_FIAL_CODE) {
				throw new AppSystemException(AppError.NET_WORK_ERROR_SIGN_VALIDATE_ERROR,
						status.getDescription());
			}

			throw new AppSystemException(AppError.NET_WORK_ERROR_UNKNOW);
		}

	}

	/**
	 * 获取孩子信息
	 * 
	 * @param phone
	 * @return
	 * @throws IOException
	 * @throws AppUserException
	 */
	public List<ChildInfo> getChildInfoList(String phone) throws IOException,
			AppBaseException {
		String url = MAIN_SERVER_URL_BASE + "getChildInfoList";
		Map<String, Object> params = MapUtil.generateMap("phone", phone);
		String responseBody = post(url, params);
		if (responseBody == null) {
			return null;
		}
		ResponseResult<Data<List<ChildInfo>>> rr = new Gson().fromJson(
				responseBody,
				new TypeToken<ResponseResult<Data<List<ChildInfo>>>>() {
				}.getType());
		return getResult(rr).getData();
	}

	/**
	 * 获取广告
	 * 
	 * @param phone
	 * @param childId
	 * @return
	 * @throws IOException
	 * @throws AppUserException
	 */
	public List<AdvertisementInfo> getAdvertisement(Integer childId,
			String phone) throws IOException, AppBaseException {
		String url = MAIN_SERVER_URL_BASE + "getAdvertisement";
		Map<String, Object> params = MapUtil.generateMap("phone", phone,
				"childId", childId);
		String responseBody = post(url, params);
		if (responseBody == null) {
			return null;
		}
		ResponseResult<Data<List<AdvertisementInfo>>> rr = new Gson().fromJson(
				responseBody,
				new TypeToken<ResponseResult<Data<List<AdvertisementInfo>>>>() {
				}.getType());
		return getResult(rr).getData();
	}

	/**
	 * 获取公告消息
	 * 
	 * @param childId
	 * @param phone
	 * @param pageSize
	 * @param pageStart
	 * @return
	 * @throws IOException
	 * @throws AppUserException
	 */
	public Pagination<MessageBean> getNoticeDetail(Integer childId,
			String phone, Integer pageSize, Integer pageStart)
			throws IOException, AppBaseException {
		String url = MAIN_SERVER_URL_BASE + "getNoticeDetail";
		Map<String, Object> params = MapUtil.generateMap("childId", childId,
				"phone", phone, "pageSize", pageSize, "pageStart", pageStart);
		String responseBody = post(url, params);
		if (responseBody == null) {
			return null;
		}
		ResponseResult<Pagination<MessageBean>> rr = new Gson().fromJson(
				responseBody,
				new TypeToken<ResponseResult<Pagination<MessageBean>>>() {
				}.getType());
		return getResult(rr);
	}

	/**
	 * 获取正常时间
	 * 
	 * @param childId
	 *            孩子id
	 * @return
	 * @throws IOException
	 * @throws AppUserException
	 */
	public NomalTime getNomalTime(Integer childId) throws IOException,
			AppBaseException {
		String url = MAIN_SERVER_URL_BASE + "getNomalTime";
		Map<String, Object> params = MapUtil.generateMap("childId", childId);
		String responseBody = post(url, params);
		if (responseBody == null) {
			return null;
		}

		ResponseResult<Data<NomalTime>> rr = new Gson().fromJson(responseBody,
				new TypeToken<ResponseResult<Data<NomalTime>>>() {
				}.getType());

		return getResult(rr).getData();

	}

	/**
	 * 获取孩子状态
	 * 
	 * @param phone
	 *            家长电话
	 * @param childId
	 *            孩子id
	 * @return
	 * @throws IOException
	 * @throws AppUserException
	 */
	public ChildState getChildState(Integer childId, String phone)
			throws IOException, AppBaseException {
		String url = MAIN_SERVER_URL_BASE + "getChildState";
		Map<String, Object> params = MapUtil.generateMap("childId", childId,
				"phone", phone);
		String responseBody = post(url, params);
		if (responseBody == null) {
			return null;
		}
		ResponseResult<Data<ChildState>> rr = new Gson().fromJson(responseBody,
				new TypeToken<ResponseResult<Data<ChildState>>>() {
				}.getType());

		return getResult(rr).getData();
	}

	/**
	 * 获取孩子状态
	 * 
	 * @param phone
	 *            家长电话
	 * @param childId
	 *            孩子id
	 * @return
	 * @throws IOException
	 * @throws AppUserException
	 */
	public Pagination<ChildCaptureInfo> getChildRecord(Integer childId,
			String phone, Date startTime, Date endTime, int pageSize,
			int pageStart) throws IOException, AppBaseException {
		String url = MAIN_SERVER_URL_BASE + "getChildRecord";
		Map<String, Object> params = MapUtil.generateMap("childId", childId,
				"phone", phone, "startTime", DateUtil.dateToString(startTime,
						AppConst.PATTERN_DATE_TIME_FROM_NET), "endTime",
				DateUtil.dateToString(endTime,
						AppConst.PATTERN_DATE_TIME_FROM_NET), "pageSize",
				pageSize, "pageStart", pageStart);
		String responseBody = post(url, params);
		if (responseBody == null) {
			return null;
		}
		Gson gson = new GsonBuilder().setDateFormat(
				AppConst.PATTERN_DATE_TIME_FROM_NET).create();
		// 转化Date变量
		ResponseResult<Pagination<ChildCaptureInfo>> rr = gson.fromJson(
				responseBody,
				new TypeToken<ResponseResult<Pagination<ChildCaptureInfo>>>() {
				}.getType());
		return getResult(rr);
	}
}
