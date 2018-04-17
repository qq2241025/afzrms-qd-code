/**
 * 
 */
package com.mapabc.gater.jms.service.impl;

import java.util.ArrayList;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.command.ActiveMQTopic;
import org.apache.commons.logging.LogFactory;

import com.mapabc.gater.jms.GPSForwardToJMS;
import com.mapabc.gater.jms.service.JmsTransmitService;
 

/**
 * @author shiguang.zhou
 * 
 */
public class JmsTransmitServiceImpl implements JmsTransmitService {
	private static org.apache.commons.logging.Log log=LogFactory.getLog(JmsTransmitServiceImpl.class);

	private boolean transacted = false;

	public boolean sendMoreMessage(Connection connection, String subject,
			ArrayList<String> pbList) {
		boolean ret = false;
		// 检查链接
		if (connection == null) {
			return false;
		}
		Session session = null;
		MessageProducer producer = null;

		try {
			// 创建一个Topic
			Topic topic = new ActiveMQTopic(subject);
			session = connection.createSession(transacted,
					Session.AUTO_ACKNOWLEDGE);

			// 创建一个生产者，然后发送多个消息。
			producer = session.createProducer(topic);
		 
			BytesMessage message = session.createBytesMessage();
			
			for (int i = 0; i < pbList.size(); i++) {
				String pb =  pbList.get(i);
				try{
				message.writeBytes(pb.getBytes());
				producer.send(message);
				log.info("多位置信息转发到JMS:" + pb);
				}catch(Exception e){
					log.error("发送数据到JMS异常:"+pb, e);
					continue;
				}
			}
	 			
			ret = true;
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			log.error("JMS发送异常：" ,e);
			e.printStackTrace();
			ret = false;
		} finally {

			try {
				if (producer != null) {
					producer.close();
					producer = null;
				}
				if (session != null) {
					session.close();
					session = null;
				}
				if (connection != null) {
					connection.close();
					connection = null;
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}

		}

		return ret;
	}

	public boolean sendSingleMessage(Connection connection, String subject,
			String pb) {
		
		boolean ret = false;
		// 检查链接
		if (connection == null) {
			return false;
		}

		Session session = null;
		MessageProducer producer = null;

		try {
			// 创建一个Topic
			Topic topic = new ActiveMQTopic(subject);

			session = connection.createSession(transacted,
					Session.AUTO_ACKNOWLEDGE);

			// 创建一个生产者，然后发送多个消息。
			producer = session.createProducer(topic);
			StringBuffer sbuf = new StringBuffer();

			BytesMessage message = session.createBytesMessage();

			message.writeBytes(pb.getBytes());
			message.setStringProperty("SrvId", "Topic://"+subject);
			message.setStringProperty("Uid", "Mapabc");
			 
			producer.send(message);

			log.info("单条位置信息转发到JMS:" + sbuf.toString());
			ret = true;
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			ret = true;
			log.error("JMS发送异常：",e);
			e.printStackTrace();
		} finally {

			try {
				if (producer != null) {
					producer.close();
					producer = null;
				}
				if (session != null) {
					session.close();
					session = null;
				}
				if (connection != null) {
					connection.close();
					connection = null;
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}

		}
		return ret;

	}
 

	//发送多条数据到JMS
	public boolean sendMoreToJms(String subject, ArrayList<String> pbList) {
		boolean flag = false;
		try {
			GPSForwardToJMS toJms = GPSForwardToJMS.getInstance();
			toJms.sendMessage(subject, pbList);
			flag = true;
		} catch (Exception e) {
			flag = false;
			log.error("发送信息到JMS异常", e);
		}
		return flag;
	}

	//发送单条数据到JMS
	public boolean sendSingleToJms(String subject, String content) {
		boolean flag = false;
		try {
			GPSForwardToJMS toJms = GPSForwardToJMS.getInstance();
			toJms.sendSingleMessage(subject, content);
			flag = true;
		} catch (Exception e) {
			flag = false;
			log.error("发送信息到JMS异常", e);
		}
		return flag;
	}

}
