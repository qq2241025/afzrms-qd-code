package com.dt.afzrms.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dt.afzrms.common.Const;
import com.dt.afzrms.service.RoleService;
import com.dt.afzrms.util.StringUtil;
import com.dt.afzrms.vo.PageVo;
import com.dt.afzrms.vo.ResultVo;
import com.dt.afzrms.vo.RoleVo;
import com.mapabc.util.json.JsonUtil;

/**
 * @Title role controller
 * @Description TODO
 * @author
 * @createDate 2015-1-25 下午2:28:49
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */
@Controller
@RequestMapping(value = "/role")
public class RoleController extends BaseController {
	@Autowired
	private RoleService roleService;

	@RequestMapping(value = "list")
	@ResponseBody
	public String list(HttpServletRequest request, HttpServletResponse response, Integer start, Integer limit,
			Integer deptId, String name) {
		Integer pageSize = limit == null ? Const.PAGE_SIZE : limit;
		Integer pageNo = start == null ? Const.PAGE_NO : (start / pageSize + 1);
		PageVo<RoleVo> findList = roleService.findList(pageNo, pageSize, deptId, name);
		return JsonUtil.convertToJsonStr(findList);
	}
	
	@RequestMapping(value = "moduleList")
	@ResponseBody
	public String moduleList(HttpServletRequest request, HttpServletResponse response, Integer roleId) {
		List<Integer> findModuleList = roleService.findModuleList(roleId);
//		return JsonUtil.convertToJsonStr(new ResultVo(true, findModuleList));
		return JSONObject.fromObject(new ResultVo(true, findModuleList)).toString();
	}

	@RequestMapping(value = "add")
	@ResponseBody
	public String add(HttpServletRequest request, HttpServletResponse response, String name, Integer deptId,
			String description, String remark, String moduleIds) {
		String reStr = Const.RESP_MSG_SUCCESS_ADD;
		Boolean flag = true;

		String createBy = currentUserName(request);
		try {
			roleService.add(name, deptId, description, remark, createBy, StringUtil.split2IntArray(moduleIds, ","));
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
			Integer deptId, String description, String remark, String moduleIds) {
		String reStr = Const.RESP_MSG_SUCCESS_UPDATE;
		Boolean flag = true;

		try {
			roleService.update(id, name, deptId, description, remark, StringUtil.split2IntArray(moduleIds, ","));
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
			roleService.delete(StringUtil.split2IntArray(ids, ","));
		} catch (Exception e) {
			logger.error("delete error", e);
			reStr = Const.RESP_MSG_FAIL_DELETE;
			flag = false;
		}
		return JsonUtil.convertToJsonStr(new ResultVo(flag, reStr));
	}
}
