package com.dt.afzrms.vo;

import java.io.Serializable;
import java.util.List;

/**
 * @Title tree vo
 * @Description TODO
 * @author
 * @createDate 2015-1-24 下午8:53:55
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public class TreeVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<TreeNodeVo> result;// 记录

	public TreeVo() {
		super();
	}

	public TreeVo(List<TreeNodeVo> result) {
		super();
		this.result = result;
	}

	public List<TreeNodeVo> getResult() {
		return result;
	}

	public void setResult(List<TreeNodeVo> result) {
		this.result = result;
	}

}
