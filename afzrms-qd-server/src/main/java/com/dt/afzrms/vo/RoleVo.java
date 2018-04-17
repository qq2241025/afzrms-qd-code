package com.dt.afzrms.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * @Title role vo
 * @Description TODO
 * @author
 * @createDate 2015-1-25 下午2:03:56
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public class RoleVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer deptId;
	private String deptName;
	private String name;
	private String description;
	private String createBy;
	private Date createTime;
	private String remark;
	private Boolean roleType;

	public RoleVo() {
		super();
	}

	public RoleVo(Integer id, Integer deptId, String deptName, String name,
			String description, String createBy, Date createTime,
			String remark, Boolean roleType) {
		super();
		this.id = id;
		this.deptId = deptId;
		this.deptName = deptName;
		this.name = name;
		this.description = description;
		this.createBy = createBy;
		this.createTime = createTime;
		this.remark = remark;
		this.roleType = roleType;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDeptId() {
		return deptId;
	}

	public void setDeptId(Integer deptId) {
		this.deptId = deptId;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
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

	public Boolean getRoleType() {
		return roleType;
	}

	public void setRoleType(Boolean roleType) {
		this.roleType = roleType;
	}

}
