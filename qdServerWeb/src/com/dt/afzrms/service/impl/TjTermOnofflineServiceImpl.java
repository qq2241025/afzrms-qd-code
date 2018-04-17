package com.dt.afzrms.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dt.afzrms.common.Const;
import com.dt.afzrms.dao.hibernate.TTjTermOnofflineDao;
import com.dt.afzrms.service.TjTermOnofflineService;
import com.dt.afzrms.util.DateUtil;
import com.dt.afzrms.vo.PageVo;
import com.dt.afzrms.vo.TjTermOnofflineVo;
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
public class TjTermOnofflineServiceImpl implements TjTermOnofflineService {
	@Autowired
	TTjTermOnofflineDao tTjTermOnofflineDao;

	@Override
	public PageVo<TjTermOnofflineVo> findList(Integer pageNo, Integer pageSize, Date beginTime, Date endTime)
			throws Exception {
		String sql = "select tj.tj_date,tj.online_count,tj.offline_count,tj.online_rate"
				+ " from T_TJ_TERM_ONOFFLINE tj" + " where 1=1"
				+ " and tj.tj_date >= str_to_date(?,'%Y-%m-%d %T') and tj.tj_date <= str_to_date(?,'%Y-%m-%d %T')";
		List<Object> values = new ArrayList<Object>();
		String beginTimeStr = DateUtil.dateTimeToStr(beginTime, Const.DATETIME_PATTERN);
		String endTimeStr = DateUtil.dateTimeToStr(endTime, Const.DATETIME_PATTERN);
		if (beginTime == null || endTime == null) {
			throw new Exception("Datetime format error");
		}
		values.add(beginTimeStr);
		values.add(endTimeStr);
		sql += " order by tj.tj_date";

		@SuppressWarnings("unchecked")
		Page<Object[]> pagingQueryWithSql = (Page<Object[]>) tTjTermOnofflineDao.pagingQueryWithSql(pageNo, pageSize,
				sql, values.toArray());
		List<TjTermOnofflineVo> data = new ArrayList<TjTermOnofflineVo>(pagingQueryWithSql.getResult().size());
		for (Object[] objs : pagingQueryWithSql.getResult()) {
			Date _tjDate = (Date) objs[0];
			Integer _onlineCount = (Integer) objs[1];
			Integer _offlineCount = (Integer) objs[2];
			Float _onlineRate = (Float) objs[3];
			TjTermOnofflineVo vo = new TjTermOnofflineVo(_tjDate, _onlineCount, _offlineCount, _onlineRate);
			data.add(vo);
		}
		return new PageVo<TjTermOnofflineVo>(pagingQueryWithSql.getTotalCount(), data);
	}

}
