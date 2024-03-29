package com.dt.afzrms.po;

// Generated 2016-8-27 18:48:46 by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * TLastLocrecord generated by hbm2java
 */
@Entity
@Table(name = "T_LAST_LOCRECORD", catalog = "qdafz")
public class TLastLocrecord implements java.io.Serializable {

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
	private Date gpsTime;
	private Date inputTime;
	private byte[] deviceStatus;
	private String status;
	private byte[] alarmSubType;
	private byte[] alarmType;
	private Float speedThreshold;
	private Integer areaNo;

	public TLastLocrecord() {
	}

	public TLastLocrecord(String id, Date gpsTime, Date inputTime) {
		this.id = id;
		this.gpsTime = gpsTime;
		this.inputTime = inputTime;
	}

	public TLastLocrecord(String id, String deviceId, Double x, Double y,
			Float speed, Float direction, Float height, Float distance,
			Date gpsTime, Date inputTime, byte[] deviceStatus, String status,
			byte[] alarmSubType, byte[] alarmType, Float speedThreshold,
			Integer areaNo) {
		this.id = id;
		this.deviceId = deviceId;
		this.x = x;
		this.y = y;
		this.speed = speed;
		this.direction = direction;
		this.height = height;
		this.distance = distance;
		this.gpsTime = gpsTime;
		this.inputTime = inputTime;
		this.deviceStatus = deviceStatus;
		this.status = status;
		this.alarmSubType = alarmSubType;
		this.alarmType = alarmType;
		this.speedThreshold = speedThreshold;
		this.areaNo = areaNo;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false, length = 40)
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name = "device_id", length = 40)
	public String getDeviceId() {
		return this.deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	@Column(name = "x", precision = 22, scale = 0)
	public Double getX() {
		return this.x;
	}

	public void setX(Double x) {
		this.x = x;
	}

	@Column(name = "y", precision = 22, scale = 0)
	public Double getY() {
		return this.y;
	}

	public void setY(Double y) {
		this.y = y;
	}

	@Column(name = "speed", precision = 12, scale = 0)
	public Float getSpeed() {
		return this.speed;
	}

	public void setSpeed(Float speed) {
		this.speed = speed;
	}

	@Column(name = "direction", precision = 12, scale = 0)
	public Float getDirection() {
		return this.direction;
	}

	public void setDirection(Float direction) {
		this.direction = direction;
	}

	@Column(name = "height", precision = 12, scale = 0)
	public Float getHeight() {
		return this.height;
	}

	public void setHeight(Float height) {
		this.height = height;
	}

	@Column(name = "distance", precision = 12, scale = 0)
	public Float getDistance() {
		return this.distance;
	}

	public void setDistance(Float distance) {
		this.distance = distance;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "gps_time", nullable = false, length = 19)
	public Date getGpsTime() {
		return this.gpsTime;
	}

	public void setGpsTime(Date gpsTime) {
		this.gpsTime = gpsTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "input_time", nullable = false, length = 19)
	public Date getInputTime() {
		return this.inputTime;
	}

	public void setInputTime(Date inputTime) {
		this.inputTime = inputTime;
	}

	@Column(name = "device_status")
	public byte[] getDeviceStatus() {
		return this.deviceStatus;
	}

	public void setDeviceStatus(byte[] deviceStatus) {
		this.deviceStatus = deviceStatus;
	}

	@Column(name = "status", length = 40)
	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "alarm_sub_type")
	public byte[] getAlarmSubType() {
		return this.alarmSubType;
	}

	public void setAlarmSubType(byte[] alarmSubType) {
		this.alarmSubType = alarmSubType;
	}

	@Column(name = "alarm_type")
	public byte[] getAlarmType() {
		return this.alarmType;
	}

	public void setAlarmType(byte[] alarmType) {
		this.alarmType = alarmType;
	}

	@Column(name = "speed_threshold", precision = 12, scale = 0)
	public Float getSpeedThreshold() {
		return this.speedThreshold;
	}

	public void setSpeedThreshold(Float speedThreshold) {
		this.speedThreshold = speedThreshold;
	}

	@Column(name = "area_no")
	public Integer getAreaNo() {
		return this.areaNo;
	}

	public void setAreaNo(Integer areaNo) {
		this.areaNo = areaNo;
	}

}
