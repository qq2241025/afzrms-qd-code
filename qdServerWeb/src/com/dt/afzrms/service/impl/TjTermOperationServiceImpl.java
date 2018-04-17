package com.dt.afzrms.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dt.afzrms.common.Const;
import com.dt.afzrms.dao.hibernate.TTjTermOperationDao;
import com.dt.afzrms.service.TjTermOperationService;
import com.dt.afzrms.util.DateUtil;
import com.dt.afzrms.vo.PageVo;
import com.dt.afzrms.vo.TjTermOperationVo;
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
public class TjTermOperationServiceImpl implements TjTermOperationService {
	@Autowired
	TTjTermOperationDao tTjTermOperationDao;

	@Override
	public PageVo<TjTermOperationVo> findList(Integer pageNo, Integer pageSize, Date beginTime, Date endTime,
			String name) throws Exception {
		String sql = "select tj.device_id,tj.tj_date,tj.travel_time,tj.engine_running_time,tj.distance,tj.max_speed,tj.max_speed_time,tj.average_speed,t.name"
				+ " from T_TJ_TERM_OPERATION tj"
				+ " left join T_TERMINAL t on tj.device_id=t.device_id"
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
		sql += " order by tj.tj_date";

		@SuppressWarnings("unchecked")
		Page<Object[]> pagingQueryWithSql = (Page<Object[]>) tTjTermOperationDao.pagingQueryWithSql(pageNo, pageSize,
				sql, values.toArray());
		List<TjTermOperationVo> data = new ArrayList<TjTermOperationVo>(pagingQueryWithSql.getResult().size());
		for (Object[] objs : pagingQueryWithSql.getResult()) {
			String _deviceId = (String) objs[0];
			Date _tjDate = (Date) objs[1];
			Integer _travelTime = (Integer) objs[2];
			Integer _engineRunningTime = (Integer) objs[3];
			Float _distance = (Float) objs[4];
			Float _maxSpeed = (Float) objs[5];
			Date _maxSpeedTime = (Date) objs[6];
			Float _averageSpeed = (Float) objs[7];
			String _name = (String) objs[8];
			TjTermOperationVo vo = new TjTermOperationVo(_deviceId, _tjDate, _travelTime, _engineRunningTime,
					_distance, _maxSpeed, _maxSpeedTime, _averageSpeed, _name);
			data.add(vo);
		}
		return new PageVo<TjTermOperationVo>(pagingQueryWithSql.getTotalCount(), data);
	}

}
