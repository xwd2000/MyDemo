package com.hikvision.parentdotworry.exception;

public class AppUserException extends AppBaseException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AppUserException(AppError error, Exception e) {
		super(error, e);
	}

	public AppUserException(AppError error) {
		super(error);
	}

	public AppUserException(AppError error,String message){
		super(error,message);
	}
}
