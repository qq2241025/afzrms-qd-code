package com.mapabc.gater.lbsgateway.alarmpool;

import java.util.TimerTask;
 

public class CheckTermAlarmPool extends TimerTask {
	private static boolean isRunning = false;
	private TermAlarmDataPool pool;

	public CheckTermAlarmPool(TermAlarmDataPool threadPool) {
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
