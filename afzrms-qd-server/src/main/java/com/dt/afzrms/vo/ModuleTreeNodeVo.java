package com.dt.afzrms.vo;

import java.util.List;

/**
 * @Title module tree node vo
 * @Description TODO
 * @author
 * @createDate 2015-1-24 下午6:51:10
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public class ModuleTreeNodeVo extends TreeNodeVo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer sortNum;
	private String urlPath;
	private String jsClassname;
	private Boolean isUsed;
	private Boolean isVisible;
	private Boolean checked;

	public ModuleTreeNodeVo() {
		super();
	}

	public ModuleTreeNodeVo(String id, String parentId, String text, Boolean leaf, List<TreeNodeVo> children,
			Integer sortNum, String urlPath, String jsClassname, Boolean isUsed, Boolean isVisible, Boolean checked) {
		super(id, parentId, text, leaf, children);
		this.sortNum = sortNum;
		this.urlPath = urlPath;
		this.jsClassname = jsClassname;
		this.isUsed = isUsed;
		this.isVisible = isVisible;
		this.checked = checked;
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

	public Boolean getChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

}
