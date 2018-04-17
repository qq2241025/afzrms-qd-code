package com.dt.afzrms.vo;

import java.util.Date;

/**
 * @Title Alarm Locrecord Vo
 * @Description TODO
 * @author
 * @createDate 2015年3月17日 下午5:04:01
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public class AlarmLocrecordVo extends LocrecordVo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String alarmType;
	private String alarmSubType;
	private String termName;
	private String simcard;
	private String deptName;
	private Float speedThreshold;
	private Integer areaNo;

	public AlarmLocrecordVo() {
		super();
	}

	public AlarmLocrecordVo(String id, String deviceId, Double x, Double y, Float speed, Float direction, Float height,
			Float distance, String gpsTime, String deviceStatus, String alarmType, String alarmSubType, String termName,
			String simcard, String deptName, Float speedThreshold, Integer areaNo) {
		super(id, deviceId, x, y, speed, direction, height, distance, gpsTime, deviceStatus,alarmType);
		this.alarmType = alarmType;
		this.alarmSubType = alarmSubType;
		this.termName = termName;
		this.simcard = simcard;
		this.deptName = deptName;
		this.speedThreshold = speedThreshold;
		this.areaNo = areaNo;
	}

	public String getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}

	public String getAlarmSubType() {
		return alarmSubType;
	}

	public void setAlarmSubType(String alarmSubType) {
		this.alarmSubType = alarmSubType;
	}

	public String getTermName() {
		return termName;
	}

	public void setTermName(String termName) {
		this.termName = termName;
	}

	public String getSimcard() {
		return simcard;
	}

	public void setSimcard(String simcard) {
		this.simcard = simcard;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public Float getSpeedThreshold() {
		return speedThreshold;
	}

	public void setSpeedThreshold(Float speedThreshold) {
		this.speedThreshold = speedThreshold;
	}

	public Integer getAreaNo() {
		return areaNo;
	}

	public void setAreaNo(Integer areaNo) {
		this.areaNo = areaNo;
	}

}
