package com.dt.afzrms.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * @Title 用户
 * @Description TODO
 * @author
 * @createDate 2015-1-24 下午3:54:35
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public class UserVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer deptId;
	private String deptName;
	private String name;
	private String account;
	private String passwd;
	private String contact;
	private int isDeleted;
	private int userType;
	private String createBy;
	private Date createTime;
	private String remark;
	private Integer roleId;
	private String roleName;

	public UserVo() {
		super();
	}

	public UserVo(Integer id, Integer deptId, String deptName, String name, String account,
			String passwd, String contact, int isDeleted, int userType,
			String createBy, Date createTime, String remark, Integer roleId,
			String roleName) {
		super();
		this.id = id;
		this.deptId = deptId;
		this.deptName = deptName;
		this.name = name;
		this.account = account;
		this.passwd = passwd;
		this.contact = contact;
		this.isDeleted = isDeleted;
		this.userType = userType;
		this.createBy = createBy;
		this.createTime = createTime;
		this.remark = remark;
		this.roleId = roleId;
		this.roleName = roleName;
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

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public int getIsUsed() {
		return isDeleted;
	}

	public void setIsUsed(int isUsed) {
		this.isDeleted = isUsed;
	}

	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {
		this.userType = userType;
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

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

}
