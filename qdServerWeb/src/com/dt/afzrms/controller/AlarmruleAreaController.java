package com.dt.afzrms.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dt.afzrms.common.Const;
import com.dt.afzrms.service.AlarmRuleAreaService;
import com.dt.afzrms.util.StringUtil;
import com.dt.afzrms.vo.AlarmruleAreaVo;
import com.dt.afzrms.vo.PageVo;
import com.dt.afzrms.vo.ResultVo;
import com.mapabc.util.json.JsonUtil;

/**
 * @Title AlarmRuleArea Controller
 * @Description TODO
 * @author
 * @createDate 2015年3月27日 上午10:57:28
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */
@Controller
@RequestMapping(value = "/alarmruleArea")
public class AlarmruleAreaController extends BaseController {
	@Autowired
	private AlarmRuleAreaService alarmRuleAreaService;

	@RequestMapping(value = "list")
	@ResponseBody
	public String list(HttpServletRequest request, HttpServletResponse response, Integer start, Integer limit,
			String name) {
		Integer pageSize = limit == null ? Const.PAGE_SIZE : limit;
		Integer pageNo = start == null ? Const.PAGE_NO : (start / pageSize + 1);
		PageVo<AlarmruleAreaVo> findList = alarmRuleAreaService.findList(pageNo, pageSize, name);
		return JsonUtil.convertToJsonStr(findList);
	}

	@RequestMapping(value = "add")
	@ResponseBody
	public String add(HttpServletRequest request, HttpServletResponse response, String name, String areas, String remark) {
		String reStr = Const.RESP_MSG_SUCCESS_ADD;
		Boolean flag = true;

		String createBy = currentUserName(request);
		try {
			alarmRuleAreaService.add(name, areas, remark, createBy);
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
			String areas, String remark) {
		String reStr = Const.RESP_MSG_SUCCESS_UPDATE;
		Boolean flag = true;

		try {
			alarmRuleAreaService.update(id, name, areas, remark);
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
			alarmRuleAreaService.delete(StringUtil.split2IntArray(ids, ","));
		} catch (Exception e) {
			logger.error("delete error", e);
			reStr = Const.RESP_MSG_FAIL_DELETE;
			flag = false;
		}
		return JsonUtil.convertToJsonStr(new ResultVo(flag, reStr));
	}

}
