package com.dt.afzrms.vo;

import java.io.Serializable;

/**
 * @Title module vo
 * @Description TODO
 * @author
 * @createDate 2015-1-24 下午6:51:10
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public class ModuleVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String name;
	private String description;
	private Integer parentId;
	private Integer sortNum;
	private String urlPath;
	private String jsClassname;
	private Boolean isUsed;
	private Boolean isVisible;
	private String parentName;

	public ModuleVo() {
		super();
	}

	public ModuleVo(Integer id, String name, String description, Integer parentId, Integer sortNum, String urlPath,
			String jsClassname, Boolean isUsed, Boolean isVisible, String parentName) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.parentId = parentId;
		this.sortNum = sortNum;
		this.urlPath = urlPath;
		this.jsClassname = jsClassname;
		this.isUsed = isUsed;
		this.isVisible = isVisible;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public String getUrlPath() {
		return urlPath;
	}

	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}

	public String getJsClassname() {
		return jsClassname;
	}

	public void setJsClassname(String jsClassname) {
		this.jsClassname = jsClassname;
	}

	public Boolean getIsUsed() {
		return isUsed;
	}

	public void setIsUsed(Boolean isUsed) {
		this.isUsed = isUsed;
	}

	public Boolean getIsVisible() {
		return isVisible;
	}

	public void setIsVisible(Boolean isVisible) {
		this.isVisible = isVisible;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

}
