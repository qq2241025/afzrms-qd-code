package com.dt.afzrms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Title terminal controller
 * @Description TODO
 * @author
 * @createDate 2015-1-25 下午3:45:08
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */
@Controller
@RequestMapping(value = "/driver")
public class TerminalDriverController extends BaseController {
	/**
	@Autowired
	TTerminalDriverService tTerminalDriverService;
  
	@RequestMapping(value = "list")
	@ResponseBody
	public String list(HttpServletRequest request, HttpServletResponse response, Integer start, Integer limit,String tername,String deptName,String driverName) {
		Integer pageSize = limit == null ? Const.PAGE_SIZE : limit;
		Integer pageNo = start == null ? Const.PAGE_NO : (start / pageSize + 1);
		JSONObject rs = new JSONObject();
		int total = 0;
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Page<Map<String, String>> findList;
		try {
			findList = tTerminalDriverService.queryDriverList(pageNo, pageSize, tername, deptName, driverName);
			if(findList!=null){
				list = findList.getResult();
				total = findList.getTotalCount();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		rs.put("total", total);
		rs.put("result", list);
		return rs.toString();
	}
	
	
	
	@RequestMapping(value = "add")
	@ResponseBody
	public String add(HttpServletRequest request, HttpServletResponse response,String deviceId ,String driverName,
			String driverage ,String driverTime,String driverSex ,String driverHeight,String education ,String driverAddress,
			String driverLeval,String driverPhone) {
		JSONObject rs = new JSONObject();
		
		if(StringUtils.isEmpty(driverName)){
			rs.put("msg", "driverName is null ");
			rs.put("success", false);
		}
		if(StringUtils.isEmpty(deviceId)){
			rs.put("msg", "deviceId is null ");
			rs.put("success", false);
		}
		double driverHeights = StringUtils.isEmpty(driverHeight)?0.0:Double.valueOf(driverHeight);
		int driverageInt = StringUtils.isEmpty(driverage)?0:Integer.parseInt(driverage);
		
		TTerminalDriver driver = new TTerminalDriver();
		driver.setDeviceId(deviceId);
		driver.setDriverage(driverageInt);
		driver.setDriverLeval(driverLeval);
		driver.setDriverSex(driverSex);
		driver.setDriverTime(driverTime);
		driver.setDriverAddress(driverAddress);
		driver.setDriverName(driverName);
		driver.setEducation(education);
		driver.setDriverHeight(driverHeights);
		try {
			tTerminalDriverService.addTTerminalDriver(driver);
			rs.put("success", true);
			rs.put("msg", "ok");
		} catch (Exception e) {
			rs.put("success", false);
			rs.put("msg", "has a error");
			e.printStackTrace();
		}
		return rs.toString();
	}
	
	
	@RequestMapping(value = "update")
	@ResponseBody
	public String update(HttpServletRequest request, HttpServletResponse response,int driverId,String deviceId ,String driverName,
			String driverage ,String driverTime,String driverSex ,String driverHeight,String education ,String driverAddress,
			String driverLeval,String driverPhone) {
		JSONObject rs = new JSONObject();
		if(StringUtils.isEmpty(driverName)){
			rs.put("msg", "driverName is null ");
			rs.put("success", false);
			return rs.toString();
		}
		if(StringUtils.isEmpty(deviceId)){
			rs.put("msg", "deviceId is null ");
			rs.put("success", false);
			return rs.toString();
		}
		double driverHeights = StringUtils.isEmpty(driverHeight)?0.0:Double.valueOf(driverHeight);
		int driverageInt = StringUtils.isEmpty(driverage)?0:Integer.parseInt(driverage);
		
		TTerminalDriver driver = new TTerminalDriver();
		driver.setId(driverId);
		driver.setDeviceId(deviceId);
		driver.setDriverage(driverageInt);
		driver.setDriverLeval(driverLeval);
		driver.setDriverSex(driverSex);
		driver.setDriverTime(driverTime);
		driver.setDriverAddress(driverAddress);
		driver.setDriverName(driverName);
		driver.setEducation(education);
		driver.setDriverHeight(driverHeights);
		try {
			tTerminalDriverService.updateTTerminalDriver(driver);
			rs.put("success", true);
			rs.put("msg", "ok");
		} catch (Exception e) {
			rs.put("success", false);
			rs.put("msg", "has a error");
			e.printStackTrace();
		}
		
		return rs.toString();
	}
	
	@RequestMapping(value = "delete")
	@ResponseBody
	public String delete(HttpServletRequest request, HttpServletResponse response,String driverIds ) {
		JSONObject rs = new JSONObject();
		if(StringUtils.isEmpty(driverIds)){
			rs.put("success", false);
			rs.put("msg", "driverIds is not null");
			return rs.toString();
		} else{
			String[] deviceIdlist = driverIds.split("@");
			try {
				tTerminalDriverService.deleteTTerminalDriver(deviceIdlist);
				rs.put("success", true);
				rs.put("msg", "ok");
			} catch (Exception e) {
				rs.put("success", false);
				rs.put("msg", "has a error");
			}
			return rs.toString();
		}
	}
	*/
}
