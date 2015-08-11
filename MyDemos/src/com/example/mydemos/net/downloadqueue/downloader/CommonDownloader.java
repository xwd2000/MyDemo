package com.example.mydemos.net.downloadqueue.downloader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

import com.example.mydemos.net.downloadqueue.bean.Task;

public class CommonDownloader extends AbsTaskDownloader{
	private static final String TAG="CommonDownloader";
	private Task task;

	@Override
	public void run() {

		try {
			URL url = new URL(task.getJob().getUrl());

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

			File file = new File(task.getJob().getSavePath());
			file.getParentFile().mkdirs();

			RandomAccessFile osavedfile = new RandomAccessFile(task.getJob()
					.getSavePath(), "rw");
			long npos = task.getCurrentPos();
			// 定位文件指针到npos位置
			osavedfile.seek(npos);
			task.setStatus(Task.STATUS_RUNNING);//开始下载
			byte[] b = new byte[1024];
			int nread;
			// 从输入流中读入字节流，然后写到文件中
			int lastNotifyPos=task.getCurrentPos();
			final int notifyMin=(task.getByteEnd()-task.getByteStart())/100;
			while ((nread = input.read(b, 0, 1024)) > 0) {
				osavedfile.write(b, 0, nread);
				task.setCurrentPos(task.getCurrentPos()+nread);
				if(task.getCurrentPos()-lastNotifyPos>notifyMin){
					lastNotifyPos=task.getCurrentPos();
					setChanged();
					notifyObservers(task);
					Log.d(TAG,file.getName()+":"+task.getCurrentPos());
				}
			}
			Log.d(TAG,file.getName()+" task finished:"+task.getByteStart()+"-"+task.getByteEnd());
			input.close();
			osavedfile.close();
			task.setStatus(Task.STATUS_FINISHED);
		} catch (MalformedURLException e) {
			task.setStatus(Task.STATUS_ERROR);
			e.printStackTrace();
		} catch (IOException e) {
			task.setStatus(Task.STATUS_ERROR);
			e.printStackTrace();
		}
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTask(Task task) {
		this.task = task;

	}
	@Override
	public Task getTask(){
		return task;
	}
}
