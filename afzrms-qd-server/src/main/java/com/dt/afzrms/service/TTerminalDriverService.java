
package com.dt.afzrms.service;

import java.util.Map;

import com.dt.afzrms.po.TTerminalDriver;
import com.mapabc.common.dao.hibernate.Page;

/**
 * @Title TODO
 * @Description TODO
 * @author 
 * @createDate 2016年8月28日 下午11:12:42
 * @modifier 
 * @modifyDate 
 * @version 1.0
 * 
 */

public interface TTerminalDriverService {
   public void addTTerminalDriver(TTerminalDriver driver) ;
   public void updateTTerminalDriver(TTerminalDriver driver) throws Exception;
   public void updateTTerminalDriverByDeviceId(TTerminalDriver driver) ;
   public void deleteTTerminalDriver(String[] driverIds) throws Exception;
   public TTerminalDriver selectTerminalDriver(String devideId) throws Exception;
   public Page<Map<String, String>> queryDriverList(Integer pageNo, Integer pageSize, 	String verhicleName,String deptName,String driverName) throws Exception;
}
