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
import com.dt.afzrms.service.UserService;
import com.dt.afzrms.util.StringUtil;
import com.dt.afzrms.vo.PageVo;
import com.dt.afzrms.vo.ResultVo;
import com.dt.afzrms.vo.UserVo;
import com.mapabc.util.json.JsonUtil;

/**
 * @Title user
 * @Description TODO
 * @author
 * @createDate 2015年1月20日 上午10:51:22
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

@Controller
@RequestMapping(value = "/user")
public class UserController extends BaseController {

	@Autowired
	UserService userService;

	@RequestMapping(value = "list")
	@ResponseBody
	public String list(HttpServletRequest request, HttpServletResponse response, Integer start, Integer limit,
			Integer deptId, String account, String name) {
		Integer pageSize = limit == null ? Const.PAGE_SIZE : limit;
		Integer pageNo = start == null ? Const.PAGE_NO : (start / pageSize + 1);
		PageVo<UserVo> findList = userService.findList(pageNo, pageSize, deptId, account, name);
		JSONObject res =  new JSONObject();
		res.put("total", findList.getTotal());
		res.put("data", findList.getData());
		return res.toString();
	}

	@RequestMapping(value = "add")
	@ResponseBody
	public String add(HttpServletRequest request, HttpServletResponse response, String account, String passwd,
			String name, Integer deptId, String contact, String remark, Integer roleId,String groups) {
		String reStr = Const.RESP_MSG_SUCCESS_ADD;
		Boolean flag = true;
		
		List<UserVo> userVos = userService.findByAccount(account);
		int userType = Const.COMMONUSER; //添加普通用户
		Integer[] groudIdlist= StringUtil.split2IntArray(groups, ","); //分组列表
		if (userVos != null && userVos.size() > 0) {
			reStr = "account:'" + account + "'" + Const.RESP_MSG_FAIL_EXIST;
			flag = false;
		} else {
			String createBy = currentUserName(request);
			try {
				userService.add(account, passwd, name, deptId, contact, remark, roleId, createBy,userType,groudIdlist);
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
	public String update(HttpServletRequest request, HttpServletResponse response, Integer id, String account,
			 String name, Integer deptId, String contact, String remark, Boolean isUsed, Integer roleId,String groups) {
		String reStr = Const.RESP_MSG_SUCCESS_UPDATE;
		Boolean flag = true;
		int userType = Const.COMMONUSER; //添加普通用户
		Integer[] groudIdlist= StringUtil.split2IntArray(groups, ","); //分组列表
		ResultVo rese= new ResultVo(flag, reStr);
		int res =userService.update(id, account, name, deptId, contact, remark, roleId,userType,groudIdlist);
		if(res == 2){
			rese = new ResultVo(flag, Const.EDITSYSMSG);
		}
		return JsonUtil.convertToJsonStr(rese);
	}

	@RequestMapping(value = "delete")
	@ResponseBody
	public String delete(HttpServletRequest request, HttpServletResponse response, String ids) {
		String reStr = Const.RESP_MSG_SUCCESS_DELETE;
		Boolean flag = true;
		ResultVo res= new ResultVo(flag, reStr);
		int type = userService.delete(StringUtil.split2IntArray(ids, ","));
		if(type == 2){
			res = new ResultVo(flag, Const.SYSMSG);
		}
		return JsonUtil.convertToJsonStr(res);
	}
}
