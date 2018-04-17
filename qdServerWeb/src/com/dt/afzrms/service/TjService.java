package com.dt.afzrms.service;

import java.util.Date;

import com.dt.afzrms.vo.AlarmLocrecordVo;
import com.dt.afzrms.vo.PageVo;

/**
 * @Title tj service interface
 * @Description TODO
 * @author
 * @createDate 2015年3月17日 下午4:58:19
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public interface TjService {

	PageVo<AlarmLocrecordVo> findOverspeedAlarmList(Integer pageNo, Integer pageSize, String name, Date beginTime,
			Date endTime,double maxSpeed , double minSpeed) ;
	
	
	PageVo<AlarmLocrecordVo> findAlarmOnceList(Integer pageNo, Integer pageSize, String deviceId,String deptName, Date beginTime,
			Date endTime,double maxSpeed , double minSpeed) ;

	PageVo<AlarmLocrecordVo> findAreaAlarmList(Integer pageNo, Integer pageSize, String name, String alarmSubType,
			Date beginTime, Date endTime) throws Exception;

}
