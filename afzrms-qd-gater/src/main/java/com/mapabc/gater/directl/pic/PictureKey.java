/**
 * 
 */
package com.mapabc.gater.directl.pic;

import java.io.Serializable;

/**
 * @author 
 *
 */
public class PictureKey implements Serializable {
	
	private String deviceId;
	private int picNum;
	private int channel;
	
	/**
	 * @return the channel
	 */
	public int getChannel() {
		return this.channel;
	}
	/**
	 * @param channel the channel to set
	 */
	public void setChannel(int channel) {
		this.channel = channel;
	}
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
	 * @return the picNum
	 */
	public int getPicNum() {
		return this.picNum;
	}
	/**
	 * @param picNum the picNum to set
	 */
	public void setPicNum(int picNum) {
		this.picNum = picNum;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.channel;
		result = prime * result
				+ ((this.deviceId == null) ? 0 : this.deviceId.hashCode());
		result = prime * result + this.picNum;
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
		final PictureKey other = (PictureKey) obj;
		if (this.channel != other.channel)
			return false;
		if (this.deviceId == null) {
			if (other.deviceId != null)
				return false;
		} else if (!this.deviceId.equals(other.deviceId))
			return false;
		if (this.picNum != other.picNum)
			return false;
		return true;
	}
 
	
	

}
