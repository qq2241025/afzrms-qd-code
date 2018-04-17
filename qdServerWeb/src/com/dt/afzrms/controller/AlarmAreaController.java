package com.dt.afzrms.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dt.afzrms.common.Const;
import com.dt.afzrms.service.AlarmAreaService;
import com.dt.afzrms.util.StringUtil;
import com.dt.afzrms.vo.AlarmAreaVo;
import com.dt.afzrms.vo.PageVo;
import com.dt.afzrms.vo.ResultVo;
import com.mapabc.util.json.JsonUtil;

/**
 * @Title alarm area controller
 * @Description TODO
 * @author
 * @createDate 2015-3-19 下午15:16:08
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */
@Controller
@RequestMapping(value = "/alarmArea")
public class AlarmAreaController extends BaseController {
	@Autowired
	private AlarmAreaService alarmAreaService;

	@RequestMapping(value = "list")
	@ResponseBody
	public String list(HttpServletRequest request, HttpServletResponse response, Integer start, Integer limit,
			String name) {
		Integer pageSize = limit == null ? Const.PAGE_SIZE : limit;
		Integer pageNo = start == null ? Const.PAGE_NO : (start / pageSize + 1);
		PageVo<AlarmAreaVo> findList = alarmAreaService.findList(pageNo, pageSize, name);
		return JsonUtil.convertToJsonStr(findList);
	}

	@RequestMapping(value = "add")
	@ResponseBody
	public String add(HttpServletRequest request, HttpServletResponse response, String name, String xys,
			String description, String remark) {
		String reStr = Const.RESP_MSG_SUCCESS_ADD;
		Boolean flag = true;

		String createBy = currentUserName(request);
		try {
			alarmAreaService.add(name, xys, description, remark, createBy);
		} catch (Exception e) {
			logger.error("add error", e);
			reStr = Const.RESP_MSG_FAIL_ADD;
			flag = false;
		}

		return JsonUtil.convertToJsonStr(new ResultVo(flag, reStr));
	}

	@RequestMapping(value = "update")
	@ResponseBody
	public String update(HttpServletRequest request, HttpServletResponse response, Integer id, String name, String xys,
			String description, String remark) {
		String reStr = Const.RESP_MSG_SUCCESS_UPDATE;
		Boolean flag = true;

		try {
			alarmAreaService.update(id, name, xys, description, remark);
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
			alarmAreaService.delete(StringUtil.split2IntArray(ids, ","));
		} catch (Exception e) {
			logger.error("delete error", e);
			reStr = Const.RESP_MSG_FAIL_DELETE;
			flag = false;
		}
		return JsonUtil.convertToJsonStr(new ResultVo(flag, reStr));
	}
	
	@RequestMapping(value = "select")
	@ResponseBody
	public String select(HttpServletRequest request, HttpServletResponse response, String deviceId) {
		return alarmAreaService. selectBindDevide(deviceId);
	}
	
	@RequestMapping(value = "cacheAreaList")
	@ResponseBody
	public String cacheAreaList(HttpServletRequest request, HttpServletResponse response) {
		return alarmAreaService.selectAllAlarmList();
	}
}
