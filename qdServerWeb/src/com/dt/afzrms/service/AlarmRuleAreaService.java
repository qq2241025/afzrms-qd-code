package com.dt.afzrms.service;

import com.dt.afzrms.vo.AlarmruleAreaVo;
import com.dt.afzrms.vo.PageVo;

/**
 * @Title alarm rule area service interface
 * @Description TODO
 * @author
 * @createDate 2015年3月27日 上午10:58:43
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public interface AlarmRuleAreaService {

	public PageVo<AlarmruleAreaVo> findList(Integer pageNo, Integer pageSize, String name);

	public Integer add(String name, String areas, String remark, String createBy);

	public Integer update(Integer id, String name, String areas, String remark);

	public void delete(Integer[] ids);

}
