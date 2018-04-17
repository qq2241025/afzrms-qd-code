/**
 * 
 */
package com.mapabc.gater.lbsgateway.alarmpool;

import org.apache.commons.logging.LogFactory;

import com.mapabc.gater.directl.Config;
 

/**
 * @author 
 *
 */
public class GpsAlarmServer {
	private static org.apache.commons.logging.Log log = LogFactory
	.getLog(GpsAlarmServer.class);
	public static void start(){
		String isUsed = Config.getInstance().getString("isStartAlarmListen");
		if (isUsed == null || isUsed.trim().equals("0")){
			return;
		}
		//定时器
		 AlarmThread alarmThread = new AlarmThread();
		 alarmThread.start();
		 log.info("启动报警队列服务！");
	}

}
