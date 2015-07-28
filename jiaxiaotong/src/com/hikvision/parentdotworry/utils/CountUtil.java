package com.hikvision.parentdotworry.utils;

import java.util.Observable;
import java.util.Timer;

import org.apache.log4j.Logger;

/**
 * 倒计时类
 * 
 * @author xuweidong
 */
public class CountUtil extends Observable {
	private Logger logger = Logger.getLogger(CountUtil.class);
	public static final int STATUS_NOT_STARTED = 0;
	public static final int STATUS_COUNTING = 1;
	public static final int STATUS_FINISHED = 2;

	private int mCountFrom;
	private int mCountTo;
	private int mCurrentCount;
	private int mUnit;
	private CountThread mCountThread;
	private int status = STATUS_NOT_STARTED;
	private boolean stoping = false;
	private Object syncObj = new Object();

	public CountUtil(int countFrom) {
		this.mCountFrom = countFrom;
		this.mCountTo = 0;
		this.mCurrentCount = countFrom;
		this.mUnit = -1;
	}

	public CountUtil(int countFrom, int countTo) {
		this.mCountFrom = countFrom;
		this.mCountTo = countTo;
		this.mCurrentCount = countFrom;
		this.mUnit = (countTo - countFrom) / Math.abs(countTo - countFrom);
	}

	public void startCount() {
		synchronized (syncObj) {
			if (status == STATUS_COUNTING) {
				logger.info("now is counting");
				return;
			}
			// finished 和 not started都可以开始
			mCountThread = new CountThread();
			stoping = false;
			status = STATUS_COUNTING;
			mCountThread.start();

		}

	}

	public void stopCount() {
		synchronized (syncObj) {
			if (isCounting()) {
				stoping = true;
				syncObj.notify();
			}
			status = STATUS_FINISHED;

		}
	}

	public boolean isCounting() {
		return status == STATUS_COUNTING;
	}

	public int getStatus() {
		return status;
	}

	public void resetCount() {
		stopCount();
		stoping = true;
		status = STATUS_NOT_STARTED;
		mCurrentCount = mCountFrom;
		mCountThread = null;
	}

	public class CountThread extends Thread {
		@Override
		public void run() {
			synchronized (syncObj) {
				while (mCountTo != mCurrentCount && !stoping) {

					setChanged();
					notifyObservers(mCurrentCount);
					mCurrentCount = mCurrentCount + mUnit;
					System.out.println("--------------------------"
							+ mCurrentCount);
					try {
						syncObj.wait(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				stoping = true;
				status = STATUS_FINISHED;
				setChanged();
				notifyObservers(mCurrentCount);
			}
			super.run();
		}
	}

	public void setCountFrom(int countFrom) {
		if (!isCounting()) {
			this.mCountFrom = countFrom;
		} else {
			logger.info("countFrom not seted,compoent is counting");
		}
	}

	public void setCountTo(int countTo) {
		if (!isCounting()) {
			this.mCountTo = countTo;
		} else {
			logger.info("countTo not seted,compoent is counting");
		}
	}

}
