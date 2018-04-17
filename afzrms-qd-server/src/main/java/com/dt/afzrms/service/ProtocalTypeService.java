package com.dt.afzrms.service;

import com.dt.afzrms.vo.PageVo;
import com.dt.afzrms.vo.ProtocalTypeVo;

/**
 * @Title 终端协议类型业务层接口
 * @Description TODO
 * @author
 * @createDate 2015-1-25 下午4:50:00
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public interface ProtocalTypeService {
	public PageVo<ProtocalTypeVo> findList(Integer pageNo, Integer pageSize,
			String name);
}
