/**
 * 
 */
package com.mapabc.gater.jms.service;

import java.util.ArrayList;

import javax.jms.Connection;

/**
 * @author 
 *
 */
public interface JmsTransmitService {

	/**
	 * 发送单条信息到JMS
	 * 
	 * @param connection
	 *            JMS连接
	 * @param subject
	 *            JMS主题
	 * @param pb
	 *            发送的信息内容
	 * @return
	 * @author 
	 */
	public abstract boolean sendSingleMessage(Connection connection, String subject, String pb);

	/**
	 * 批量发送消息
	 * 
	 * @param connection
	 *            JMS连接
	 * @param subject
	 *            JMS主题
	 * @param pbList
	 *            消息列表
	 * @return
	 * @author 
	 */
	public abstract boolean sendMoreMessage(Connection connection, String subject, ArrayList<String> pbList);

	/**
	 * 发送单条信息到JMS
	 * 
	 * @param subject
	 *            主题
	 * @param content
	 *            内容
	 * @return
	 * @author 
	 */
	public abstract boolean sendSingleToJms(String subject, String content);

	/**
	 * 批量发送消息到JMS
	 * 
	 * @param subject
	 *            主题
	 * @param pbList
	 *            消息列表
	 * @return
	 * @author 
	 */
	public boolean sendMoreToJms(String subject, ArrayList<String> pbList);
}
