package com.dt.afzrms.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dt.afzrms.common.Const;
import com.dt.afzrms.dao.hibernate.TTjDeptAlarmDao;
import com.dt.afzrms.service.TjDeptAlarmService;
import com.dt.afzrms.util.DateUtil;
import com.dt.afzrms.vo.PageVo;
import com.dt.afzrms.vo.TjDeptAlarmVo;
import com.mapabc.common.dao.hibernate.Page;

/**
 * @Title tj dept alarm service implement class
 * @Description TODO
 * @author
 * @createDate 2015年3月31日 下午3:47:06
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

@Service
public class TjDeptAlarmServiceImpl implements TjDeptAlarmService {
	@Autowired
	TTjDeptAlarmDao tTjDeptAlarmDao;

	@Override
	public PageVo<TjDeptAlarmVo> findList(Integer pageNo, Integer pageSize, Date beginTime, Date endTime, String name)
			throws Exception {
		String sql = "select tj.dept_id,tj.tj_date,tj.speed_alarm_count,tj.area_alarm_count,tj.area_speed_alarm_count,d.name"
				+ " from T_TJ_DEPT_ALARM tj"
				+ " left join T_TERMINAL t on tj.dept_id=t.dept_id"
				+ " left join T_DEPT d on t.dept_id=d.id"
				+ " where t.dept_id is not null"
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
			sql += " and d.name like ?";
			values.add("%" + name + "%");
		}
		sql += " order by tj.tj_date";

		@SuppressWarnings("unchecked")
		Page<Object[]> pagingQueryWithSql = (Page<Object[]>) tTjDeptAlarmDao.pagingQueryWithSql(pageNo, pageSize, sql,
				values.toArray());
		List<TjDeptAlarmVo> data = new ArrayList<TjDeptAlarmVo>(pagingQueryWithSql.getResult().size());
		for (Object[] objs : pagingQueryWithSql.getResult()) {
			Integer _deviceId = (Integer) objs[0];
			Date _tjDate = (Date) objs[1];
			Integer _speedAlarmCount = (Integer) objs[2];
			Integer _areaAlarmCount = (Integer) objs[3];
			Integer _areaSpeedAlarmCount = (Integer) objs[4];
			String _name = (String) objs[5];
			TjDeptAlarmVo vo = new TjDeptAlarmVo(_deviceId, _tjDate, _speedAlarmCount, _areaAlarmCount,
					_areaSpeedAlarmCount, _name);
			data.add(vo);
		}
		return new PageVo<TjDeptAlarmVo>(pagingQueryWithSql.getTotalCount(), data);
	}

}
