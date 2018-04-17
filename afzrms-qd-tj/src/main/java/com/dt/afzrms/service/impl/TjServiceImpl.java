package com.dt.afzrms.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dt.afzrms.common.Const;
import com.dt.afzrms.dao.hibernate.TLocrecordDao;
import com.dt.afzrms.dao.hibernate.TTerminalDao;
import com.dt.afzrms.dao.hibernate.TTjDeptAlarmDao;
import com.dt.afzrms.dao.hibernate.TTjTermAlarmDao;
import com.dt.afzrms.dao.hibernate.TTjTermOnofflineDao;
import com.dt.afzrms.dao.hibernate.TTjTermOperationDao;
import com.dt.afzrms.po.TTjTermAlarm;
import com.dt.afzrms.po.TTjTermAlarmId;
import com.dt.afzrms.po.TTjTermOnoffline;
import com.dt.afzrms.po.TTjTermOperation;
import com.dt.afzrms.po.TTjTermOperationId;
import com.dt.afzrms.service.TjService;
import com.dt.afzrms.util.DateUtil;

/**
 * @Title tj service impl class
 * @Description TODO
 * @author
 * @createDate 2015年4月6日 上午10:19:45
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

@Service
public class TjServiceImpl implements TjService {
	@Autowired
	private TTerminalDao tTerminalDao;
	@Autowired
	private TLocrecordDao tLocrecordDao;
	@Autowired
	private TTjTermOperationDao tTjTermOperationDao;
	@Autowired
	private TTjTermAlarmDao tTjTermAlarmDao;
	@Autowired
	private TTjTermOnofflineDao tTjTermOnofflineDao;
	@Autowired
	private TTjDeptAlarmDao tTjDeptAlarmDao;

	@Override
	public List<Object[]> findDeviceIdList() {
		String sql = "select t.device_id,t.dept_id from T_TERMINAL t where t.is_used=1";
		@SuppressWarnings("unchecked")
		List<Object[]> findListWithSql = (List<Object[]>) tTerminalDao.findListWithSql(sql);
		return findListWithSql;
	}

	@Override
	public List<Object[]> findLocrecordList(String deviceId, Date beginDate, Date endDate) throws Exception {
		String sql = "select l.x,l.y,l.speed,l.direction,l.height,l.distance,l.gps_time,l.device_status,l.alarm_type,l.alarm_sub_type,l.area_no,l.speed_threshold from T_LOCRECORD l"
				+ " where l.device_id=?"
				+ " and l.gps_time >= str_to_date(?,'%Y-%m-%d') and l.gps_time < str_to_date(?,'%Y-%m-%d')"
				+ " and l.distance <= 100  "
				+ " order by l.gps_time";
		List<Object> values = new ArrayList<Object>();
		values.add(deviceId);
		String beginTimeStr = DateUtil.dateTimeToStr(beginDate, Const.DATETIME_PATTERN);
		String endTimeStr = DateUtil.dateTimeToStr(endDate, Const.DATETIME_PATTERN);
		if (beginTimeStr == null || endTimeStr == null) {
			throw new Exception("Datetime format error");
		}
		values.add(beginTimeStr);
		values.add(endTimeStr);

		@SuppressWarnings("unchecked")
		List<Object[]> findListWithSql = (List<Object[]>) tLocrecordDao.findListWithSql(sql, values.toArray());

		return findListWithSql;
	}

	@Override
	public void saveTjDeptAlarmInit(Date tjDate) {
		// delete first
		List<Object> values = new ArrayList<Object>();
		values.add(DateUtil.dateTimeToStr(tjDate, Const.DATE_PATTERN));
		String sql = "delete from T_TJ_DEPT_ALARM where tj_date=str_to_date(?,'%Y-%m-%d')";
		tTjDeptAlarmDao.updateWithSql(sql, values.toArray());

		sql = "insert into T_TJ_DEPT_ALARM(dept_id,tj_date,speed_alarm_count,area_alarm_count,area_speed_alarm_count) "
				+ "select id,str_to_date(?,'%Y-%m-%d'),0,0,0 from T_DEPT";
		tTjDeptAlarmDao.updateWithSql(sql, values.toArray());
	}

	@Override
	public void saveTjTermOnofflineInit(Date tjDate, Integer offlineCount) {
		// delete first
		List<Object> values = new ArrayList<Object>();
		values.add(tjDate);
		String hql = "delete from TTjTermOnoffline where tjDate=?";
		tTjDeptAlarmDao.deleteByHql(hql, values.toArray());

		TTjTermOnoffline tTjTermOnoffline = new TTjTermOnoffline(tjDate, 0, offlineCount, 0f);
		tTjTermOnofflineDao.save(tTjTermOnoffline);
	}

	@Override
	public void saveTjTermOperation(String deviceId, Date tjDate, Integer travelTime, Integer engineRunningTime,
			Float distance, Float maxSpeed, Float averageSpeed, Date maxSpeedTime) {
		// delete first
		List<Object> values = new ArrayList<Object>();
		values.add(deviceId);
		values.add(tjDate);
		String hql = "delete from TTjTermOperation where id.deviceId=? and id.tjDate=?";
		tTjTermOperationDao.deleteByHql(hql, values.toArray());

		TTjTermOperationId id = new TTjTermOperationId(deviceId, tjDate);
		TTjTermOperation tTjTermOperation = new TTjTermOperation(id, travelTime, engineRunningTime, distance, maxSpeed,
				averageSpeed, maxSpeedTime);
		tTjTermOperationDao.save(tTjTermOperation);
	}

	@Override
	public void saveTjTermAlarm(String deviceId, Date tjDate, Integer speedAlarmCount, Integer areaAlarmCount,
			Integer areaSpeedAlarmCount) {
		// delete first
		List<Object> values = new ArrayList<Object>();
		values.add(deviceId);
		values.add(tjDate);
		String hql = "delete from TTjTermAlarm where id.deviceId=? and id.tjDate=?";
		tTjTermAlarmDao.deleteByHql(hql, values.toArray());

		TTjTermAlarmId id = new TTjTermAlarmId(deviceId, tjDate);
		TTjTermAlarm tTjTermAlarm = new TTjTermAlarm(id, speedAlarmCount, areaAlarmCount, areaSpeedAlarmCount);
		tTjTermAlarmDao.save(tTjTermAlarm);
	}

	@Override
	public void updateTjTermOnofflineAddOnlineCount(Date tjDate) {
		String sql = "update T_TJ_TERM_ONOFFLINE set online_count=online_count+1,offline_count=offline_count-1,online_rate=(online_count)/(online_count+offline_count)"
				+ " where tj_date=str_to_date(?,'%Y-%m-%d')";
		List<Object> values = new ArrayList<Object>();
		values.add(DateUtil.dateTimeToStr(tjDate, Const.DATE_PATTERN));
		tTjTermOnofflineDao.updateWithSql(sql, values.toArray());
	}

	@Override
	public void updateTjDeptAlarmAdd(Integer deptId, Date tjDate, Integer speedAlarmCount, Integer areaAlarmCount,
			Integer areaSpeedAlarmCount) {
		String sql = "update T_TJ_DEPT_ALARM"
				+ " set speed_alarm_count=speed_alarm_count+?,area_alarm_count=area_alarm_count+?,area_speed_alarm_count=area_speed_alarm_count+?"
				+ " where dept_id=? and tj_date=str_to_date(?,'%Y-%m-%d')";
		List<Object> values = new ArrayList<Object>();
		values.add(speedAlarmCount);
		values.add(areaAlarmCount);
		values.add(areaSpeedAlarmCount);
		values.add(deptId);
		values.add(DateUtil.dateTimeToStr(tjDate, Const.DATE_PATTERN));
		tTjDeptAlarmDao.updateWithSql(sql, values.toArray());
	}

}
