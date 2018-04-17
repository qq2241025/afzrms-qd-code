package com.dt.afzrms.service;

import com.dt.afzrms.vo.AlarmAreaVo;
import com.dt.afzrms.vo.PageVo;

/**
 * @Title service interface
 * @Description TODO
 * @author
 * @createDate 2015年3月19日 下午3:17:19
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public interface AlarmAreaService {
	public PageVo<AlarmAreaVo> findList(Integer pageNo, Integer pageSize, String name);

	public Integer add(String name, String xys, String description, String remark, String createBy);

	public Integer update(Integer id, String name, String xys, String description, String remark);

	public void delete(Integer[] ids);
	
	public String selectBindDevide(String deviceId);
	
	public String selectAllAlarmList();
}
