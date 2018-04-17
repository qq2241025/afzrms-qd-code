package com.dt.afzrms.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dt.afzrms.common.Const;
import com.dt.afzrms.po.TTerminalRepair;
import com.dt.afzrms.service.TermailRepairService;
import com.dt.afzrms.util.StringUtil;
import com.dt.afzrms.vo.UserVo;
import com.mapabc.common.dao.hibernate.Page;

/**
 * @Title terminal controller
 * @Description TODO
 * @author
 * @createDate 2015-1-25 下午3:45:08
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */
@Controller
@RequestMapping(value = "/repair")
public class TerminalRepairController extends BaseController {
	@Autowired
	TermailRepairService termailRepairService;

	@RequestMapping(value = "list")
	@ResponseBody
	public String list(HttpServletRequest request, HttpServletResponse response, Integer start, Integer limit,String tername,String deptName) {
		Integer pageSize = limit == null ? Const.PAGE_SIZE : limit;
		Integer pageNo = start == null ? Const.PAGE_NO : (start / pageSize + 1);
		JSONObject rs = new JSONObject();
		UserVo user =this.getLoginUser(request);
		int total = 0;
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Page<Map<String, String>> findList;
		try {
			findList = termailRepairService.findList(pageNo, pageSize,  tername, deptName ,user);
			if(findList!=null){
				list = findList.getResult();
				total = findList.getTotalCount();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		rs.put("total", total);
		rs.put("result", list);
		return rs.toString();
	}

	@RequestMapping(value = "add")
	@ResponseBody
	public String add(HttpServletRequest request, HttpServletResponse response, String deviceId,String desc) {
		JSONObject rs = new JSONObject();
		//验证判断
		if(StringUtils.isEmpty(deviceId)){
			rs.put("success", true);
			rs.put("msg", "deviceId is null");
			return rs.toString(); 
		}
		//验证判断
		if(StringUtils.isEmpty(desc)){
			rs.put("success", true);
			rs.put("msg", "desc is null");
			return rs.toString(); 
		}
		UserVo user =this.getLoginUser(request);
		int userId = 0;
		if(user !=null ){
			userId = user.getId();
		}
		TTerminalRepair repair = new TTerminalRepair();
		repair.setDeviceId(deviceId);
		repair.setUserId(userId);
		repair.setRemark(desc);
		repair.setCreateTime(new Date());
		try {
			termailRepairService.addTermailRepair(repair);
			rs.put("success", true);
			rs.put("msg", "ok");
		} catch (Exception e) {
			e.printStackTrace();
			rs.put("success", false);
			rs.put("msg", "to add has happend a error ");
		}
		return rs.toString(); 
	}
	
	@RequestMapping(value = "delete")
	@ResponseBody
	public String delete(HttpServletRequest request, HttpServletResponse response, String ids) {
		JSONObject rs = new JSONObject();
		//验证判断
		if(StringUtils.isEmpty(ids)){
			rs.put("success", true);
			rs.put("msg", "ids is null");
			return rs.toString(); 
		}
		try {
			termailRepairService.deleteTermailRepair(StringUtil.split2IntArray(ids, ","));
			rs.put("success", true);
			rs.put("msg", "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			rs.put("success", false);
			rs.put("msg", "删除失败");
		}
		return rs.toString(); 
	}
	
	
}
