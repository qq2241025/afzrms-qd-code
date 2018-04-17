
package com.dt.afzrms.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dt.afzrms.dao.hibernate.TTerminalDriverDao;
import com.dt.afzrms.po.TTerminalDriver;
import com.dt.afzrms.service.TTerminalDriverService;
import com.mapabc.common.dao.hibernate.Page;

/**
 * @Title TODO
 * @Description TODO
 * @author 
 * @createDate 2016年8月28日 下午11:14:48
 * @modifier 
 * @modifyDate 
 * @version 1.0
 * 
 */
@Service
public class TTerminalDriverServiceImpl implements TTerminalDriverService {
  
	@Autowired
	TTerminalDriverDao tTerminalDriverDao;
	
	@Override
	public void addTTerminalDriver(TTerminalDriver driver) {
		try{
			String devideId = driver.getDeviceId();
			TTerminalDriver olddirver = this.selectTerminalDriver(devideId);
			if(olddirver==null){
				tTerminalDriverDao.save(driver);
			}else{
				olddirver.setDriverName(driver.getDriverName());
				olddirver.setDriverPhone(driver.getDriverPhone());
				this.updateTTerminalDriver(olddirver);
			}
		}catch(Exception e){
			 e.printStackTrace();
		}
	}

	@Override
	public void updateTTerminalDriver(TTerminalDriver driver) {
		// TODO Auto-generated method stub
		 tTerminalDriverDao.update(driver);
	}

	@Override
	public void deleteTTerminalDriver(String[] devideId) {
		// TODO Auto-generated method stub
		tTerminalDriverDao.deleteByHql("delete TTerminalDriver where devideId=? ", devideId);
	}

	@Override
	public TTerminalDriver selectTerminalDriver(String devideId) {
		// TODO Auto-generated method stub
		TTerminalDriver driver = null;
		String hql = " from TTerminalDriver where deviceId = '"+devideId+"'";
		List<TTerminalDriver> list = (List<TTerminalDriver>) tTerminalDriverDao.findList(hql);
		if(list!=null && list.size() >0){
			driver = list.get(0);
		}
		return driver;
	}

	@Override
	public Page<Map<String, String>> queryDriverList(Integer pageNo, Integer pageSize,
			String verhicleName, String deptName, String driverName) {
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
		sql.append(" order by re.createTime ");
		Page<?> qpage= tTerminalDriverDao.pagingQueryWithSqlReturnMap(pageNo, pageSize, sql.toString());
		Page<Map<String, String>> page = (Page<Map<String, String>>) qpage;
		return page;
	}

	@Override
	public void updateTTerminalDriverByDeviceId(TTerminalDriver driver) {
		try{
			String deviceid= driver.getDeviceId();
			String driverName = driver.getDriverName();
			String driverPhone = driver.getDriverPhone();
			TTerminalDriver dbdirver = this.selectTerminalDriver(deviceid);
			if(dbdirver!=null){
				dbdirver.setDriverName(driverName);
				dbdirver.setDriverPhone(driverPhone);
				tTerminalDriverDao.update(dbdirver);
			}else{
				tTerminalDriverDao.save(driver);
			}
		}catch(Exception e){
			 e.printStackTrace();
		}
	}

}
