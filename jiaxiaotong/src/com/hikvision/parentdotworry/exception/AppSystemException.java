package com.hikvision.parentdotworry.exception;

public class AppSystemException extends AppBaseException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AppSystemException(AppError error, Exception e) {
		super(error, e);
	}

	public AppSystemException(AppError error) {
		super(error);
	}
}
