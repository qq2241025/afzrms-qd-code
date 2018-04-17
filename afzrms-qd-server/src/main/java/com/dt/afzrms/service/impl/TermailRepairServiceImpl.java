
package com.dt.afzrms.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dt.afzrms.common.Const;
import com.dt.afzrms.dao.hibernate.TTerminalRepairDao;
import com.dt.afzrms.po.TRole;
import com.dt.afzrms.po.TTerminalRepair;
import com.dt.afzrms.service.TermailRepairService;
import com.dt.afzrms.vo.UserVo;
import com.mapabc.common.dao.hibernate.Page;

/**
 * @Title TODO
 * @Description TODO
 * @author 
 * @createDate 2016年9月1日 下午11:08:03
 * @modifier 
 * @modifyDate 
 * @version 1.0
 * 
 */
@Service
public class TermailRepairServiceImpl implements TermailRepairService{
	@Autowired
	TTerminalRepairDao tTerminalRepairDao;
	
	@Override
	public Page<Map<String, String>> findList(Integer pageNo,
			Integer pageSize,String verhicleName,String deptName,UserVo uservo)
			throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("select re.id,re.deviceId,re.remark descText,date_format(re.createTime,'%Y-%m-%d %T') createTime,us.`name` username, ");
		sql.append(" ter.dept_id deptId,dept.`name` deptName,ter.`name` terName,ter.simcard simcard,ter.vehicle_type_id vehicleTypeId, ");
		sql.append(" type.`name` vehicleTypeName from T_TERMINAL_REPAIR re  ");
		sql.append(" left join T_TERMINAL ter on ter.device_id = re.deviceId left join T_DEPT dept on dept.id = ter.dept_id  ");
		sql.append(" left join T_VEHICLE_TYPE type on type.id = ter.vehicle_type_id left join T_USER us on us.ID = re.userId  where 1=1 ");
		if(!StringUtils.isEmpty(verhicleName)){
			sql.append(" and ter.name like '%"+verhicleName+"%'");
		}
		if(!StringUtils.isEmpty(deptName)){
			sql.append(" and dept.`name` like '%"+deptName+"%'");
		}
		//普通用户查询
		if(uservo!=null && uservo.getUserType() == Const.COMMONUSER){
			sql.append(" and us.ID = '"+uservo.getId()+"' ");
		}
		sql.append(" order by re.createTime ");
		Page<?> qpage= tTerminalRepairDao.pagingQueryWithSqlReturnMap(pageNo, pageSize, sql.toString());
		Page<Map<String, String>> page = (Page<Map<String, String>>) qpage;
		return page;
	}

	@Override
	public void addTermailRepair(TTerminalRepair repair) throws Exception {
		String deviceid= repair.getDeviceId();
		List<TTerminalRepair> list =(List<TTerminalRepair>) tTerminalRepairDao.findList(" from TTerminalRepair where deviceId = '"+deviceid+"' ");
		if(list!=null && list.size() ==0 ){
			tTerminalRepairDao.save(repair);
		}else{
			TTerminalRepair dbrepair = list.get(0);
			dbrepair.setRemark(repair.getRemark());
			tTerminalRepairDao.update(repair);
		}
	}
	
	private String listStr(String[] deviceIds){
		StringBuffer para =new StringBuffer();
		para.append("(");
		int len = deviceIds.length;
		if(deviceIds!=null && len>0){
			for (int i = 0; i < len; i++) {
				String string = deviceIds[i];
				para.append("'"+string+"'");
				if(i < len-1){
					para.append(",");
				}
			}
		}
		return para.append(") ").toString();
	}

	@Override
	public void deleteTermailRepair(Integer[] ids) throws Exception {
		tTerminalRepairDao.deleteByIds(ids, TTerminalRepair.class);
	}

}
