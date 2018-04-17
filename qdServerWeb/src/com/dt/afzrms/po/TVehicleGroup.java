package com.dt.afzrms.po;

// Generated 2015-1-22 18:06:40 by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * TVehicleGroup generated by hbm2java
 */
@Entity
@Table(name = "T_VEHICLE_GROUP", catalog = "qdafz")
public class TVehicleGroup implements java.io.Serializable {

	private Integer id;
	private String name;
	private String uses;
	private String createBy;
	private Date createTime;
	private String remark;
	private Set<RefVehicleGroup> refVehicleGroups = new HashSet<RefVehicleGroup>(0);

	public TVehicleGroup() {
	}
	
	public TVehicleGroup(Integer id) {
		this.id = id;
	}

	public TVehicleGroup(String name, String uses, String createBy, Date createTime, String remark,
			Set<RefVehicleGroup> refVehicleGroups) {
		this.name = name;
		this.uses = uses;
		this.createBy = createBy;
		this.createTime = createTime;
		this.remark = remark;
		this.refVehicleGroups = refVehicleGroups;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "name", length = 50)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "uses", length = 100)
	public String getUses() {
		return this.uses;
	}

	public void setUses(String uses) {
		this.uses = uses;
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "TVehicleGroup")
	public Set<RefVehicleGroup> getRefVehicleGroups() {
		return this.refVehicleGroups;
	}

	public void setRefVehicleGroups(Set<RefVehicleGroup> refVehicleGroups) {
		this.refVehicleGroups = refVehicleGroups;
	}

}
