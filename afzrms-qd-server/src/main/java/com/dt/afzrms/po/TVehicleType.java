package com.dt.afzrms.po;

// Generated 2016-9-25 15:35:45 by Hibernate Tools 3.4.0.CR1

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
 * TVehicleType generated by hbm2java
 */
@Entity
@Table(name = "T_VEHICLE_TYPE", catalog = "qdafz")
public class TVehicleType implements java.io.Serializable {

	private Integer id;
	private String name;
	private String description;
	private String createBy;
	private Date createTime;
	private String remark;
	private String color;
	private Set<TTerminal> TTerminals = new HashSet<TTerminal>(0);

	public TVehicleType() {
	}
	
	public TVehicleType(Integer id) {
		this.id = id;
	}

	public TVehicleType(Date createTime) {
		this.createTime = createTime;
	}

	public TVehicleType(String name, String description, String createBy,
			Date createTime, String remark, String color,
			Set<TTerminal> TTerminals) {
		this.name = name;
		this.description = description;
		this.createBy = createBy;
		this.createTime = createTime;
		this.remark = remark;
		this.color = color;
		this.TTerminals = TTerminals;
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

	@Column(name = "description", length = 100)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "create_by", length = 50)
	public String getCreateBy() {
		return this.createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time", nullable = false, length = 19)
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

	@Column(name = "color")
	public String getColor() {
		return this.color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "TVehicleType")
	public Set<TTerminal> getTTerminals() {
		return this.TTerminals;
	}

	public void setTTerminals(Set<TTerminal> TTerminals) {
		this.TTerminals = TTerminals;
	}

}
