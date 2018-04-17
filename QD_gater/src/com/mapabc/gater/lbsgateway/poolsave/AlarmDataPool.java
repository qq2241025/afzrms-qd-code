package com.mapabc.gater.lbsgateway.poolsave;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import org.apache.commons.logging.LogFactory;

//import com.mapabc.alarm.api.AlarmService;
//import com.mapabc.alarm.api.impl.AlarmServiceLocalImpl;
//import com.mapabc.alarm.api.impl.AlarmServiceRemoteImpl;
import com.mapabc.gater.directl.Config;

/**
 * @author xiaojun.luan
 * @date 2011-7-25
 */
public class AlarmDataPool {
	private static org.apache.commons.logging.Log log = LogFactory
			.getLog(AlarmDataPool.class);
	private static org.apache.commons.logging.Log logw = LogFactory
			.getLog(WorkerThread.class);

	private static AlarmDataPool alarmDataPool = null;
	private int checkTime = 2;// 线程自检的分钟数
	private java.util.Timer timer = null; // 定时器
	private ArrayList workThreadList;

	private AlarmWorkQueue workQueue;

	private static final int DEFAULT_SIZE = 8;
	private int threadCount = 8;
	private static int alarmType = 1;
	private static boolean isSentAlarm = false;
	private static String alarmServiceType;
	private static String alarmServiceUrl;

	static {
		String s = Config.getInstance().getProperty("isSentAlarm");

		if (s != null && s.equals("1")) {
			isSentAlarm = true;
		}

		alarmServiceType = Config.getInstance().getString("alarmServiceType");
		alarmServiceUrl = Config.getInstance().getString("alarmServiceUrl");

	}

	/**
	 * Create a default size thread pool.
	 */
	private AlarmDataPool() {
		this(DEFAULT_SIZE);
	}

	public static synchronized AlarmDataPool getInstance() {
		if (alarmDataPool == null) {
			alarmDataPool = new AlarmDataPool();
		}
		return alarmDataPool;
	}

	/**
	 * Create a thread pool.
	 * 
	 * @arg int size - The number of threads initially created.
	 */
	private AlarmDataPool(int size) {
		if (size < 1) {
			throw new IllegalArgumentException();
		}
		workQueue = new AlarmWorkQueue(this);

		workThreadList = new ArrayList();
		this.startPool();
	}

	/**
	 * Starts the thread pool running. Each thread in the pool waits for work to
	 * be added using the add() method.
	 */
	public void startPool() {
		try {
			String alarmThreadPoolSize = Config.getInstance().getProperty(
					"alarmThreadPoolSize");
			threadCount = Integer.parseInt(alarmThreadPoolSize);

		} catch (Exception e) {
		}
		for (int i = 0; i < this.threadCount; i++) {
			WorkerThread work = new WorkerThread("AlarmPoolThread: "
					+ workThreadList.size());
			workThreadList.add(work);
			work.start();
		}
		if (log.isDebugEnabled()) {
			log.debug("start " + (workThreadList.size())
					+ " AlarmDataPool Success.");
		}
		try {
			timer = new Timer(true); // 启动定时器
			timer.schedule(new CheckAlarmPool(this), new Date(System
					.currentTimeMillis()), checkTime * 60 * 1000);
		} catch (Exception e) {
		}

	}

	public void checkAllThreads() {
		if (logw.isDebugEnabled())
			log.debug("start to check alarmPool thread status");

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
		if (logw.isDebugEnabled())
			log
					.debug("end to check alarmPool thread status,total thread count is:"
							+ workThreadList.size());

	}

	/**
	 * Add work to the queue.
	 * 
	 * @arg Runnable task - the task that is to be run.
	 */
	public void add(List dataList) {

		workQueue.addWork(dataList);

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
//			AlarmService alarm = null;
			try {

				if (alarmServiceType != null) {
					alarmServiceType = alarmServiceType.trim();
					alarmType = Integer.parseInt(alarmServiceType);
				} else {
					alarmServiceType = "";
					return;
					// 不做报警处理
				}

				if (alarmServiceUrl != null) {
					alarmServiceUrl = alarmServiceUrl.trim();
				} else {
					alarmServiceUrl = "";
				}

				if (alarmType == 1) {
//					alarm = new AlarmServiceRemoteImpl(alarmServiceUrl, "utf-8");
				} else {
//					alarm = new AlarmServiceLocalImpl();
				}
			} catch (Exception e) {
				logw.error("read alarm-config error:", e);
			}
			while (isLive) {
				try {

					List list = workQueue.getWork();
					if (null != list && list.size() > 0) {

						// 批量报警判断：add by 
						if (logw.isDebugEnabled())
							logw.debug("待发送到报警服务数量：" + list.size());

						long alarmS = System.currentTimeMillis();

						// TODO
						logw.info("alarm judge TODO");
//						alarm.alarmJudge(list);

						long alarmE = System.currentTimeMillis();

						if (logw.isDebugEnabled())
							logw.debug("发送位置到报警服务成功, gpsdatalist : "
									+ list.size() + ", 耗时:" + (alarmE - alarmS)
									+ "ms");

						list.clear();
						list = null;

					}

				} catch (Exception ie) {
					// Log.getInstance().outLog(ie.getMessage());
					isLive = false;
					logw.error("发送报警异常==", ie);
					ie.printStackTrace();
				}

			}
		}
	}
}
