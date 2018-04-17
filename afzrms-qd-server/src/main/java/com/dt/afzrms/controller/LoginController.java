package com.dt.afzrms.controller;

import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dt.afzrms.common.Const;
import com.dt.afzrms.service.UserService;
import com.dt.afzrms.singleLogin.Login.HttpLoginUserSession;
import com.dt.afzrms.singleLogin.Login.LoginUserCache;
import com.dt.afzrms.singleLogin.Login.LoginUtils;
import com.dt.afzrms.util.NetUtil;
import com.dt.afzrms.vo.UserVo;

/**
 * @Title login
 * @Description TODO
 * @author
 * @createDate 2015年1月20日 上午10:51:22
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

@Controller
@RequestMapping(value = "/login")
public class LoginController extends BaseController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	UserService userService;

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
	public void login(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			@RequestParam("account") String account, @RequestParam("password") String pwd,
			@RequestParam("validateCode") String validateCode) {
		String msg = null;// 返回信息
		String errorCode = null;// 错误代码
		boolean flag = false; // 结果标识

		String validate_code = (String) session.getAttribute(Const.VALIDATE_CODE);// 验证码
		
//		validate_code = validateCode;
		try {
			if (StringUtils.isNotBlank(validateCode) && validateCode.equalsIgnoreCase(validate_code)) {

				session.removeAttribute(Const.VALIDATE_CODE);// 删除验证码
				writeJsonString(response, this.generateResult(request, response, session, account, pwd));
				return;
			} else {
				errorCode = "1";
				msg = "验证码错误";
			}
		} catch (Exception e) {
			msg = "未知错误";
			errorCode = "-1";
			logger.error("登录错误", e);
		}
		writeJsonString(response, "{\"success\":" + flag + ",\"info\":\"" + msg + "\",\"errorCode\":" + errorCode
				+ ",\"sessionID\":\"" + session.getId() + "\"}");
	}

	private String generateResult(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			String account, String pwd) {
		String msg = null;// 返回信息
		String errorCode = null;// 错误代码
		boolean flag = false; // 结果标识
		JSONObject res = new JSONObject();
		String logcode = "";
		try {
			if (StringUtils.isNotBlank(account) && StringUtils.isNotBlank(pwd)) {

				account = new String(Base64.decodeBase64(account), "utf-8");
				pwd = new String(Base64.decodeBase64(pwd), "utf-8");
				List<UserVo> userVos = userService.findByAccount(account);
				if (userVos != null && userVos.size() == 1) {
					UserVo userVo = userVos.get(0);
					String username = userVo.getAccount();
					String userpass = userVo.getPasswd();
					// 密码md5存储
					// TODO
					if (pwd.equals(userVo.getPasswd())) {
						flag = true;
						session.setAttribute(Const.CURRENT_USER, userVo);// 登录成功，session写入信息
						errorCode = "0";
						msg = "登录成功!";
						res.put("isAdmin", userVo.getUserType() == Const.SYSTEMUSER ? true: false);
						String code = username +"@"+ userpass;
			            logcode= LoginUtils.getEncryptCode(code); //生成登录后的一个唯一code吗
			            boolean  islogin = true; //已经登录
						HttpLoginUserSession logiUser =  new HttpLoginUserSession(session, userVo, logcode,islogin);
						LoginUserCache.addLoginUser(logcode,logiUser);
					}
				}

				if (!flag) {
					errorCode = "3";
					msg = "用户名或密码错误！";
				}
				String currentDatatime = DateFormatUtils.format(Calendar.getInstance(), Const.DATETIME_PATTERN);
				String ipAddress = NetUtil.getRemoteAddress(request);
				logger.info("登录成功， 时间：" + currentDatatime + "，IP：" + ipAddress + "，account：" + account);
			} else {
				errorCode = "2";
				msg = "用户名密码不能为空";
			}
		} catch (Exception e1) {
			msg = "未知错误";
			errorCode = "-1";
			logger.error("登录错误", e1);
		}
		res.put("success", flag);
		res.put("info", msg);
		res.put("errorCode", errorCode);
		res.put("sessionID", logcode);
		return res.toString();

	}
}
