/**
 * 
 */
package com.mapabc.gater.gpscom;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.mapabc.gater.directl.AllConfigCache;
import com.mapabc.gater.lbsgateway.poolsave.GpsData;

/**
 * 
 * @author 
 *
 */
public class GpsCompenstateQueue {
 	
	private LinkedList compenWork;

	private int maxBatch = 100;

	private GpsCompPool dataPool = null;

	public GpsCompenstateQueue(GpsCompPool dataPool) {
		this.dataPool = dataPool;
 		compenWork = new LinkedList();
 		HashMap<String, String> configMap = AllConfigCache.getInstance().getConfigMap();

		//从配置文件获取频率入库数目
		String maxBth = configMap.get("save_count");
		
		String maxCount = (maxBth==null||maxBth.length()<=0)?"100":maxBth;
		this.maxBatch = Integer.parseInt(maxCount);
		 

	}
 
	//加入到补偿队列
	public synchronized void addGpsCompensate(GpsData task) {
 		compenWork.add(task);
		notify();
  	}
  
	//获取队列补偿数据
	public synchronized ArrayList getGpsCompentWork() throws InterruptedException {
 				
		if (!dataPool.isCompenTimeUp()) {
			while (compenWork.isEmpty() || compenWork.size() < maxBatch) {  
				try {
					wait();
				} catch (InterruptedException ie) {
					throw ie;
				}
			}
		}
		ArrayList ls = new ArrayList();
		
		for (int i = 0; i < compenWork.size(); i++) {
			ls.add(compenWork.get(i));
		}

		compenWork = new LinkedList();
		dataPool.setCompenTimeUp(false);

		return ls;
	}
}
