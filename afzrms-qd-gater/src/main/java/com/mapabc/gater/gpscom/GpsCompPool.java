package com.mapabc.gater.gpscom;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.LogFactory;

import com.mapabc.gater.directl.AllConfigCache;
import com.mapabc.gater.lbsgateway.poolsave.BatchSaveUuid;
import com.mapabc.gater.lbsgateway.poolsave.GpsData;

public class GpsCompPool {
	private static org.apache.commons.logging.Log log=LogFactory.getLog(GpsCompPool.class);

	private ArrayList workThreadList;

	private GpsCompenstateQueue gpsCmptQueue; // GPS补偿队列
	private java.util.Timer timer = null; // 定时器
	private int checkTime = 5;// 线程自检的分钟数
	public static final int DEFAULT_SIZE = 3;
	private int saveTime = 30;
	private boolean started;
	private int threadCount = 3;
	private int count = 0;
	private int count2 = 0;
	// private boolean timeUp = false;
	private boolean compenTimeUp = false; // 用于GPS补偿数据

	/**
	 * Create a default size thread pool.
	 */
	public GpsCompPool() {
		this(DEFAULT_SIZE);
	}

	/**
	 * Create a thread pool.
	 * 
	 * @arg int size - The number of threads initially created.
	 */
	public GpsCompPool(int size) {
		if (size < 1) {
			throw new IllegalArgumentException();
		}

		gpsCmptQueue = new GpsCompenstateQueue(this);
		workThreadList = new ArrayList();
		this.threadCount = size;
		started = false;
		HashMap<String, String> configMap = AllConfigCache.getInstance().getConfigMap();

		// 从配置文件获取扫描频率
		String saveFreq = configMap.get("save_time");
		String save_Time = (saveFreq == null || saveFreq.length() <= 0) ? "10"
				: saveFreq;
		this.saveTime = Integer.parseInt(save_Time);
		 

	}

	/**
	 * Starts the thread pool running. Each thread in the pool waits for work to
	 * be added using the add() method.
	 */
	public void startPool() {
		if (!started) {
			started = true;
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
			timer.schedule(new CheckCompDataPool(this), new Date(
					System.currentTimeMillis()), checkTime * 60 * 1000);

			// 批量入库定时器
			Timer saveTimer = new Timer();
			saveTimer.schedule(new TimerTask() {
				public void run() {

					try {
 						setCompenTimeUp(true);
						ArrayList compenWorks = (ArrayList) gpsCmptQueue
								.getGpsCompentWork();
						if (null != compenWorks && compenWorks.size() > 0) {
							if (log.isDebugEnabled())
								log.debug(
									"GPS补偿数据定时入库，入库队列长度：" + compenWorks.size());
							long t1 = System.currentTimeMillis();
							BatchSaveUuid batchsave =  new BatchSaveUuid();

							batchsave.saveTrackLoc(compenWorks);
							long t2 = System.currentTimeMillis();
							if (log.isDebugEnabled())
								log.debug(
									"GPS补偿数据定时入库耗时：" + (t2 - t1) + "ms");
						}

					} catch (Exception ie) {
						log.error(ie.getMessage(), ie);
						ie.printStackTrace();
					} finally {

					}
				}

			}, saveTime * 1000, saveTime * 1000);
		}
	}

	public void checkAllThreads() {
		log.info("start to check dataPool thread status");
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
	 * 增加GPS补偿数据
	 * 
	 * @arg Runnable task - the task that is to be run.
	 */
	public void addGpsCompensate(GpsData task) {
		count++;
		gpsCmptQueue.addGpsCompensate(  task );

	}

 

	/**
	 * inner class that does all the work
	 */
	private class WorkerThread extends Thread {
		private boolean isLive = false;

		private WorkerThread(String name) {
			setName(name);
			isLive = true;
		}

		public void run() {
			while (isLive) {
				try {

					ArrayList compenWorks = (ArrayList) gpsCmptQueue
							.getGpsCompentWork();
					if (null != compenWorks && compenWorks.size() > 0) {
						if (log.isDebugEnabled())
							log.debug(
								"GPS补偿数据按批入库，入库队列长度：" + compenWorks.size());
						long t1 = System.currentTimeMillis();
						BatchSaveUuid batchsave =  new BatchSaveUuid();

						batchsave.saveTrackLoc(compenWorks);
						long t2 = System.currentTimeMillis();
						if (log.isDebugEnabled())
							log.debug(
								"GPS补偿数据按批入库耗时：" + (t2 - t1) + "ms");
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

	/**
	 * @return the compenTimeUp
	 */
	public boolean isCompenTimeUp() {
		return this.compenTimeUp;
	}

	/**
	 * @param compenTimeUp
	 *            the compenTimeUp to set
	 */
	public void setCompenTimeUp(boolean compenTimeUp) {
		this.compenTimeUp = compenTimeUp;
	}
}
