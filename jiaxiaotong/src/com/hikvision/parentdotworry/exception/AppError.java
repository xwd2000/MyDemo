package com.hikvision.parentdotworry.exception;

import com.hikvision.parentdotworry.R;


public enum AppError{
	
	
	
	/*--------------------2000NETWORK-------------------------*/
	NET_WORK_ERROR(2000,R.string.error_network_2000_error),
	NET_WORK_ERROR_TIMEOUT(2001,R.string.error_network_2001_timeout),
	NET_WORK_ERROR_DECODE_RESPONSE(2002,R.string.error_network_2002_decode_response),
	NET_WORK_ERROR_NO_STATUS_RETURN(2003,R.string.error_network_2003_no_status_return),
	NET_WORK_ERROR_UNABLE_TO_CONNECT_SERVER(2004,R.string.error_network_2004_unable_to_connect_server),
	NET_WORK_ERROR_UNAVAILABLE(2005,R.string.error_network_2005_unavailable),
	NET_WORK_ERROR_SOCKET_TIMEOUT(2006,R.string.error_network_2006_socket_timeout),
	
	NET_WORK_ERROR_UNKNOW(2009,R.string.error_network_2009_error_unknow),
	NET_WORK_ERROR_SIGN_VALIDATE_ERROR(2010,R.string.error_network_2010_sign_validate_error),

	/*--------------------3000SERVERERROR-------------------------*/
	
	

	SYSTEM_ERROR_CHANGE_PWD_GET_SMS_CODE_ERROR(3201,R.string.error_system_3201_get_verify_code_error),
	SYSTEM_ERROR_CHANGE_PWD_ERROR(3202,R.string.error_system_3202_reset_password_error),
	
	/*--------------------9000USERERROR-------------------------*/
	USER_ERROR_NO_CHILD_INFO(9001,R.string.error_user_9001_no_child_info),
	USER_ERROR_NO_NOMAL_TIME(9002,R.string.error_user_9002_no_nomal_time),
	USER_ERROR_ARGUMENT(9002,R.string.error_user_9003_argument),
	
	//对应服务器代码：1014
	USER_ERROR_LOGIN_ERROR(9100,R.string.error_user_9100_login_error),
	USER_ERROR_LOGIN_PASSWORK_ERROR(9101,R.string.error_user_9101_login_password_error),
	USER_ERROR_LOGIN_ACCOUNT_OR_PASSWORK_EMPTY(9102,R.string.error_user_9102_login_account_or_password_empty),
	
	USER_ERROR_CHANGE_PWD_GET_SMS_CODE_PHONE_EMPTY(9201,R.string.error_user_9201_change_pwd_get_sms_code_phone_empty),
	USER_ERROR_CHANGE_PWD_GET_SMS_CODE_PHONE_ERROR(9202,R.string.error_user_9202_change_pwd_get_sms_code_phone_error),
	USER_ERROR_CHANGE_PWD_PASSWORD_DIFFERENT(9203,R.string.error_user_9203_change_pwd_password_different),
	USER_ERROR_CHANGE_PWD_VERIFY_CODE_EMPTY(9204,R.string.error_user_9204_change_pwd_verify_code_empty),
	USER_ERROR_CHANGE_PWD_NEW_PASSWORD_EMPTY(9205,R.string.error_user_9205_change_pwd_new_password_empty);
	
	
	

	
	
	
	
	public final int code;
	public final int message;
	
	private AppError(int code,int message){
		this.code=code;
		this.message=message;
	}
}
