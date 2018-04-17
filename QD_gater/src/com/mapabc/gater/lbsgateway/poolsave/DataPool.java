package com.mapabc.gater.lbsgateway.poolsave;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.Date;
import java.util.TimerTask;

import org.apache.commons.logging.LogFactory;


import com.mapabc.gater.directl.AllConfigCache;
import com.mapabc.gater.directl.ParseConfigParamUtil;
import com.mapabc.gater.directl.parse.ParseBase;

public class DataPool {
	private static org.apache.commons.logging.Log log=LogFactory.getLog(DataPool.class);
 	
	private ArrayList workThreadList;

	private DataWorkQueue workQueue;

	private java.util.Timer timer = null; // 定时器

	private int checkTime = 5;// 线程自检的分钟数

	public static final int DEFAULT_SIZE = 3;

	private int saveTime = 30;

	// private boolean started;

	private static boolean finished;

	private int threadCount = 3;

	/**
	 * Create a default size thread pool.
	 */
	public DataPool() {
		this(DEFAULT_SIZE);
	}

	/**
	 * Create a thread pool.
	 * 
	 * @arg int size - The number of threads initially created.
	 */
	public DataPool(int size) {
		if (size < 1) {
			throw new IllegalArgumentException();
		}
		workQueue = new DataWorkQueue(this);

		workThreadList = new ArrayList();
		this.threadCount = size;
		// started = false;
		// 从配置文件获取扫描频率
		HashMap<String, String> configMap = AllConfigCache.getInstance().getConfigMap();

		String saveFreq = configMap.get("save_time").trim();
		String save_Time = (saveFreq == null || saveFreq.length() <= 0) ? "10"
				: saveFreq;
		this.saveTime = Integer.parseInt(save_Time);
		// log.info("数据池批量入库扫描频率为：" + saveTime);

	}

	/**
	 * Starts the thread pool running. Each thread in the pool waits for work to
	 * be added using the add() method.
	 */
	public void startPool() {

		for (int i = 0; i < this.threadCount; i++) {
			WorkerThread work = new WorkerThread("PoolDataThread: "
					+ workThreadList.size());
			workThreadList.add(work);
			work.start();
		}
		if (log.isDebugEnabled())
			log.debug(
				"start " + (workThreadList.size()) + " DataPool Success.");

		timer = new Timer(true); // 启动定时器
		timer.schedule(new CheckDataPool(this, workQueue), new Date(System
				.currentTimeMillis()), checkTime * 60 * 1000);

		// 批量入库定时器
		Timer saveTimer = new Timer();
		saveTimer.schedule(new TimerTask() {
			public void run() {

				try {
 
					ArrayList works = (ArrayList) workQueue.getWork(false);
					if (null != works && works.size() > 0) {
						if (log.isDebugEnabled())
							log.debug("位置按时入库，入库队列长度：" + works.size());
						long t1 = System.currentTimeMillis();
						BatchSaveUuid batchsave = new BatchSaveUuid();
						batchsave.batchSave(works);
						long t2 = System.currentTimeMillis();
						if (log.isDebugEnabled())
							log.debug("位置按时入库耗时：" + (t2 - t1) + "ms");


					}
 
				} catch (Exception ie) {
					// started = false;
					log.error(ie.getMessage(), ie);
					ie.printStackTrace();
				} finally {

				}
			}

		}, saveTime * 1000, saveTime * 1000);

	}

	public synchronized void checkAllThreads() {
		if (log.isDebugEnabled())
			log.debug("start to check dataPool thread status");
		for (int i = workThreadList.size(); i > 0; i--) { // 逐个遍厉
			WorkerThread workThread = (WorkerThread) workThreadList.get(i - 1);
			if (!(workThread.isAlive())) {
				workThreadList.remove(i - 1);
			}
		}
		while (workThreadList.size() < this.threadCount) {
			WorkerThread work = new WorkerThread("dataThread: "
					+ workThreadList.size());
			workThreadList.add(work);
			work.start();
		}
		if (log.isDebugEnabled())
			log.debug(
				"end to check dataPool thread status,total thread count is:"
						+ workThreadList.size());
	}

	/**
	 * Add work to the queue.
	 * 
	 * @arg Runnable task - the task that is to be run.
	 */
	public void add(GpsData task) {

		workQueue.addWork( task );

	}

	/**
	 * inner class that does all the work
	 */
	private class WorkerThread extends Thread {
		private boolean isLive = false;
		private boolean isRunning = false;

		private WorkerThread(String name) {
			setName(name);
			isLive = true;
		}

		public void run() {
			while (isLive) {
				try {

					ArrayList works = (ArrayList) workQueue.getWork(true);
					if (null != works && works.size() > 0) {

						if (log.isDebugEnabled())
							log.debug("位置按批入库队列长度：" + works.size());
						long t1 = System.currentTimeMillis();
						BatchSaveUuid batchsave = new BatchSaveUuid();
						batchsave.batchSave(works);
						long t2 = System.currentTimeMillis();
						if (log.isDebugEnabled())
							log.debug("位置按批入库耗时：" + (t2 - t1) + "ms");
  						
					}

				} catch (Exception ie) {
					isLive = false;

					// log.info(ie.getMessage());
					log.error(ie.getMessage(), ie);
					ie.printStackTrace();
				}
			}
		}
	}

}
