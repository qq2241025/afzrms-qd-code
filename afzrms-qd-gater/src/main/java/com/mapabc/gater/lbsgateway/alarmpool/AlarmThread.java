/**
 * 
 */
package com.mapabc.gater.lbsgateway.alarmpool;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.LogFactory;

import com.mapabc.gater.directl.Config;
import com.mapabc.gater.directl.parse.ParseBase;
 

/**
 * @author 
 * 
 */
public class AlarmThread extends Thread {
	private static org.apache.commons.logging.Log log = LogFactory
	.getLog(AlarmThread.class);
	
	private static boolean isSaving = false;
	private Timer timer = new Timer();
	private Object obj = new Object();
	private int interval = 5;

	public AlarmThread() {
		String stime = Config.getInstance().getString("alarmFreq");
		interval = Integer.parseInt(stime == null ? "10" : stime);
		log.error("报警队列检查间隔："+interval+"S");
	}

	public  void run() {

		while (true) {
			try {
				if (!isSaving) {
					isSaving = true;
					checkAlarmQueue();
					 Thread.sleep(interval * 1000);
					isSaving = false;
				}
				
			} catch (Exception e) {
				isSaving = false;
				e.printStackTrace();
				log.error("checking alarm queue error", e);
				continue;
			}
		}

	}

	private  void checkAlarmQueue() {

		AlarmQueue queue = AlarmQueue.getInstance();
		ArrayList<ParseBase> pbList = new ArrayList<ParseBase>();

		// if (queue.size() > count) {
		for (int i = 0; i < queue.size(); i++) {
			try {
				ParseBase pb = queue.getParseBase();
				log.debug("缓存报警===："+pb.getDeviceSN()+","+pb.getAlarmType()+","+pb.getAlarmSubType()+","+pb.getAreaNo()+","+pb.getSpeedThreshold());
				pbList.add(pb);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		TermAlarmDataPool.getInstance().add(pbList);
		
 

	}

}
