
package com.mapabc.gater.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
/**
 * @Title TODO
 * @Description TODO
 * @author 
 * @createDate 2015年8月25日 下午3:35:01
 * @modifier 
 * @modifyDate 
 * @version 1.0
 */

public class BootstartListeners implements ServletContextListener{
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		BootTimerTask threa=new BootTimerTask();
		threa.start();
	}
}
