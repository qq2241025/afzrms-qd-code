package com.dt.afzrms.vo;

import java.io.Serializable;
import java.util.List;

/**
 * @Title 分页
 * @Description TODO
 * @author
 * @createDate 2015-1-24 下午3:59:46
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public class PageVo<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer total;// 总记录条数
	private List<T> data;// 记录

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	public PageVo() {
		super();
	}

	public PageVo(Integer total, List<T> data) {
		super();
		this.total = total;
		this.data = data;
	}

}
