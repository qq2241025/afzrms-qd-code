package com.dt.afzrms.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dt.afzrms.common.Const;
import com.dt.afzrms.dao.hibernate.TLocrecordDao;
import com.dt.afzrms.service.TjService;
import com.dt.afzrms.util.DateUtil;
import com.dt.afzrms.vo.AlarmLocrecordVo;
import com.dt.afzrms.vo.PageVo;
import com.mapabc.common.dao.hibernate.Page;

/**
 * @Title tj service interface impl class
 * @Description TODO
 * @author
 * @createDate 2015年3月17日 下午5:09:10
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

@Service
public class TjServiceImpl implements TjService {
	@Autowired
	TLocrecordDao tLocrecordDao;

	@Override
	public PageVo<AlarmLocrecordVo> findOverspeedAlarmList(Integer pageNo, Integer pageSize, String name,
			Date beginTime, Date endTime,double maxSpeed , double minSpeed) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer sql =  new StringBuffer();
		sql.append(" select l.id,l.device_id,l.x,l.y,l.speed,l.direction,l.height,l.distance,DATE_FORMAT(l.gps_time,'%Y-%m-%d %T') gps_time, ");
		sql.append(" CAST(l.device_status AS CHAR) device_status,CAST(l.alarm_type AS CHAR) alarm_type,CAST(l.alarm_sub_type AS CHAR) alarm_sub_type, ");
		sql.append(" t.name,t.simcard,d.name as deptName,l.speed_threshold,l.area_no ");
		sql.append(" from ( ");	
		sql.append(" select l.* from T_LOCRECORD l where l.gps_time >= str_to_date(?,'%Y-%m-%d %T') and l.gps_time <= str_to_date(?,'%Y-%m-%d %T')  and l.alarm_type =1 ");
		if(minSpeed!=0){
			sql.append(" and l.speed >= '"+minSpeed+"'  ");
		}
		if(maxSpeed!=0){
			sql.append(" and l.speed <= '"+maxSpeed+"'  ");
		}
		sql.append(" ) l ");
		sql.append(" left join T_TERMINAL t on l.device_id=t.device_id");
		sql.append(" left join T_DEPT d on t.dept_id=d.id ");
		sql.append(" where  t.device_id is not null ");
			
				
		String beginTimeStr = DateUtil.dateTimeToStr(beginTime, Const.DATETIME_PATTERN);
		String endTimeStr = DateUtil.dateTimeToStr(endTime, Const.DATETIME_PATTERN);
		values.add(beginTimeStr);
		values.add(endTimeStr);
		if (!StringUtils.isEmpty(name)) {
			sql.append(" and t.name like ? ");
			values.add("%" + name + "%");
		}
		sql.append(" order by l.gps_time ");

		@SuppressWarnings("unchecked")
		Page<Object[]> pagingQueryWithSql = (Page<Object[]>) tLocrecordDao.pagingQueryWithSql(pageNo, pageSize, sql.toString(),
				values.toArray());
		return help(pagingQueryWithSql);
	}

	@Override
	public PageVo<AlarmLocrecordVo> findAreaAlarmList(Integer pageNo, Integer pageSize, String name,
			String alarmSubType, Date beginTime, Date endTime) throws Exception {
		List<Object> values = new ArrayList<Object>();
		String sql = "select l.id,l.device_id,l.x,l.y,l.speed,l.direction,l.height,l.distance,l.gps_time,l.device_status,l.alarm_type,l.alarm_sub_type"
				+ ",t.name,t.simcard,d.name as deptName,l.speed_threshold,l.area_no"
				+ " from (select l.* from T_LOCRECORD l"
				+ " where l.gps_time >= str_to_date(?,'%Y-%m-%d %T') and l.gps_time <= str_to_date(?,'%Y-%m-%d %T')"
				+ " and l.alarm_type&2=2) l"
				+ " left join T_TERMINAL t on l.device_id=t.device_id"
				+ " left join T_DEPT d on t.dept_id=d.id"
				+ " where t.device_id is not null and l.alarm_type&2=2";
		String beginTimeStr = DateUtil.dateTimeToStr(beginTime, Const.DATETIME_PATTERN);
		String endTimeStr = DateUtil.dateTimeToStr(endTime, Const.DATETIME_PATTERN);
		if (beginTime == null || endTime == null) {
			throw new Exception("Datetime format error");
		}
		values.add(beginTimeStr);
		values.add(endTimeStr);

		if (!StringUtils.isEmpty(name)) {
			sql += " and t.name like ?";
			values.add("%" + name + "%");
		}
		if (!StringUtils.isEmpty(alarmSubType)) {
			sql += " and cast(l.alarm_sub_type as signed int)=?";
			values.add(alarmSubType);
		}

		sql += " order by l.gps_time";

		@SuppressWarnings("unchecked")
		Page<Object[]> pagingQueryWithSql = (Page<Object[]>) tLocrecordDao.pagingQueryWithSql(pageNo, pageSize, sql,
				values.toArray());
		return help(pagingQueryWithSql);
	}
	
	private int getInt(Object res){
		return res==null ? 0 : Integer.parseInt(res.toString());
	}
	
	private String getString(Object res){
		return res==null ? "" : res.toString();
	}
	
	private double getDouble(Object res){
		return res==null ? 0.0 :Double.parseDouble(res.toString());
	}
	
	private float getFloat(Object res){
		return res==null ? 0.0f :Float.parseFloat(res.toString());
	}

	private PageVo<AlarmLocrecordVo> help(Page<Object[]> pagingQueryWithSql) {
		PageVo<AlarmLocrecordVo> page = new PageVo<AlarmLocrecordVo>();
		List<AlarmLocrecordVo> data = new ArrayList<AlarmLocrecordVo>();
		int total = 0;
		List<Object[]> list = pagingQueryWithSql.getResult();
		if(list!=null && list.size() > 0){
			for (Object[] objs : pagingQueryWithSql.getResult()) {
				String id = this.getString(objs[0]);
				String deviceId = this.getString(objs[1]);
				Double x = this.getDouble(objs[2]);
				Double y = this.getDouble(objs[3]);
				Float speed = this.getFloat(objs[4]);
				Float direction = this.getFloat(objs[5]);
				Float height = this.getFloat(objs[6]);
				Float distance = this.getFloat(objs[7]);
				String gpsTime = this.getString(objs[8]);
				String deviceStatus = this.getString(objs[9]);
				String alarmType =  this.getString(objs[10]);
				String alarmSubType = this.getString(objs[11]);
				String termName = this.getString(objs[12]);
				String simcard = this.getString(objs[13]);
				String deptName = this.getString(objs[14]);
				Float speedThreshold = this.getFloat(objs[15]);
				Integer areaNo = this.getInt(objs[16]);
				AlarmLocrecordVo deptVo = new AlarmLocrecordVo(id, deviceId, x, y, speed,direction, height, distance, gpsTime,
						deviceStatus, alarmType, alarmSubType, termName, simcard, deptName, speedThreshold, areaNo);
				
				data.add(deptVo);
			}
			total = pagingQueryWithSql.getTotalCount();
		}
		page.setData(data);
		page.setTotal(total);
		return page;
	}

	@Override
	public PageVo<AlarmLocrecordVo> findAlarmOnceList(Integer pageNo,
			Integer pageSize, String deviceId,String deptName, Date beginTime, Date endTime,double maxSpeed , double minSpeed) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer sql =  new StringBuffer();
		sql.append(" select l.id,l.device_id,l.x,l.y,l.speed,l.direction,l.height,l.distance,DATE_FORMAT(l.gps_time,'%Y-%m-%d %T') gps_time, ");
		sql.append(" CAST(l.device_status AS CHAR) device_status,CAST(l.alarm_type AS CHAR) alarm_type,CAST(l.alarm_sub_type AS CHAR) alarm_sub_type, ");
		sql.append(" t.name,t.simcard,d.name as deptName,l.speed_threshold,l.area_no ");
		sql.append(" from ( ");	
		sql.append(" select l.* from T_LOCRECORD l where l.gps_time >= str_to_date(?,'%Y-%m-%d %T') and l.gps_time <= str_to_date(?,'%Y-%m-%d %T')  and l.alarm_type is not null ");
		if(minSpeed!=0){
			sql.append(" and l.speed >= '"+minSpeed+"'  ");
		}
		if(maxSpeed!=0){
			sql.append(" and l.speed <= '"+maxSpeed+"'  ");
		}
		sql.append(" and l.device_id = '"+deviceId+"'  ");
		sql.append(" ) l ");
		sql.append(" left join T_TERMINAL t on l.device_id=t.device_id");
		sql.append(" left join T_DEPT d on t.dept_id=d.id ");
		sql.append(" where  1=1 ");
		if(!StringUtils.isEmpty(deptName)){
			sql.append(" and d.name like  '%"+deptName+"%'" );
		}
		sql.append(" order by l.gps_time ");
		
		String beginTimeStr = DateUtil.dateTimeToStr(beginTime, Const.DATETIME_PATTERN);
		String endTimeStr = DateUtil.dateTimeToStr(endTime, Const.DATETIME_PATTERN);
		values.add(beginTimeStr);
		values.add(endTimeStr);
		@SuppressWarnings("unchecked")
		Page<Object[]> pagingQueryWithSql = (Page<Object[]>) tLocrecordDao.pagingQueryWithSql(pageNo, pageSize, sql.toString(),
				values.toArray());
		return help(pagingQueryWithSql);
	}

}
