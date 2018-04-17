package com.mapabc.gater.directl.bean.command;

import java.util.Date;

/**
 * AbstractTStructions generated by MyEclipse Persistence Tools
 */

public abstract class AbstractTStructions implements java.io.Serializable {

	// Fields

	private String id;

	private String deviceId;

	private String objId;

	private String objType;

	private String req;

	private Date receiveTime;

	private String type;

	private String instruction;

	private String param;

	private Date sendTime;

	private String sendCount;

	private String reply;

	private String state;

	private String descp;

	private String creator;

	private Date createTime;
	
	private long createTimeMills;

	// Constructors

	public synchronized long getCreateTimeMills() {
		return createTimeMills;
	}

	public synchronized void setCreateTimeMills(long createTimeMills) {
		this.createTimeMills = createTimeMills;
	}

	/** default constructor */
	public AbstractTStructions() {
	}

	/** full constructor */
	public AbstractTStructions(String deviceId, String objId, String objType,
			String req, Date receiveTime, String type, String instruction,
			String param, Date sendTime, String sendCount, String reply,
			String state, String descp, String creator, Date createTime) {
		this.deviceId = deviceId;
		this.objId = objId;
		this.objType = objType;
		this.req = req;
		this.receiveTime = receiveTime;
		this.type = type;
		this.instruction = instruction;
		this.param = param;
		this.sendTime = sendTime;
		this.sendCount = sendCount;
		this.reply = reply;
		this.state = state;
		this.descp = descp;
		this.creator = creator;
		this.createTime = createTime;
	}

	// Property accessors

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDeviceId() {
		return this.deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getObjId() {
		return this.objId;
	}

	public void setObjId(String objId) {
		this.objId = objId;
	}

	public String getObjType() {
		return this.objType;
	}

	public void setObjType(String objType) {
		this.objType = objType;
	}

	public String getReq() {
		return this.req;
	}

	public void setReq(String req) {
		this.req = req;
	}

	public Date getReceiveTime() {
		return this.receiveTime;
	}

	public void setReceiveTime(Date receiveTime) {
		this.receiveTime = receiveTime;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getInstruction() {
		return this.instruction;
	}

	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}

	public String getParam() {
		return this.param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public Date getSendTime() {
		return this.sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public String getSendCount() {
		return this.sendCount;
	}

	public void setSendCount(String sendCount) {
		this.sendCount = sendCount;
	}

	public String getReply() {
		return this.reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDescp() {
		return this.descp;
	}

	public void setDescp(String descp) {
		this.descp = descp;
	}

	public String getCreator() {
		return this.creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}