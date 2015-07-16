package com.hikvision.parentdotworry.exception;

import com.hikvision.parentdotworry.R;


public enum AppError{
	
	
	
	/*--------------------2000NETWORK-------------------------*/
	NET_WORK_ERROR(2000,R.string.error_network_2000_error),
	NET_WORK_ERROR_TIMEOUT(2001,R.string.error_network_2001_timeout),
	NET_WORK_ERROR_DECODE_RESPONSE(2002,R.string.error_network_2002_decode_response),
	NET_WORK_ERROR_NO_STATUS_RETURN(2003,R.string.error_network_2003_no_status_return),

	
	NET_WORK_ERROR_UNKNOW(2009,R.string.error_network_2009_error_unknow),
	
	

	/*--------------------9000USERERROR-------------------------*/
	USER_ERROR_NO_CHILD_INFO(9001,R.string.error_user_9001_no_child_info),

	USER_ERROR_ARGUMENT(9002,R.string.error_user_9003_argument),
	USER_ERROR_NO_NOMAL_TIME(9002,R.string.error_user_9002_no_nomal_time);
	
	public final int code;
	public final int message;
	
	private AppError(int code,int message){
		this.code=code;
		this.message=message;
	}
}
