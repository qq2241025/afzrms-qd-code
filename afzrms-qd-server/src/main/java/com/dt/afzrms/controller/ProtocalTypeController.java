package com.dt.afzrms.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dt.afzrms.common.Const;
import com.dt.afzrms.service.ProtocalTypeService;
import com.dt.afzrms.vo.PageVo;
import com.dt.afzrms.vo.ProtocalTypeVo;
import com.mapabc.util.json.JsonUtil;

/**
 * @Title ProtocalType controller
 * @Description TODO
 * @author
 * @createDate 2015-1-25 下午3:45:08
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */
@Controller
@RequestMapping(value = "/protocalType")
public class ProtocalTypeController extends BaseController {
	@Autowired
	ProtocalTypeService protocalTypeService;

	@RequestMapping(value = "list")
	@ResponseBody
	public String list(HttpServletRequest request, HttpServletResponse response, Integer start, Integer limit,
			Integer deptId, String account, String name) {
		Integer pageSize = limit == null ? Const.PAGE_SIZE : limit;
		Integer pageNo = start == null ? Const.PAGE_NO : (start / pageSize + 1);
		PageVo<ProtocalTypeVo> findList = protocalTypeService.findList(pageNo, pageSize, name);
		return JsonUtil.convertToJsonStr(findList);
	}

}
