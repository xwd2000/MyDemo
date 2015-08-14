package com.example.mydemos.net.downloadqueue.downloader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

import com.example.mydemos.net.downloadqueue.bean.Job;
import com.example.mydemos.net.downloadqueue.bean.Task;
import com.example.util.EmptyUtil;

public class CommonDownloader extends AbsTaskDownloader{
	private static final String TAG="CommonDownloader";
	private Task task;

	@Override
	public void run() {

		try {
			if (task == null || EmptyUtil.isEmpty(task.getJob().getUrl())) {
				Log.e(TAG, "路径未定义");
				return ;
			}
			Job job=task.getJob();
			task.setStatus(Task.STATUS_INITING);//初始化
			setChanged();
			notifyObservers(0);
			URL url = new URL(job.getUrl());

			HttpURLConnection httpconnection = (HttpURLConnection) url
					.openConnection();
			// 设置user-agent
			//httpconnection.setRequestProperty("user-agent", "netfox");
			httpconnection.setAllowUserInteraction(true);  
			// 设置断点续传的开始位置
			httpconnection.setRequestProperty("Range",
					"bytes=" + task.getCurrentPos()+"-"+task.getByteEnd());
			// 获得输入流
			InputStream input = httpconnection.getInputStream();

			File file = new File(job.getSavePath());
			file.getParentFile().mkdirs();

			RandomAccessFile osavedfile = new RandomAccessFile(job
					.getSavePath(), "rw");
			long npos = task.getCurrentPos();
			// 定位文件指针到npos位置
			osavedfile.seek(npos);
			
			
			task.setStatus(Task.STATUS_RUNNING);//开始下载
			setChanged();
			notifyObservers(0);
			
			int bufferSize=20*1024;
			byte[] b = new byte[bufferSize];
			int nread;
			// 从输入流中读入字节流，然后写到文件中
			final int notifyMin=job.getTotalSize()/100;
			int receiveBolck=0;
			while ((nread = input.read(b, 0, bufferSize)) > 0) {
				osavedfile.write(b, 0, nread);
				task.setCurrentPos(task.getCurrentPos()+nread);
				receiveBolck+=nread;
				if(receiveBolck>notifyMin){
					setChanged();
					notifyObservers(receiveBolck);
					receiveBolck=0;
					//Log.d(TAG,file.getName()+":"+task.getCurrentPos());
				}
			}
			Log.d(TAG,file.getName()+" task finished:"+task.getByteStart()+"-"+task.getByteEnd());
			input.close();
			osavedfile.close();
			task.setStatus(Task.STATUS_FINISHED);
			setChanged();
			notifyObservers(receiveBolck);
		} catch (MalformedURLException e) {
			task.setStatus(Task.STATUS_ERROR);
			setChanged();
			notifyObservers(0);
			e.printStackTrace();
		} catch (IOException e) {
			task.setStatus(Task.STATUS_ERROR);
			setChanged();
			notifyObservers(0);
			e.printStackTrace();
		}
	}


	@Override
	public void setTask(Task task) {
		this.task = task;

	}
	@Override
	public Task getTask(){
		return task;
	}


	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean isRunning() {
		// TODO Auto-generated method stub
		return false;
	}
	

}
