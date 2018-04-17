package com.dt.afzrms.service;

import com.dt.afzrms.vo.ModuleVo;
import com.dt.afzrms.vo.PageVo;
import com.dt.afzrms.vo.TreeVo;

/**
 * @Title module interface
 * @Description TODO
 * @author
 * @createDate 2015-1-24 下午6:49:03
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public interface ModuleService {
	public PageVo<ModuleVo> findList(Integer pageNo, Integer pageSize, Integer parentId, String name);

	public TreeVo tree(Integer id);
	
	public TreeVo tree(Integer id, Integer roleId);
}
