/**
 * 
 */
package com.mapabc.gater.directl.parse.service;

import java.net.Socket;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mapabc.gater.directl.parse.ParseBase;

/**
 * 
 * 上行数据解析接口,共提供4种接口
 * 
 */
public interface ParseService {
 	/**
 	 * 获取终端 SOCKET信息
 	 * @return
 	 */
	public Socket getSocket();
	
	/**
	 * 设置终端 SOCKET信息
	 * @param socket
	 */
	public void setSocket(Socket socket);

	/**
	 * TCP或UDP上行信息解析入口,对于解析批次上传数据的可以使用
	 * 
	 * @param moBytes
	 *            设备上行的批量信息
	 * @return 返回ParseBase对象的ArrayList列表
	 * 
	 */
	public abstract ArrayList<ParseBase> parseModata(byte[] moBytes);

	/**
	 * 通过HTTP上行信息的解析入口,对于解析批次上传数据的可以使用
	 * 
	 * @param request
	 *            http上行请求
	 * @param response
	 *            http上行请求的响应
	 * @param cont
	 *            设备上行的批量信息
	 * @return 返回ParseBase对象的ArrayList列表
	 */
	public abstract ArrayList<ParseBase> parseHttpGrps(
			HttpServletRequest request, HttpServletResponse response,
			byte[] cont);

	/**
	 * TCP或UDP上行信息解析入口,单条上报时使用
	 * 
	 * @param moBytes
	 *            设备上行信息
	 * @return 返回ParseBase对象
	 * 
	 */
	public abstract ParseBase parseSingleGprs(byte[] moBytes);

	/**
	 * 通过HTTP上行信息的解析入口,单条上报时使用
	 * 
	 * @param request
	 *            http上行请求
	 * @param response
	 *            http上行请求的响应
	 * @param cont
	 *            设备上行信息
	 * @return 返回ParseBase对象
	 */
	public abstract ParseBase parseSingleHttpGrps(HttpServletRequest request,
			HttpServletResponse response, byte[] cont);

}
