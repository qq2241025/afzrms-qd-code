package com.dt.afzrms.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dt.afzrms.common.Const;
import com.dt.afzrms.po.TTerminalDriver;
import com.dt.afzrms.service.TTerminalDriverService;
import com.dt.afzrms.service.TerminalService;
import com.dt.afzrms.util.StringUtil;
import com.dt.afzrms.vo.PageVo;
import com.dt.afzrms.vo.ResultVo;
import com.dt.afzrms.vo.TTerminalAlarmsetVo;
import com.dt.afzrms.vo.TerminalVo;
import com.mapabc.common.dao.hibernate.Page;
import com.mapabc.util.json.JsonUtil;

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
@RequestMapping(value = "/terminal")
public class TerminalController extends BaseController {
	@Autowired
	TerminalService terminalService;
	@Autowired
	TTerminalDriverService tTerminalDriverService;

	@RequestMapping(value = "list")
	@ResponseBody
	public String list(HttpServletRequest request, HttpServletResponse response, Integer start, Integer limit,
			Integer deptId, String account, String name) {
		Integer pageSize = limit == null ? Const.PAGE_SIZE : limit;
		Integer pageNo = start == null ? Const.PAGE_NO : (start / pageSize + 1);
		PageVo<TerminalVo> findList = terminalService.findList(pageNo, pageSize, deptId, name);
		return JsonUtil.convertToJsonStr(findList);
	}
	
	
	@RequestMapping(value = "maplist")
	@ResponseBody
	public String maplist(HttpServletRequest request, HttpServletResponse response, Integer start, Integer limit,
			Integer deptId, String account, String name,String driverName) {
		Integer pageSize = limit == null ? Const.PAGE_SIZE : limit;
		Integer pageNo = start == null ? Const.PAGE_NO : (start / pageSize + 1);
		JSONObject rs = new JSONObject();
		int total = 0;
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Page<Map<String, String>> findList;
		try {
			findList = terminalService.findMapList(pageNo, pageSize, deptId, name, driverName);
			if(findList!=null){
				list = findList.getResult();
				total = findList.getTotalCount();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		rs.put("total", total);
		rs.put("data", list);
		return rs.toString();
	}

	@RequestMapping(value = "add")
	@ResponseBody
	public String add(HttpServletRequest request, HttpServletResponse response, String deviceId, String name,
			Integer deptId, Integer vehicleTypeId, String protocalType, String simcard, Boolean locateType,
			String remark, Integer vehicleBrandId,String driverName,String driverPhone) {
		String reStr = Const.RESP_MSG_SUCCESS_ADD;
		Boolean flag = true;
		
		TerminalVo terminalVo = terminalService.findByDeviceId(deviceId);
		if (terminalVo != null) {
			reStr = "deviceId:'" + deviceId + "'" + Const.RESP_MSG_FAIL_EXIST;
			flag = false;
		} else {
			String createBy = currentUserName(request);
			try {
				terminalService.add(deviceId, name, deptId, vehicleTypeId, protocalType, simcard, locateType, remark,
						createBy, vehicleBrandId);
				TTerminalDriver diver = new TTerminalDriver();
				diver.setDeviceId(deviceId);
				diver.setDriverName(driverName);
				diver.setDriverPhone(driverPhone);
				tTerminalDriverService.addTTerminalDriver(diver);
			} catch (Exception e) {
				logger.error("add error", e);
				reStr = Const.RESP_MSG_FAIL_ADD;
				flag = false;
			}
		}
		return JsonUtil.convertToJsonStr(new ResultVo(flag, reStr));
	}

	@RequestMapping(value = "update")
	@ResponseBody
	public String update(HttpServletRequest request, HttpServletResponse response, String deviceId, String name,
			Integer deptId, Integer vehicleTypeId, String protocalType, String simcard, Boolean locateType,
			String remark, Integer vehicleBrandId,String driverName,String driverPhone) {
		String reStr = Const.RESP_MSG_SUCCESS_UPDATE;
		Boolean flag = true;
		try {
			terminalService.update(deviceId, name, deptId, vehicleTypeId, protocalType, simcard, locateType, remark, vehicleBrandId);
			TTerminalDriver diver = new TTerminalDriver();
			diver.setDeviceId(deviceId);
			diver.setDriverName(driverName);
			diver.setDriverPhone(driverPhone);
			tTerminalDriverService.updateTTerminalDriverByDeviceId(diver);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("update error", e);
			reStr = Const.RESP_MSG_FAIL_UPDATE;
			flag = false;
		}
		return JsonUtil.convertToJsonStr(new ResultVo(flag, reStr));
	}

	@RequestMapping(value = "delete")
	@ResponseBody
	public String delete(HttpServletRequest request, HttpServletResponse response, String ids) {
		String reStr = Const.RESP_MSG_SUCCESS_DELETE;
		Boolean flag = true;

		try {
			terminalService.delete(StringUtil.split2StringArray(ids, ","));
		} catch (Exception e) {
			logger.error("delete error", e);
			reStr = Const.RESP_MSG_FAIL_DELETE;
			flag = false;
		}
		return JsonUtil.convertToJsonStr(new ResultVo(flag, reStr));
	}

	@RequestMapping(value = "findAlarmSet")
	@ResponseBody
	public String findAlarmSet(HttpServletRequest request, HttpServletResponse response, String deviceId) {
		TTerminalAlarmsetVo findAlarmSetByDeviceId = terminalService.findAlarmSetByDeviceId(deviceId);
		return JsonUtil.convertToJsonStr(findAlarmSetByDeviceId);
	}
	
	
	@RequestMapping(value = "terminalInfo")
	@ResponseBody
	public String TerminalInfo(HttpServletRequest request, HttpServletResponse response, String deviceId ) {
		try {
			String resulr = terminalService.getTerminalDetailInfo(deviceId);
			return resulr;
		} catch (Exception e) {
			e.printStackTrace();
			boolean flag = false;
			return "{\"success\":" + flag + ",\"info\":\"异常\"}";
		}
	}
	
	
	
}
