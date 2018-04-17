package com.dt.afzrms.service;

import java.util.Date;
import com.dt.afzrms.vo.LocrecordVo;
import com.dt.afzrms.vo.PageVo;
import com.mapabc.common.dao.hibernate.Page;

/**
 * @Title TODO
 * @Description TODO
 * @author
 * @createDate 2015年2月10日 上午10:28:24
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public interface LocrecordService {
	public PageVo<LocrecordVo> findList(Integer pageNo, Integer pageSize, String deviceId, Date beginTime, Date endTime);
	
	public String findLocrecordList(Integer pageNo, Integer pageSize, String[] deviceIds, Date beginDate, Date endDate);
	
	public String getLostTrackData(String[] deviceIds);
	
	public String getPageLostTrackData(Integer pageNo, Integer pageSize);
	
	public String getAllLastTrackData();
	
	public Page<Object[]> findLocrecordListByPage(Integer pageNo, Integer pageSize, String[] deviceIds, Date beginDate, Date endDate);
}
