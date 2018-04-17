package com.mapabc.gater.lbsgateway.poolsave;

import java.util.LinkedList;
import java.util.List;

/**
 * @author xiaojun.luan
 * @date 2011-7-25
 */
public class AlarmWorkQueue {
	private LinkedList<List> work;

	private AlarmDataPool alarmDataPool = null;
	private Object notEmpty = new Object();

	public AlarmWorkQueue(AlarmDataPool dataPool) {
		this.alarmDataPool = dataPool;
		work = new LinkedList();
		// Collections.synchronizedList(work);

	}

	public   void addWork(List dataList) {
		synchronized (notEmpty) {
			work.addLast(dataList);
			notEmpty.notifyAll();
		}

	}

	public   List getWork() throws InterruptedException {
		// int size=work.size();
		// if(size==0)return null;
		// ArrayList ls = new ArrayList();
		// work.removeAll(ls);
		// for(int i=0;i<size;i++){
		// ls.add(work.remove());
		// }
		// return ls;
		// return work.remove();
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
