/**
 * @author 
 *
 */
package com.mapabc.gater.lbsgateway.bean;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author 
 *
 */
public class BaJuTask implements Serializable{
	
	private long id;
	
	private String deviceId;
	
	private String taskContet;
	
	private String state;
	
	private String type;
	
	private String reply;
	
	private Date crtdate;
	
	private String crtman;
	
	private byte[] buf;
	
	private String reqCont;
	
	private String taskTitle;
	
	private String userId;

	private Timestamp timestamp;
	 

	/**
	 * @return the timestamp
	 */
	public Timestamp getTimestamp() {
		return this.timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the reqCont
	 */
	public String getReqCont() {
		return this.reqCont;
	}

	/**
	 * @return the taskTitle
	 */
	public String getTaskTitle() {
		return this.taskTitle;
	}

	/**
	 * @param reqCont the reqCont to set
	 */
	public void setReqCont(String reqCont) {
		this.reqCont = reqCont;
	}

	/**
	 * @param taskTitle the taskTitle to set
	 */
	public void setTaskTitle(String taskTitle) {
		this.taskTitle = taskTitle;
	}

	public  Date getCrtdate() {
		return crtdate;
	}

	public  void setCrtdate(Date crtdate) {
		this.crtdate = crtdate;
	}

	public  String getCrtman() {
		return crtman;
	}

	public  void setCrtman(String crtman) {
		this.crtman = crtman;
	}

	public  String getDeviceId() {
		return deviceId;
	}

	public  void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public  long getId() {
		return id;
	}

	public  void setId(long id) {
		this.id = id;
	}

	public  String getReply() {
		return reply;
	}

	public  void setReply(String reply) {
		this.reply = reply;
	}

	public  String getState() {
		return state;
	}

	public  void setState(String state) {
		this.state = state;
	}

	public  String getTaskContet() {
		return taskContet;
	}

	public  void setTaskContet(String taskContet) {
		this.taskContet = taskContet;
	}

	public  String getType() {
		return type;
	}

	public  void setType(String type) {
		this.type = type;
	}

	public synchronized byte[] getBuf() {
		return buf;
	}

	public synchronized void setBuf(byte[] buf) {
		this.buf = buf;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return this.userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	

}
