package com.dt.afzrms.service;

import com.dt.afzrms.vo.PageVo;
import com.dt.afzrms.vo.TTerminalAlarmsetVo;
import com.dt.afzrms.vo.TerminalVo;

/**
 * @Title 终端业务层接口
 * @Description TODO
 * @author
 * @createDate 2015-1-25 下午3:08:50
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public interface TerminalService {
	public PageVo<TerminalVo> findList(Integer pageNo, Integer pageSize,
			Integer deptId, String name);

	public TerminalVo findByDeviceId(String deviceId);

	public String add(String deviceId, String name, Integer deptId,
			Integer vehicleTypeId, String protocalType, String simcard,
			Boolean locateType, String remark, String createBy, Integer vehicleBrandId);

	public String update(String deviceId, String name,
			Integer deptId, Integer vehicleTypeId, String protocalType,
			String simcard, Boolean locateType, String remark, Integer vehicleBrandId);

	public void delete(String[] ids);
	
	public String getTerminalDetailInfo(String deviceId);
	
	public TTerminalAlarmsetVo findAlarmSetByDeviceId(String deviceId);
}
