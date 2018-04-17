/**
 * 
 */
package com.mapabc.gater.directl.bean;

/**
 * @author 
 *
 */
public class TaskStatusBean {
	
	private String key ;
	
	private String value;
	
	private String groupId;
	
	private String deviceId;
	
	private String frequence;

	/**
	 * @return the frequence
	 */
	public String getFrequence() {
		return this.frequence;
	}

	/**
	 * @param frequence the frequence to set
	 */
	public void setFrequence(String frequence) {
		this.frequence = frequence;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return this.key;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * @return the groupId
	 */
	public String getGroupId() {
		return this.groupId;
	}

	/**
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return this.deviceId;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	/**
	 * @param deviceId the deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	

}
