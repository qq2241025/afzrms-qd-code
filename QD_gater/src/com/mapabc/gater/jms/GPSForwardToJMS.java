 
package com.mapabc.gater.jms;

import java.util.ArrayList;
import java.util.HashMap;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.commons.logging.LogFactory;

import com.mapabc.gater.directl.AllConfigCache;
 
 
 

/**
 * @author shiguang.zhou
 * 把GPS信息转发到JMS
 */
public class GPSForwardToJMS {
	private static org.apache.commons.logging.Log log=LogFactory.getLog(GPSForwardToJMS.class);

	private static GPSForwardToJMS instance = null;
	
	private static Connection connection = null;
	
	private boolean transacted;

	//private long sleepTime = 1;
	
	
	
	public static synchronized GPSForwardToJMS getInstance(){
		if(instance == null)
		{
			instance = new GPSForwardToJMS();
		}
		
		return instance;		
	}
	
	private GPSForwardToJMS(){
		BuildJMSConnection();
	}
	
	private void BuildJMSConnection(){
		HashMap<String, String> jmsMap = AllConfigCache.getInstance().getConfigMap();
 		String jmsip = jmsMap.get("mqIpPort");
		
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(jmsip);
		if(connection != null)
		{//首先要关闭原来的链接
			try {
				connection.close();
			} catch (Throwable ignore) {
			}
			connection = null;
		}
		
		try {
			connection = factory.createConnection();
			connection.start();
		}catch(JMSException e) {
			// TODO Auto-generated catch block
			log.info("创建JMS连接异常："+e.getMessage());
			e.printStackTrace();
		}
	}
		
	public void sendMessage(String subject, ArrayList<String> pbList){
		//检查链接
		if(connection == null){
			BuildJMSConnection();
		}
		Session session = null;
		MessageProducer producer = null;
		try {
			 //创建一个Topic
	        Topic topic= new ActiveMQTopic(subject);
	         session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
	        
	        //创建一个生产者，然后发送多个消息。
	         producer = session.createProducer(topic);
 	        
	        BytesMessage  message = session.createBytesMessage();
	        
	        for(int i=0; i<pbList.size(); i++){
	        	String pb =  pbList.get(i);
	        	message.writeBytes(pb.getBytes());
 	 	        producer.send(message);   
 	 	      log.info("信息转发到JMS:"+pb);
	        }
 
		} catch( org.apache.activemq.ConnectionFailedException cfe){
			log.info("JMS连接异常："+cfe.getMessage()+",重新建立连接");
			System.out.println("JMS连接异常："+cfe.getMessage()+",重新建立连接");
			BuildJMSConnection();			
		 
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			log.info("JMS发送异常："+e.getMessage());
			e.printStackTrace();
		}finally {
			try {
				if (producer != null)
					producer.close();
				if (session != null)
					session.close();
 
			} catch (Exception ee) {
				ee.printStackTrace();
			}
	}
}
	
	public synchronized void sendSingleMessage(String subject, String content){
		//检查链接
		if(connection == null){
			BuildJMSConnection();
		}
		Session session = null;
		MessageProducer producer = null;
		try {
			 //创建一个Topic
//	        Topic topic= new ActiveMQTopic(subject);
//	         session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
//	        
//	        //创建一个生产者
//	         producer = session.createProducer(topic);
//	        String cont = content;
//	          
//	        BytesMessage  message = session.createBytesMessage();
//	        message.writeBytes(cont.getBytes());
//	        message.setStringProperty("SrvId","Topic://"+subject);
//	        message.setStringProperty("Uid","Mapabc");
// 	        producer.send(message);
//	        if (transacted)
//	            session.commit();
	        log.info("信息转发到JMS:"+content);
	 
		}finally {
			try {
				if (producer != null)
					producer.close();
				if (session != null)
					session.close();
 
			} catch (Exception ee) {
				ee.printStackTrace();
			}
	}
}
	public static void main(String[] args){
		GPSForwardToJMS conn = GPSForwardToJMS.getInstance();
		int i=0;
		while (i<5){
			  conn.sendMessage(null, null);
			 
		}
	}
}
