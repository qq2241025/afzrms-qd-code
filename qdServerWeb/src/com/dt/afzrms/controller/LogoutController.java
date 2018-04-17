package com.dt.afzrms.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dt.afzrms.common.Const;

/**
 * @Title logout
 * @Description TODO
 * @author
 * @createDate 2015年1月20日 上午10:51:22
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

@Controller
@RequestMapping(value = "/logout")
public class LogoutController extends BaseController {

	/**
	 * 登陆系统
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @param areaId
	 * @param pwd
	 * @param validateCode
	 * 
	 */
	@RequestMapping(value = "")
	public void logout(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		boolean flag = true; // 结果标识
		session.removeAttribute(Const.CURRENT_USER);
		writeJsonString(response, "{\"success\":" + flag + "}");
	}
}
