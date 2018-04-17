package com.dt.afzrms.vo;

import java.util.List;

/**
 * @Title dept tree node vo
 * @Description TODO
 * @author
 * @createDate 2015-1-24 下午6:51:10
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public class DeptTreeNodeVo extends TreeNodeVo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer sortNum;

	public DeptTreeNodeVo() {
		super();
	}

	public DeptTreeNodeVo(String id, String parentId, String text, Boolean leaf,
			List<TreeNodeVo> children, Integer sortNum) {
		super(id, parentId, text, leaf, children);
		this.sortNum = sortNum;
	}

	public Integer getSortNum() {
		return sortNum;
	}

	public void setSortNum(Integer sortNum) {
		this.sortNum = sortNum;
	}
}
