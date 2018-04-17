package com.dt.afzrms.service;

import java.util.List;

import com.dt.afzrms.vo.PageVo;
import com.dt.afzrms.vo.UserVo;

/**
 * @Title 用户业务层接口
 * @Description TODO
 * @author
 * @createDate 2015年1月20日 上午11:15:36
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public interface UserService {
	public List<UserVo> findByAccount(String account);

	public PageVo findList(Integer pageNo, Integer pageSize, Integer deptId, String account, String name);

	public UserVo findById(Integer id);

	public Integer add(String account, String passwd, String name, Integer deptId,
			String contact, String remark, Integer roleId, String createBy,int userType,Integer[] groupIds);

	public Integer update(Integer id, String account, String name, Integer deptId,
			String contact, String remark, Integer roleId,int userType,Integer[] groupIds);

	public int delete(Integer[] ids);
}
