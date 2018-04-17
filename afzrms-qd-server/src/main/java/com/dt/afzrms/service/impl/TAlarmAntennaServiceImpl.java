
package com.dt.afzrms.service.impl;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dt.afzrms.common.Const;
import com.dt.afzrms.dao.hibernate.TAlarmAntennaDao;
import com.dt.afzrms.service.TAlarmAntennaService;
import com.dt.afzrms.vo.UserVo;
import com.mapabc.common.dao.hibernate.Page;

/**
 * @Title TODO
 * @Description TODO
 * @author 
 * @createDate 2016年10月9日 下午4:52:42
 * @modifier 
 * @modifyDate 
 * @version 1.0
 * 
 */
@Service
public class TAlarmAntennaServiceImpl implements TAlarmAntennaService{
	
	@Autowired
	TAlarmAntennaDao tAlarmAntennaDao;
	
	@Override
	public String queryDriverList(Integer pageNo,
			Integer pageSize, String vehicleName, String deptName,String startTime,String endTime, UserVo user)
			throws Exception {
		JSONObject result = new JSONObject();
		StringBuffer sql = new StringBuffer();
		sql.append(" select a.id ,a.device_id deviceId,b.name deviceName,c.`name` typeName,a.alarm_type alarmType,d.`name` deptName,e.`name` brandName, ");
		sql.append(" DATE_FORMAT(a.gps_time,'%Y-%m-%d %T') gpsTime, ");
		sql.append(" DATE_FORMAT(a.start_time,'%Y-%m-%d %T') startTime, ");
		sql.append(" DATE_FORMAT(a.end_time ,'%Y-%m-%d %T') endTime ");
		sql.append(" from T_ALARM_ANTENNA a left join T_TERMINAL b on a.device_id = b.device_id ");
		sql.append(" left join T_VEHICLE_TYPE c on c.id = b.vehicle_type_id   ");
		sql.append(" left join T_DEPT d on d.id = b.dept_id   ");
		sql.append(" LEFT JOIN T_VEHICLE_BRAND e on e.id = b.vehicle_brand_id   ");
		sql.append(" where 1=1  ");
		
		//普通用户查询
		if(Const.COMMONUSER== user.getUserType()){
			sql.append(" and a.device_id in  ");
			sql.append(" (select b.device_id from REF_USER_VEHICLE_GROUP a ");
			sql.append(" left join REF_VEHICLE_GROUP b on a.vehicleGroupId = b.vehicle_group_id ");
			sql.append(" where 1=1 and a.userId = '"+user.getId()+"' ) ");
		}
		//终端名称
		if(StringUtils.isNotEmpty(vehicleName)){
			sql.append(" and b.`name` like '%"+vehicleName+"%' ");
		}
		//部门查询
		if(StringUtils.isNotEmpty(deptName)){
			sql.append(" and d.`name`  like '%"+deptName+"%' ");
		}
		
		//时间查询
		if(StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime) ){
			sql.append(" and a.start_time >= STR_TO_DATE('"+startTime+"','%Y-%m-%d %T') and a.end_time <= STR_TO_DATE('"+endTime+"','%Y-%m-%d %T')  ");
		}
		
		String sqlstr = sql.toString();
		Page<?> pagingQuery = tAlarmAntennaDao.pagingQueryWithSqlReturnMap(pageNo, pageSize, sqlstr);
		List<?> list = pagingQuery.getResult();
		if(list==null){
			list = new ArrayList<Object>();
		}
		result.put("total", pagingQuery.getTotalCount());
		result.put("result", list);
		result.put("success", true);
	    return result.toString();
	}

}
