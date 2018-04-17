package com.mapabc.gater.lbsgateway.poolsave;

import java.util.TimerTask;

public class CheckReadPool extends TimerTask {
	private static boolean isRunning = false;
	private ReadPool pool;

	public CheckReadPool(ReadPool datapool) {
		this.pool = datapool;
	}

	public void run() {
		if (!isRunning) {
			isRunning = true;
			pool.checkAllThreads();
			isRunning = false;
		}
	}
}