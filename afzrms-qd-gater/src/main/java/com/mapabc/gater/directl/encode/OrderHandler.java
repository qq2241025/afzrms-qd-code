/**
 * 
 */
package com.mapabc.gater.directl.encode;

import java.util.HashMap;
import java.util.LinkedList;

import org.apache.commons.logging.LogFactory;

import com.mapabc.gater.directl.AllConfigCache;


/**
 * @author 
 * 
 */
public class OrderHandler extends Thread {
	private static org.apache.commons.logging.Log log=LogFactory.getLog(OrderHandler.class);

	
	private static HashMap<String, String> serviceMap = AllConfigCache
	.getInstance().getConfigMap();

	private int result=-1 ;
 	
	/**
	 * 命令下发缓存，下发失败后的保存，重发次数达到设定次数后丢弃
	 */
	private static LinkedList<Request> orders = new LinkedList<Request>();

	private static int RESEND_COUNT = Integer.parseInt(serviceMap
			.get ("RESEND_COUNT"));

	private static int RESEND_TIME_INTERVIAL = Integer.parseInt(serviceMap
			.get ("RESEND_TIME_INTERVIAL"));

	public synchronized void addOrder(Request order) {
		order.setFirstTime(System.currentTimeMillis());
		orders.addLast(order);
	}
	
 

	public void run() {
		while (true) {
			synchronized (orders) {
				try {
					if (orders.size() > 0 && !orders.isEmpty()) {
						final Request req = orders.removeLast();
						 
						if (req.isTimeUp(RESEND_TIME_INTERVIAL) && req.getReSendCount() < RESEND_COUNT) {
							int result = Controller.sendOrder(req);
 							
							log.info("缓存执行结果："+result);
							this.setResult(result);
							
							
//							if (req.getReSendCount() < RESEND_COUNT && result != 0) {
//								addOrder(req);
//							}
						}
						else if(req.getReSendCount() < RESEND_COUNT) {
							orders.addLast(req);
						}
					}  
					this.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
					log.error("缓存发送异常", e);
				}
			}
		}
	}

	/**
	 * @return the result
	 */
	public synchronized int getResult() {
		return this.result;
	}

	/**
	 * @param result the result to set
	 */
	public synchronized void setResult(int result) {
		this.result = result;
	}
}
