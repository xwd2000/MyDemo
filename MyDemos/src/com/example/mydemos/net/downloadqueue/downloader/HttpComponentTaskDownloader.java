package com.example.mydemos.net.downloadqueue.downloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Observable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGetHC4;
import org.apache.http.client.utils.URLEncodedUtilsHC4;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

import com.example.mydemos.net.downloadqueue.bean.Task;
import com.example.util.EmptyUtil;
/**
 * use notifyObservers to tell task updated
 * @author xuweidong
 */
public class HttpComponentTaskDownloader extends AbsTaskDownloader {
	private Task task;
	private static final String TAG = "HttpComponentTaskDownloader";

	@Override
	public void run() {
		try {
			if (task == null || EmptyUtil.isEmpty(task.getJob().getUrl())) {
				Log.e(TAG, "路径未定义");
				return ;
			}
			HttpClient client = new DefaultHttpClient();
			String urlStr=task.getJob().getUrl();
			HttpGetHC4 httpget = new HttpGetHC4(urlStr);
			httpget.setHeader("Range",
					"bytes=" + task.getCurrentPos()+"-"+task.getByteEnd());
			
			HttpResponse response = client.execute(httpget);
			HttpEntity entity = response.getEntity();
			InputStream input = entity.getContent();

			
			File file = new File(task.getJob().getSavePath());
			file.getParentFile().mkdirs();

			RandomAccessFile osavedfile = new RandomAccessFile(task.getJob()
					.getSavePath(), "rw");
			long npos = task.getCurrentPos();
			// 定位文件指针到npos位置
			osavedfile.seek(npos);
			task.setStatus(Task.STATUS_RUNNING);//开始下载
			setChanged();
			notifyObservers(0);
			int bufferSize=1024;
			byte[] b = new byte[bufferSize];
			int nread;
			// 从输入流中读入字节流，然后写到文件中
			final int notifyMin=(task.getByteEnd()-task.getByteStart())/100;
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
			//发送结束
			Log.d(TAG,file.getName()+" task finished:"+task.getByteStart()+"-"+task.getByteEnd());
			input.close();
			osavedfile.close();
			task.setStatus(Task.STATUS_FINISHED);
			setChanged();
			notifyObservers(receiveBolck);
		} catch (Exception e) {
			e.printStackTrace();
			task.setStatus(Task.STATUS_ERROR);
			setChanged();
			notifyObservers(0);
		}
		
	}

	@Override
	public void pause() {

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
