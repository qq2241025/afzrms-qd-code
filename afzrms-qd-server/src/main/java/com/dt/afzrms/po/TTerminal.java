package com.dt.afzrms.po;

// Generated 2015-1-22 18:06:40 by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * TTerminal generated by hbm2java
 */
@Entity
@Table(name = "T_TERMINAL", catalog = "qdafz")
public class TTerminal implements java.io.Serializable {

	private String deviceId;
	private TVehicleType TVehicleType;
	private TDept TDept;
	private TProtocalType TProtocalType;
	private String name;
	private String simcard;
	private Boolean locateType;
	private Boolean isUsed;
	private String createBy;
	private Date createTime;
	private String remark;
	private Integer frequency;
	private TVehicleBrand TVehicleBrand;
	private Set<RefVehicleGroup> refVehicleGroups = new HashSet<RefVehicleGroup>(0);
	private Set<RefUserTerminal> refUserTerminals = new HashSet<RefUserTerminal>(0);
	private Set<RefTerminalAlarmruleArea> refTerminalAlarmruleAreas = new HashSet<RefTerminalAlarmruleArea>(0);
	private TTerminalAlarmset TTerminalAlarmset;

	public TTerminal() {
	}

	public TTerminal(String deviceId) {
		this.deviceId = deviceId;
	}

	public TTerminal(String deviceId, TVehicleType TVehicleType, TDept TDept, TProtocalType TProtocalType, String name,
			String simcard, Boolean locateType, Boolean isUsed, String createBy, Date createTime, String remark,
			Set<RefVehicleGroup> refVehicleGroups, Set<RefUserTerminal> refUserTerminals,
			Set<RefTerminalAlarmruleArea> refTerminalAlarmruleAreas, TTerminalAlarmset TTerminalAlarmset, TVehicleBrand TVehicleBrand) {
		this.deviceId = deviceId;
		this.TVehicleType = TVehicleType;
		this.TDept = TDept;
		this.TProtocalType = TProtocalType;
		this.name = name;
		this.simcard = simcard;
		this.locateType = locateType;
		this.isUsed = isUsed;
		this.createBy = createBy;
		this.createTime = createTime;
		this.remark = remark;
		this.TVehicleBrand = TVehicleBrand;
		this.refVehicleGroups = refVehicleGroups;
		this.refUserTerminals = refUserTerminals;
		this.refTerminalAlarmruleAreas = refTerminalAlarmruleAreas;
		this.TTerminalAlarmset = TTerminalAlarmset;
	}

	@Id
	@Column(name = "device_id", unique = true, nullable = false, length = 40)
	public String getDeviceId() {
		return this.deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vehicle_type_id")
	public TVehicleType getTVehicleType() {
		return this.TVehicleType;
	}

	public void setTVehicleType(TVehicleType TVehicleType) {
		this.TVehicleType = TVehicleType;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dept_id")
	public TDept getTDept() {
		return this.TDept;
	}

	public void setTDept(TDept TDept) {
		this.TDept = TDept;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "protocal_type")
	public TProtocalType getTProtocalType() {
		return this.TProtocalType;
	}

	public void setTProtocalType(TProtocalType TProtocalType) {
		this.TProtocalType = TProtocalType;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vehicle_brand_id")
	public TVehicleBrand getTVehicleBrand() {
		return this.TVehicleBrand;
	}

	public void setTVehicleBrand(TVehicleBrand TVehicleBrand) {
		this.TVehicleBrand = TVehicleBrand;
	}

	@Column(name = "name", length = 50)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "simcard", length = 11)
	public String getSimcard() {
		return this.simcard;
	}

	public void setSimcard(String simcard) {
		this.simcard = simcard;
	}

	@Column(name = "locate_type")
	public Boolean getLocateType() {
		return this.locateType;
	}

	public void setLocateType(Boolean locateType) {
		this.locateType = locateType;
	}

	@Column(name = "is_used")
	public Boolean getIsUsed() {
		return isUsed;
	}

	public void setIsUsed(Boolean isUsed) {
		this.isUsed = isUsed;
	}

	@Column(name = "create_by", length = 50)
	public String getCreateBy() {
		return this.createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time", length = 19)
	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "remark", length = 200)
	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	@Column(name = "frequency")
	public Integer getFrequency() {
		return frequency;
	}

	public void setFrequency(Integer frequency) {
		this.frequency = frequency;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "TTerminal")
	public Set<RefVehicleGroup> getRefVehicleGroups() {
		return this.refVehicleGroups;
	}

	public void setRefVehicleGroups(Set<RefVehicleGroup> refVehicleGroups) {
		this.refVehicleGroups = refVehicleGroups;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "TTerminal")
	public Set<RefUserTerminal> getRefUserTerminals() {
		return this.refUserTerminals;
	}

	public void setRefUserTerminals(Set<RefUserTerminal> refUserTerminals) {
		this.refUserTerminals = refUserTerminals;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "TTerminal")
	public Set<RefTerminalAlarmruleArea> getRefTerminalAlarmruleAreas() {
		return this.refTerminalAlarmruleAreas;
	}

	public void setRefTerminalAlarmruleAreas(Set<RefTerminalAlarmruleArea> refTerminalAlarmruleAreas) {
		this.refTerminalAlarmruleAreas = refTerminalAlarmruleAreas;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "TTerminal")
	public TTerminalAlarmset getTTerminalAlarmset() {
		return this.TTerminalAlarmset;
	}

	public void setTTerminalAlarmset(TTerminalAlarmset TTerminalAlarmset) {
		this.TTerminalAlarmset = TTerminalAlarmset;
	}

}
