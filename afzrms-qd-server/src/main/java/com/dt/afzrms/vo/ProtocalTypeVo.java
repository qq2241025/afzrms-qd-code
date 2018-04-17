package com.dt.afzrms.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * @Title ProtocalType Vo
 * @Description TODO
 * @author
 * @createDate 2015-1-25 下午4:48:23
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public class ProtocalTypeVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String protocalName;
	private String name;
	private String description;
	private String createBy;
	private Date createTime;

	public ProtocalTypeVo() {
		super();
	}

	public ProtocalTypeVo(String protocalName, String name, String description, String createBy, Date createTime) {
		super();
		this.protocalName = protocalName;
		this.name = name;
		this.description = description;
		this.createBy = createBy;
		this.createTime = createTime;
	}

	public String getProtocalName() {
		return protocalName;
	}

	public void setProtocalName(String protocalName) {
		this.protocalName = protocalName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}
