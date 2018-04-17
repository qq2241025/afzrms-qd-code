/**
 * 
 */
package com.mapabc.gater.directl.bean.status;

/**
 * 终端所有状态
 * 
 */
public abstract class AbstractTermExtendStatus implements java.io.Serializable {
	/**
	 * 设备ID
	 */
	private String deviceId;
	/**
	 * 位置ID
	 */
	private String locUuid;

	/**
	 * 位置ID(UUID形式)
	 */
	public  String getLocUuid() {
		return locUuid;
	}
	/**
	 * 设置位置ID(UUID形式)
	 * @param locUuid
	 */
	public  void setLocUuid(String locUuid) {
		this.locUuid = locUuid;
	}

	/**
	 * 设备ID
	 */
	public String getDeviceId() {
		return this.deviceId;
	}

	/**
	 * 设置设备ID
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

 
	
}
