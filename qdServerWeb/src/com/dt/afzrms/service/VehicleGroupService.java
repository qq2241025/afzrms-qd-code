package com.dt.afzrms.service;

import java.util.List;

import com.dt.afzrms.po.TVehicleGroup;
import com.dt.afzrms.vo.PageVo;
import com.dt.afzrms.vo.TreeNodeVo;
import com.dt.afzrms.vo.TreeVo;
import com.dt.afzrms.vo.VehicleGroupVo;

/**
 * @Title 车辆分组业务层接口
 * @Description TODO
 * @author
 * @createDate 2015-1-25 下午4:50:00
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public interface VehicleGroupService {
	public PageVo<VehicleGroupVo> findList(Integer pageNo, Integer pageSize,
			String name);

	public Integer add(String name, String uses, String remark, String createBy, String[] deviceIds);

	public Integer update(Integer id, String name, String uses, String remark, String[] deviceIds);

	public void delete(Integer[] ids);
	
	public TreeVo treeTerminal(Boolean returnBindAreaAlarm);
	public List<TreeNodeVo> OthertreeTerminal(int UserId,Boolean returnBindAreaAlarm);

	public List<String> findDeviceIdList(Integer vehicleGroupId);
	
	public List<TVehicleGroup> findAllGroups();
}
