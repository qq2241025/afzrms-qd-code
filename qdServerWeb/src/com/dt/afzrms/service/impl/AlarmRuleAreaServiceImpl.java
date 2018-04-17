package com.dt.afzrms.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dt.afzrms.dao.hibernate.RefAlarmruleAreaAlarmareaDao;
import com.dt.afzrms.dao.hibernate.TAlarmRuleAreaDao;
import com.dt.afzrms.po.RefAlarmruleAreaAlarmarea;
import com.dt.afzrms.po.TAlarmArea;
import com.dt.afzrms.po.TAlarmruleArea;
import com.dt.afzrms.service.AlarmRuleAreaService;
import com.dt.afzrms.util.StringUtil;
import com.dt.afzrms.vo.AlarmruleAreaVo;
import com.dt.afzrms.vo.PageVo;
import com.dt.afzrms.vo.RefAlarmRuleAreaAlarmAreaVo;
import com.mapabc.common.dao.hibernate.Page;

/**
 * @Title alarm rule area service impl class
 * @Description TODO
 * @author
 * @createDate 2015年3月27日 上午11:13:50
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

@Service
public class AlarmRuleAreaServiceImpl implements AlarmRuleAreaService {
	@Autowired
	private TAlarmRuleAreaDao tAlarmRuleAreaDao;
	@Autowired
	private RefAlarmruleAreaAlarmareaDao refAlarmruleAreaAlarmareaDao;

	@Override
	public PageVo<AlarmruleAreaVo> findList(Integer pageNo, Integer pageSize, String name) {
		String hql = "select r from TAlarmruleArea r left join fetch r.refAlarmruleAreaAlarmareas where 1=1";
		List<Object> values = new ArrayList<Object>();
		if (StringUtils.isNotEmpty(name)) {
			hql += " and r.name like ?";
			values.add("%" + name + "%");
		}
		hql += " order by r.name";
		Page<TAlarmruleArea> pagingQuery = tAlarmRuleAreaDao.pagingQuery(pageNo, pageSize, hql, values.toArray());
		PageVo<AlarmruleAreaVo> vo = new PageVo<AlarmruleAreaVo>();
		vo.setTotal(pagingQuery.getTotalCount());
		List<AlarmruleAreaVo> data = new ArrayList<AlarmruleAreaVo>(pagingQuery.getResult().size());
		for (TAlarmruleArea alarmRuleArea : pagingQuery.getResult()) {
			AlarmruleAreaVo alarmruleAreaVo = helpPo2Vo(alarmRuleArea);
			data.add(alarmruleAreaVo);
		}
		vo.setData(data);
		return vo;

	}

	@Override
	public Integer add(String name, String areas, String remark, String createBy) {
		TAlarmruleArea tAlarmruleArea = new TAlarmruleArea(name, null, createBy, null, remark, null, null);
		tAlarmRuleAreaDao.save(tAlarmruleArea);

		helpBatchSaveRef(tAlarmruleArea, areas);
		return tAlarmruleArea.getId();
	}

	@Override
	public Integer update(Integer id, String name, String areas, String remark) {
		TAlarmruleArea findById = tAlarmRuleAreaDao.findById(TAlarmruleArea.class, id);
		if (findById != null) {
			findById.setName(name);
			findById.setRemark(remark);
			tAlarmRuleAreaDao.saveOrUpdate(findById);

			refAlarmruleAreaAlarmareaDao.deleteByHql("delete from RefAlarmruleAreaAlarmarea where TAlarmruleArea.id=?",
					id);
			helpBatchSaveRef(findById, areas);
		}
		return null;
	}

	@Override
	public void delete(Integer[] ids) {
		tAlarmRuleAreaDao.deleteByIds(ids, TAlarmruleArea.class);
	}

	private AlarmruleAreaVo helpPo2Vo(TAlarmruleArea alarmruleArea) {
		if (alarmruleArea == null) {
			return null;
		}
		Set<RefAlarmruleAreaAlarmarea> lists = alarmruleArea.getRefAlarmruleAreaAlarmareas();
		List<RefAlarmRuleAreaAlarmAreaVo> areas = new ArrayList<RefAlarmRuleAreaAlarmAreaVo>();
		for (RefAlarmruleAreaAlarmarea aaa : lists) {
			Integer id = aaa.getId();
			Integer alarmAreaId = aaa.getTAlarmArea().getId();
			Integer alarmRuleAreaId  = aaa.getTAlarmruleArea().getId();
			
			Integer alarmType = aaa.getAlarmType();
			Integer alarmNo = aaa.getAreaNo();
			Float overspeedThreshold = aaa.getOverspeedThreshold();
			RefAlarmRuleAreaAlarmAreaVo vo = new RefAlarmRuleAreaAlarmAreaVo(id, alarmRuleAreaId , alarmAreaId , alarmType , alarmNo , overspeedThreshold );
			areas.add(vo);
		}
		return new AlarmruleAreaVo(alarmruleArea.getId(), alarmruleArea.getName(), alarmruleArea.getIsUsed(),
				alarmruleArea.getCreateBy(), alarmruleArea.getCreateTime(), alarmruleArea.getRemark(), areas);
	}

	private int helpBatchSaveRef(TAlarmruleArea tAlarmruleArea, String areas) {
		String[] split2StringArray = StringUtil.split2StringArray(areas, "@");
		List<RefAlarmruleAreaAlarmarea> entities = new ArrayList<RefAlarmruleAreaAlarmarea>(split2StringArray.length);
		for (String string : split2StringArray) {
			String[] split2StringArray2 = StringUtil.split2StringArray(string, ",");
			Integer areaId = StringUtil.str2Integer(split2StringArray2[0]);
			Integer alarmType = StringUtil.str2Integer(split2StringArray2[1]);
			Integer areaNo = StringUtil.str2Integer(split2StringArray2[2]);
			Float overspeedThreshold = StringUtil.str2Float(split2StringArray2[3]);

			RefAlarmruleAreaAlarmarea refAlarmruleAreaAlarmarea = new RefAlarmruleAreaAlarmarea(tAlarmruleArea,
					new TAlarmArea(areaId), alarmType, areaNo, overspeedThreshold);
			entities.add(refAlarmruleAreaAlarmarea);
		}
		return refAlarmruleAreaAlarmareaDao.batchSave(entities);
	}
}
