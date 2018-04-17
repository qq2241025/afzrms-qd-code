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

public class TjDeptAlarmVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int deptId;
	private Date tjDate;
	private int speedAlarmCount;
	private int areaAlarmCount;
	private int areaSpeedAlarmCount;
	private String name;

	public TjDeptAlarmVo() {
		super();
	}

	public TjDeptAlarmVo(int deviceId, Date tjDate, int speedAlarmCount, int areaAlarmCount,
			int areaSpeedAlarmCount, String name) {
		super();
		this.deptId = deviceId;
		this.tjDate = tjDate;
		this.speedAlarmCount = speedAlarmCount;
		this.areaAlarmCount = areaAlarmCount;
		this.areaSpeedAlarmCount = areaSpeedAlarmCount;
		this.name = name;
	}

	public int getDeptId() {
		return deptId;
	}

	public void setDeptId(int deptId) {
		this.deptId = deptId;
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

}
