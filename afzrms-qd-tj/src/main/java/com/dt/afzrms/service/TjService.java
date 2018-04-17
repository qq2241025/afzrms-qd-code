package com.dt.afzrms.service;

import java.util.Date;
import java.util.List;

/**
 * @Title tj service interface
 * @Description TODO
 * @author
 * @createDate 2015年4月6日 上午10:18:58
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public interface TjService {
	List<Object[]> findDeviceIdList();

	List<Object[]> findLocrecordList(String deviceId, Date beginDate, Date endDate) throws Exception;

	void saveTjDeptAlarmInit(Date tjDate);

	void saveTjTermOnofflineInit(Date tjDate, Integer offlineCount);

	void saveTjTermOperation(String deviceId, Date tjDate, Integer travelTime, Integer engineRunningTime,
			Float distance, Float maxSpeed, Float averageSpeed, Date maxSpeedTime);

	void saveTjTermAlarm(String deviceId, Date tjDate, Integer speedAlarmCount, Integer areaAlarmCount,
			Integer areaSpeedAlarmCount);

	void updateTjTermOnofflineAddOnlineCount(Date tjDate);

	void updateTjDeptAlarmAdd(Integer deptId, Date tjDate, Integer speedAlarmCount, Integer areaAlarmCount,
			Integer areaSpeedAlarmCount);
}
