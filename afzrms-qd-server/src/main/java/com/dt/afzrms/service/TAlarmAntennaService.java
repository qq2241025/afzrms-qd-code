
package com.dt.afzrms.service;

import com.dt.afzrms.vo.UserVo;

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

public interface TAlarmAntennaService {
   public String queryDriverList(Integer pageNo, Integer pageSize, 	String verhicleName,String deptName,String startTime,String endTime,UserVo user) throws Exception;
}
