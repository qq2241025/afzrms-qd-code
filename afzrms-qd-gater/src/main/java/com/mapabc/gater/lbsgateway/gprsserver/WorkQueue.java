package com.mapabc.gater.lbsgateway.gprsserver;

/**
 * The queue where all the work is placed.
 */

import java.util.*;

import org.apache.commons.logging.LogFactory;

class WorkQueue {
	private static org.apache.commons.logging.Log log = LogFactory.getLog(WorkQueue.class);

	private LinkedList work;

	public WorkQueue() {
		work = new LinkedList();
	}

	public synchronized void addWork(Runnable task) {
		// log.info("There has "+work.size()+" Data in GateWayPool , waitting. ");
		work.add(task);
		if (work.size() > 0 && work.size() > 1000) {
			log.info("解析数据池已积累：" + work.size());
		}
		notifyAll();// 有任务时，唤醒等待的线程
	}

	public synchronized Object getWork() throws InterruptedException {

		while (work.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException ie) {
				log.error("解析队列", ie);
				throw ie;
			}
		}
		return work.remove(0);
	}
}
