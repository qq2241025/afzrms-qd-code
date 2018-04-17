package com.dt.afzrms.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dt.afzrms.dao.hibernate.RefVehicleGroupDao;
import com.dt.afzrms.dao.hibernate.TVehicleGroupDao;
import com.dt.afzrms.po.RefTerminalAlarmruleArea;
import com.dt.afzrms.po.RefVehicleGroup;
import com.dt.afzrms.po.TDept;
import com.dt.afzrms.po.TProtocalType;
import com.dt.afzrms.po.TTerminal;
import com.dt.afzrms.po.TVehicleBrand;
import com.dt.afzrms.po.TVehicleGroup;
import com.dt.afzrms.po.TVehicleType;
import com.dt.afzrms.service.VehicleGroupService;
import com.dt.afzrms.vo.PageVo;
import com.dt.afzrms.vo.TerminalTreeNodeVo;
import com.dt.afzrms.vo.TreeNodeVo;
import com.dt.afzrms.vo.TreeVo;
import com.dt.afzrms.vo.VehicleGroupTreeNodeVo;
import com.dt.afzrms.vo.VehicleGroupVo;
import com.mapabc.common.dao.hibernate.Page;

/**
 * @Title 车辆分组业务层接口实现类
 * @Description TODO
 * @author
 * @createDate 2015-1-25 下午4:59:29
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */
@Service
public class VehicleGroupServiceImpl implements VehicleGroupService {

	@Autowired
	TVehicleGroupDao tVehicleGroupDao;
	@Autowired
	RefVehicleGroupDao refVehicleGroupDao;

	@Override
	public PageVo<VehicleGroupVo> findList(Integer pageNo, Integer pageSize, String name) {
		String hql = "select t from TVehicleGroup t where 1=1";
		List<Object> values = new ArrayList<Object>();
		if (StringUtils.isNotEmpty(name)) {
			hql += " and t.name like ?";
			values.add("%" + name + "%");
		}
		hql += " order by t.name";
		Page<TVehicleGroup> pagingQuery = tVehicleGroupDao.pagingQuery(pageNo, pageSize, hql, values.toArray());
		PageVo<VehicleGroupVo> vo = new PageVo<VehicleGroupVo>();
		vo.setTotal(pagingQuery.getTotalCount());
		List<VehicleGroupVo> data = new ArrayList<VehicleGroupVo>(pagingQuery.getResult().size());
		for (TVehicleGroup tVehicleGroup : pagingQuery.getResult()) {
			VehicleGroupVo roleVo = helpPo2Vo(tVehicleGroup);
			data.add(roleVo);
		}
		vo.setData(data);
		return vo;
	}

	@Override
	public Integer add(String name, String uses, String remark, String createBy, String[] deviceIds) {
		TVehicleGroup tVehicleGroup = new TVehicleGroup(name, uses, createBy, null, remark, null);
		tVehicleGroupDao.save(tVehicleGroup);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("deviceIds", Arrays.asList(deviceIds));
		refVehicleGroupDao.updateWithSqlSupportList("delete from REF_VEHICLE_GROUP where device_id in (:deviceIds)",
				map);

		List<RefVehicleGroup> entities = new ArrayList<RefVehicleGroup>(deviceIds.length);
		for (String deviceId : deviceIds) {
			entities.add(new RefVehicleGroup(new TVehicleGroup(tVehicleGroup.getId()), new TTerminal(deviceId)));
		}
		refVehicleGroupDao.batchSave(entities);

		return tVehicleGroup.getId();
	}

	@Override
	public Integer update(Integer id, String name, String uses, String remark, String[] deviceIds) {
		TVehicleGroup findById = tVehicleGroupDao.findById(TVehicleGroup.class, id);
		if (findById != null) {
			findById.setName(name);
			findById.setUses(uses);
			findById.setRemark(remark);
			tVehicleGroupDao.saveOrUpdate(findById);

			// refVehicleGroupDao.deleteByHql("delete from RefVehicleGroup where TVehicleGroup.id=?",
			// id);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("vehicleGroupId", id);
			map.put("deviceIds", Arrays.asList(deviceIds));
			refVehicleGroupDao
					.updateWithSqlSupportList(
							"delete from REF_VEHICLE_GROUP where vehicle_group_id=:vehicleGroupId or device_id in (:deviceIds)",
							map);

			List<RefVehicleGroup> entities = new ArrayList<RefVehicleGroup>(deviceIds.length);
			for (String deviceId : deviceIds) {
				entities.add(new RefVehicleGroup(new TVehicleGroup(id), new TTerminal(deviceId)));
			}
			refVehicleGroupDao.batchSave(entities);

			return findById.getId();
		}
		return null;
	}

	@Override
	public void delete(Integer[] ids) {
		tVehicleGroupDao.deleteByIds(ids, TVehicleGroup.class);
	}

	private VehicleGroupVo helpPo2Vo(TVehicleGroup tVehicleGroup) {
		if (tVehicleGroup == null) {
			return null;
		}
		VehicleGroupVo vehicleGroupVo = new VehicleGroupVo(tVehicleGroup.getId(), tVehicleGroup.getName(),
				tVehicleGroup.getUses(), tVehicleGroup.getCreateBy(), tVehicleGroup.getCreateTime(),
				tVehicleGroup.getRemark());
		return vehicleGroupVo;
	}

	@Override
	public TreeVo treeTerminal(Boolean returnBindAreaAlarm) {
		String hql = "from TVehicleGroup order by id";
		@SuppressWarnings("unchecked")
		List<TVehicleGroup> findList = (List<TVehicleGroup>) tVehicleGroupDao.findList(hql);
		List<TreeNodeVo> result = new ArrayList<TreeNodeVo>();
		for (TVehicleGroup tVehicleGroup : findList) {
			VehicleGroupTreeNodeVo vehicleGroupVo = new VehicleGroupTreeNodeVo(tVehicleGroup.getId().toString(), null,
					tVehicleGroup.getName(), false, new ArrayList<TreeNodeVo>());
			result.add(vehicleGroupVo);
		}
		hql = "select t,ref.TVehicleGroup,vt,pt,dt,vb";
		if (returnBindAreaAlarm != null && returnBindAreaAlarm.booleanValue() == true) {
			hql += ",reftaas";
		}
		hql += " from TTerminal t left join t.refVehicleGroups ref  left join t.TVehicleType vt"
				+ " left join t.TProtocalType pt  left join t.TDept dt" + " left join t.TVehicleBrand vb "
				+ " left join TTerminalDriver driver on driver.deviceId = t.deviceId ";

		if (returnBindAreaAlarm != null && returnBindAreaAlarm.booleanValue() == true) {
			hql += " left join t.refTerminalAlarmruleAreas reftaas";
		}
		hql += " order by ref.TVehicleGroup.id";
		@SuppressWarnings("unchecked")
		List<Object[]> findList2 = (List<Object[]>) tVehicleGroupDao.findList(hql);

		VehicleGroupTreeNodeVo tempVehicleGroupTreeNodeVo = null;
		for (Object[] objs : findList2) {
			TTerminal tTerminal = (TTerminal) objs[0];
			TVehicleGroup tVehicleGroup = (TVehicleGroup) objs[1];
			TVehicleType tVehicleType = (TVehicleType) objs[2];
			TProtocalType tProtocalType = (TProtocalType) objs[3];
			TDept tDept = (TDept) objs[4];
			TVehicleBrand tVehicleBrand = (TVehicleBrand) objs[5];
			Integer vehicleTypeId = tVehicleType == null ? null : tVehicleType.getId();
			String vehicleTypeName = tVehicleType == null ? null : tVehicleType.getName();
			String protocalType = tProtocalType == null ? null : tProtocalType.getProtocalType();
			String protocalTypeName = tProtocalType == null ? null : tProtocalType.getName();
			Integer deptId = tDept == null ? null : tDept.getId();
			String deptName = tDept == null ? null : tDept.getName();
			Integer vehicleBrandId = tVehicleBrand == null ? null : tVehicleBrand.getId();
			String vehicleBrandName = tVehicleBrand == null ? null : tVehicleBrand.getName();
            String typeColor = tVehicleType.getColor();
			Boolean hasBindAreaAlarm = null;
			if (returnBindAreaAlarm != null && returnBindAreaAlarm.booleanValue() == true) {
				RefTerminalAlarmruleArea refTerminalAlarmruleArea = (RefTerminalAlarmruleArea) objs[6];
				hasBindAreaAlarm = refTerminalAlarmruleArea == null ? false : true;
			}

			TerminalTreeNodeVo terminalTreeNodeVo = new TerminalTreeNodeVo(tTerminal.getDeviceId(),
					String.valueOf(tVehicleGroup.getId()), tTerminal.getName(), true, new ArrayList<TreeNodeVo>(),
					false, tTerminal.getSimcard(), tTerminal.getLocateType(), vehicleTypeId, vehicleTypeName,
					protocalType, protocalTypeName, deptId, deptName, vehicleBrandId, vehicleBrandName);
			terminalTreeNodeVo.setColor(typeColor);
			terminalTreeNodeVo.setHasBindAreaAlarm(hasBindAreaAlarm);
			if (tempVehicleGroupTreeNodeVo != null
					&& Integer.parseInt(tempVehicleGroupTreeNodeVo.getId()) == tVehicleGroup.getId().intValue()) {
				tempVehicleGroupTreeNodeVo.getChildren().add(terminalTreeNodeVo);
			} else {
				for (TreeNodeVo vehicleGroupTreeNodeVo : result) {
					if (Integer.parseInt(vehicleGroupTreeNodeVo.getId()) == tVehicleGroup.getId().intValue()) {
						tempVehicleGroupTreeNodeVo = (VehicleGroupTreeNodeVo) vehicleGroupTreeNodeVo;
						tempVehicleGroupTreeNodeVo.getChildren().add(terminalTreeNodeVo);
						break;
					}
				}
			}
		}

		TreeVo treeVo = new TreeVo();
		treeVo.setResult(result);
		return treeVo;
	}
	
	@Override
	public List<TreeNodeVo> OthertreeTerminal(int UserId, Boolean returnBindAreaAlarm) {
        StringBuffer sql = new StringBuffer();
        sql.append(" select vg.vehicle_group_id groupId,gr.`name` groupName,t.dept_id deptId, dp.`name` deptName, ");
        sql.append(" t.device_id devideId,t.protocal_type protocalType,t.`name` terName, ");
        sql.append(" t.simcard,t.vehicle_brand_id vehicleBrandId,tb.`name` vehicleBrandName,tp.color, ");
        sql.append(" t.vehicle_type_id  vehicleTypeId, tp.`name` vehicleTypeName, ");
        sql.append(" dirver.driverName,dirver.driverPhone ");
        
        if(returnBindAreaAlarm !=null && returnBindAreaAlarm){
        	sql.append(" ,IFNULL(aref.device_id, 0) isBindAreaAlarm ");
        }
        
        
        
        sql.append("  from REF_USER_VEHICLE_GROUP re");
        sql.append(" left join T_VEHICLE_GROUP gr on re.vehicleGroupId = gr.id ");
        sql.append(" left join REF_VEHICLE_GROUP  vg on  gr.id = vg.vehicle_group_id  ");
        sql.append(" left join T_TERMINAL t on t.device_id = vg.device_id  ");
        sql.append(" left join T_VEHICLE_TYPE tp on tp.id = t.vehicle_type_id ");
        sql.append(" left join T_VEHICLE_BRAND tb on tb.id = t.vehicle_brand_id ");
        sql.append(" left join T_DEPT dp on dp.id = t.dept_id ");
        sql.append(" left join T_TERMINAL_DRIVER dirver on dirver.deviceId = t.device_id ");
        
        if(returnBindAreaAlarm !=null && returnBindAreaAlarm){
       	  sql.append(" left join REF_TERMINAL_ALARMRULE_AREA  aref on aref.device_id = t.device_id ");
        }
        sql.append(" where 1=1 and re.userId = '"+UserId+"' ");
        
        List<Map> maplist = (List<Map>) tVehicleGroupDao.findMapListWithSql(sql.toString());
        List<TreeNodeVo> newlist =new  ArrayList<TreeNodeVo>();
        if(maplist!=null && maplist.size() >0){
        	newlist = this.helpVo(maplist);
        }
        return newlist;
	}
	
	
	
	@Override
	public List<TreeNodeVo> treeTerminalSQL(Boolean returnBindAreaAlarm) {
		StringBuffer sql = new StringBuffer();
        sql.append(" select gr.id groupId,gr.`name` groupName,t.dept_id deptId, dp.`name` deptName, ");
        sql.append(" t.device_id devideId,t.protocal_type protocalType,t.`name` terName, ");
        sql.append(" t.simcard,t.vehicle_brand_id vehicleBrandId,tb.`name` vehicleBrandName,tp.color, ");
        sql.append(" t.vehicle_type_id  vehicleTypeId, tp.`name` vehicleTypeName, ");
        sql.append(" dirver.driverName,dirver.driverPhone ");
        
        if(returnBindAreaAlarm !=null && returnBindAreaAlarm){
        	sql.append(" ,IFNULL(aref.device_id, 0) isBindAreaAlarm ");
        }
        sql.append("  from  T_VEHICLE_GROUP gr");
        sql.append(" left join REF_VEHICLE_GROUP  vg on  gr.id = vg.vehicle_group_id ");
        sql.append(" left join T_TERMINAL t on t.device_id = vg.device_id  ");
        sql.append(" left join T_VEHICLE_TYPE tp on tp.id = t.vehicle_type_id ");
        sql.append(" left join T_VEHICLE_BRAND tb on tb.id = t.vehicle_brand_id ");
        sql.append(" left join T_DEPT dp on dp.id = t.dept_id ");
        sql.append(" left join T_TERMINAL_DRIVER dirver on dirver.deviceId = t.device_id ");
        if(returnBindAreaAlarm !=null && returnBindAreaAlarm){
        	 sql.append(" left join REF_TERMINAL_ALARMRULE_AREA  aref on aref.device_id = t.device_id ");
        }
        sql.append(" order by vg.vehicle_group_id ");
        List<Map> maplist = (List<Map>) tVehicleGroupDao.findMapListWithSql(sql.toString());
        List<TreeNodeVo> newlist =new  ArrayList<TreeNodeVo>();
        if(maplist!=null && maplist.size() >0){
        	newlist = this.helpVo(maplist);
        }
        return newlist;
		
	}
	
	
	
	

	@Override
	public List<String> findDeviceIdList(Integer vehicleGroupId) {
		@SuppressWarnings("unchecked")
		List<RefVehicleGroup> findByProperty = (List<RefVehicleGroup>) refVehicleGroupDao.findByProperty(
				RefVehicleGroup.class, "TVehicleGroup.id", vehicleGroupId);
		int size = findByProperty == null ? 0 : findByProperty.size();
		List<String> temp = new ArrayList<String>(size);
		if (size > 0) {
			for (RefVehicleGroup refVehicleGroup : findByProperty) {
				temp.add(refVehicleGroup.getTTerminal().getDeviceId());
			}
		}
		return temp;
	}
 
	
	private int getInt(Object res){
		return res==null ? 0 : Integer.parseInt(res.toString());
	}
	
	private String getString(Object res){
		return res==null ? "" : res.toString();
	}
	
	private Boolean getBooean(Object res){
		return res==null ? false : true;
	}
	
	private List<TreeNodeVo> helpVo(List<Map> mapList){
		Map<Integer,TreeNodeVo> grouplist = new HashMap<Integer,TreeNodeVo>();
		List<TreeNodeVo> newlist  = new ArrayList<TreeNodeVo>();
		for (Map object : mapList) {
			String devideId = this.getString(object.get("devideId"));
			String deviceName = this.getString(object.get("terName"));
			
			int groupId = this.getInt(object.get("groupId"));
			String groupName = this.getString(object.get("groupName"));
			
			String simcard = this.getString(object.get("simcard"));
			boolean locateType = this.getBooean(object.get("locateType"));
			int vehicleBrandId = this.getInt(object.get("vehicleBrandId"));
			String vehicleBrandName = this.getString(object.get("vehicleBrandName"));
			
			String protocalTypeName = this.getString(object.get("protocalTypeName"));
			String protocalType = this.getString(object.get("protocalTypeName"));
			
			int deptId = this.getInt(object.get("deptId"));
			String deptName = this.getString(object.get("deptName"));
			
			int vehicleTypeId = this.getInt(object.get("vehicleTypeId"));
			String vehicleTypeName = this.getString(object.get("vehicleTypeName"));
			String color = this.getString(object.get("color"));
			String driverName = this.getString(object.get("driverName"));
			String driverPhone = this.getString(object.get("driverPhone"));
			
			TerminalTreeNodeVo treeNode  =  new TerminalTreeNodeVo();
			treeNode.setDriverName(driverName);
			treeNode.setDriverPhone(driverPhone);
			treeNode.setId(devideId);
			treeNode.setText(deviceName);
			treeNode.setLeaf(true);
			treeNode.setColor(color);
			treeNode.setChildren(new ArrayList());
			treeNode.setSimcard(simcard);
			treeNode.setLocateType(locateType);
			treeNode.setChecked(false);
			treeNode.setParentId(String.valueOf(groupId));
			treeNode.setVehicleBrandId(vehicleBrandId);
			treeNode.setVehicleBrandName(vehicleBrandName);
			treeNode.setProtocalType(protocalType);
			treeNode.setProtocalTypeName(protocalTypeName);
			treeNode.setDeptId(deptId);
			treeNode.setDeptName(deptName);
			treeNode.setVehicleTypeId(vehicleTypeId);
			treeNode.setVehicleTypeName(vehicleTypeName);
			
			TreeNodeVo groupList = grouplist.get(groupId);
			
			if(groupList == null){
				TreeNodeVo groupTreeVo = new TreeNodeVo();
				groupTreeVo.setId(String.valueOf(groupId));
            	groupTreeVo.setLeaf(false);
            	groupTreeVo.setText(groupName);
            	groupTreeVo.setChildren(new ArrayList<TreeNodeVo>());
            	if( StringUtils.isNotEmpty(devideId)){
            		groupTreeVo.getChildren().add(treeNode);
    			}
            	grouplist.put(groupId, groupTreeVo);
            	newlist.add(groupTreeVo);
			}else{
				if( StringUtils.isNotEmpty(devideId)){
					groupList.getChildren().add(treeNode);
				}
			}
		}
		return newlist;
	}

	public List<TVehicleGroup> findAllGroups() {
		return (List<TVehicleGroup>) tVehicleGroupDao.findAll(TVehicleGroup.class);
	}
	
	
}
