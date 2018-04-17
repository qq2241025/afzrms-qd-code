/**
 * 
 */
package com.mapabc.gater.lbsgateway.service.flowvolume;

import java.util.Date;
import java.util.Timer;
import javax.servlet.ServletException;
import org.apache.commons.logging.LogFactory;
import com.mapabc.gater.directl.AllConfigCache;
 

/**
 * @author 
 *
 */
public class StartFlowServer {
	private static org.apache.commons.logging.Log log=LogFactory.getLog(StartFlowServer.class);

	private static Timer timer = null;
 	 
	public static void start() throws ServletException {
		// Put your code here
		String isstart =AllConfigCache.getInstance().getConfigMap().get("isStartFlow");
		if (isstart==null || isstart.equals("0")){
			return;
		}
		timer = new Timer();
		FlowVolumeSave task = new FlowVolumeSave();
		timer.schedule(task, new Date(), 30 * 1000);
		log.info("启动了流量定时入库服务，频率为30S.");
	}
}
