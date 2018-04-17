package com.dt.afzrms.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dt.afzrms.common.Const;
import com.dt.afzrms.service.TerminalInstructionService;
import com.dt.afzrms.vo.ResultVo;
import com.mapabc.util.json.JsonUtil;

/**
 * @Title terminal instruction controller
 * @Description TODO
 * @author
 * @createDate 2015-3-10 上午10:45:08
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */
@Controller
@RequestMapping(value = "/terminalInstruction")
public class TerminalInstructionController extends BaseController {
	@Autowired
	TerminalInstructionService terminalInstructionService;

	@RequestMapping(value = "timeInter")
	@ResponseBody
	public String timeInter(HttpServletRequest request, HttpServletResponse response, String deviceIds, int interval) {
		String reStr = Const.RESP_MSG_FAIL_TERMINALINSTRUCTION_SETUP;
		Boolean flag = false;
		try {
			String resp = terminalInstructionService.timeInter(deviceIds, interval, 0);
			if (!StringUtils.isEmpty(resp)) {
				flag = true;
				reStr = Const.RESP_MSG_SUCCESS_TERMINALINSTRUCTION_SETUP;
			} else {
				logger.error("set up TerminalInstruction error");
			}
		} catch (Exception e) {
			logger.error("set up TerminalInstruction error", e);
		}
		return JsonUtil.convertToJsonStr(new ResultVo(flag, reStr));
	}

	@RequestMapping(value = "overspeedAlarm")
	@ResponseBody
	public String overspeedAlarm(HttpServletRequest request, HttpServletResponse response, String deviceIds, float max) {
		String reStr = Const.RESP_MSG_FAIL_TERMINALINSTRUCTION_SETUP;
		Boolean flag = false;
		try {
			String resp = terminalInstructionService.saveOverspeedAlarm(deviceIds, max, 0);
			if (!StringUtils.isEmpty(resp)) {
				flag = true;
				reStr = Const.RESP_MSG_SUCCESS_TERMINALINSTRUCTION_SETUP;
			} else {
				logger.error("set up TerminalInstruction error");
			}
		} catch (Exception e) {
			logger.error("set up TerminalInstruction error", e);
		}
		return JsonUtil.convertToJsonStr(new ResultVo(flag, reStr));
	}

	@RequestMapping(value = "areaAlarm")
	@ResponseBody
	public String areaAlarm(HttpServletRequest request, HttpServletResponse response, String deviceIds,
			int alarmruleAreaId) {
		String reStr = Const.RESP_MSG_FAIL_TERMINALINSTRUCTION_SETUP;
		Boolean flag = false;
		try {
			String resp = terminalInstructionService.saveAreaAlarm(deviceIds, alarmruleAreaId);
			if (!StringUtils.isEmpty(resp)) {
				flag = true;
				reStr = Const.RESP_MSG_SUCCESS_TERMINALINSTRUCTION_SETUP;
			} else {
				logger.error("set up TerminalInstruction error");
			}
		} catch (Exception e) {
			logger.error("set up TerminalInstruction error", e);
		}
		return JsonUtil.convertToJsonStr(new ResultVo(flag, reStr));
	}

	@RequestMapping(value = "cancleOverspeed")
	@ResponseBody
	public String cancleOverspeed(HttpServletRequest request, HttpServletResponse response, String deviceIds) {
		String reStr = Const.RESP_MSG_FAIL_TERMINALINSTRUCTION_SETUP;
		Boolean flag = false;
		try {
			String resp = terminalInstructionService.saveCancleOverspeed(deviceIds);
			if (!StringUtils.isEmpty(resp)) {
				flag = true;
				reStr = Const.RESP_MSG_SUCCESS_TERMINALINSTRUCTION_SETUP;
			} else {
				logger.error("set up TerminalInstruction error");
			}
		} catch (Exception e) {
			logger.error("set up TerminalInstruction error", e);
		}
		return JsonUtil.convertToJsonStr(new ResultVo(flag, reStr));
	}

	@RequestMapping(value = "cancleArea")
	@ResponseBody
	public String cancleArea(HttpServletRequest request, HttpServletResponse response, String deviceIds) {
		String reStr = Const.RESP_MSG_FAIL_TERMINALINSTRUCTION_SETUP;
		Boolean flag = false;
		try {
			String resp = terminalInstructionService.saveCancleArea(deviceIds);
			if (!StringUtils.isEmpty(resp)) {
				flag = true;
				reStr = Const.RESP_MSG_SUCCESS_TERMINALINSTRUCTION_SETUP;
			} else {
				logger.error("set up TerminalInstruction error");
			}
		} catch (Exception e) {
			logger.error("set up TerminalInstruction error", e);
		}
		return JsonUtil.convertToJsonStr(new ResultVo(flag, reStr));
	}

	@RequestMapping(value = "getStructions")
	@ResponseBody
	public String getStructions(HttpServletRequest request, HttpServletResponse response, String deviceId) {
		logger.info("request getStructions,deviceId=" + deviceId);
		String re = terminalInstructionService.getStructions(deviceId);
		logger.info("response getStructions,deviceId=" + deviceId + ",return=" + re);
		return re;
	}
}
