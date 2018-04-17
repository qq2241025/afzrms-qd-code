package com.dt.afzrms.service;

import com.dt.afzrms.po.TMarkers;
import com.dt.afzrms.vo.UserVo;

/**
 * @Title tj term alarm service interface
 * @Description TODO
 * @author
 * @createDate 2015年3月31日 下午3:30:46
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public interface TMarkersService {

	public String findList(Integer pageNo, Integer pageSize, String poiName, UserVo user);

	public void add(TMarkers markers);

	public void update(TMarkers markers);

	public void delete(String[] ids);
	
}
