package com.dt.afzrms.vo;

import java.io.Serializable;

/**
 * @Title dept vo
 * @Description TODO
 * @author
 * @createDate 2015-1-24 下午6:51:10
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public class DeptVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String name;
	private Integer parentId;
	private Integer sortNum;
	private String duty;
	private String director;
	private String remark;
	private String parentName;

	public DeptVo() {
		super();
	}

	public DeptVo(Integer id, String name, Integer parentId, Integer sortNum,
			String duty, String director, String remark, String parentName) {
		super();
		this.id = id;
		this.name = name;
		this.parentId = parentId;
		this.sortNum = sortNum;
		this.duty = duty;
		this.director = director;
		this.remark = remark;
		this.parentName = parentName;
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

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Integer getSortNum() {
		return sortNum;
	}

	public void setSortNum(Integer sortNum) {
		this.sortNum = sortNum;
	}

	public String getDuty() {
		return duty;
	}

	public void setDuty(String duty) {
		this.duty = duty;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

}
