package com.dt.afzrms.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dt.afzrms.common.Const;
import com.dt.afzrms.po.TVehicleGroup;
import com.dt.afzrms.service.VehicleGroupService;
import com.dt.afzrms.util.StringUtil;
import com.dt.afzrms.vo.PageVo;
import com.dt.afzrms.vo.ResultVo;
import com.dt.afzrms.vo.TreeNodeVo;
import com.dt.afzrms.vo.TreeVo;
import com.dt.afzrms.vo.UserVo;
import com.dt.afzrms.vo.VehicleGroupVo;
import com.mapabc.util.json.JsonUtil;

/**
 * @Title VehicleGroup controller
 * @Description TODO
 * @author
 * @createDate 2015-1-25 下午3:45:08
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */
@Controller
@RequestMapping(value = "/vehicleGroup")
public class VehicleGroupController extends BaseController {
	@Autowired
	VehicleGroupService vehicleGroupService;

	@RequestMapping(value = "list")
	@ResponseBody
	public String list(HttpServletRequest request, HttpServletResponse response, Integer start, Integer limit,
			Integer deptId, String account, String name) {
		Integer pageSize = limit == null ? Const.PAGE_SIZE : limit;
		Integer pageNo = start == null ? Const.PAGE_NO : (start / pageSize + 1);
		PageVo<VehicleGroupVo> findList = vehicleGroupService.findList(pageNo, pageSize, name);
		return JsonUtil.convertToJsonStr(findList);
	}
	
	
	@RequestMapping(value = "listall")
	@ResponseBody
	public String listall(HttpServletRequest request, HttpServletResponse response) {
		List<TVehicleGroup> findList = vehicleGroupService.findAllGroups();
		JSONObject res = new JSONObject();
		JSONArray list = new JSONArray();
		if(findList!=null){
			for (TVehicleGroup group : findList) {
				JSONObject gr = new JSONObject();
				gr.put("id", group.getId());
				gr.put("name", group.getName());
				gr.put("text", group.getName());
				list.add(gr);
			}
		}
		res.put("success", true);
		res.put("result", list);
		res.put("total", list.size());
		return res.toString();
	}

	@RequestMapping(value = "treeTerminal")
	@ResponseBody
	public String treeTerminal(HttpServletRequest request, HttpServletResponse response, Boolean returnBindAreaAlarm) {
		
		UserVo user = this.getLoginUser(request);
		if(user !=null && Const.SYSTEMUSER != user.getUserType()){ //普通管理员
			int userId = user.getId();
			List<TreeNodeVo> list = vehicleGroupService.OthertreeTerminal(userId, returnBindAreaAlarm);
			return JsonUtil.convertToJsonStr(new ResultVo(true, list));
		}else{ //超级管理员
			TreeVo treeVo = vehicleGroupService.treeTerminal(returnBindAreaAlarm);
			return JsonUtil.convertToJsonStr(treeVo);
		}
		
	}

	@RequestMapping(value = "deviceIdList")
	@ResponseBody
	public String deviceIdList(HttpServletRequest request, HttpServletResponse response, Integer vehicleGroupId) {
		List<String> findDeviceIdList = vehicleGroupService.findDeviceIdList(vehicleGroupId);
		// return JsonUtil.convertToJsonStr(new ResultVo(true, findModuleList));
		return JSONObject.fromObject(new ResultVo(true, findDeviceIdList)).toString();
	}

	@RequestMapping(value = "add")
	@ResponseBody
	public String add(HttpServletRequest request, HttpServletResponse response, String name, String uses,
			String remark, String deviceIds) {
		String reStr = Const.RESP_MSG_SUCCESS_ADD;
		Boolean flag = true;
		String createBy = currentUserName(request);
		try {
			vehicleGroupService.add(name, uses, remark, createBy, StringUtil.split2StringArray(deviceIds, ","));
		} catch (Exception e) {
			logger.error("add error", e);
			reStr = Const.RESP_MSG_FAIL_ADD;
			flag = false;
		}
		return JsonUtil.convertToJsonStr(new ResultVo(flag, reStr));
	}

	@RequestMapping(value = "update")
	@ResponseBody
	public String update(HttpServletRequest request, HttpServletResponse response, Integer id, String name,
			String uses, String remark, String deviceIds) {
		String reStr = Const.RESP_MSG_SUCCESS_UPDATE;
		Boolean flag = true;
		try {
			vehicleGroupService.update(id, name, uses, remark, StringUtil.split2StringArray(deviceIds, ","));
		} catch (Exception e) {
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
			vehicleGroupService.delete(StringUtil.split2IntArray(ids, ","));
		} catch (Exception e) {
			logger.error("delete error", e);
			reStr = Const.RESP_MSG_FAIL_DELETE;
			flag = false;
		}
		return JsonUtil.convertToJsonStr(new ResultVo(flag, reStr));
	}

}
