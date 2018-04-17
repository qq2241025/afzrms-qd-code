package com.dt.afzrms.po;

// Generated 2015-3-27 10:49:39 by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * RefAlarmruleAreaAlarmarea generated by hbm2java
 */
@Entity
@Table(name = "REF_ALARMRULE_AREA_ALARMAREA", catalog = "qdafz")
public class RefAlarmruleAreaAlarmarea implements java.io.Serializable {

	private Integer id;
	private TAlarmruleArea TAlarmruleArea;
	private TAlarmArea TAlarmArea;
	private Integer alarmType;
	private Integer areaNo;
	private Float overspeedThreshold;

	public RefAlarmruleAreaAlarmarea() {
	}

	public RefAlarmruleAreaAlarmarea(TAlarmruleArea TAlarmruleArea, TAlarmArea TAlarmArea, Integer alarmType,
			Integer areaNo, Float overspeedThreshold) {
		this.TAlarmruleArea = TAlarmruleArea;
		this.TAlarmArea = TAlarmArea;
		this.alarmType = alarmType;
		this.areaNo = areaNo;
		this.overspeedThreshold = overspeedThreshold;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "alarmrule_area_id")
	public TAlarmruleArea getTAlarmruleArea() {
		return this.TAlarmruleArea;
	}

	public void setTAlarmruleArea(TAlarmruleArea TAlarmruleArea) {
		this.TAlarmruleArea = TAlarmruleArea;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "alarm_area_id")
	public TAlarmArea getTAlarmArea() {
		return this.TAlarmArea;
	}

	public void setTAlarmArea(TAlarmArea TAlarmArea) {
		this.TAlarmArea = TAlarmArea;
	}

	@Column(name = "alarm_type")
	public Integer getAlarmType() {
		return this.alarmType;
	}

	public void setAlarmType(Integer alarmType) {
		this.alarmType = alarmType;
	}

	@Column(name = "area_no")
	public Integer getAreaNo() {
		return this.areaNo;
	}

	public void setAreaNo(Integer areaNo) {
		this.areaNo = areaNo;
	}

	@Column(name = "overspeed_threshold", precision = 12, scale = 0)
	public Float getOverspeedThreshold() {
		return this.overspeedThreshold;
	}

	public void setOverspeedThreshold(Float overspeedThreshold) {
		this.overspeedThreshold = overspeedThreshold;
	}

}
