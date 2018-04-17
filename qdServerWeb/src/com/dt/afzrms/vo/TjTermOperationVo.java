package com.dt.afzrms.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * @Title tj term alarm vo
 * @Description TODO
 * @author
 * @createDate 2015年3月31日 下午3:31:25
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public class TjTermOperationVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String deviceId;
	private Date tjDate;
	private int travelTime;
	private int engineRunningTime;
	private float distance;
	private float maxSpeed;
	private Date maxSpeedTime;
	private float averageSpeed;
	private String name;

	public TjTermOperationVo() {
		super();
	}

	public TjTermOperationVo(String deviceId, Date tjDate, int travelTime, int engineRunningTime, float distance,
			float maxSpeed, Date maxSpeedTime, float averageSpeed, String name) {
		super();
		this.deviceId = deviceId;
		this.tjDate = tjDate;
		this.travelTime = travelTime;
		this.engineRunningTime = engineRunningTime;
		this.distance = distance;
		this.maxSpeed = maxSpeed;
		this.maxSpeedTime = maxSpeedTime;
		this.averageSpeed = averageSpeed;
		this.name = name;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public Date getTjDate() {
		return tjDate;
	}

	public void setTjDate(Date tjDate) {
		this.tjDate = tjDate;
	}

	public int getTravelTime() {
		return travelTime;
	}

	public void setTravelTime(int travelTime) {
		this.travelTime = travelTime;
	}

	public int getEngineRunningTime() {
		return engineRunningTime;
	}

	public void setEngineRunningTime(int engineRunningTime) {
		this.engineRunningTime = engineRunningTime;
	}

	public float getDistance() {
		return distance;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}

	public float getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(float maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public Date getMaxSpeedTime() {
		return maxSpeedTime;
	}

	public void setMaxSpeedTime(Date maxSpeedTime) {
		this.maxSpeedTime = maxSpeedTime;
	}

	public float getAverageSpeed() {
		return averageSpeed;
	}

	public void setAverageSpeed(float averageSpeed) {
		this.averageSpeed = averageSpeed;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
