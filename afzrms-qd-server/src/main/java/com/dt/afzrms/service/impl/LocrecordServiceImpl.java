package com.dt.afzrms.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dt.afzrms.common.Const;
import com.dt.afzrms.dao.hibernate.TLocrecordDao;
import com.dt.afzrms.service.LocrecordService;
import com.dt.afzrms.util.DateUtil;
import com.dt.afzrms.vo.LocrecordVo;
import com.dt.afzrms.vo.PageVo;
import com.mapabc.common.dao.hibernate.Page;

/**
 * @Title TODO
 * @Description TODO
 * @author
 * @createDate 2015年2月10日 上午10:29:59
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

@Service
public class LocrecordServiceImpl implements LocrecordService {

	@Autowired
	TLocrecordDao tLocrecordDao;

	public PageVo<LocrecordVo> findList(Integer pageNo, Integer pageSize, String deviceId, Date beginTime, Date endTime)
	 {
		String sql = "select id,device_id,x,y,speed,direction,height,distance,DATE_FORMAT(gps_time,'%Y-%m-%d %T') gps_time "
				+ ",IFNULL(CAST(device_status AS CHAR),0) device_status ,IFNULL(CAST(alarm_type AS CHAR),0)  alarm_type "
				+ " from T_LOCRECORD l  where l.device_id=?"
				+ " and l.gps_time >= str_to_date(?,'%Y-%m-%d %T') and l.gps_time <= str_to_date(?,'%Y-%m-%d %T')"
				+ " and l.distance <= 0.3 "
				+ " and l.speed <= 90"
				+ " order by l.gps_time";
		List<Object> values = new ArrayList<Object>();
		values.add(deviceId);
		String beginTimeStr = DateUtil.dateTimeToStr(beginTime, Const.DATETIME_PATTERN);
		String endTimeStr = DateUtil.dateTimeToStr(endTime, Const.DATETIME_PATTERN);
		values.add(beginTimeStr);
		values.add(endTimeStr);

		Page mapPage =  tLocrecordDao.pagingQueryWithSqlReturnMap(pageNo, pageSize, sql, values.toArray());
		
		List<LocrecordVo> data = new ArrayList<LocrecordVo>();
		List<Map> maplist = mapPage.getResult();
		for (Map objs : maplist) {
			String _id = this.getString(objs.get("id"));
			String _deviceId = this.getString(objs.get("device_id"));
			double _x = this.getDouble(objs.get("x"));
			double _y = this.getDouble(objs.get("y"));
			Float _speed =this.getFloat(objs.get("speed"));
			Float _direction = this.getFloat(objs.get("direction"));
			Float _height = this.getFloat(objs.get("height"));
			Float _distance = this.getFloat(objs.get("distance"));
			String _gpsTime = this.getString(objs.get("gps_time"));
			String _deviceStatus = this.getString(objs.get("device_status"));
			String alarmType = this.getString(objs.get("alarm_type"));
			//判断点在青岛范围内
			if(this.isQDBounds(_x, _y)){
				LocrecordVo deptVo = new LocrecordVo();
				deptVo.setId(_id);
				deptVo.setDeviceId(_deviceId);
				deptVo.setX(_x);
				deptVo.setY(_y);
				deptVo.setSpeed(_speed);
				deptVo.setHeight(_height);
				deptVo.setDistance(_distance);
				deptVo.setDirection(_direction);
				deptVo.setGpsTime(_gpsTime);
				deptVo.setDeviceStatus(_deviceStatus);
				deptVo.setAlarmTypes(alarmType);
				data.add(deptVo);
			}else{
				Log.info(String.format("【120，121,36,37判断点在青岛范围内【%s,%s】", _x, _y));
			}
		}
		return new PageVo<LocrecordVo>(mapPage.getTotalCount(), data);
	}
	
	private int getInt(Object res){
		return res==null ? 0 : Integer.parseInt(res.toString());
	}
	
	private String getString(Object res){
		return res==null ? "" : res.toString();
	}
	
	private double getDouble(Object res){
		return res==null ? 0.0 :Double.parseDouble(res.toString());
	}
	
	private float getFloat(Object res){
		return res==null ? 0.0f :Float.parseFloat(res.toString());
	}
	
	private boolean isQDBounds(double lng,double lat){
		boolean res = false;
		double minX = 120, maxX = 121,minY =36,maxY =37;
		if(lng >= minX && lng <=maxX && lat>=minY && lat<=maxY){
		      res =  true;
		}
		return res;
	}

	@Override
	public String findLocrecordList(Integer pageNo, Integer pageSize, String[] deviceIds, Date beginDate, Date endDate)  {
		String beginTimeStr = DateUtil.dateTimeToStr(beginDate, Const.DATETIME_PATTERN);
		String endTimeStr = DateUtil.dateTimeToStr(endDate, Const.DATETIME_PATTERN);
		StringBuffer sql = new StringBuffer();
		String idList = listStr(deviceIds);
		//每条数据记录的deviceId,在这段时间段内最大速度 的GPS时间段
		//ps :利用程序一条条遍历程序也是这样的结果，因此认为sql木有问题啦
		String maxSpeedTimesql = "(select a.gpstime  FROM T_TJ_TREM_HOURS a  where 1=1 "
				+ " and a.deviceId in(a1) "
				+ " and a.tjTime >= str_to_date('"+beginTimeStr+"','%Y-%m-%d %T')  "
				+ " and a.tjTime <= str_to_date('"+endTimeStr+"','%Y-%m-%d %T') "
				+ " order by a.maxspeed desc limit 1) a7";
		
		sql.append(" select FORMAT(sum(distance),3) a0, deviceId a1,  FORMAT(max(maxspeed),3) a2, FORMAT(min(minspeed),3) a3 ,");
		sql.append(" sum(alarmNum) a4, sum(areaAlarmNum) a5,ter.name a6, ");
		
		sql.append(maxSpeedTimesql); //最大速度的时刻点
		
		sql.append(" FROM T_TJ_TREM_HOURS gps LEFT JOIN T_TERMINAL ter on ter.device_id = gps.deviceId  ");
		sql.append(" where 1= 1 and ");
		sql.append(" gps.deviceId in " + idList); 
		sql.append(" and gps.tjTime >= str_to_date('"+beginTimeStr+"','%Y-%m-%d %T') ");
		sql.append(" and gps.tjTime <= str_to_date('"+endTimeStr+"','%Y-%m-%d %T') ");
		sql.append(" group by gps.deviceId ");
		sql.append(" order by ter.device_id");
		@SuppressWarnings("unchecked")
		Page<Object[]> pagingQuery = (Page<Object[]>) tLocrecordDao.pagingQueryWithSql(pageNo, pageSize, sql.toString());
		return this.querytjByhours(pagingQuery,beginTimeStr,endTimeStr).toString();
	}
	
	@Override
	public Page<Object[]> findLocrecordListByPage(Integer pageNo, Integer pageSize, String[] deviceIds, Date beginDate, Date endDate)  {
		String beginTimeStr = DateUtil.dateTimeToStr(beginDate, Const.DATETIME_PATTERN);
		String endTimeStr = DateUtil.dateTimeToStr(endDate, Const.DATETIME_PATTERN);
		StringBuffer sql = new StringBuffer();
		String idList = listStr(deviceIds);
		//每条数据记录的deviceId,在这段时间段内最大速度 的GPS时间段
		//ps :利用程序一条条遍历程序也是这样的结果，因此认为sql木有问题啦
		String maxSpeedTimesql = "(select a.gpstime  FROM T_TJ_TREM_HOURS a  where 1=1 "
				+ " and a.deviceId in(a1) "
				+ " and a.tjTime >= str_to_date('"+beginTimeStr+"','%Y-%m-%d %T')  "
				+ " and a.tjTime <= str_to_date('"+endTimeStr+"','%Y-%m-%d %T') "
				+ " order by a.maxspeed desc limit 1) a7";
		
		sql.append(" select FORMAT(sum(distance),3) a0, deviceId a1,  FORMAT(max(maxspeed),3) a2, FORMAT(min(minspeed),3) a3 ,");
		sql.append(" sum(alarmNum) a4, sum(areaAlarmNum) a5,ter.name a6, ");
		
		sql.append(maxSpeedTimesql); //最大速度的时刻点
		
		sql.append(" FROM T_TJ_TREM_HOURS gps LEFT JOIN T_TERMINAL ter on ter.device_id = gps.deviceId  ");
		sql.append(" where 1= 1 and ");
		sql.append(" gps.deviceId in " + idList); 
		sql.append(" and gps.tjTime >= str_to_date('"+beginTimeStr+"','%Y-%m-%d %T') ");
		sql.append(" and gps.tjTime <= str_to_date('"+endTimeStr+"','%Y-%m-%d %T') ");
		sql.append(" group by gps.deviceId ");
		sql.append(" order by ter.device_id");
		@SuppressWarnings("unchecked")
		Page<Object[]> pagingQuery = (Page<Object[]>) tLocrecordDao.pagingQueryWithSql(pageNo, pageSize, sql.toString());
		return pagingQuery;
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
	
	

	private String querytjByhours(Page<Object[]> page,String beginTimeStr,String endTimeStr){
		List<Object[]> list = page.getResult();
		int total =page.getTotalCount();
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		if(list!=null && list.size() > 0){
			for (int i = 0; i < list.size(); i++) {
				Object[] obj = list.get(i);
				JSONObject items = new JSONObject();
				items.put("tjtime", beginTimeStr +"---"+ endTimeStr );
				items.put("distance", obj[0]!=null?obj[0]:"0");
				items.put("deviceId", obj[1]!=null?obj[1]:"0");
				items.put("maxspeed", obj[2]!=null?obj[2]:"");
				items.put("minspeed", obj[3]!=null?obj[3]:"");
				items.put("speedAlarm", obj[4]!=null?obj[4]:"");
				items.put("areaAlarm", obj[5]!=null?obj[5]:"");
				items.put("deviceName", obj[6]!=null?obj[6]:"");
				items.put("gpsTime", obj[7]!=null?obj[7]:"");
				array.add(items);
			}
		}
		result.put("total", total);
		result.put("result", array);
		result.put("success", true);
	    return result.toString();
	}
    //批量查询终端的最后的轨迹数据 
	@Override
	public String getLostTrackData(String[] deviceIds) {
		JSONObject result = new JSONObject();
		StringBuffer sql = new StringBuffer();
		sql.append("select last.alarm_sub_type alarmSubType,last.alarm_type alarmType,last.device_id deviceId, ");
		sql.append(" last.direction, last.distance, date_format(last.gps_time,'%Y-%m-%d %T') gpstime, last.height, last.id, last.speed, last.x, last.y, ");
		sql.append(" last.speed_threshold speedThreshold, dept.id deptId, dept.`name` deptName, ");
		sql.append(" ter.simcard,ter.name, ter.protocal_type protocalType, ter.remark,type.`name` vehicleTypeName ");
		sql.append(" from T_LAST_LOCRECORD last  left join T_TERMINAL ter on ter.device_id = last.device_id ");
		sql.append(" left join T_VEHICLE_TYPE type on type.id = ter.vehicle_type_id  ");
		sql.append(" left join T_DEPT dept on dept.id = ter.dept_id  ");
		sql.append(" where 1=1 and last.device_id in ");
		String conaction = this.listStr(deviceIds);
		sql.append(conaction);
		String sqlstr = sql.toString();
		List<?> pagingQuery = tLocrecordDao.findMapListWithSql(sqlstr);
		
		if(pagingQuery==null){
			 pagingQuery = new ArrayList<Object>();
		}
		result.put("total", pagingQuery.size());
		result.put("result", pagingQuery);
		result.put("success", true);
	    return result.toString();
	}
    /**
     *     查询所有的最后轨迹和分页
     */
	@Override
	public String getPageLostTrackData(Integer pageNo, Integer pageSize) {
		JSONObject result = new JSONObject();
		StringBuffer sql = new StringBuffer();
		sql.append("select last.alarm_sub_type alarmSubType,last.alarm_type alarmType,last.device_id deviceId, ");
		sql.append(" last.direction, last.distance, last.gps_time, last.height, last.id, last.speed, last.x, last.y, ");
		sql.append(" last.speed_threshold speedThreshold, dept.id deptId, dept.`name` deptName, ");
		sql.append(" ter.simcard, ter.protocal_type protocalType, ter.remark");
		sql.append(" T_LAST_LOCRECORD from T_LAST_LOCRECORD last  left join T_TERMINAL ter on ter.device_id = last.device_id ");
		sql.append(" left join T_DEPT dept on dept.id = ter.dept_id  ");
		sql.append(" where 1=1  ");
		String sqlstr = sql.toString();
		Page<?> pagingQuery = tLocrecordDao.pagingQueryWithSqlReturnMap(pageNo, pageSize, sqlstr);
		List<?> list = pagingQuery.getResult();
		if(list==null){
			list = new ArrayList<Object>();
		}
		result.put("total", pagingQuery.getTotalCount());
		result.put("result", list);
		result.put("success", true);
	    return result.toString();
	}
	//批量查询终端的最后的轨迹数据 
	@Override
	public String getAllLastTrackData() {
		JSONObject result = new JSONObject();
		StringBuffer sql = new StringBuffer();
		sql.append("select last.alarm_sub_type alarmSubType,last.alarm_type alarmType,last.device_id deviceId, ");
		sql.append(" last.direction, last.distance, date_format(last.gps_time,'%Y-%m-%d %T') gpstime, last.height, last.id, last.speed, last.x, last.y, ");
		sql.append(" last.speed_threshold speedThreshold, dept.id deptId, dept.`name` deptName, ");
		sql.append(" ter.simcard,ter.name, ter.protocal_type protocalType, ter.remark,type.`name` vehicleTypeName  ");
		sql.append(" from T_LAST_LOCRECORD last  left join T_TERMINAL ter on ter.device_id = last.device_id ");
		sql.append(" left join T_VEHICLE_TYPE type on type.id = ter.vehicle_type_id  ");
		sql.append(" left join T_DEPT dept on dept.id = ter.dept_id  ");
		sql.append(" where 1=1  ");
		String sqlstr = sql.toString();
		List<?> pagingQuery = tLocrecordDao.findMapListWithSql(sqlstr);
		
		if(pagingQuery==null ){
			 pagingQuery = new ArrayList<Object>();
		}
		result.put("total", pagingQuery.size());
		result.put("result", pagingQuery);
		result.put("success", true);
	    return result.toString();
	}
	
}
