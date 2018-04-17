package com.mapabc.gater.lbsgateway.poolsave;

import java.util.TimerTask;


public class CheckAlarmPool extends TimerTask {
	private static boolean isRunning = false;
	private AlarmDataPool pool;

	public CheckAlarmPool(AlarmDataPool threadPool) {
		this.pool = threadPool;
	}

	public void run() {
		if (!isRunning) {
			isRunning = true;
			pool.checkAllThreads();
			isRunning = false;
		}
	}
}
