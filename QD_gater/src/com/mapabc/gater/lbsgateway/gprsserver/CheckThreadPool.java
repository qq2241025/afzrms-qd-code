package com.mapabc.gater.lbsgateway.gprsserver;

import java.util.TimerTask;

public class CheckThreadPool
    extends TimerTask {
  private static boolean isRunning = false;
  private ThreadPool pool;
  public CheckThreadPool(ThreadPool threadPool) {
    this.pool=threadPool;
  }
  public void run() {
    if (!isRunning) {
      isRunning = true;
      pool.checkAllThreads();
      isRunning = false;
    }
  }
}
