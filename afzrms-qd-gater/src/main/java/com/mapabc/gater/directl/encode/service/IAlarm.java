package com.mapabc.gater.directl.encode.service;

import com.mapabc.gater.directl.encode.Request;

public interface IAlarm {
	/**
	 * 区域报警设置
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 */
	public String areaAlarm(Request req);

	/**
	 * 路线报警设置
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 */
	public String lineAlarm(Request req);

	/**
	 * 超速报警设置
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String overspeedAlarm(Request req);

	/**
	 * 取消区域围栏
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String cancleArea(Request req);

	/**
	 * 取消路线设置
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String cancleLine(Request req);

	/**
	 * 取消报警
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String cancelAlarm(Request req);

	/**
	 * 设置报警参数
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String alarmParams(Request req);

	/**
	 * 报警开关
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String switchAlarm(Request req);

	/**
	 * 区域报警监控设置
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String areaControlSetting(Request req);

	/**
	 * 区域监控设置查看
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String viewAreaControl(Request req);

	/**
	 * 查询围栏点
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String viewAreas(Request req);

	/**
	 * 查看路线监控设置
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String viewLineControl(Request req);

	/**
	 * 线路监控设置
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String lineControlSetting(Request req);

	/**
	 * 查询线路点
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String viewLines(Request req);

	/**
	 * 超时停车设置
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String timeoutStopCarAlarm(Request req);

	/**
	 * 温度报警设置
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String temperatureSetting(Request req);

}
