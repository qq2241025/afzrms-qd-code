/**
 * 
 */
package com.mapabc.gater.lbsgateway.service.online;

import org.apache.commons.logging.LogFactory;

import com.mapabc.AppCtxServer;
import com.mapabc.gater.common.Const;
import com.mapabc.gater.directl.dbutil.service.DBService;

/**
 * @author 
 * 
 */
public class StartInitOlineService {
	private static org.apache.commons.logging.Log log=LogFactory.getLog(StartInitOlineService.class);

	public static void start() {
//		String isstart = AllConfigCache.getInstance().getConfigMap().get(
//				"isSaveOnlie");
//		if (isstart == null || isstart.equals("0")) {
//			return;
//		}
		try {
			DBService service = (DBService) AppCtxServer.getInstance().getBean(Const.SPRING_BEAN_NAME_DBSERVICE);
			service.initOlineState();
		} catch (Exception e) {
			log.error("初始化在线状态异常", e);
		}

	}

}
