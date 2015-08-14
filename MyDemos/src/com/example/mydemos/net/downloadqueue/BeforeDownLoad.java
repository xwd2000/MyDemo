package com.example.mydemos.net.downloadqueue;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BeforeDownLoad extends Thread{
	
	private String url;
	private AfterFileLengthGeted afterFileLengthGetted;

	
	public BeforeDownLoad(String url,AfterFileLengthGeted afterFileLengthGetted) {
		super();
		this.url = url;
		this.afterFileLengthGetted=afterFileLengthGetted;
	}

	@Override
	public void run() {
		try {
			String fileName=null;
			try{
				fileName = url.substring(url.lastIndexOf("/")+1);
				url=url.substring(0, url.lastIndexOf("/")+1)+URLEncoder.encode(fileName, "utf-8").replaceAll("\\+", "%20");
			}catch(Exception e){
				e.printStackTrace();
			}
			URL u = new URL(url);
			HttpURLConnection urlcon = (HttpURLConnection) u.openConnection();
			final int fileLength =  urlcon.getContentLength(); 
			if(afterFileLengthGetted!=null){
				afterFileLengthGetted.afterFileLengthGetted(fileLength);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		super.run();
	}
	
	public interface AfterFileLengthGeted{
		public void afterFileLengthGetted(int fileLength);
	}
}
