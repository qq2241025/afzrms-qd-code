package com.dt.afzrms.vo;

import java.util.List;

/**
 * @Title Terminal tree node vo
 * @Description TODO
 * @author
 * @createDate 2015-1-24 下午6:51:10
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public class TerminalTreeNodeVo extends TreeNodeVo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Boolean checked;
	private String simcard;
	private Boolean locateType;
	private Integer vehicleTypeId;
	private String vehicleTypeName;
	private String protocalType;
	private String protocalTypeName;
	private Integer deptId;
	private String deptName;
	private Integer vehicleBrandId;
	private String vehicleBrandName;
	private Boolean hasBindAreaAlarm;
	private String color;
	private String driverName;
	private String driverPhone;

	public TerminalTreeNodeVo() {
		super();
	}

	public TerminalTreeNodeVo(String id, String parentId, String text, Boolean leaf, List<TreeNodeVo> children,
			Boolean checked, String simcard, Boolean locateType, Integer vehicleTypeId, String vehicleTypeName,
			String protocalType, String protocalTypeName, Integer deptId, String deptName, Integer vehicleBrandId,
			String vehicleBrandName) {
		super(id, parentId, text, leaf, children);
		this.checked = checked;
		this.simcard = simcard;
		this.locateType = locateType;
		this.vehicleTypeId = vehicleTypeId;
		this.vehicleTypeName = vehicleTypeName;
		this.protocalType = protocalType;
		this.protocalTypeName = protocalTypeName;
		this.deptId = deptId;
		this.deptName = deptName;
		this.vehicleBrandId = vehicleBrandId;
		this.vehicleBrandName = vehicleBrandName;
	}

	public Boolean getChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
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

	public Boolean getHasBindAreaAlarm() {
		return hasBindAreaAlarm;
	}

	public void setHasBindAreaAlarm(Boolean hasBindAreaAlarm) {
		this.hasBindAreaAlarm = hasBindAreaAlarm;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getDriverPhone() {
		return driverPhone;
	}

	public void setDriverPhone(String driverPhone) {
		this.driverPhone = driverPhone;
	}

}
