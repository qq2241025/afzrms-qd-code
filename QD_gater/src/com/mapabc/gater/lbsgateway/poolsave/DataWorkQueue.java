package com.mapabc.gater.lbsgateway.poolsave;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.commons.logging.LogFactory;

import com.mapabc.gater.counter.Counter;
import com.mapabc.gater.directl.AllConfigCache;

class DataWorkQueue {

	private static org.apache.commons.logging.Log log = LogFactory
			.getLog(DataWorkQueue.class);

	private LinkedList work;

	private int maxBatch = 100;

	private int batch = 100;

	private DataPool dataPool = null;

	public DataWorkQueue(DataPool dataPool) {
		this.dataPool = dataPool;
		work = new LinkedList();
		// Collections.synchronizedList(work);
		HashMap<String, String> configMap = AllConfigCache.getInstance()
				.getConfigMap();

		// 从配置文件获取频率入库数目
		String maxBth = configMap.get("save_count").trim();

		String maxCount = (maxBth == null || maxBth.length() <= 0) ? "100"
				: maxBth;
		this.maxBatch = Integer.parseInt(maxCount);
		// log.info("数据池批量入库数配置为："+maxBatch);

		String max = configMap.get("max_count").trim();
		this.batch = Integer.parseInt(max);
		// log.info("定时入库允许入最大条数为："+batch);

	}

	public synchronized void addWork(GpsData task) {

		// work.add(task);
		work.addLast(task);
		if (work.size() > 0 && work.size() > 1000) {
			log.info("入库数据池已积累：" + work.size());
		} 
		notifyAll();
		Counter.setDbPoolTCount();

	}

	public synchronized ArrayList getWork(boolean isBatch)
			throws InterruptedException {
		ArrayList ls = null;
		try {
			if (isBatch) {
				if (work.isEmpty() || work.size() < this.maxBatch) {
					wait(); 
				}
				 

				if (work.size() >= this.maxBatch) {
					ls = new ArrayList();
					while (ls.size() < this.maxBatch) {
						Object obj = work.remove();
						if (obj != null)
							ls.add(obj);
					} 
				}

			} else {

				int n = work.size() > this.batch ? this.batch : work.size();
				ls = new ArrayList();
				while (ls.size() < n) {
					ls.add(work.remove());
				}
				 
			}
		} catch (Exception e) {
			log.error("读取入库数据池数据异常", e);
		}
		return ls;

		// if (!dataPool.isTimeUp()) {
		// while (work.isEmpty() || work.size() < maxBatch) { //
		// try {
		// wait();
		// } catch (InterruptedException ie) {
		// throw ie;
		// }
		// }
		// }
		// ArrayList ls = new ArrayList();
		//		
		// for (int i = 0; i < work.size(); i++) {
		// ls.add(work.get(i));
		// }
		//
		// work = new LinkedList();
		// dataPool.setTimeUp(false);

		// return ls;
	}

}
