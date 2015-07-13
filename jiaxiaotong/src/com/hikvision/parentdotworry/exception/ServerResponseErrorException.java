package com.hikvision.parentdotworry.exception;

public class ServerResponseErrorException extends Exception {

	private static final long serialVersionUID = 1L;
	public static final int ERROR_TEST = 100;

	private int errorCode;
	public ServerResponseErrorException() {

	}

	public ServerResponseErrorException(int code,String message) {
		super(message);
		this.errorCode = code;
	}

	public ServerResponseErrorException(int code) {
		this.errorCode = code;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
}
