package com.mapabc.gater.lbsgateway.alarmpool;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import org.apache.commons.logging.LogFactory;

import com.mapabc.AppCtxServer;
import com.mapabc.gater.common.Const;
import com.mapabc.gater.directl.Config;
import com.mapabc.gater.directl.ParseConfigParamUtil;
import com.mapabc.gater.directl.dbutil.service.DBService;
import com.mapabc.gater.directl.parse.ParseBase;

 
public class TermAlarmDataPool {
	private static org.apache.commons.logging.Log log = LogFactory
			.getLog(TermAlarmDataPool.class);
	private static org.apache.commons.logging.Log logw = LogFactory
			.getLog(WorkerThread.class);

	public static boolean isSentAlarm = false;
	private static TermAlarmDataPool alarmDataPool = null;
	private int checkTime = 2;// 线程自检的分钟数
	private java.util.Timer timer = null; // 定时器
	private ArrayList workThreadList;

	private TermAlarmWorkQueue workQueue;

	public static final int DEFAULT_SIZE = 8;
	private int threadCount = 20;
	public static int alarmType = 1;

	static {
		String s = Config.getInstance().getProperty("isStartAlarmListen");

		if (s != null && s.equals("1")) {
			isSentAlarm = true;
		}

	}

	/**
	 * Create a default size thread pool.
	 */
	private TermAlarmDataPool() {
		this(DEFAULT_SIZE);
	}

	public static synchronized TermAlarmDataPool getInstance() {
		if (alarmDataPool == null) {
			alarmDataPool = new TermAlarmDataPool();
		}
		return alarmDataPool;
	}

	/**
	 * Create a thread pool.
	 * 
	 * @arg int size - The number of threads initially created.
	 */
	private TermAlarmDataPool(int size) {
		if (size < 1) {
			throw new IllegalArgumentException();
		}
		workQueue = new TermAlarmWorkQueue();

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
					"termAlarmThreadPoolSize");
			threadCount = Integer.parseInt(alarmThreadPoolSize);

		} catch (Exception e) {
		}
		for (int i = 0; i < this.threadCount; i++) {
			WorkerThread work = new WorkerThread("TermAlarmPoolThread: "
					+ workThreadList.size());
			workThreadList.add(work);
			work.start();
		}
		if (log.isDebugEnabled()) {
			log.debug("start " + (workThreadList.size())
					+ " TermAlarmPoolThread Success.");
		}
		try {
			timer = new Timer(true); // 启动定时器
			timer.schedule(new CheckTermAlarmPool(this), new Date(System
					.currentTimeMillis()), checkTime * 60 * 1000);
		} catch (Exception e) {
		}

	}

	public void checkAllThreads() {
		if (logw.isDebugEnabled())
		log.debug("start to check TermAlarmPoolThread thread status");

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
		log.debug("end to check TermAlarmPoolThread thread status,total thread count is:"
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
			 
			while (isLive) {
				try { 
					List list = workQueue.getWork();
					if (null != list && list.size() > 0) {
						ArrayList<ParseBase> pbList = (ArrayList<ParseBase>) list;
						
						long s = System.currentTimeMillis();
						if (logw.isDebugEnabled())
						logw.debug("按批获取终端报警队列条数：" + pbList.size());
				 
						try {
							if (pbList.size() > 0) {
								DBService dbserv = (DBService) AppCtxServer.getInstance().getBean(Const.SPRING_BEAN_NAME_DBSERVICE);
								dbserv.saveMoreAlarm(pbList);
								if (logw.isDebugEnabled())
									logw.debug("入库终端报警条数：" + pbList.size());
							}
							// jms send
							for (ParseBase pb : pbList) {
								ParseConfigParamUtil.handleConfig(pb);
							}
						} catch (Exception e) {
							 
							logw.error("按批保存报警信息异常", e); 
						}
						pbList.clear();
						pbList = null;
						long e = System.currentTimeMillis();
						if (logw.isDebugEnabled())
							logw.debug("按批保存终端报警队列耗时：" + (e - s) / 1000 + "秒");
						
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
