package com.dt.afzrms.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import com.dt.afzrms.service.TjDeptAlarmService;
import com.dt.afzrms.service.TjService;
import com.dt.afzrms.service.TjTermAlarmService;
import com.dt.afzrms.service.TjTermOnofflineService;
import com.dt.afzrms.service.TjTermOperationService;
import com.dt.afzrms.util.DateUtil;
import com.dt.afzrms.vo.AlarmLocrecordVo;
import com.dt.afzrms.vo.PageVo;
import com.dt.afzrms.vo.TjDeptAlarmVo;
import com.dt.afzrms.vo.TjTermAlarmVo;
import com.dt.afzrms.vo.TjTermOnofflineVo;
import com.dt.afzrms.vo.TjTermOperationVo;
import com.mapabc.util.json.JsonUtil;

/**
 * @Title tj controller
 * @Description TODO
 * @author
 * @createDate 2015-3-17 下午16:45:08
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */
@Controller
@RequestMapping(value = "/tj")
public class TjController extends BaseController {
	@Autowired
	TjService tjService;
	@Autowired
	TjTermAlarmService tjTermAlarmService;
	@Autowired
	TjDeptAlarmService tjDeptAlarmService;
	@Autowired
	TjTermOperationService tjTermOperationService;
	@Autowired
	TjTermOnofflineService tjTermOnofflineService;

	@InitBinder
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(Const.DATE_PATTERN);
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, null, new CustomDateEditor(dateFormat, true));
	}

	/**************************************start***********************/
	
	@RequestMapping(value = "overspeedAlarm")
	@ResponseBody
	public String overspeedAlarm(HttpServletRequest request, HttpServletResponse response, Integer start,
			Integer limit, String name, String beginTime, String endTime,String maxSpeed , String minSpeed) {
		Integer pageSize = limit == null ? Const.PAGE_SIZE : limit;
		Integer pageNo = start == null ? Const.PAGE_NO : (start / pageSize + 1);
		
		Date beginTimeStr = DateUtil.strToDateTime(beginTime, Const.DATETIME_PATTERN);
		Date endTimeStr = DateUtil.strToDateTime(endTime, Const.DATETIME_PATTERN);
		
		double minS = Double.valueOf(StringUtils.isEmpty(minSpeed)?"0":minSpeed);
		double maxS = Double.valueOf(StringUtils.isEmpty(maxSpeed)?"0":maxSpeed);
		
		if (beginTimeStr == null || endTimeStr == null) {
			boolean flag = false;
			return "{\"success\":" + flag + ",\"info\":\"时间格式错误\"}";
		}
		JSONObject rs = new JSONObject();
		int total = 0;
		List<AlarmLocrecordVo> list = new ArrayList<AlarmLocrecordVo>();
		PageVo<AlarmLocrecordVo> findList =tjService.findOverspeedAlarmList(pageNo, pageSize, name,
				beginTimeStr,
				endTimeStr,maxS,minS);
		if(findList!=null){
			list = findList.getData();
			total = findList.getTotal();
		}
		rs.put("total", total);
		rs.put("data", list);
		return rs.toString();
	}
	
	//导出超速报警查询【ok】
	@RequestMapping(value = "overspeedAlarmExport")
	public String overspeedAlarmExport(HttpServletRequest request, HttpServletResponse response, Integer start,
			Integer limit, String name, String beginTime, String endTime,String maxSpeed , String minSpeed) {
		Integer pageSize = limit == null ? Const.PAGE_SIZE : limit;
		Integer pageNo = start == null ? Const.PAGE_NO : (start / pageSize + 1);
		
		PageVo<AlarmLocrecordVo> findList;
		double minS = Double.valueOf(StringUtils.isEmpty(minSpeed)?"0":minSpeed);
		double maxS = Double.valueOf(StringUtils.isEmpty(maxSpeed)?"0":maxSpeed);
		try {
			findList = tjService.findOverspeedAlarmList(pageNo, pageSize, name,
					DateUtil.strToDateTime(beginTime, Const.DATETIME_PATTERN),
					DateUtil.strToDateTime(endTime, Const.DATETIME_PATTERN),maxS,minS);
			 List<AlarmLocrecordVo> resultList = findList.getData();
			 String[] header =  new String[]{"序号","车牌号","设备序列号","所属部门","SIM号码","报警时间","行驶车速【km/h】","超速阀值【km/h】","行驶方向"};
			 String sheetname = "超速报警查询";
			 String xlsFileName = sheetname + ".xls";
			 xlsFileName =  new String(xlsFileName.getBytes("gbk"), "ISO-8859-1");
			 response.setContentType("application/msexcel");
			 response.addHeader("Content-disposition", "attachment;filename="+ xlsFileName);
			 ExportExcelUtils.exportOverspeedAlarm(sheetname, header, resultList, response.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**************************************end***********************/
	
	
	
	
	
	//查询一辆车报警查询【ok】
	@RequestMapping(value = "alarmOnce")
	@ResponseBody
	public String alarmOnces(HttpServletRequest request, HttpServletResponse response, Integer start,
			Integer limit, String deviceId, String queryTime,String maxSpeed , String minSpeed,String deptName,String startDate,String endDate) {
		Integer pageSize = limit == null ? Const.PAGE_SIZE : limit;
		Integer pageNo = start == null ? Const.PAGE_NO : (start / pageSize + 1);
		
		double minS = Double.valueOf(StringUtils.isEmpty(minSpeed)?"0":minSpeed);
		double maxS = Double.valueOf(StringUtils.isEmpty(maxSpeed)?"0":maxSpeed);
		
		String newStartTime = "",newendTime = "";
		if(StringUtils.isNotEmpty(startDate ) && StringUtils.isNotEmpty(endDate)){
			newStartTime  = queryTime + "  "+ startDate +":00";
			newendTime    = queryTime + "  "+ endDate +":00";
		}else if(StringUtils.isEmpty(startDate ) && StringUtils.isNotEmpty(endDate )){
			newStartTime  = queryTime + "  00:00:00";
			newendTime    = queryTime + "  "+ endDate +":00";
		}else if(StringUtils.isNotEmpty(startDate ) && StringUtils.isEmpty(endDate )){
			newStartTime  = queryTime + "  "+ startDate +":00";
			newendTime    = queryTime + "  23:59:00";
		}else {
			newStartTime  = queryTime + "  00:00:00";
			newendTime    = queryTime + "  23:59:00";
		}
		Date beginTimeDate = DateUtil.strToDateTime(newStartTime, Const.DATETIME_PATTERN);
		Date endTimeDate = DateUtil.strToDateTime(newendTime, Const.DATETIME_PATTERN);
		if (beginTimeDate == null && endTimeDate == null) {
			boolean flag = false;
			return "{\"success\":" + flag + ",\"info\":\"时间格式错误\"}";
		}
	    
		JSONObject rs = new JSONObject();
		int total = 0;
		List<AlarmLocrecordVo> list = new ArrayList<AlarmLocrecordVo>();
		PageVo<AlarmLocrecordVo> findList =tjService.findAlarmOnceList(pageNo, pageSize, deviceId,deptName,
				beginTimeDate,endTimeDate,minS,maxS);
		if(findList!=null){
			list = findList.getData();
			total = findList.getTotal();
		}
		rs.put("total", total);
		rs.put("data", list);
		return rs.toString();
	}
	
	//导出一辆车报警查询【ok】
	@RequestMapping(value = "exportAlarmOnce")
	public String exportAlarmOnces(HttpServletRequest request, HttpServletResponse response, Integer start,
			Integer limit, String deviceId, String queryTime,String maxSpeed , String minSpeed,String deptName,String startDate,String endDate) {
		Integer pageSize = limit == null ? Const.PAGE_SIZE : limit;
		Integer pageNo = start == null ? Const.PAGE_NO : (start / pageSize + 1);
		
		PageVo<AlarmLocrecordVo> findList;
		
		double minS = Double.valueOf(StringUtils.isEmpty(minSpeed)?"0":minSpeed);
		double maxS = Double.valueOf(StringUtils.isEmpty(maxSpeed)?"0":maxSpeed);
		
		String newStartTime = "",newendTime = "";
		if(StringUtils.isEmpty(startDate ) && StringUtils.isEmpty(endDate)){
			newStartTime  = queryTime + "  "+ startDate +":00";
			newendTime    = queryTime + "  "+ endDate +":00";
		}else{
			newStartTime  = queryTime + "  00:00:00";
			newendTime    = queryTime + "  23:59:00";
		}
		Date beginTimeDate = DateUtil.strToDateTime(newStartTime, Const.DATETIME_PATTERN);
		Date endTimeDate = DateUtil.strToDateTime(newendTime, Const.DATETIME_PATTERN);
		if (beginTimeDate == null && endTimeDate == null) {
			boolean flag = false;
			return "{\"success\":" + flag + ",\"info\":\"时间格式错误\"}";
		}
		
		try {
			findList =tjService.findAlarmOnceList(pageNo, pageSize, deviceId,deptName,
					beginTimeDate,endTimeDate,minS,maxS);
			 List<AlarmLocrecordVo> resultList = findList.getData();
			 String[] header =  new String[]{"序号","车牌号","设备序列号","所属部门","SIM号码","报警时间","行驶车速【km/h】","超速阀值【km/h】","行驶方向"};
			 String sheetname = "超速报警查询";
			 String xlsFileName = sheetname + ".xls";
			 xlsFileName =  new String(xlsFileName.getBytes("gbk"), "ISO-8859-1");
			 response.setContentType("application/msexcel");
			 response.addHeader("Content-disposition", "attachment;filename="+ xlsFileName);
			 ExportExcelUtils.exportOverspeedAlarm(sheetname, header, resultList, response.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	
	/**************************************start***********************/
	@RequestMapping(value = "areaAlarm")
	@ResponseBody
	public String areaAlarm(HttpServletRequest request, HttpServletResponse response, Integer start, Integer limit,
			String name, String alarmSubType, String beginTime, String endTime) {
		Integer pageSize = limit == null ? Const.PAGE_SIZE : limit;
		Integer pageNo = start == null ? Const.PAGE_NO : (start / pageSize + 1);
		PageVo<AlarmLocrecordVo> findList;
		try {
			findList = tjService.findAreaAlarmList(pageNo, pageSize, name, alarmSubType,
					DateUtil.strToDateTime(beginTime, Const.DATETIME_PATTERN),
					DateUtil.strToDateTime(endTime, Const.DATETIME_PATTERN));
			return JsonUtil.convertToJsonStr(findList);
		} catch (Exception e) {
			// TODO
			boolean flag = false;
			String msg = e.getMessage();
			return "{\"success\":" + flag + ",\"info\":\"" + msg + "\"}";
		}
	}
	
	
	
	//导出区域报警查询
	@RequestMapping(value = "areaAlarmExport")
	public String areaAlarmExport(HttpServletRequest request, HttpServletResponse response, Integer start, Integer limit,
			String name, String alarmSubType, String beginTime, String endTime) {
		Integer pageSize = limit == null ? Const.PAGE_SIZE : limit;
		Integer pageNo = start == null ? Const.PAGE_NO : (start / pageSize + 1);
		PageVo<AlarmLocrecordVo> findList;
		try {
			findList = tjService.findAreaAlarmList(pageNo, pageSize, name, alarmSubType,
					DateUtil.strToDateTime(beginTime, Const.DATETIME_PATTERN),
					DateUtil.strToDateTime(endTime, Const.DATETIME_PATTERN));
			 List<AlarmLocrecordVo> resultList = findList.getData();
			 String[] header =  new String[]{"序号","车牌号","所属部门","SIM号码", "报警时间", "行驶车速【km/h】","区域类型","行驶方向"};
			 String sheetname = "区域报警查询";
			 String xlsFileName = sheetname + ".xls";
			 xlsFileName =  new String(xlsFileName.getBytes("gbk"), "ISO-8859-1");
			 response.setContentType("application/msexcel");
			 response.addHeader("Content-disposition", "attachment;filename="+ xlsFileName);
			 ExportExcelUtils.exportAreaAlarm(sheetname, header, resultList, response.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**************************************end***********************/
	
	/**************************************start***********************/
	@RequestMapping(value = "termAlarm")
	@ResponseBody
	public String termAlarm(HttpServletRequest request, HttpServletResponse response, Integer start, Integer limit,
			String name, Date beginTime, Date endTime,String deptName) {
		Integer pageSize = limit == null ? Const.PAGE_SIZE : limit;
		Integer pageNo = start == null ? Const.PAGE_NO : (start / pageSize + 1);
		PageVo<TjTermAlarmVo> findList;
		try {
			findList = tjTermAlarmService.findList(pageNo, pageSize, beginTime, endTime, name,deptName);
			return JsonUtil.convertToJsonStr(findList);
		} catch (Exception e) {
			boolean flag = false;
			String msg = e.getMessage();
			return "{\"success\":" + flag + ",\"info\":\"" + msg + "\"}";
		}
	}
	
    //导出车辆违章统计
	@RequestMapping(value = "termAlarmExport")
	public String termAlarmExport(HttpServletRequest request, HttpServletResponse response, Integer start, Integer limit,
			String name, Date beginTime, Date endTime,String deptName) {
		Integer pageSize = limit == null ? Const.PAGE_SIZE : limit;
		Integer pageNo = start == null ? Const.PAGE_NO : (start / pageSize + 1);
		PageVo<TjTermAlarmVo> findList;
		try {
			findList = tjTermAlarmService.findList(pageNo, pageSize, beginTime, endTime, name,deptName);
			 List<TjTermAlarmVo> resultList = findList.getData();
			 String[] header =  new String[]{"序号","统计时间","车牌号","区域报警次数","超速报警次数", "区域超速报警次数"};
			 String sheetname = "车辆违章统计";
			 String xlsFileName = sheetname + ".xls";
			 xlsFileName =  new String(xlsFileName.getBytes("gbk"), "ISO-8859-1");
			 response.setContentType("application/msexcel");
			 response.addHeader("Content-disposition", "attachment;filename="+ xlsFileName);
			 ExportExcelUtils.exportTermAlarm(sheetname, header, resultList, response.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**************************************end***********************/
	
	
	
	/**************************************start***********************/

	@RequestMapping(value = "deptAlarm")
	@ResponseBody
	public String deptAlarm(HttpServletRequest request, HttpServletResponse response, Integer start, Integer limit,
			String name, Date beginTime, Date endTime) {
		Integer pageSize = limit == null ? Const.PAGE_SIZE : limit;
		Integer pageNo = start == null ? Const.PAGE_NO : (start / pageSize + 1);
		PageVo<TjDeptAlarmVo> findList;
		try {
			findList = tjDeptAlarmService.findList(pageNo, pageSize, beginTime, endTime, name);
			return JsonUtil.convertToJsonStr(findList);
		} catch (Exception e) {
			// TODO
			boolean flag = false;
			String msg = e.getMessage();
			return "{\"success\":" + flag + ",\"info\":\"" + msg + "\"}";
		}
	}
	
	//导出部门统计
	@RequestMapping(value = "deptAlarmExport")
	public String deptAlarmExport(HttpServletRequest request, HttpServletResponse response, Integer start, Integer limit,
			String name, Date beginTime, Date endTime) {
		Integer pageSize = limit == null ? Const.PAGE_SIZE : limit;
		Integer pageNo = start == null ? Const.PAGE_NO : (start / pageSize + 1);
		PageVo<TjDeptAlarmVo> findList;
		try {
			 findList = tjDeptAlarmService.findList(pageNo, pageSize, beginTime, endTime, name);
			 List<TjDeptAlarmVo> resultList = findList.getData();
			 String[] header =  new String[]{"序号","统计时间","所属部门","区域报警次数","超速报警次数", "区域超速报警次数"};
			 String sheetname = "部门违章统计";
			 String xlsFileName = sheetname + ".xls";
			 xlsFileName =  new String(xlsFileName.getBytes("gbk"), "ISO-8859-1");
			 response.setContentType("application/msexcel");
			 response.addHeader("Content-disposition", "attachment;filename="+ xlsFileName);
			 ExportExcelUtils.exportDeptExcel(sheetname, header, resultList, response.getOutputStream());
		} catch (Exception e) {
			boolean flag = false;
			String msg = e.getMessage();
			return "{\"success\":" + flag + ",\"info\":\"" + msg + "\"}";
		}
		return null;
	}
	/**************************************end***********************/
	
	/**************************************start***********************/

	@RequestMapping(value = "termOperation")
	@ResponseBody
	public String termOperation(HttpServletRequest request, HttpServletResponse response, Integer start, Integer limit,
			String name, Date beginTime, Date endTime) {
		Integer pageSize = limit == null ? Const.PAGE_SIZE : limit;
		Integer pageNo = start == null ? Const.PAGE_NO : (start / pageSize + 1);
		PageVo<TjTermOperationVo> findList;
		try {
			findList = tjTermOperationService.findList(pageNo, pageSize, beginTime, endTime, name);
			return JsonUtil.convertToJsonStr(findList);
		} catch (Exception e) {
			// TODO
			boolean flag = false;
			String msg = e.getMessage();
			return "{\"success\":" + flag + ",\"info\":\"" + msg + "\"}";
		}
	}
	
	//导出车辆运行统计【ok】
	@RequestMapping(value = "termOperationExport")
	public String termOperationExport(HttpServletRequest request, HttpServletResponse response, Integer start, Integer limit,
			String name, Date beginTime, Date endTime) {
		Integer pageSize = limit == null ? Const.PAGE_SIZE : limit;
		Integer pageNo = start == null ? Const.PAGE_NO : (start / pageSize + 1);
		PageVo<TjTermOperationVo> findList;
		try {
			findList = tjTermOperationService.findList(pageNo, pageSize, beginTime, endTime, name);
			 List<TjTermOperationVo> resultList = findList.getData();
			 String[] header =  new String[]{"序号","统计时间","车牌号","行驶有效时间","行驶里程", "最大速度时间点", "最大速度", "平均速度"};
			 String sheetname = "车辆运行统计";
			 String xlsFileName = sheetname + ".xls";
			 xlsFileName =  new String(xlsFileName.getBytes("gbk"), "ISO-8859-1");
			 response.setContentType("application/msexcel");
			 response.addHeader("Content-disposition", "attachment;filename="+ xlsFileName);
			 ExportExcelUtils.exportTermOperation(sheetname, header, resultList, response.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**************************************end***********************/
	
	/**************************************start***********************/
	@RequestMapping(value = "termOperationCustom")
	@ResponseBody
	public String termOperationCustom(HttpServletRequest request, HttpServletResponse response, Integer start,
			Integer limit, String name, Date beginTime, Date endTime) {
		Integer pageSize = limit == null ? Const.PAGE_SIZE : limit;
		Integer pageNo = start == null ? Const.PAGE_NO : (start / pageSize + 1);
		PageVo<TjTermOperationVo> findList;
		try {
			findList = tjTermOperationService.findList(pageNo, pageSize, beginTime, endTime, name);
			return JsonUtil.convertToJsonStr(findList);
		} catch (Exception e) {
			// TODO
			boolean flag = false;
			String msg = e.getMessage();
			return "{\"success\":" + flag + ",\"info\":\"" + msg + "\"}";
		}
	}
	
	
//	//导出部门统计
//	@RequestMapping(value = "termOperationCustomExport")
//	@ResponseBody
//	public String termOperationCustomExport(HttpServletRequest request, HttpServletResponse response, Integer start,
//			Integer limit, String name, Date beginTime, Date endTime) {
//		Integer pageSize = limit == null ? Const.PAGE_SIZE : limit;
//		Integer pageNo = start == null ? Const.PAGE_NO : (start / pageSize + 1);
//		PageVo<TjDeptAlarmVo> findList;
//		try {
//			 findList = tjDeptAlarmService.findList(pageNo, pageSize, beginTime, endTime, name);
//			 List<TjDeptAlarmVo> resultList = findList.getData();
//			 String[] header =  new String[]{"序号","统计时间","所属部门","区域报警次数","超速报警次数", "区域超速报警次数"};
//			 String sheetname = "部门违章统计";
//			 String xlsFileName = sheetname + ".xls";
//			 xlsFileName =  new String(xlsFileName.getBytes("gbk"), "ISO-8859-1");
//			 response.setContentType("application/msexcel");
//			 response.addHeader("Content-disposition", "attachment;filename="+ xlsFileName);
//			 ExportExcelUtils.exportDeptExcel(sheetname, header, resultList, response.getOutputStream());
//		} catch (Exception e) {
//			boolean flag = false;
//			String msg = e.getMessage();
//			return "{\"success\":" + flag + ",\"info\":\"" + msg + "\"}";
//		}
//		return null;
//	}
	
	/**************************************start***********************/
	@RequestMapping(value = "termOnoffline")
	@ResponseBody
	public String termOnoffline(HttpServletRequest request, HttpServletResponse response, Integer start, Integer limit,
			Date beginTime, Date endTime) {
		Integer pageSize = limit == null ? Const.PAGE_SIZE : limit;
		Integer pageNo = start == null ? Const.PAGE_NO : (start / pageSize + 1);
		PageVo<TjTermOnofflineVo> findList;
		try {
			findList = tjTermOnofflineService.findList(pageNo, pageSize, beginTime, endTime);
			return JsonUtil.convertToJsonStr(findList);
		} catch (Exception e) {
			// TODO
			boolean flag = false;
			String msg = e.getMessage();
			return "{\"success\":" + flag + ",\"info\":\"" + msg + "\"}";
		}
	}
	
	//导出车辆在线离线统计【ok】
	@RequestMapping(value = "termOnofflineExport")
	public String termOnofflineExport(HttpServletRequest request, HttpServletResponse response, Integer start, Integer limit,
			Date beginTime, Date endTime) {
		Integer pageSize = limit == null ? Const.PAGE_SIZE : limit;
		Integer pageNo = start == null ? Const.PAGE_NO : (start / pageSize + 1);
		PageVo<TjTermOnofflineVo> findList;
		try {
			 findList = tjTermOnofflineService.findList(pageNo, pageSize, beginTime, endTime);
			 List<TjTermOnofflineVo> resultList = findList.getData();
			 String[] header =  new String[]{"序号","统计时间","离线车辆","在线车辆","离线率数"};
			 String sheetname = "车辆在线离线统计";
			 String xlsFileName = sheetname + ".xls";
			 xlsFileName =  new String(xlsFileName.getBytes("gbk"), "ISO-8859-1");
			 response.setContentType("application/msexcel");
			 response.addHeader("Content-disposition", "attachment;filename="+ xlsFileName);
			 ExportExcelUtils.exportTermOnoffline(sheetname, header, resultList, response.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**************************************end***********************/
}
