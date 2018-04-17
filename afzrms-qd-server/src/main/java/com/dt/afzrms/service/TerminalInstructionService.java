package com.dt.afzrms.service;

/**
 * @Title 终端指令业务层接口
 * @Description TODO
 * @author
 * @createDate 2015年3月10日 上午11:14:00
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public interface TerminalInstructionService {
	public String timeInter(String deviceIds, int interval, int count);

	public String saveOverspeedAlarm(String deviceIds, float max, int duration);

	public String saveAreaAlarm(String deviceIds, int alarmruleAreaId);

	public String saveCancleOverspeed(String deviceIds);
	
	public String saveCancleArea(String deviceIds);
	
	public String getStructions(String deviceId);
}
