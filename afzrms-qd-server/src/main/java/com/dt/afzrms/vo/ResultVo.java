package com.dt.afzrms.vo;

import java.io.Serializable;

/**
 * @Title result vo
 * @Description TODO
 * @author
 * @createDate 2015-1-24 下午6:26:30
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public class ResultVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Boolean success;
	private Object result;

	public ResultVo() {
		super();
	}

	public ResultVo(Boolean success, Object result) {
		super();
		this.success = success;
		this.result = result;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

}
