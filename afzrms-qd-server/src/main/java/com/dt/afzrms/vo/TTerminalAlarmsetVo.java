package com.dt.afzrms.vo;

import java.io.Serializable;

/**
 * @Title TODO
 * @Description TODO
 * @author
 * @createDate 2015年5月28日 下午5:47:44
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public class TTerminalAlarmsetVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String deviceId;
	private Float overspeedThreshold;

	public TTerminalAlarmsetVo() {
		super();
	}

	public TTerminalAlarmsetVo(String deviceId, Float overspeedThreshold) {
		super();
		this.deviceId = deviceId;
		this.overspeedThreshold = overspeedThreshold;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public Float getOverspeedThreshold() {
		return overspeedThreshold;
	}

	public void setOverspeedThreshold(Float overspeedThreshold) {
		this.overspeedThreshold = overspeedThreshold;
	}

}
