package com.hikvision.parentdotworry.exception;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;

import com.hikvision.parentdotworry.utils.ActivityUtils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class CustomExceptionHandler implements UncaughtExceptionHandler {
	private UncaughtExceptionHandler defaultUEH;
	private Context context;
	private Logger logger = Logger.getLogger(CustomExceptionHandler.class);
	public CustomExceptionHandler(Context context) {
		this.context = context;
		this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
	}
	
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if(ex instanceof ClientProtocolException){
			Toast.makeText(context,ex.getMessage(),Toast.LENGTH_SHORT).show();
			return;
		}
		if(ex instanceof ServerResponseErrorException){
			Toast.makeText(context,ex.getMessage(),Toast.LENGTH_SHORT).show();
			return;
		}
		
		logger.error( ex.getMessage());
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ex.printStackTrace(new PrintStream(bos));
		try {
			bos.flush();
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.error(ex.getStackTrace().toString());
		logger.error(bos.toString());
		//Log.e("com.videogo", ex.getStackTrace().toString());
		//Log.e("com.videogo", bos.toString());
		defaultUEH.uncaughtException(thread, ex);;
	}
	
	
}
