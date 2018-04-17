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

/**
 * @Title TODO
 * @Description TODO
 * @author Administrator
 * @createDate 2016年5月5日 下午11:18:41
 * @modifier 
 * @modifyDate 
 * @version 1.0
 * 
 */
public class TjTermAlarmVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String deviceId;
	private Date tjDate;
	private int speedAlarmCount;
	private int areaAlarmCount;
	private int areaSpeedAlarmCount;
	private String name;
	private String deptName;
	private String vetypeName;
	private String vehicleBrand;
	public TjTermAlarmVo() {
		super();
	}

	public TjTermAlarmVo(String deviceId, Date tjDate, int speedAlarmCount, int areaAlarmCount,
			int areaSpeedAlarmCount, String name, String deptName,String vetypeName,String vehicleBrand) {
		super();
		this.deviceId = deviceId;
		this.tjDate = tjDate;
		this.speedAlarmCount = speedAlarmCount;
		this.areaAlarmCount = areaAlarmCount;
		this.areaSpeedAlarmCount = areaSpeedAlarmCount;
		this.name = name;
		this.deptName = deptName;
		this.vetypeName = vetypeName;
		this.vehicleBrand = vehicleBrand;
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

	public int getSpeedAlarmCount() {
		return speedAlarmCount;
	}

	public void setSpeedAlarmCount(int speedAlarmCount) {
		this.speedAlarmCount = speedAlarmCount;
	}

	public int getAreaAlarmCount() {
		return areaAlarmCount;
	}

	public void setAreaAlarmCount(int areaAlarmCount) {
		this.areaAlarmCount = areaAlarmCount;
	}

	public int getAreaSpeedAlarmCount() {
		return areaSpeedAlarmCount;
	}

	public void setAreaSpeedAlarmCount(int areaSpeedAlarmCount) {
		this.areaSpeedAlarmCount = areaSpeedAlarmCount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getVetypeName() {
		return vetypeName;
	}

	public void setVetypeName(String vetypeName) {
		this.vetypeName = vetypeName;
	}

	public String getVehicleBrand() {
		return vehicleBrand;
	}

	public void setVehicleBrand(String vehicleBrand) {
		this.vehicleBrand = vehicleBrand;
	}

}
