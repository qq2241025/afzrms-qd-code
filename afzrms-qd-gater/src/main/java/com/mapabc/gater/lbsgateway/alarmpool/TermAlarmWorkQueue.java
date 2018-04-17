package com.mapabc.gater.lbsgateway.alarmpool;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author xiaojun.luan
 * @date 2011-7-25
 */
public class TermAlarmWorkQueue {
	private LinkedList<List> work;
	private Object notEmpty = new Object();

	public TermAlarmWorkQueue() {
		work = new LinkedList();
	}

	public void addWork(List dataList) {
		synchronized (notEmpty) {
			work.addLast(dataList);
			notEmpty.notifyAll();
		}

	}

	public List getWork() throws InterruptedException {
		synchronized (notEmpty) {
			while (work.isEmpty()) {
				try {
					notEmpty.wait();
				} catch (InterruptedException ie) {
					throw ie;
				}
			}
			return work.remove();
		}
	}
}
