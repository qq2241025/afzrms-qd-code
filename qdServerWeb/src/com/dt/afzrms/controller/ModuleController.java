package com.dt.afzrms.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dt.afzrms.common.Const;
import com.dt.afzrms.service.ModuleService;
import com.dt.afzrms.vo.ModuleVo;
import com.dt.afzrms.vo.PageVo;
import com.dt.afzrms.vo.TreeVo;
import com.mapabc.util.json.JsonUtil;

/**
 * @Title module
 * @Description TODO
 * @author
 * @createDate 2015-1-24 下午6:47:31
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */
@Controller
@RequestMapping(value = "/module")
public class ModuleController extends BaseController {
	@Autowired
	ModuleService moduleService;

	@RequestMapping(value = "list")
	@ResponseBody
	public String list(HttpServletRequest request, HttpServletResponse response, Integer start, Integer limit,
			Integer parentId, String name) {
		Integer pageSize = limit == null ? Const.PAGE_SIZE : limit;
		Integer pageNo = start == null ? Const.PAGE_NO : (start / pageSize + 1);
		PageVo<ModuleVo> findList = moduleService.findList(pageNo, pageSize, parentId, name);
		return JsonUtil.convertToJsonStr(findList);
	}

	@RequestMapping(value = "tree")
	@ResponseBody
	public String tree(HttpServletRequest request, HttpServletResponse response, Integer id) {
		TreeVo tree = moduleService.tree(id);
		return JsonUtil.convertToJsonStr(tree);
	}
	
	@RequestMapping(value = "treeWithRole")
	@ResponseBody
	public String treeWithRole(HttpServletRequest request, HttpServletResponse response, Integer id) {
		Integer currentUserRoleId = currentUserRoleId(request);
		TreeVo tree = moduleService.tree(id, currentUserRoleId);
		return JsonUtil.convertToJsonStr(tree);
	}
//
//	@RequestMapping(value = "add")
//	@ResponseBody
//	public String add(HttpServletRequest request, HttpServletResponse response, String name, Integer parentId,
//			Integer sortNum, String urlPath, Boolean isUsed, Boolean isVisible, String remark) {
//		String reStr = Const.RESP_MSG_SUCCESS_ADD;
//		Boolean flag = true;
//
//		String createBy = currentUserName(request);
//		try {
//			moduleService.add(name, parentId, sortNum, urlPath, isUsed, isVisible, remark, createBy);
//		} catch (Exception e) {
//			logger.error("add error", e);
//			reStr = Const.RESP_MSG_FAIL_ADD;
//			flag = false;
//		}
//
//		return JsonUtil.convertToJsonStr(new ResultVo(flag, reStr));
//	}
//
//	@RequestMapping(value = "update")
//	@ResponseBody
//	public String update(HttpServletRequest request, HttpServletResponse response, Integer id, String name,
//			Integer parentId, Integer sortNum, String urlPath, Boolean isUsed, Boolean isVisible, String remark) {
//		String reStr = Const.RESP_MSG_SUCCESS_UPDATE;
//		Boolean flag = true;
//
//		try {
//			moduleService.update(id, name, parentId, sortNum, urlPath, isUsed, isVisible, remark);
//		} catch (Exception e) {
//			logger.error("update error", e);
//			reStr = Const.RESP_MSG_FAIL_UPDATE;
//			flag = false;
//		}
//		return JsonUtil.convertToJsonStr(new ResultVo(flag, reStr));
//	}
//
//	@RequestMapping(value = "delete")
//	@ResponseBody
//	public String delete(HttpServletRequest request, HttpServletResponse response, String ids) {
//		String reStr = Const.RESP_MSG_SUCCESS_DELETE;
//		Boolean flag = true;
//
//		try {
//			moduleService.delete(StringUtil.split2IntArray(ids, ","));
//		} catch (Exception e) {
//			logger.error("delete error", e);
//			reStr = Const.RESP_MSG_FAIL_DELETE;
//			flag = false;
//		}
//		return JsonUtil.convertToJsonStr(new ResultVo(flag, reStr));
//	}
}
