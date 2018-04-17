package com.dt.afzrms.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * @Title terminal vo
 * @Description TODO
 * @author
 * @createDate 2015-1-25 下午3:05:50
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public class TerminalVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String deviceId;
	private Integer deptId;
	private String deptName;
	private Integer vehicleTypeId;
	private String vehicleTypeName;
	private Integer vehicleBrandId;
	private String vehicleBrandName;
	private String protocalType;
	private String protocalTypeName;
	private String name;
	private String simcard;
	private Boolean locateType;
	private String createBy;
	private Date createTime;
	private String remark;

	public TerminalVo() {
		super();
	}

	public TerminalVo(String deviceId, Integer deptId, String deptName,
			Integer vehicleTypeId, String vehicleTypeName,
			Integer vehicleBrandId, String vehicleBrandName,
			String protocalType, String protocalTypeName, String name,
			String simcard, Boolean locateType, String createBy,
			Date createTime, String remark) {
		super();
		this.deviceId = deviceId;
		this.deptId = deptId;
		this.deptName = deptName;
		this.vehicleTypeId = vehicleTypeId;
		this.vehicleTypeName = vehicleTypeName;
		this.vehicleBrandId = vehicleBrandId;
		this.vehicleBrandName = vehicleBrandName;
		this.protocalType = protocalType;
		this.protocalTypeName = protocalTypeName;
		this.name = name;
		this.simcard = simcard;
		this.locateType = locateType;
		this.createBy = createBy;
		this.createTime = createTime;
		this.remark = remark;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
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

	public Integer getVehicleTypeId() {
		return vehicleTypeId;
	}

	public void setVehicleTypeId(Integer vehicleTypeId) {
		this.vehicleTypeId = vehicleTypeId;
	}

	public String getVehicleTypeName() {
		return vehicleTypeName;
	}

	public void setVehicleTypeName(String vehicleTypeName) {
		this.vehicleTypeName = vehicleTypeName;
	}

	public Integer getVehicleBrandId() {
		return vehicleBrandId;
	}

	public void setVehicleBrandId(Integer vehicleBrandId) {
		this.vehicleBrandId = vehicleBrandId;
	}

	public String getVehicleBrandName() {
		return vehicleBrandName;
	}

	public void setVehicleBrandName(String vehicleBrandName) {
		this.vehicleBrandName = vehicleBrandName;
	}

	public String getProtocalType() {
		return protocalType;
	}

	public void setProtocalType(String protocalType) {
		this.protocalType = protocalType;
	}

	public String getProtocalTypeName() {
		return protocalTypeName;
	}

	public void setProtocalTypeName(String protocalTypeName) {
		this.protocalTypeName = protocalTypeName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSimcard() {
		return simcard;
	}

	public void setSimcard(String simcard) {
		this.simcard = simcard;
	}

	public Boolean getLocateType() {
		return locateType;
	}

	public void setLocateType(Boolean locateType) {
		this.locateType = locateType;
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
