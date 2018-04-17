package com.dt.afzrms.service;

import java.util.Map;

import com.dt.afzrms.po.TTerminalRepair;
import com.dt.afzrms.vo.UserVo;
import com.mapabc.common.dao.hibernate.Page;

/**
 * @Title tj term alarm service interface
 * @Description TODO
 * @author
 * @createDate 2015年3月31日 下午3:30:46
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public interface TermailRepairService {
	public Page<Map<String,String>> findList(Integer pageNo, Integer pageSize, 	String verhicleName,String deptName,UserVo user)
			throws Exception;
	public void addTermailRepair(TTerminalRepair repair) throws Exception;
	
	public void deleteTermailRepair(Integer[] ids) throws Exception;
	
}
