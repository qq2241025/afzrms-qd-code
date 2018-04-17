package com.dt.afzrms.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dt.afzrms.common.Const;
import com.dt.afzrms.dao.hibernate.TTjTermAlarmDao;
import com.dt.afzrms.service.TjTermAlarmService;
import com.dt.afzrms.util.DateUtil;
import com.dt.afzrms.vo.PageVo;
import com.dt.afzrms.vo.TjTermAlarmVo;
import com.mapabc.common.dao.hibernate.Page;

/**
 * @Title tj term alarm service implement class
 * @Description TODO
 * @author
 * @createDate 2015年3月31日 下午3:47:06
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

@Service
public class TjTermAlarmServiceImpl implements TjTermAlarmService {
	@Autowired
	TTjTermAlarmDao tTjTermAlarmDao;

	@Override
	public PageVo<TjTermAlarmVo> findList(Integer pageNo, Integer pageSize, Date beginTime, Date endTime, String name,String deptName)
			throws Exception {
		String sql = "select tj.device_id,tj.tj_date,tj.speed_alarm_count,tj.area_alarm_count,tj.area_speed_alarm_count,t.name,dept.name deptName,"
				+ " type.`name` vetypeName,brand.name brandName"
				+ " from T_TJ_TERM_ALARM tj"
				+ " left join T_TERMINAL t on tj.device_id=t.device_id"
				+ " left join T_VEHICLE_TYPE type on type.id = t.vehicle_type_id"
				+ " left join T_VEHICLE_BRAND brand on brand.id = t.vehicle_brand_id "
				+ " left join T_DEPT dept on t.dept_id = dept.id"
				+ " where t.device_id is not null"
				+ " and tj.tj_date >= str_to_date(?,'%Y-%m-%d %T') and tj.tj_date <= str_to_date(?,'%Y-%m-%d %T')";
		List<Object> values = new ArrayList<Object>();
		String beginTimeStr = DateUtil.dateTimeToStr(beginTime, Const.DATETIME_PATTERN);
		String endTimeStr = DateUtil.dateTimeToStr(endTime, Const.DATETIME_PATTERN);
		if (beginTime == null || endTime == null) {
			throw new Exception("Datetime format error");
		}
		values.add(beginTimeStr);
		values.add(endTimeStr);
		if (StringUtils.isNotEmpty(name)) {
			sql += " and t.name like ?";
			values.add("%" + name + "%");
		}
		
		if (StringUtils.isNotEmpty(deptName)) {
			sql += " and dept.name like ?";
			values.add("%" + deptName + "%");
		}
		sql += " order by tj.tj_date";

		@SuppressWarnings("unchecked")
		Page<Object[]> pagingQueryWithSql = (Page<Object[]>) tTjTermAlarmDao.pagingQueryWithSql(pageNo, pageSize, sql,
				values.toArray());
		List<TjTermAlarmVo> data = new ArrayList<TjTermAlarmVo>(pagingQueryWithSql.getResult().size());
		for (Object[] objs : pagingQueryWithSql.getResult()) {
			String _deviceId = (String) objs[0];
			Date _tjDate = (Date) objs[1];
			Integer _speedAlarmCount = (Integer) objs[2];
			Integer _areaAlarmCount = (Integer) objs[3];
			Integer _areaSpeedAlarmCount = (Integer) objs[4];
			String _name = (String) objs[5];
			String deName = (String) objs[6];
			
			String vetypeName = (String) objs[7];
			String vehicleBrand = (String) objs[8];
			
			TjTermAlarmVo vo = new TjTermAlarmVo(_deviceId, _tjDate, _speedAlarmCount, _areaAlarmCount,
					_areaSpeedAlarmCount, _name,deName,vetypeName,vehicleBrand);
			data.add(vo);
		}
		return new PageVo<TjTermAlarmVo>(pagingQueryWithSql.getTotalCount(), data);
	}

}
