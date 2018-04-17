
package com.dt.afzrms.controller;

import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.dt.afzrms.common.Const;
import com.dt.afzrms.po.TMarkers;
import com.dt.afzrms.service.TMarkersService;
import com.dt.afzrms.util.StringUtil;
import com.dt.afzrms.vo.UserVo;

/**
 * @Title TODO
 * @Description TODO
 * @author 
 * @createDate 2016年9月24日 下午10:15:23
 * @modifier 
 * @modifyDate 
 * @version 1.0
 * 
 */

@Controller
@RequestMapping(value = "/marker")
public class TMarkersController extends BaseController {
	@Autowired
	TMarkersService tMarkersService;

	@RequestMapping(value = "list")
	@ResponseBody
	public String list(HttpServletRequest request, HttpServletResponse response, String  poiName,Integer limit,Integer start) {
		Integer pageSize = limit == null ? Const.PAGE_SIZE : limit;
		Integer pageNo = start == null ? Const.PAGE_NO : (start / pageSize + 1);
		UserVo user = this.getLoginUser(request);
		String res = tMarkersService.findList(pageNo, pageSize, poiName, user);
		return res;
	}
	

	@RequestMapping(value = "add")
	@ResponseBody
	public String add(HttpServletRequest request, HttpServletResponse response,  String name,String lat,String lng,String desc) {
		String reStr = Const.RESP_MSG_SUCCESS_ADD;
		JSONObject res = new JSONObject();
		try {
			if(StringUtils.isEmpty(lat) ||  StringUtils.isEmpty(lng)){
				res.put("success", false);
				res.put("msg", "lng and lat is not null");
				return res.toString();
			}
			double x= Double.valueOf(lng);
			double y= Double.valueOf(lat);
			UserVo user = this.getLoginUser(request);
			Integer userId = user.getId();
			TMarkers marker = new TMarkers();
			marker.setUserId(userId);
			marker.setX(x);
			marker.setY(y);
			marker.setStatus(1);
			marker.setName(name);
			marker.setRemarker(desc);
			marker.setCreateTime(new Date());
			tMarkersService.add(marker);
		} catch (Exception e) {
			res.put("success", false);
			res.put("msg", Const.RESP_MSG_FAIL_ADD);
			return res.toString();
		}
		res.put("success", true);
		res.put("msg", reStr);
		return res.toString();
	}

	@RequestMapping(value = "update")
	@ResponseBody
	public String update(HttpServletRequest request, HttpServletResponse response,String id,String name,String lat,String lng,String desc) {
		String reStr = Const.RESP_MSG_SUCCESS_UPDATE;
		JSONObject res = new JSONObject();
		try {
			if(StringUtils.isEmpty(id) ){
				res.put("success", false);
				res.put("msg", "id not null");
				return res.toString();
			}
			Integer ids = Integer.parseInt(id);
			if(StringUtils.isEmpty(lat) ||  StringUtils.isEmpty(lng)){
				res.put("success", false);
				res.put("msg", "lng and lat is not null");
				return res.toString();
			}
			double x= Double.valueOf(lng);
			double y= Double.valueOf(lat);
			UserVo user = this.getLoginUser(request);
			Integer userId = user.getId();
			TMarkers marker = new TMarkers();
			marker.setUserId(userId);
			marker.setId(ids);
			marker.setX(x);
			marker.setStatus(1);
			marker.setY(y);
			marker.setName(name);
			marker.setCreateTime(new Date());
			marker.setRemarker(desc);
			tMarkersService.update(marker);
		} catch (Exception e) {
			res.put("success", false);
			res.put("msg", Const.RESP_MSG_FAIL_UPDATE);
			return res.toString();
		}
		res.put("success", true);
		res.put("msg", reStr);
		return res.toString();
	}

	@RequestMapping(value = "delete")
	@ResponseBody
	public String delete(HttpServletRequest request, HttpServletResponse response, String ids) {
		String reStr = Const.RESP_MSG_SUCCESS_DELETE;
		JSONObject res = new JSONObject();
		try {
			if(StringUtils.isEmpty(ids) ){
				res.put("success", false);
				res.put("msg", "ids not null");
				return res.toString();
			}
			tMarkersService.delete(StringUtil.split2StringArray(ids, ","));
		} catch (Exception e) {
			logger.error("delete error", e);
			res.put("success", false);
			res.put("msg", Const.RESP_MSG_FAIL_DELETE);
			return res.toString();
		}
		res.put("success", true);
		res.put("msg", reStr);
		return res.toString();
	}
	
}

