/**
 * 
 */
package com.mapabc.gater.lbsgateway.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 *
 */
public class InstructionBean implements Serializable{
	
	private long id;
	
	private String instuction;
	
	private String simcard;
	
	private String state;
	
	private String type;
	
	private String reply;
	
	private Date createDate;
	
	private String createMan;
	
	private long sendCount;

	public long getSendCount() {
		return sendCount;
	}

	public void setSendCount(long sendCount) {
		this.sendCount = sendCount;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getCreateMan() {
		return createMan;
	}

	public void setCreateMan(String createMan) {
		this.createMan = createMan;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getInstuction() {
		return instuction;
	}

	public void setInstuction(String instuction) {
		this.instuction = instuction;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	public String getSimcard() {
		return simcard;
	}

	public void setSimcard(String simcard) {
		this.simcard = simcard;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	

}
