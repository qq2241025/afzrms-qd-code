package com.mapabc.gater.gpscom;

import java.util.TimerTask;

public class CheckCompDataPool
    extends TimerTask {
  private static boolean isRunning = false;
  private GpsCompPool pool;
  
  public CheckCompDataPool(GpsCompPool datapool) {
    this.pool=datapool;
 
  }
  public void run() {
    if (!isRunning) {
      isRunning = true;
      pool.checkAllThreads();
      isRunning = false;
    }
  }
}
