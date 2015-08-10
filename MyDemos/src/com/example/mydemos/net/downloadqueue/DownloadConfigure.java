package com.example.mydemos.net.downloadqueue;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import android.util.Log;

import com.example.mydemos.net.downloadqueue.assist.threadpool.DequeThreadPoolExecutor;





public class DownloadConfigure {
	
	final int threadPoolSize;
	final int threadNumPerJob;
	final int threadPriority;
	final DequeThreadPoolExecutor taskExecutor;
	final String savePathBase;
	
	private DownloadConfigure(final Builder builder) {
		threadPoolSize=builder.threadPoolSize;
		threadNumPerJob=builder.threadNumPerJob;
		threadPriority=builder.threadPriority;
		taskExecutor = builder.taskExecutor;
		savePathBase =  builder.savePathBase;
		
	}

	public static class Builder {
		private static final String TAG = "DownLoadConfigure.Builder";
		private static final String WARNING_OVERLAP_EXECUTOR="overlap taskExecutor()";
		/** {@value} */
		public static final int DEFAULT_THREAD_POOL_SIZE = 5;
		/** {@value} */
		public static final int DEFAULT_THREAD_PRIORITY = Thread.NORM_PRIORITY - 2;
		
		
		private int threadPoolSize = DEFAULT_THREAD_POOL_SIZE;
		private int threadNumPerJob = 2;
		private int threadPriority = DEFAULT_THREAD_PRIORITY;
		private DequeThreadPoolExecutor taskExecutor = null;
		private String savePathBase = null;
		/** Builds configured {@link DownLoadConfigure} object */
		public DownloadConfigure build() {
			initEmptyFieldsWithDefaultValues();
			return new DownloadConfigure(this);
		}
		
		public Builder threadPoolSize(int threadPoolSize) {
			if (taskExecutor != null) {
				Log.w(TAG,WARNING_OVERLAP_EXECUTOR);
			}

			this.threadPoolSize = threadPoolSize;
			return this;
		}
		
		public Builder threadNumPerJob(int threadNumPerJob) {
			if (taskExecutor != null) {
				Log.w(TAG,WARNING_OVERLAP_EXECUTOR);
			}
			if(threadNumPerJob>threadPoolSize){
				this.threadNumPerJob=threadPoolSize;
			}else{
				this.threadNumPerJob = threadNumPerJob;
			}
			return this;
		}
		
		public Builder threadPriority(int threadPriority) {
			if (taskExecutor != null) {
				Log.w(TAG,"overlap taskExecutor()");
			}

			if (threadPriority < Thread.MIN_PRIORITY) {
				this.threadPriority = Thread.MIN_PRIORITY;
			} else {
				if (threadPriority > Thread.MAX_PRIORITY) {
					this.threadPriority = Thread.MAX_PRIORITY;
				} else {
					this.threadPriority = threadPriority;
				}
			}
			return this;
		}
		
		public Builder savePathBase(String path) {
			this.savePathBase=path;
			return this;
		}
		
		
		
		public void initEmptyFieldsWithDefaultValues(){
			taskExecutor=new DequeThreadPoolExecutor(threadPoolSize, threadPoolSize,
	                0L, TimeUnit.MILLISECONDS,
	                new LinkedBlockingDeque<Runnable>(),createThreadFactory(threadPriority,"-download-"));
			
			
		}
	}
	/** Creates default implementation of {@linkplain ThreadFactory thread factory} for task executor */
	private static ThreadFactory createThreadFactory(int threadPriority, String threadNamePrefix) {
		return new DefaultThreadFactory(threadPriority, threadNamePrefix);
	}

	private static class DefaultThreadFactory implements ThreadFactory {

		private static final AtomicInteger poolNumber = new AtomicInteger(1);

		private final ThreadGroup group;
		private final AtomicInteger threadNumber = new AtomicInteger(1);
		private final String namePrefix;
		private final int threadPriority;

		DefaultThreadFactory(int threadPriority, String threadNamePrefix) {
			this.threadPriority = threadPriority;
			group = Thread.currentThread().getThreadGroup();
			namePrefix = threadNamePrefix + poolNumber.getAndIncrement() + "-thread-";
		}

		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
			if (t.isDaemon()) t.setDaemon(false);
			t.setPriority(threadPriority);
			return t;
		}
	}
	
}
