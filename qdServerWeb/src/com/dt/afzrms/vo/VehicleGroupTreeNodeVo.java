package com.dt.afzrms.vo;

import java.util.List;

/**
 * @Title VehicleGroup tree node vo
 * @Description TODO
 * @author
 * @createDate 2015-1-24 下午6:51:10
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public class VehicleGroupTreeNodeVo extends TreeNodeVo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public VehicleGroupTreeNodeVo() {
		super();
	}

	public VehicleGroupTreeNodeVo(String id, String parentId, String text, Boolean leaf,
			List<TreeNodeVo> children) {
		super(id, parentId, text, leaf, children);
	}
}
