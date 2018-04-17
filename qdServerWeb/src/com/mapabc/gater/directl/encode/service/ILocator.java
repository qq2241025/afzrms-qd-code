package com.mapabc.gater.directl.encode.service;

import com.mapabc.gater.directl.encode.Request;

public interface ILocator {

	/**
	 * 点名
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String locate(Request req);

	/**
	 * 按时按次
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String timeInter(Request req);

	/**
	 * 按距离按次
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String distanceInter(Request req);

	/**
	 * 定时间点
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String timeLocate(Request req);

}
