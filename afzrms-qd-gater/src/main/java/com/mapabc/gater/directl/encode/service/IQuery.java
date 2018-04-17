/**
 * 
 */
package com.mapabc.gater.directl.encode.service;

import com.mapabc.gater.directl.encode.Request;

/**
 * @author 
 * 
 */
public interface IQuery {

	/**
	 * 查询车机状态
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public abstract String workStatus(Request req);

	/**
	 * 查询里程
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public abstract String findDistances(Request req);

 

	/**
	 * 基本参数查询
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public abstract String queryParam(Request req);

}
