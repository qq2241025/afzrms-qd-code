/**
 * 
 */
package com.mapabc.gater.directl.encode;

import java.sql.Date;

 

/**
 * 
 * @author 
 *
 */
public class Order {
	private String cmdId = "";
	private String deviceId = "";
	private String deviceType = "";
	private String content = "";
  
	public Order(){
		
	}
	
	public Order(String cmdId, String deviceId, String deviceType, String order){
		this.cmdId = cmdId;
		this.deviceId = deviceId;
		this.deviceType = deviceType;
		this.content = order;
	}
	
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	public String getDeviceId() {
		return deviceId;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getContent() {
		return content;
	}
 

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setCmdId(String cmdId) {
		this.cmdId = cmdId;
	}

	public String getCmdId() {
		return cmdId;
	}

 
 

//	public void setFirstTime(long firstTime) {
//		this.firstTime = firstTime;
//	}
//
//	public long getFirstTime() {
//		return firstTime;
//	}
//	
//	public boolean isTimeUp(int sec){
//		long time = System.currentTimeMillis() - firstTime;
//		if(time >= (sec * 1000)){//修改间隔控制单位为秒
//			return true;
//		}
//		else{
//			return false;
//		}
//	}
}
