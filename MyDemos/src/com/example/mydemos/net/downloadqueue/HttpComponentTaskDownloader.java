package com.example.mydemos.net.downloadqueue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Observable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGetHC4;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

import com.example.mydemos.net.downloadqueue.bean.Task;
import com.example.util.EmptyUtil;

public class HttpComponentTaskDownloader extends Observable implements
		Downloader {
	private Task task;
	private static final String TAG = "HttpComponentTaskDownloader";

	@Override
	public void run() {

		try {
			if (task == null || EmptyUtil.isEmpty(task.getJob().getUrl())) {
				Log.e(TAG, "路径未定义");
				return;
			}
			HttpClient client = new DefaultHttpClient();
			HttpGetHC4 httpget = new HttpGetHC4(task.getJob().getUrl());
			HttpResponse response = client.execute(httpget);

			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();

			File file = new File(task.getJob().getSavePath());
			file.getParentFile().mkdirs();
			FileOutputStream fileout = new FileOutputStream(file);
			/**
			 * 根据实际运行效果 设置缓冲区大小
			 */
			byte[] buffer = new byte[1024*5];
			int ch = 0;
			while ((ch = is.read(buffer)) != -1) {
				fileout.write(buffer, 0, ch);
				hasChanged();
				notifyObservers(task);
			}
			is.close();
			fileout.flush();
			fileout.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void pause() {

	}

	@Override
	public void setTask(Task task) {
		this.task = task;
	}

}
