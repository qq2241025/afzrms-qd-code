package com.dt.afzrms.vo;

import java.io.Serializable;
import java.util.List;

/**
 * @Title tree node vo
 * @Description TODO
 * @author
 * @createDate 2015-1-24 下午6:51:10
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public class TreeNodeVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String parentId;
	private String text;
	private Boolean leaf;
	private List<TreeNodeVo> children;

	public TreeNodeVo() {
		super();
	}

	public TreeNodeVo(String id, String parentId, String text, Boolean leaf,
			List<TreeNodeVo> children) {
		super();
		this.id = id;
		this.parentId = parentId;
		this.text = text;
		this.leaf = leaf;
		this.children = children;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Boolean getLeaf() {
		return leaf;
	}

	public void setLeaf(Boolean leaf) {
		this.leaf = leaf;
	}

	public List<TreeNodeVo> getChildren() {
		return children;
	}

	public void setChildren(List<TreeNodeVo> children) {
		this.children = children;
	}

}
