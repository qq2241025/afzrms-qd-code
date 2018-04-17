package com.dt.afzrms.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * @Title alarm area vo
 * @Description TODO
 * @author
 * @createDate 2015-3-19 下午3:18:56
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public class AlarmAreaVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String name;
	private String description;
	private String xys;
	private Boolean isUsed;
	private String createBy;
	private Date createTime;
	private String remark;

	public AlarmAreaVo() {
		super();
	}

	public AlarmAreaVo(Integer id, String name, String description, String xys, Boolean isUsed, String createBy,
			Date createTime, String remark) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.xys = xys;
		this.isUsed = isUsed;
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

	public String getXys() {
		return xys;
	}

	public void setXys(String xys) {
		this.xys = xys;
	}

	public Boolean getIsUsed() {
		return isUsed;
	}

	public void setIsUsed(Boolean isUsed) {
		this.isUsed = isUsed;
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
