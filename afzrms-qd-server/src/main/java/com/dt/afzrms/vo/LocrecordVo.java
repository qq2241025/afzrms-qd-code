package com.dt.afzrms.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * @Title TODO
 * @Description TODO
 * @author
 * @createDate 2015年2月10日 上午10:23:31
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public class LocrecordVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String deviceId;
	private Double x;
	private Double y;
	private Float speed;
	private Float direction;
	private Float height;
	private Float distance;
	private String gpsTime;
	private String deviceStatus;
	private String alarmTypes;

	public LocrecordVo() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public Double getX() {
		return x;
	}

	public void setX(Double x) {
		this.x = x;
	}

	public Double getY() {
		return y;
	}

	public void setY(Double y) {
		this.y = y;
	}

	public Float getSpeed() {
		return speed;
	}

	public void setSpeed(Float speed) {
		this.speed = speed;
	}

	public Float getDirection() {
		return direction;
	}

	public void setDirection(Float direction) {
		this.direction = direction;
	}

	public Float getHeight() {
		return height;
	}

	public void setHeight(Float height) {
		this.height = height;
	}

	public Float getDistance() {
		return distance;
	}

	public void setDistance(Float distance) {
		this.distance = distance;
	}

	

	public String getDeviceStatus() {
		return deviceStatus;
	}

	public void setDeviceStatus(String deviceStatus) {
		this.deviceStatus = deviceStatus;
	}

	public String getAlarmTypes() {
		return alarmTypes;
	}

	public void setAlarmTypes(String alarmTypes) {
		this.alarmTypes = alarmTypes;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getGpsTime() {
		return gpsTime;
	}

	public void setGpsTime(String gpsTime) {
		this.gpsTime = gpsTime;
	}

	public LocrecordVo(String id, String deviceId, Double x, Double y,
			Float speed, Float direction, Float height, Float distance,
			String gpsTime, String deviceStatus, String alarmTypes) {
		super();
		this.id = id;
		this.deviceId = deviceId;
		this.x = x;
		this.y = y;
		this.speed = speed;
		this.direction = direction;
		this.height = height;
		this.distance = distance;
		this.gpsTime = gpsTime;
		this.deviceStatus = deviceStatus;
		this.alarmTypes = alarmTypes;
	}


	
}
