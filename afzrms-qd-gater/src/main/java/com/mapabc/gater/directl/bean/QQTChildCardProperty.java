/**
 * 
 */
package com.mapabc.gater.directl.bean;

import java.io.Serializable;

/**
 * @author 
 * 亲情通子卡信息
 */
public class QQTChildCardProperty implements Serializable{
	
	private String sosKey;
	
	private String key1,key2,key3,key4,key5;
 	
	private String gpsModel,cidModel,silenceModel;
 	
	private String gpsInterval,cidInterval,silenceInterval;
	
 	private String gpsSendCycle,gpsSampleCycle,gpsSendTimes;
	
	private String cidSendCycle,cidSampleCycle,cidSendTimes;
	
	private String silenceSendCycle;
	
	private String newLocKeepTime;
	
	private String areaPoints;//区域点
	
	private String areaRadius;//区域半径
	
	private String areaTimeVal; //报警时间间隔
	
	private String locateSetting;//定时定位设置

	/**
	 * @return the areaTimeVal
	 */
	public String getAreaTimeVal() {
		return getFeildValue(this.areaTimeVal).replaceAll(":", "");
	}

	/**
	 * @param areaTimeVal the areaTimeVal to set
	 */
	public void setAreaTimeVal(String areaTimeVal) {
		this.areaTimeVal = areaTimeVal;
	}

	/**
	 * @return the areaPoints
	 */
	public String getAreaPoints() {
		return getFeildValue(this.areaPoints);
	}

	/**
	 * @return the areaRadius
	 */
	public String getAreaRadius() {
		return getFeildValue(this.areaRadius);
	}

 

	/**
	 * @return the locateSetting
	 */
	public String getLocateSetting() {
		return getFeildValue(this.locateSetting).replaceAll(":", "");
	}

	/**
	 * @param areaPoints the areaPoints to set
	 */
	public void setAreaPoints(String areaPoints) {
		this.areaPoints = areaPoints;
	}

	/**
	 * @param areaRadius the areaRadius to set
	 */
	public void setAreaRadius(String areaRadius) {
		this.areaRadius = areaRadius;
	}

 
	/**
	 * @param locateSetting the locateSetting to set
	 */
	public void setLocateSetting(String locateSetting) {
		this.locateSetting = locateSetting;
	}

	/**
	 * @return the sosKey
	 */
	public String getSosKey() {
		return getFeildValue(this.sosKey);
	}

	/**
	 * @return the key1
	 */
	public String getKey1() {
		return getFeildValue(this.key1);
	}

	/**
	 * @return the key2
	 */
	public String getKey2() {
		return getFeildValue(this.key2);
	}

	/**
	 * @return the key3
	 */
	public String getKey3() {
		return getFeildValue(this.key3);
	}

	/**
	 * @return the key4
	 */
	public String getKey4() {
		return getFeildValue(this.key4);
	}

	/**
	 * @return the key5
	 */
	public String getKey5() {
		return getFeildValue(this.key5);
	}

	/**
	 * @return the gpsModel
	 */
	public String getGpsModel() {
		return getFeildValue(this.gpsModel);
	}

	/**
	 * @return the cidModel
	 */
	public String getCidModel() {
		return getFeildValue(this.cidModel);
	}

 

	/**
	 * @return the gpsInterval
	 */
	public String getGpsInterval() {
		return getFeildValue(this.gpsInterval);
	}

	/**
	 * @return the cidInterval
	 */
	public String getCidInterval() {
		return getFeildValue(this.cidInterval);
	}

	/**
	 * @return the silenceInterval
	 */
	public String getSilenceInterval() {
		return getFeildValue(this.silenceInterval);
	}

	/**
	 * @return the gpsSendCycle
	 */
	public String getGpsSendCycle() {
		return getFeildValue(this.gpsSendCycle);
	}

	/**
	 * @return the gpsSampleCycle
	 */
	public String getGpsSampleCycle() {
		return getFeildValue(this.gpsSampleCycle);
	}

	/**
	 * @return the gpsSendTimes
	 */
	public String getGpsSendTimes() {
		return getFeildValue(this.gpsSendTimes);
	}

	/**
	 * @return the cidSendCycle
	 */
	public String getCidSendCycle() {
		return getFeildValue(this.cidSendCycle);
	}

	/**
	 * @return the cidSampleCycle
	 */
	public String getCidSampleCycle() {
		return getFeildValue(this.cidSampleCycle);
	}

	/**
	 * @return the cidSendTimes
	 */
	public String getCidSendTimes() {
		return getFeildValue(this.cidSendTimes);
	}

	/**
	 * @return the silenceSendCycle
	 */
	public String getSilenceSendCycle() {
		return getFeildValue(this.silenceSendCycle);
	}

	/**
	 * @return the newLocKeepTime
	 */
	public String getNewLocKeepTime() {
		return getFeildValue(this.newLocKeepTime);
	}

	/**
	 * @param sosKey the sosKey to set
	 */
	public void setSosKey(String sosKey) {
		this.sosKey = sosKey;
	}

	/**
	 * @param key1 the key1 to set
	 */
	public void setKey1(String key1) {
		this.key1 = key1;
	}

	/**
	 * @param key2 the key2 to set
	 */
	public void setKey2(String key2) {
		this.key2 = key2;
	}

	/**
	 * @param key3 the key3 to set
	 */
	public void setKey3(String key3) {
		this.key3 = key3;
	}

	/**
	 * @param key4 the key4 to set
	 */
	public void setKey4(String key4) {
		this.key4 = key4;
	}

	/**
	 * @param key5 the key5 to set
	 */
	public void setKey5(String key5) {
		this.key5 = key5;
	}

	/**
	 * @param gpsModel the gpsModel to set
	 */
	public void setGpsModel(String gpsModel) {
		this.gpsModel = gpsModel;
	}

	/**
	 * @param cidModel the cidModel to set
	 */
	public void setCidModel(String cidModel) {
		this.cidModel = cidModel;
	}

 
	/**
	 * @return the silenceModel
	 */
	public String getSilenceModel() {
		return getFeildValue(this.silenceModel);
	}

	/**
	 * @param silenceModel the silenceModel to set
	 */
	public void setSilenceModel(String silenceModel) {
		this.silenceModel = silenceModel;
	}

	/**
	 * @param gpsInterval the gpsInterval to set
	 */
	public void setGpsInterval(String gpsInterval) {
		this.gpsInterval = gpsInterval;
	}

	/**
	 * @param cidInterval the cidInterval to set
	 */
	public void setCidInterval(String cidInterval) {
		this.cidInterval = cidInterval;
	}

	/**
	 * @param silenceInterval the silenceInterval to set
	 */
	public void setSilenceInterval(String silenceInterval) {
		this.silenceInterval = silenceInterval;
	}

	/**
	 * @param gpsSendCycle the gpsSendCycle to set
	 */
	public void setGpsSendCycle(String gpsSendCycle) {
		this.gpsSendCycle = gpsSendCycle;
	}

	/**
	 * @param gpsSampleCycle the gpsSampleCycle to set
	 */
	public void setGpsSampleCycle(String gpsSampleCycle) {
		this.gpsSampleCycle = gpsSampleCycle;
	}

	/**
	 * @param gpsSendTimes the gpsSendTimes to set
	 */
	public void setGpsSendTimes(String gpsSendTimes) {
		this.gpsSendTimes = gpsSendTimes;
	}

	/**
	 * @param cidSendCycle the cidSendCycle to set
	 */
	public void setCidSendCycle(String cidSendCycle) {
		this.cidSendCycle = cidSendCycle;
	}

	/**
	 * @param cidSampleCycle the cidSampleCycle to set
	 */
	public void setCidSampleCycle(String cidSampleCycle) {
		this.cidSampleCycle = cidSampleCycle;
	}

	/**
	 * @param cidSendTimes the cidSendTimes to set
	 */
	public void setCidSendTimes(String cidSendTimes) {
		this.cidSendTimes = cidSendTimes;
	}

	/**
	 * @param silenceSendCycle the silenceSendCycle to set
	 */
	public void setSilenceSendCycle(String silenceSendCycle) {
		this.silenceSendCycle = silenceSendCycle;
	}

	/**
	 * @param newLocKeepTime the newLocKeepTime to set
	 */
	public void setNewLocKeepTime(String newLocKeepTime) {
		this.newLocKeepTime = newLocKeepTime;
	}

	public String getResponseCommond(){
		String ret = "";
		StringBuilder sbuilder = new StringBuilder();
		sbuilder.append("0="+getFeildValue(this.sosKey));
		sbuilder.append("!");
		sbuilder.append("1="+getFeildValue(this.key1));
		sbuilder.append("!");
		sbuilder.append("2="+getFeildValue(this.key1));
		sbuilder.append("!");
		sbuilder.append("3="+getFeildValue(this.key1));
		sbuilder.append("!");
		sbuilder.append("4="+getFeildValue(this.key1));
		sbuilder.append("!");
		
		return ret;
	}
	
	private String getFeildValue(String filed){
		return filed==null?"":filed;
	}
	
	
	
	
	
 
}
