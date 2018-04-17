package com.dt.afzrms.vo;

import java.io.Serializable;

/**
 * @Title ref alarmRuleArea alarmArea
 * @Description TODO
 * @author
 * @createDate 2015年3月27日 上午11:03:45
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public class RefAlarmRuleAreaAlarmAreaVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer alarmRuleAreaId;
	private Integer alarmAreaId;
	private Integer alarmType;
	private Integer alarmNo;
	private Float overspeedThreshold;

	public RefAlarmRuleAreaAlarmAreaVo() {
		super();
	}

	public RefAlarmRuleAreaAlarmAreaVo(Integer id, Integer alarmRuleAreaId, Integer alarmAreaId, Integer alarmType,
			Integer alarmNo, Float overspeedThreshold) {
		super();
		this.id = id;
		this.alarmRuleAreaId = alarmRuleAreaId;
		this.alarmAreaId = alarmAreaId;
		this.alarmType = alarmType;
		this.alarmNo = alarmNo;
		this.overspeedThreshold = overspeedThreshold;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getAlarmRuleAreaId() {
		return alarmRuleAreaId;
	}

	public void setAlarmRuleAreaId(Integer alarmRuleAreaId) {
		this.alarmRuleAreaId = alarmRuleAreaId;
	}

	public Integer getAlarmAreaId() {
		return alarmAreaId;
	}

	public void setAlarmAreaId(Integer alarmAreaId) {
		this.alarmAreaId = alarmAreaId;
	}

	public Integer getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(Integer alarmType) {
		this.alarmType = alarmType;
	}

	public Integer getAlarmNo() {
		return alarmNo;
	}

	public void setAlarmNo(Integer alarmNo) {
		this.alarmNo = alarmNo;
	}

	public Float getOverspeedThreshold() {
		return overspeedThreshold;
	}

	public void setOverspeedThreshold(Float overspeedThreshold) {
		this.overspeedThreshold = overspeedThreshold;
	}

}
