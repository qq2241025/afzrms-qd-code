package com.dt.afzrms.service;

import com.dt.afzrms.vo.DeptVo;
import com.dt.afzrms.vo.PageVo;
import com.dt.afzrms.vo.TreeVo;

/**
 * @Title dept interface
 * @Description TODO
 * @author
 * @createDate 2015-1-24 下午6:49:03
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public interface DeptService {
	public PageVo<DeptVo> findList(Integer pageNo, Integer pageSize,
			Integer parentId, String name);

	public TreeVo tree(Integer id);

	public Integer add(String name, Integer parentId, Integer sortNum,
			String duty, String director, String remark, String createBy);

	public Integer update(Integer id, String name, Integer parentId,
			Integer sortNum, String duty, String director, String remark);

	public void delete(Integer[] ids);
	
	public TreeVo treeTerminal();
}
