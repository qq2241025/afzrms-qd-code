package com.dt.afzrms.service;

import java.util.List;

import com.dt.afzrms.vo.PageVo;
import com.dt.afzrms.vo.RoleVo;

/**
 * @Title 角色业务层接口
 * @Description TODO
 * @author
 * @createDate 2015-1-25 下午2:02:56
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public interface RoleService {
	public PageVo<RoleVo> findList(Integer pageNo, Integer pageSize,
			Integer deptId, String name);

	public List<Integer> findModuleList(Integer roleId);
	
	public Integer add(String name, Integer deptId, String description,
			String remark, String createBy, Integer[] moduleIds);

	public Integer update(Integer id, String name, Integer deptId,
			String description, String remark, Integer[] moduleIds);

	public void delete(Integer[] ids);
}
