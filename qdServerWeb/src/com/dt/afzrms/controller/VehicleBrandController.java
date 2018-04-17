package com.dt.afzrms.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dt.afzrms.common.Const;
import com.dt.afzrms.service.VehicleBrandService;
import com.dt.afzrms.util.StringUtil;
import com.dt.afzrms.vo.PageVo;
import com.dt.afzrms.vo.ResultVo;
import com.dt.afzrms.vo.VehicleBrandVo;
import com.mapabc.util.json.JsonUtil;

/**
 * @Title VehicleBrand controller
 * @Description TODO
 * @author
 * @createDate 2015-1-25 下午3:45:08
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */
@Controller
@RequestMapping(value = "/vehicleBrand")
public class VehicleBrandController extends BaseController {
	@Autowired
	VehicleBrandService vehicleBrandService;

	@RequestMapping(value = "list")
	@ResponseBody
	public String list(HttpServletRequest request, HttpServletResponse response, Integer start, Integer limit,
			Integer deptId, String account, String name) {
		Integer pageSize = limit == null ? Const.PAGE_SIZE : limit;
		Integer pageNo = start == null ? Const.PAGE_NO : (start / pageSize + 1);
		PageVo<VehicleBrandVo> findList = vehicleBrandService.findList(pageNo, pageSize, name);
		return JsonUtil.convertToJsonStr(findList);
	}

	@RequestMapping(value = "add")
	@ResponseBody
	public String add(HttpServletRequest request, HttpServletResponse response, String name, String description,
			String simcard, String remark) {
		String reStr = Const.RESP_MSG_SUCCESS_ADD;
		Boolean flag = true;
		String createBy = currentUserName(request);
		try {
			vehicleBrandService.add(name, description, remark, createBy);
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
			String description, String remark) {
		String reStr = Const.RESP_MSG_SUCCESS_UPDATE;
		Boolean flag = true;
		try {
			vehicleBrandService.update(id, name, description, remark);
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
			vehicleBrandService.delete(StringUtil.split2IntArray(ids, ","));
		} catch (Exception e) {
			logger.error("delete error", e);
			reStr = Const.RESP_MSG_FAIL_DELETE;
			flag = false;
		}
		return JsonUtil.convertToJsonStr(new ResultVo(flag, reStr));
	}

}
