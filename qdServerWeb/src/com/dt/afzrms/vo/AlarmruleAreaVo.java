package com.dt.afzrms.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Title alarm rule area vo
 * @Description TODO
 * @author
 * @createDate 2015年3月27日 上午11:00:10
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public class AlarmruleAreaVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String name;
	private Boolean isUsed;
	private String createBy;
	private Date createTime;
	private String remark;
	private List<RefAlarmRuleAreaAlarmAreaVo> areas;

	public AlarmruleAreaVo() {
		super();
	}

	public AlarmruleAreaVo(Integer id, String name, Boolean isUsed, String createBy, Date createTime, String remark,
			List<RefAlarmRuleAreaAlarmAreaVo> areas) {
		super();
		this.id = id;
		this.name = name;
		this.isUsed = isUsed;
		this.createBy = createBy;
		this.createTime = createTime;
		this.remark = remark;
		this.areas = areas;
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

	public List<RefAlarmRuleAreaAlarmAreaVo> getAreas() {
		return areas;
	}

	public void setAreas(List<RefAlarmRuleAreaAlarmAreaVo> areas) {
		this.areas = areas;
	}

}
