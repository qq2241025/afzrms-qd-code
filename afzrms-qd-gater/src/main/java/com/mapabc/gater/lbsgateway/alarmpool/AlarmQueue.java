/**
 * 
 */
package com.mapabc.gater.lbsgateway.alarmpool;

import java.util.LinkedList;

import org.apache.commons.logging.LogFactory;

import com.mapabc.gater.directl.Config;
import com.mapabc.gater.directl.parse.ParseBase;

/**
 * @author 
 *
 */
public class AlarmQueue extends LinkedList<ParseBase>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static org.apache.commons.logging.Log log = LogFactory
	.getLog(AlarmQueue.class);
	
	static AlarmQueue instance;
	
	public static synchronized AlarmQueue getInstance(){
		
		if (instance == null){
			instance = new AlarmQueue();
		}
		
		return instance;
	}
	
	public synchronized void addAlarm(ParseBase parse){
		String isUsed = Config.getInstance().getString("isStartAlarmListen");
		if (isUsed == null || isUsed.trim().equals("0")){
			log.info("未启动报警队列服务，报警信息不添加到缓存队列！");
			return;
		}
		
		if (parse != null){
			ParseBase pb = (ParseBase)parse.clone();
			 
			log.debug("缓存报警："+pb.getDeviceSN()+","+pb.getAlarmType()+","+pb.getAlarmSubType()+","+pb.getAreaNo()+","+pb.getSpeedThreshold());

			instance.add(pb);
		}
		log.info("缓存共有"+instance.size()+"条报警记录");
	}
	
	public  ParseBase getParseBase(){
		return (ParseBase)instance.removeFirst();
	}

}
