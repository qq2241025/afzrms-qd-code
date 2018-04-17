package com.dt.afzrms.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dt.afzrms.dao.hibernate.TAlarmAreaDao;
import com.dt.afzrms.po.TAlarmArea;
import com.dt.afzrms.service.AlarmAreaService;
import com.dt.afzrms.util.JtsUtils;
import com.dt.afzrms.vo.AlarmAreaVo;
import com.dt.afzrms.vo.PageVo;
import com.mapabc.common.dao.hibernate.Page;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @Title service interface impl class
 * @Description TODO
 * @author
 * @createDate 2015年3月19日 下午3:27:23
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

@Service
public class AlarmAreaServiceImpl implements AlarmAreaService {
	@Autowired
	TAlarmAreaDao tAlarmAreaDao;

	@Override
	public PageVo<AlarmAreaVo> findList(Integer pageNo, Integer pageSize, String name) {
		String hql = "select r from TAlarmArea r where 1=1";
		List<Object> values = new ArrayList<Object>();
		if (StringUtils.isNotEmpty(name)) {
			hql += " and r.name like ?";
			values.add("%" + name + "%");
		}
		hql += " order by r.name";
		Page<TAlarmArea> pagingQuery = tAlarmAreaDao.pagingQuery(pageNo, pageSize, hql, values.toArray());
		PageVo<AlarmAreaVo> vo = new PageVo<AlarmAreaVo>();
		vo.setTotal(pagingQuery.getTotalCount());
		List<AlarmAreaVo> data = new ArrayList<AlarmAreaVo>(pagingQuery.getResult().size());
		for (TAlarmArea alarmArea : pagingQuery.getResult()) {
			AlarmAreaVo alarmAreaVo = helpPo2Vo(alarmArea);
			data.add(alarmAreaVo);
		}
		vo.setData(data);
		return vo;
	}

	private AlarmAreaVo helpPo2Vo(TAlarmArea alarmArea) {
		if (alarmArea == null) {
			return null;
		}
		return new AlarmAreaVo(alarmArea.getId(), alarmArea.getName(), alarmArea.getDescription(),
				JtsUtils.helpGeometry2Str(alarmArea.getXys()), alarmArea.getIsUsed(), alarmArea.getCreateBy(),
				alarmArea.getCreateTime(), alarmArea.getRemark());
	}

	@Override
	public Integer add(String name, String xys, String description, String remark, String createBy) {
		TAlarmArea tAlarmArea = new TAlarmArea(name, description, JtsUtils.helpStr2Geometry(xys), null, createBy, null, remark, null);
		tAlarmAreaDao.save(tAlarmArea);
		return tAlarmArea.getId();
	}

	@Override
	public Integer update(Integer id, String name, String xys, String description, String remark) {
		TAlarmArea findById = tAlarmAreaDao.findById(TAlarmArea.class, id);
		if (findById != null) {
			findById.setName(name);
			findById.setXys(JtsUtils.helpStr2Geometry(xys));
			findById.setDescription(description);
			findById.setRemark(remark);

			tAlarmAreaDao.saveOrUpdate(findById);
			return findById.getId();
		}
		return null;
	}

	@Override
	public void delete(Integer[] ids) {
		tAlarmAreaDao.deleteByIds(ids, TAlarmArea.class);
	}
	
	@Override
	public String selectBindDevide(String deviceId) {
		
		StringBuffer sql = new StringBuffer();
		sql.append("select  terule.device_id devideId, da.areaName areaname, da.ruleId, da.rulename, da.areaId, da.areaRemark,");
		sql.append(" DATE_FORMAT(da.createTime, '%Y-%m-%d %T')  createTime ");
		
		sql.append(" from REF_TERMINAL_ALARMRULE_AREA terule left join ");
		sql.append(" ( select area.`name` areaName,area.id areaId , rule.`name` rulename,rule.id ruleId ,area.remark areaRemark,area.create_time createTime  ");
		sql.append(" from REF_ALARMRULE_AREA_ALARMAREA ref LEFT JOIN T_ALARMRULE_AREA area  ");
		sql.append(" on area.id = ref.alarm_area_id LEFT JOIN T_ALARMRULE_AREA rule on ref.alarmrule_area_id = rule.id  ");
		sql.append(" ) da on da.ruleId = terule.alarmrule_area_id  where 1=1  ");
		List<Object> values = new ArrayList<Object>();
		if (StringUtils.isNotEmpty(deviceId)) {
			sql.append(" and  terule.device_id = ? " );
			values.add( deviceId );
		}
		List<?> list = tAlarmAreaDao.findMapListWithSql(sql.toString(), values.toArray());
		JSONObject json = new JSONObject();
		JSONArray listArr = new JSONArray();
		if(list!=null && list.size()>0){
			 listArr = JSONArray.fromObject(list);
		}
		json.put("successed", true);
		json.put("result", listArr);
		return json.toString();
	}
	
	
	public String selectAllAlarmList() {
		
		StringBuffer sql = new StringBuffer();
		sql.append(" select area.id areaId,area.`name` areaName,area.description remark from T_ALARM_AREA  area where 1=1");
		List<?> list = tAlarmAreaDao.findMapListWithSql(sql.toString());
		JSONObject json = new JSONObject();
		JSONArray listArr = new JSONArray();
		if(list!=null && list.size()>0){
			 listArr = JSONArray.fromObject(list);
		}
		json.put("successed", true);
		json.put("result", listArr);
		return json.toString();
	}
	
}
