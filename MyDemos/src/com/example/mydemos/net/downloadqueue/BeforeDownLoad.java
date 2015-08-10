package com.example.mydemos.net.downloadqueue;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
