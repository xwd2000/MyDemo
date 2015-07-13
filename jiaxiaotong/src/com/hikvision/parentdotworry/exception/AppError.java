package com.hikvision.parentdotworry.exception;

import com.hikvision.parentdotworry.R;


public enum AppError{
	
	
	
	/*--------------------2000NETWORK-------------------------*/
	NET_WORK_ERROR(2000,R.string.error_network_2000_error),
	NET_WORK_ERROR_TIMEOUT(2001,R.string.error_network_2001_timeout),
	NET_WORK_ERROR_DECODE_RESPONSE(2002,R.string.error_network_2002_decode_response),
	
	

	/*--------------------9000USERERROR-------------------------*/
	USER_WORK_ERROR_NO_CHILD_INFO(9001,R.string.error_user_9001_no_child_info),
	
	USER_WORK_ERROR_NO_NOMAL_TIME(9002,R.string.error_user_9002_no_nomal_time);
	
	public final int code;
	public final int message;
	
	private AppError(int code,int message){
		this.code=code;
		this.message=message;
	}
}
