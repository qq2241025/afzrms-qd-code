package com.dt.afzrms.service;

import com.dt.afzrms.vo.PageVo;
import com.dt.afzrms.vo.VehicleTypeVo;

/**
 * @Title 车辆类型业务层接口
 * @Description TODO
 * @author
 * @createDate 2015-1-25 下午4:50:00
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public interface VehicleTypeService {
	public PageVo<VehicleTypeVo> findList(Integer pageNo, Integer pageSize,
			String name);

	public Integer add(String name, String uses, String remark, String createBy);

	public Integer update(Integer id, String name, String uses, String remark);

	public void delete(Integer[] ids);
}
