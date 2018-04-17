package com.mapabc.gater.lbsgateway.gprsserver;

 
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;

import org.apache.commons.logging.LogFactory;

public class ThreadPool {
	private static org.apache.commons.logging.Log log=LogFactory.getLog(ThreadPool.class);

	
  private ArrayList workThreadList;
  private WorkQueue workQueue;
  private java.util.Timer timer = null; // 定时器
  private int checkTime=5;//线程自检的分钟数
  public static final int DEFAULT_SIZE = 3;
  private volatile boolean shouldRun;
  private boolean started;
  private int threadCount = 3;
  /**
   * Create a default size thread pool.
   */
  public ThreadPool() {
    this(DEFAULT_SIZE);
  }

  /**
   * Create a thread pool.
   * @arg int size - The number of threads initially created.
   */
  public ThreadPool(int size) {
    if (size < 1) {
      throw new IllegalArgumentException();
    }
    workQueue = new WorkQueue();
    workThreadList = new ArrayList();
    this.threadCount = size;
    shouldRun = true;
    started = false;
  }

  /**
   * Starts the thread pool running. Each thread in the pool waits
   * for work to be added using the add() method.
   */
  public void startPool() {
    if (!started) {
      started = true;
      for (int i = 0; i < this.threadCount; i++) {
        WorkerThread work = new WorkerThread("Thread: " + workThreadList.size());
        workThreadList.add(work);
        work.start();
      }
      // log.info("已经启动"+( pool.size())+"个线程.");
      timer = new Timer(true); // 启动定时器
      timer.schedule(new CheckThreadPool(this),
                     new Date(System.currentTimeMillis()),
                     checkTime * 60 * 1000);

    }
  }
  //检查线程池内存中的线程活动状态
  public synchronized void checkAllThreads() {
	  log.info ("start to check parseThreadPool thread status");
	  for (int i=workThreadList.size();i>0;i--) { // 逐个遍厉
	    WorkerThread workThread = (WorkerThread) workThreadList.get(i-1);
	    if (! (workThread.isAlive())) {//把不活动的解析线程从内存中移除
	      workThreadList.remove(i-1);
	    }
	  }
	  while(workThreadList.size()<this.threadCount){//补充不足给定线程数threadCount的线程。
	      WorkerThread work = new WorkerThread("PoolParseThread: " +workThreadList.size());
	      workThreadList.add(work);
	      work.start();
	  }
	    log.info ("end to check parseThreadPool thread status,total thread count is:"+workThreadList.size());
	  }

  /**
   * Stop the pool.
   */
  public void stopPool() {
    shouldRun = false;
  }

  /**
   * Add work to the queue.
   * @arg Runnable task - the task that is to be run.
   */
  public  void add(Runnable task) {
    workQueue.addWork(task);
  }

  /**
   * inner class that does all the work
   */
  private class WorkerThread
      extends Thread {
    private boolean isLive = false;
    private WorkerThread(String name) {
      setName(name);
      isLive = true;
    }

    public void run() {
      while (isLive) {
        try {
          
          Runnable work = (Runnable) workQueue.getWork();
          if (work != null){ 
        	  work.run();
          } 
        }
        catch (Exception ie) {
            isLive = false;
            //log.info(ie.getMessage());
            log.error(ie.getMessage(), ie);
            ie.printStackTrace();
        }
      }
    }
  }
}
