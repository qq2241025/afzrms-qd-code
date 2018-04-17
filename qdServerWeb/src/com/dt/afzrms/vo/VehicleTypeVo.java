package com.dt.afzrms.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * @Title VehicleType Vo
 * @Description TODO
 * @author
 * @createDate 2015-1-25 下午4:48:23
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public class VehicleTypeVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String name;
	private String description;
	private String createBy;
	private Date createTime;
	private String remark;

	public VehicleTypeVo() {
		super();
	}

	public VehicleTypeVo(Integer id, String name, String description,
			String createBy, Date createTime, String remark) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.createBy = createBy;
		this.createTime = createTime;
		this.remark = remark;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
