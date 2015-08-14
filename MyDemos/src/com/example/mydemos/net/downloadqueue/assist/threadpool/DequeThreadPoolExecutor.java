package com.example.mydemos.net.downloadqueue.assist.threadpool;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class DequeThreadPoolExecutor extends ThreadPoolExecutor {
	BlockingDeque<Runnable> workQueue;

	public DequeThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingDeque<Runnable> workQueue, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				handler);
		this.workQueue = workQueue;
	}

	public DequeThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingDeque<Runnable> workQueue, ThreadFactory threadFactory,
			RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				threadFactory, handler);
		this.workQueue = workQueue;
	}

	public DequeThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingDeque<Runnable> workQueue, ThreadFactory threadFactory) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				threadFactory);
		this.workQueue = workQueue;
	}

	public DequeThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit, BlockingDeque<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		this.workQueue = workQueue;
	}

	/**
	 * Executes the given task sometime in the future. The task may execute in a
	 * new thread or in an existing pooled thread.
	 * 
	 * If the task cannot be submitted for execution, either because this
	 * executor has been shutdown or because its capacity has been reached, the
	 * task is handled by the current {@code RejectedExecutionHandler}.
	 * 
	 * @param command
	 *            the task to execute
	 * @throws RejectedExecutionException
	 *             at discretion of {@code RejectedExecutionHandler}, if the
	 *             task cannot be accepted for execution
	 * @throws NullPointerException
	 *             if {@code command} is null
	 */
	public void executeFirst(Runnable command) {
		if (command == null)
			throw new NullPointerException();
		if(!workQueue.contains(command))
			super.execute(command);
		/*
		 * Proceed in 3 steps:
		 * 
		 * 1. If fewer than corePoolSize threads are running, try to start a new
		 * thread with the given command as its first task. The call to
		 * addWorker atomically checks runState and workerCount, and so prevents
		 * false alarms that would add threads when it shouldn't, by returning
		 * false.
		 * 
		 * 2. If a task can be successfully queued, then we still need to
		 * double-check whether we should have added a thread (because existing
		 * ones died since last checking) or that the pool shut down since entry
		 * into this method. So we recheck state and if necessary roll back the
		 * enqueuing if stopped, or start a new thread if there are none.
		 * 
		 * 3. If we cannot queue task, then we try to add a new thread. If it
		 * fails, we know we are shut down or saturated and so reject the task.
		 */
		int c = ctl.get();
		if (workerCountOf(c) < getCorePoolSize()) {
			if (addWorker(command, true))
				return;
			c = ctl.get();
		}
		if (isRunning(c) && workQueue.offerFirst(command)) {
			int recheck = ctl.get();
			if (!isRunning(recheck) && remove(command))
				reject(command);
			else if (workerCountOf(recheck) == 0)
				addWorker(null, false);
		} else if (!addWorker(command, false))
			reject(command);
	}
	
	@Override
	public void execute(Runnable command) {
		if(!workQueue.contains(command))
			super.execute(command);
	}

	public <T> Future<T> submitFirst(Callable<T> task) {
		if (task == null)
			throw new NullPointerException();
		RunnableFuture<T> ftask = newTaskFor(task);
		executeFirst(ftask);
		return ftask;
	}

	public Future<?> submitFirst(Runnable task) {
		if (task == null)
			throw new NullPointerException();
		RunnableFuture<Void> ftask = newTaskFor(task, null);
		executeFirst(ftask);
		return ftask;
	}

	public <T> Future<T> submitFirst(Runnable task, T result) {
		if (task == null)
			throw new NullPointerException();
		RunnableFuture<T> ftask = newTaskFor(task, result);
		executeFirst(ftask);
		return ftask;
	}
	
	public BlockingDeque<Runnable> getWorkDeque(){
		return workQueue;
	}
}
