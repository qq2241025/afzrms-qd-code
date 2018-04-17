package com.dt.afzrms.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dt.afzrms.common.Const;
import com.dt.afzrms.common.ExportExcelUtils;
import com.dt.afzrms.service.LocrecordService;
import com.dt.afzrms.service.TAlarmAntennaService;
import com.dt.afzrms.util.DateUtil;
import com.dt.afzrms.vo.LocrecordVo;
import com.dt.afzrms.vo.PageVo;
import com.dt.afzrms.vo.UserVo;
import com.mapabc.common.dao.hibernate.Page;

/**
 * @Title TODO
 * @Description TODO
 * @author
 * @createDate 2015年2月10日 上午10:52:22
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */
@Controller
@RequestMapping(value = "/locrecord")
public class LocrecordController extends BaseController {
	@Autowired
	LocrecordService locrecordService;
	@Autowired
	TAlarmAntennaService tAlarmAntennaService;

	@InitBinder
	protected void initBinder(HttpServletRequest request,
			ServletRequestDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				Const.DATETIME_PATTERN);
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, null, new CustomDateEditor(
				dateFormat, true));
	}
	
	
	
	
	@RequestMapping(value = "getLastTrackData")
	@ResponseBody
	public String getLastTrackData(HttpServletRequest request,
			HttpServletResponse response, 
			String deviceIds) {
		try {
			if(deviceIds == null){
				return "{\"success\":" + false + ",\"info\": \"deviceIds is not null \"}";
			} else{
				String[] deviceIdlist = deviceIds.split("@");
				if(deviceIdlist!=null && deviceIdlist.length>0){
					String resulr = locrecordService.getLostTrackData(deviceIdlist);
					return resulr;
				}else{
					return "{\"success\":false,\"info\":\"没有找到相应的数据\"}";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			boolean flag = false;
			return "{\"success\":" + flag + ",\"info\":\"异常\"}";
		}
	}
	
	@RequestMapping(value = "getAlarmAntennaList")
	@ResponseBody
	public String getAlarmAntennaList(HttpServletRequest request,
			HttpServletResponse response, Integer start, Integer limit, 
			String deptName,String vehicleName,String startTime,String endTime) {
		UserVo uservo = this.currentUser(request);
		Integer pageSize = limit == null ? Const.PAGE_SIZE : limit;
		Integer pageNo = start == null ? Const.PAGE_NO : (start / pageSize + 1);
		try {
			//时间校验
			if(StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime) ){
				if(!this.isDateYMDHMS(startTime) || !this.isDateYMDHMS(endTime)){
					return this.getErrorResult(Const.MSG_TIME_FORMAT_ERROR).toString();
				}
			}
			if(uservo!=null){
				String resulr = tAlarmAntennaService.queryDriverList(pageNo, pageSize, vehicleName, deptName,startTime,endTime, uservo);
				return resulr;
			}else{
				return "{\"success\":" + false + ",\"info\":\"无法获取用户信息\"}";
			}
		} catch (Exception e) {
			e.printStackTrace();
			boolean flag = false;
			return "{\"success\":" + flag + ",\"info\":\"异常\"}";
		}
	}
	
	
	@RequestMapping(value = "getAllLastTrackData")
	@ResponseBody
	public String getAllLastTrackData(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			String resulr = locrecordService.getAllLastTrackData();
			return resulr;
		} catch (Exception e) {
			e.printStackTrace();
			boolean flag = false;
			return "{\"success\":" + flag + ",\"info\":\"异常\"}";
		}
	}
	
	
	@RequestMapping(value = "getPageLastTrackData")
	@ResponseBody
	public String getAllLastTrackData(HttpServletRequest request,
			HttpServletResponse response,Integer start, Integer limit) {
		Integer pageSize = limit == null ? Const.PAGE_SIZE : limit;
		Integer pageNo = start == null ? Const.PAGE_NO : (start / pageSize + 1);
		try {
			String resulr = locrecordService.getPageLostTrackData(pageNo, pageSize);
			return resulr;
		} catch (Exception e) {
			e.printStackTrace();
			boolean flag = false;
			return "{\"success\":" + flag + ",\"info\":\"异常\"}";
		}
	}
	
	
	@RequestMapping(value = "searchlist")
	@ResponseBody
	public String searchlist(HttpServletRequest request,
			HttpServletResponse response, Integer start, Integer limit,
			String deviceIds, Date beginTime, Date endTime) {
		Integer pageSize = limit == null ? Const.PAGE_SIZE : limit;
		Integer pageNo = start == null ? Const.PAGE_NO : (start / pageSize + 1);
		try {
			if(deviceIds == null || beginTime == null || endTime == null){
				return "{\"success\":" + false + ",\"info\": \"deviceIds is not null \"}";
			} else{
				String[] deviceIdlist = deviceIds.split("@");
				String resulr = locrecordService.findLocrecordList(pageNo, pageSize, deviceIdlist, beginTime, endTime);
				return resulr;
			}
		} catch (Exception e) {
			e.printStackTrace();
			boolean flag = false;
			return "{\"success\":" + flag + ",\"info\":\"异常\"}";
		}
	}
	
	@RequestMapping(value = "searchlistExport")
	public String searchlistExport(HttpServletRequest request,HttpServletResponse response, Integer start, Integer limit,
			String deviceIds, Date beginTime, Date endTime) {
		Integer pageSize = limit == null ? Const.PAGE_SIZE : limit;
		Integer pageNo = start == null ? Const.PAGE_NO : (start / pageSize + 1);
		String beginTimeStr = DateUtil.dateTimeToStr(beginTime, Const.DATETIME_PATTERN);
		String endTimeStr = DateUtil.dateTimeToStr(endTime, Const.DATETIME_PATTERN);
		try {
			if(deviceIds == null || beginTime == null || endTime == null){
				return "{\"success\":" + false + ",\"info\": \"deviceIds is not null \"}";
			} else{
				String[] deviceIdlist = deviceIds.split("@");
				Page<Object[]> resulr = locrecordService.findLocrecordListByPage(pageNo, pageSize, deviceIdlist, beginTime, endTime);
				List<Object[]> list = resulr.getResult();
				
				String[] header =  new String[]{"序号","统计时间段","终端名称","行驶里程(km)","最大速度时间点","最大速度(km/h)","最小速度(km/h)","超速报警次数","区域报警次数"};
				String sheetname = "综合查询统计";
				String xlsFileName = sheetname + ".xls";
				xlsFileName =  new String(xlsFileName.getBytes("gbk"), "ISO-8859-1");
				response.setContentType("application/msexcel");
				response.addHeader("Content-disposition", "attachment;filename="+ xlsFileName);
				ExportExcelUtils.zhongheTongji(sheetname, header, list, beginTimeStr, endTimeStr, response.getOutputStream());
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	//查询车辆轨迹
	@RequestMapping(value = "list")
	@ResponseBody
	public String list(HttpServletRequest request,
			HttpServletResponse response, Integer start, Integer limit,
			String deviceId, Date beginTime, Date endTime) {
		Integer pageSize = limit == null ? Const.PAGE_SIZE : limit;
		Integer pageNo = start == null ? Const.PAGE_NO : (start / pageSize + 1);
		
		String beginTimeStr = DateUtil.dateTimeToStr(beginTime, Const.DATETIME_PATTERN);
		String endTimeStr = DateUtil.dateTimeToStr(endTime, Const.DATETIME_PATTERN);
		if (beginTimeStr == null || endTimeStr == null) {
			boolean flag = false;
			return "{\"success\":" + flag + ",\"info\":\"时间格式错误\"}";
		}
		JSONObject rs = new JSONObject();
		int total = 0;
		List<LocrecordVo> list = new ArrayList<LocrecordVo>();
		PageVo<LocrecordVo> findList = locrecordService.findList(pageNo, pageSize, deviceId,beginTime, endTime);
		if(findList!=null){
			list = findList.getData();
			total = findList.getTotal();
		}
		rs.put("total", total);
		rs.put("data", list);
		return rs.toString();
	}
	
	
	
	
	
	
}