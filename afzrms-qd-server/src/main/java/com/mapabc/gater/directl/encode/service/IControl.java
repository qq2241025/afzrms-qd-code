package com.mapabc.gater.directl.encode.service;

import com.mapabc.gater.directl.encode.Request;

public interface IControl {
	/**
	 * 调度信息
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String msg(Request req);

	/**
	 * 目的地任务
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String destination(Request req);

	/**
	 * 路径任务
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String route(Request req);

	/**
	 * 透传
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String convey(Request req);
 
	/**
	 * 远程控制
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String remoteControl(Request req);

	/**
	 * 油路控制
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String oilElecControl(Request req);

	/**
	 * 锁门控制
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String switchDoorControl(Request req);

	/**
	 * 监听
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String listen(Request req);

	/**
	 * 通话限制
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String callRestrict(Request req);

	/**
	 * 远程下载升级
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String remoteLoading(Request req);

	/**
	 * 休眠设置
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String sleep(Request req);


	/**
	 * 终端复位重启
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String reset(Request req);

}
