package com.hikvision.parentdotworry.dataprovider.httpdata.core;



public class ConstParam {

	//正常状态返回结果
	public static final int SUCCESS_CODE = 0; 
	public static final String SUCCESS_DECRIPTION = "返回结果成功";
	
	//服务器查询数据出错
	public static final int NO_ERROR = 1; 
	public static final String NO_ERROR_DECRIPTION = "返回对象为空，查询出错";
	

	//签名认证失败状态码
	public static final int SIGN_FIAL_CODE = 2; 
	public static final String SIGN_FIAL_DECRIPTION = "签名认证失败";	
	
	//签名认证不存在
	public static final int SIGN_NOT_EXIST_CODE = 3; 
	public static final String SIGN_NOT_EXIST_DECRIPTION = "请求签名不存在";
	
	//签名认证计算时出错
	public static final int SIGN_CALC_ERROR_CODE = 4; 
	public static final String SIGN_CALC_ERROR_DECRIPTION = "计算签名出错";
	//查询无数据
	public static final int NO_DATA_CODE = 5; 
	public static final String NO_DATA_DECRIPTION = "查询无数据";
	
	//查询无数据
	public static final int NO_METHOD_CODE = 6; 
	public static final String NO_METHOD_DECRIPTION = "请求方法名不匹配";
	
	//参数错误
	public static final int ARGUMENT_ERROR = 7; 
	public static final String ARGUMENT_ERROR_DECRIPTION = "参数错误";
	
	public static final int ERROR_UNKNOW = 8; 
	public static final String ERROR_UNKNOW_DECRIPTION = "未知错误";
	
	
}
