/**
 * 
 */
package com.mapabc.gater.directl.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 *
 */
public class TermOnlieStatus implements Serializable{
	
	private String id;
	
	private String deviceId;
	
	private String curIp;
	
	private String status;
	
	private Date inDate;
	
	private Date outDate;
	
	/**链路类型 0：TCP 1:UDP*/
	private String type;
	

	/**
	 * @return the type
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return this.deviceId;
	}

	/**
	 * @return the curIp
	 */
	public String getCurIp() {
		return this.curIp;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return this.status;
	}

 

	/**
	 * @param deviceId the deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	/**
	 * @param curIp the curIp to set
	 */
	public void setCurIp(String curIp) {
		this.curIp = curIp;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the inDate
	 */
	public Date getInDate() {
		return this.inDate;
	}

	/**
	 * @return the outDate
	 */
	public Date getOutDate() {
		return this.outDate;
	}

	/**
	 * @param inDate the inDate to set
	 */
	public void setInDate(Date inDate) {
		this.inDate = inDate;
	}

	/**
	 * @param outDate the outDate to set
	 */
	public void setOutDate(Date outDate) {
		this.outDate = outDate;
	}

 
	

}
