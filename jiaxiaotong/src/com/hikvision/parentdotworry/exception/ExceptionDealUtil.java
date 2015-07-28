package com.hikvision.parentdotworry.exception;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.apache.http.conn.HttpHostConnectException;
import org.apache.log4j.Logger;

import com.videogo.exception.BaseException;

import android.content.Context;
import android.widget.Toast;

public class ExceptionDealUtil {
	private Context context;
	private Logger logger=Logger.getLogger(ExceptionDealUtil.class);
	
	public ExceptionDealUtil(Context context) {
		super();
		this.context = context;
	}

	public void deal(Exception e){
		try{
			throw e;
		}catch(BaseException aue){
			toast(e.getMessage());
		}catch(AppUserException aue){
			AppError appError = aue.getError();
			toast(getString(appError.message));
		}catch(AppSystemException ase){
			AppError appError = ase.getError();
			toast(getString(appError.message));
			if(ase.getTargetException()!=null){
				logger.debug(getString(appError.message)+e.getMessage()+ase.getTargetException().getMessage());
			}
		}catch(AppBaseException abe){
			AppError appError = abe.getError();
			toast(getString(appError.message)+e.getMessage());
		}catch(HttpHostConnectException hhce){
			AppError appError =AppError.NET_WORK_ERROR_UNABLE_TO_CONNECT_SERVER;
			toast(getString(appError.message));
		}catch(SocketTimeoutException ste){
			AppError appError =AppError.NET_WORK_ERROR_SOCKET_TIMEOUT;
			toast(getString(appError.message));
		}catch(IOException ioe){
			String message = ioe.getMessage();
			toast(message);
			logger.info(ioe.getMessage());
		}catch(Exception ee){
			toast(ee.getMessage());
			logger.error(ee.getMessage());
		}
	}
	
	public void deal(AppError ae){
		deal(new AppBaseException(ae));
	}
	
	
	private void toast(String message){
		Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
	}

	private String getString(int resId){
		return context.getString(resId);
	}
}
