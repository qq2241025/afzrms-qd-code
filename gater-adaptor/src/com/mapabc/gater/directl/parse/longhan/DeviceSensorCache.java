package com.mapabc.gater.directl.parse.longhan;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Hashtable;

import org.apache.commons.logging.LogFactory;
 
import com.mapabc.gater.directl.Log;
import com.mapabc.gater.directl.dbutil.DbOperation;
import com.mapabc.gater.directl.dbutil.DbUtil;
import com.mapabc.gater.directl.dbutil.service.DBService;
import com.mapabc.gater.directl.dbutil.service.DBServiceImpl; 

public class DeviceSensorCache {
	private static org.apache.commons.logging.Log log = LogFactory
	.getLog(DeviceSensorCache.class);
	private static DeviceSensorCache instance = null;

	private static HashMap<String, SensorBean> sensorMap = new HashMap<String, SensorBean>();

	public static synchronized DeviceSensorCache getInstance() {
		if (instance == null) {
			instance = new DeviceSensorCache();
 		}
		return instance;
	}
	
	public void loadDeviceSensors(){
		try {
			 loadDeviceSensor(null);
		} catch (Exception e) {
			Log.getInstance().errorLog("加载传感异常", e);
		}
	}

	public synchronized  void addDeviceSensor(String deviceId, SensorBean bean) {
		instance.sensorMap.put(deviceId, bean);
		log.info("\r\n增加/修改"+deviceId+"传感信息："+bean.toString());
	}

	public  synchronized void removeDeviceSensor(String deviceId) {
		SensorBean bean = instance.sensorMap.remove(deviceId);
		if (bean != null)
		log.info("\r\n删除"+deviceId+"传感信息："+bean.toString());
		 
	}

	public SensorBean getDeviceSensor(String deviceId) {
		return instance.sensorMap.get(deviceId);
	}
	
	public HashMap<String, SensorBean> loadDeviceSensor(String deviceId) {
		// TODO Auto-generated method stub
		String sql = "select * from e_sensor s,e_vehicle v where s.SENSOR_ID=v.sensor_id";
		if (deviceId != null)
			sql += " and v.DEVICE_ID='"+deviceId+"'";
		
		Connection conn = DbUtil.getConnection();
		Statement stm = null;
		ResultSet rs = null;
		HashMap<String, SensorBean> deviceSensorMap = new HashMap<String, SensorBean>();
		
		try {
			stm = conn.createStatement();
			rs = stm.executeQuery(sql);
			while(rs.next()){
				SensorBean bean = new SensorBean();
				
				bean.setDeviceId(rs.getString("device_id"));
				bean.setSensorId(rs.getLong("SENSOR_ID"));
				bean.setSensorName(rs.getString("SENSOR_NM"));
				bean.setStatus1(rs.getString("status1"));
				bean.setStatus2(rs.getString("status2"));
				bean.setStatus3(rs.getString("status3"));
				bean.setStatus4(rs.getString("status4"));
				bean.setStatus5(rs.getString("status5"));
				bean.setStatus6(rs.getString("status6"));
				bean.setStatus7(rs.getString("status7"));
				bean.setStatus8(rs.getString("status8"));
				bean.loadSensorInfo();
				log.info(bean.toString());
				
				deviceSensorMap.put(bean.getDeviceId(), bean);
			}
	 			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.getInstance().errorLog("加载传感方案异常", e);
		}finally{
			DbOperation.release(stm, rs, null, null, conn);
		}
		
		return deviceSensorMap;
	}

}
