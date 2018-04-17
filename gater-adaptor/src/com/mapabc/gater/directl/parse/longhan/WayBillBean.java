/**
 * 
 */
package com.mapabc.gater.directl.parse.longhan;

import java.io.Serializable;

/**
 * @author shiguang.zhou
 *
 */
public class WayBillBean implements Serializable{
	
	private String deviceId;
	private String wayNum;
	private boolean flag;
	/**
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return this.deviceId;
	}
	/**
	 * @param deviceId the deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	/**
	 * @return the wayNum
	 */
	public String getWayNum() {
		return this.wayNum;
	}
	/**
	 * @param wayNum the wayNum to set
	 */
	public void setWayNum(String wayNum) {
		this.wayNum = wayNum;
	}
	/**
	 * @return the flag
	 */
	public boolean isFlag() {
		return this.flag;
	}
	/**
	 * @param flag the flag to set
	 */
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.deviceId == null) ? 0 : this.deviceId.hashCode());
		result = prime * result
				+ ((this.wayNum == null) ? 0 : this.wayNum.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final WayBillBean other = (WayBillBean) obj;
		if (this.deviceId == null) {
			if (other.deviceId != null)
				return false;
		} else if (!this.deviceId.equals(other.deviceId))
			return false;
		if (this.wayNum == null) {
			if (other.wayNum != null)
				return false;
		} else if (!this.wayNum.equals(other.wayNum))
			return false;
		return true;
	}
	
	

}
