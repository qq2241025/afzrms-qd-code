package com.mapabc.gater.lbsgateway.poolsave;

import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;

import org.apache.commons.logging.LogFactory;

import com.mapabc.gater.gpscom.GpsCompPool;
import com.mapabc.gater.lbsgateway.GPRSThread;
import com.mapabc.gater.lbsgateway.gprsserver.ThreadPool;
import com.mapabc.gater.lbsgateway.gprsserver.udp.GprsSocketChannel;

public class ReadPool {
	private static org.apache.commons.logging.Log log=LogFactory.getLog(ReadPool.class);

	private ArrayList workThreadList;
	private ReadWorkQueue workQueue;

	private DataPool dataPool;
	private ThreadPool threadPool;
	private GpsCompPool gpsPool;

	private java.util.Timer timer = null; // 定时器
	private int checkTime = 5;// 线程自检的分钟数
	public static final int DEFAULT_SIZE = 3;
	private volatile boolean shouldRun;
	private boolean started;
	private int threadCount = 3;

	/**
	 * Create a default size thread pool.
	 */
	public ReadPool(DataPool dataPool, ThreadPool threadPool,GpsCompPool gpsPool) {
		this(DEFAULT_SIZE, dataPool, threadPool, gpsPool);
	}

	/**
	 * Create a thread pool.
	 * 
	 * @arg int size - The number of threads initially created.
	 */
	public ReadPool(int size, DataPool dataPool, ThreadPool threadPool,GpsCompPool gpsPool) {
		if (size < 1) {
			throw new IllegalArgumentException();
		}

		this.dataPool = dataPool;
		this.threadPool = threadPool;
		this.gpsPool = gpsPool;

		workQueue = new ReadWorkQueue();
		workThreadList = new ArrayList();
		this.threadCount = size;
		shouldRun = true;
		started = false;
	}

	/**
	 * Starts the thread pool running. Each thread in the pool waits for work to
	 * be added using the add() method.
	 */
	public void startPool() {
		if (!started) {
			started = true;
			for (int i = 0; i < this.threadCount; i++) {
				WorkerThread work = new WorkerThread("PoolReadThread: "
						+ workThreadList.size());
				workThreadList.add(work);
				work.start();
			}
			// log.info("已经启动"+( pool.size())+"个线程.");
			timer = new Timer(true); // 启动定时器
			timer.schedule(new CheckReadPool(this), new Date(System
					.currentTimeMillis()), checkTime * 60 * 1000);

		}
	}

	public synchronized void checkAllThreads() {
		log.info("start to check parseThreadPool thread status");
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
		log.info(
				"end to check parseThreadPool thread status,total thread count is:"
						+ workThreadList.size());
	}

	/**
	 * Stop the pool.
	 */
	public void stopPool() {
		shouldRun = false;
	}

	/**
	 * Add work to the queue.
	 * 
	 * @arg Runnable task - the task that is to be run.
	 */
	public void add(SelectionKey task) {
		workQueue.addWork(task);
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

					SelectionKey key = (SelectionKey) workQueue.getWork();
					if (key != null){
					GprsSocketChannel gprsSocketChannel = new GprsSocketChannel(
							(DatagramChannel) key.channel());
					byte[] socketData = gprsSocketChannel.readSocketBytes(key); // manipulates
																				// dataFromSocket
					//log.info(new String(socketData));
					
					if (socketData == null ) {
						return;
					}
					if(socketData.length!=0){
						threadPool.add(new GPRSThread(dataPool,
								gprsSocketChannel, socketData,gpsPool));
						 
					}
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
