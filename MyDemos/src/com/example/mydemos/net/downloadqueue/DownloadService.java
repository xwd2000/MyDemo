package com.example.mydemos.net.downloadqueue;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.example.mydemos.net.downloadqueue.JobManager.OnJobStatusChangeListener;
import com.example.mydemos.net.downloadqueue.bean.Job;
import com.example.mydemos.net.downloadqueue.downloader.CommonDownloader;
import com.example.mydemos.net.downloadqueue.downloader.HttpComponentTaskDownloader;
import com.example.mydemos.net.downloadqueue.statusstore.PerferenceStore;
import com.example.mydemos.net.downloadqueue.statusstore.StatusStore;

public class DownloadService extends Service{
	public static final String OPERATE_KEY = "operate";
	public static final int OPERATE_STAET_DOWNLOAD_SERVICE = 1;
	public static final int OPERATE_STOP_DOWNLOAD_SERVICE = 2;
	
	private JobManager jobManage;
	private DownloadConfigure dc;
	private ServiceBinder mBinder;
	private StatusStore store;
	private boolean isActivityReady=false;
	@Override
	public void onCreate() {
		mBinder = new ServiceBinder();
		dc=new DownloadConfigure.Builder()
		.downLoaderClass(HttpComponentTaskDownloader.class)
		.threadPoolSize(5)
		.threadNumPerJob(3)
		.threadPriority(Thread.NORM_PRIORITY+2)
		.savePathBase(Environment.getExternalStorageDirectory().toString())
		.build();
		store = new PerferenceStore(this);
		jobManage=new JobManager(dc,store);
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		int operate = intent.getIntExtra(OPERATE_KEY, 0);
		switch (operate) {
		case OPERATE_STAET_DOWNLOAD_SERVICE:
			
		
			break;
		case OPERATE_STOP_DOWNLOAD_SERVICE:
			
			
			break;

		default:
			break;
		}
		
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	
	public Job addNewDownLoad(String url,final Handler handler){
		Job job=jobManage.newSubmitJob(url);
		setJobHandler(job,handler);
		return job;
				
	}
	
	public void setJobHandler(Job job,final Handler handler){
		jobManage.addJobListener(job, new OnJobStatusChangeListener() {
			@Override
			public void onJobfinished(Job job) {
				sendJobToHandlerIfExist(handler,job,null);
			}

			@Override
			public void onJobInited(Job job) {
				sendJobToHandlerIfExist(handler,job,null);
			}

			@Override
			public void onJobStart(Job job) {
				sendJobToHandlerIfExist(handler,job,null);
			}

			@Override
			public void onJobProcessUpdated(Job job, int process) {
				sendJobToHandlerIfExist(handler,job,process);
			}

			@Override
			public void onJobError(Job job) {
				sendJobToHandlerIfExist(handler,job,null);
			}

			@Override
			public void onJobStop(Job job) {
				sendJobToHandlerIfExist(handler,job,null);
			}
			
		});
	}
	
	public List<Job> getUnfinishedJob(){
		List<Job> jobsInStore = store.getUnFinishJob();
		Deque<Job> jobsInMemery = jobManage.getJobMemeryStore().getAllJobs();
		List<Job> result=new ArrayList<Job>();
		for(int i=0;i<jobsInStore.size();i++){
			Job job=jobsInStore.get(i);
			for(Job jobMem:jobsInMemery){
				if(job.equals(jobMem)){
					job=jobMem;
					break;
				}
			}
			result.add(job);
		}
		return result;
	}
	
	private void sendJobToHandlerIfExist(Handler handler,Job job,Integer arg1){
		if(isActivityReady){
			Message msg=handler.obtainMessage();
			msg.what=Job.STATUS_ERROR;
			msg.obj=job;
			if(arg1!=null){
				msg.arg1=arg1;
			}
			handler.sendMessage(msg);
		}
	}
	
	public void setActivityReady(){
		isActivityReady=true;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		isActivityReady=false;
		return true;
	}
	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
	}
	
	public class ServiceBinder extends Binder {
		public DownloadService getService() {
			return DownloadService.this;
		}
	}
	

	@Override
	public void onDestroy() {
		dc.deBuild();
		isActivityReady=false;
		dc=null;
		jobManage=null;
		super.onDestroy();
	}
	


}
