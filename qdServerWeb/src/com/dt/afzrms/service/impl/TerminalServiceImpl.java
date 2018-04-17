package com.dt.afzrms.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dt.afzrms.dao.hibernate.TTerminalAlarmsetDao;
import com.dt.afzrms.dao.hibernate.TTerminalDao;
import com.dt.afzrms.po.TDept;
import com.dt.afzrms.po.TProtocalType;
import com.dt.afzrms.po.TTerminal;
import com.dt.afzrms.po.TTerminalAlarmset;
import com.dt.afzrms.po.TVehicleBrand;
import com.dt.afzrms.po.TVehicleType;
import com.dt.afzrms.service.TerminalService;
import com.dt.afzrms.vo.PageVo;
import com.dt.afzrms.vo.TTerminalAlarmsetVo;
import com.dt.afzrms.vo.TerminalVo;
import com.mapabc.common.dao.hibernate.Page;

import net.sf.json.JSONObject;

/**
 * @Title 终端业务层接口实现类
 * @Description TODO
 * @author
 * @createDate 2015-1-25 下午3:16:20
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */
@Service
public class TerminalServiceImpl implements TerminalService {

	@Autowired
	TTerminalDao tTerminalDao;
	@Autowired
	private TTerminalAlarmsetDao tTerminalAlarmsetDao;

	@Override
	public PageVo<TerminalVo> findList(Integer pageNo, Integer pageSize,
			Integer deptId, String name) {
		String hql = "select t from TTerminal t " + "where 1=1";
		List<Object> values = new ArrayList<Object>();
		if (deptId != null) {
			hql += " and t.TDept.id=?";
			values.add(deptId);
		}
		if (StringUtils.isNotEmpty(name)) {
			hql += " and t.name like ?";
			values.add("%" + name + "%");
		}
		hql += " order by t.name";
		Page<TTerminal> pagingQuery = tTerminalDao.pagingQuery(pageNo,
				pageSize, hql, values.toArray());
		PageVo<TerminalVo> vo = new PageVo<TerminalVo>();
		vo.setTotal(pagingQuery.getTotalCount());
		List<TerminalVo> data = new ArrayList<TerminalVo>(pagingQuery
				.getResult().size());
		for (TTerminal terminal : pagingQuery.getResult()) {
			TerminalVo terminalVo = helpPo2Vo(terminal);
			data.add(terminalVo);
		}
		vo.setData(data);
		return vo;
	}

	@Override
	public TerminalVo findByDeviceId(String deviceId) {
		TTerminal findById = tTerminalDao.findById(TTerminal.class, deviceId);
		return helpPo2Vo(findById);
	}

	@Override
	public String add(String deviceId, String name, Integer deptId,
			Integer vehicleTypeId, String protocalType, String simcard,
			Boolean locateType, String remark, String createBy, Integer vehicleBrandId) {
		TTerminal tTerminal = new TTerminal(deviceId, new TVehicleType(
				vehicleTypeId), new TDept(deptId), new TProtocalType(
				protocalType), name, simcard, locateType, true, createBy, null,
				remark, null, null, null, null, new TVehicleBrand(vehicleBrandId));
		tTerminalDao.save(tTerminal);
		return tTerminal.getDeviceId();
	}

	@Override
	public String update(String deviceId, String name, Integer deptId,
			Integer vehicleTypeId, String protocalType, String simcard,
			Boolean locateType, String remark, Integer vehicleBrandId) {
		TTerminal findById = tTerminalDao.findById(TTerminal.class, deviceId);
		if (findById != null) {
			findById.setName(name);
			findById.setTDept(new TDept(deptId));
			findById.setTVehicleType(new TVehicleType(vehicleTypeId));
			findById.setTVehicleBrand(new TVehicleBrand(vehicleBrandId));
			findById.setTProtocalType(new TProtocalType(protocalType));
			findById.setSimcard(simcard);
			findById.setLocateType(locateType);
			findById.setRemark(remark);
			tTerminalDao.saveOrUpdate(findById);
			return findById.getDeviceId();
		}
		return null;
	}

	@Override
	public void delete(String[] ids) {
		tTerminalDao.deleteByIds(ids, TTerminal.class);
	}

	private TerminalVo helpPo2Vo(TTerminal tTerminal) {
		if (tTerminal == null) {
			return null;
		}
		TDept tDept = tTerminal.getTDept();
		Integer deptId = tDept == null ? null : tDept.getId();
		String deptName = tDept == null ? null : tDept.getName();
		TVehicleType tVehicleType = tTerminal.getTVehicleType();
		TVehicleBrand tVehicleBrand = tTerminal.getTVehicleBrand();
		Integer vehicleTypeId = tVehicleType == null ? null : tVehicleType
				.getId();
		String vehicleTypeName = tVehicleType == null ? null : tVehicleType
				.getName();
		Integer vehicleBrandId = tVehicleBrand == null ? null : tVehicleBrand
				.getId();
		String vehicleBrandName = tVehicleBrand == null ? null : tVehicleBrand
				.getName();
		TProtocalType tProtocalType = tTerminal.getTProtocalType();
		String protocalType = tProtocalType == null ? null : tProtocalType
				.getProtocalType();
		String protocalTypeName = tProtocalType == null ? null : tProtocalType
				.getName();
		TerminalVo terminalVo = new TerminalVo(tTerminal.getDeviceId(), deptId,
				deptName, vehicleTypeId, vehicleTypeName, vehicleBrandId, vehicleBrandName, protocalType,
				protocalTypeName, tTerminal.getName(), tTerminal.getSimcard(),
				tTerminal.getLocateType(), tTerminal.getCreateBy(),
				tTerminal.getCreateTime(), tTerminal.getRemark());
		return terminalVo;
	}

	@Override
	public TTerminalAlarmsetVo findAlarmSetByDeviceId(String deviceId) {
		TTerminalAlarmset findById = tTerminalAlarmsetDao.findById(TTerminalAlarmset.class, deviceId);
		if(findById != null){
			TTerminalAlarmsetVo vo = new TTerminalAlarmsetVo(deviceId, findById.getOverspeedThreshold());
			return vo;
		}
		return null;
	}

	@Override
	/**
	 * 查询车辆的基本信息【其中包括是否设置超速】
	 */
	public String getTerminalDetailInfo(String deviceId) {
		JSONObject result = new JSONObject();
		StringBuffer sql = new StringBuffer();
		sql.append("select t.device_id,t.name tname,t.simcard,t.remark,dept.name dname,b.name bname, ");
		sql.append(" type.name cname,alaset.overspeed_threshold speedset,rule.alarmrule_area_id ruleAreaId,area.name aname  ");
		sql.append(" from T_TERMINAL t  left join T_TERMINAL_ALARMSET alaset on alaset.device_id = t.device_id ");
		sql.append(" left join T_DEPT dept on dept.id = t.dept_id ");
		sql.append(" left join T_VEHICLE_TYPE type on type.id = t.vehicle_type_id ");
		sql.append(" left join T_VEHICLE_BRAND b on b.id = t.vehicle_brand_id ");
		sql.append(" left join REF_TERMINAL_ALARMRULE_AREA rule on rule.device_id = t.device_id  ");
		sql.append(" left join T_ALARMRULE_AREA area on area.id = rule.alarmrule_area_id  ");
		sql.append(" where 1 = 1 and t.device_id = '"+deviceId+"' ");
		
		@SuppressWarnings("unchecked")
		List<Object[]> delist = (List<Object[]>) tTerminalDao.findListWithSql(sql.toString());
		Object[] verhicleInfo = delist.get(0);
		result.put("deviceId", verhicleInfo[0]);
		result.put("deviceName", verhicleInfo[1]);
		result.put("simcard", verhicleInfo[2]);
		result.put("remark", verhicleInfo[3]);
		result.put("deptName", verhicleInfo[4]);
		result.put("pingpaiName", verhicleInfo[5]);
		result.put("typeName", verhicleInfo[6]);
		result.put("speedset", verhicleInfo[7]!=""?verhicleInfo[7]:""); //设置速度
		result.put("ruleAreaId", verhicleInfo[8]!=""?verhicleInfo[8]:""); //是否绑定规则
		result.put("ruleName", verhicleInfo[9]!=""?verhicleInfo[9]:""); //是否绑定规则
		JSONObject res = new JSONObject();
		res.put("successed", true);
		res.put("result", result);
		return res.toString();
	}
}
