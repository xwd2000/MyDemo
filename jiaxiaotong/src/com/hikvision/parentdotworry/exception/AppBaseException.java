package com.hikvision.parentdotworry.exception;

public class AppBaseException extends Exception{
	
	private static final long serialVersionUID = 1L;
	private AppError error;
	private Exception targetException;
	
	public AppBaseException(AppError error){
		this.error=error;
	}
	public AppBaseException(AppError error,Exception e){
		this.error=error;
		this.targetException=e;
	}
	
	public AppBaseException(AppError error,String message){
		super(message);
		this.error=error;
	}
	
	public AppError getError(){
		return error;
	}
	public Exception getTargetException() {
		return targetException;
	}
	
}
