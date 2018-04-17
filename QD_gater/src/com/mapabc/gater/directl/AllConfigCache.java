/**
 * 
 */
package com.mapabc.gater.directl;

import java.util.HashMap;

import org.apache.commons.logging.LogFactory;

import com.mapabc.gater.util.PropertyReader;

/**
 * @author 
 * 
 */
public class AllConfigCache {
	private static org.apache.commons.logging.Log log = LogFactory.getLog(AllConfigCache.class);

	static AllConfigCache instance;
	private static String serviceFileName = "service.properties";
	private static String servicekeyFileName = "servicekey.properties";
	private static String configFileName = "config.properties";
	private static String loadFileName = "load.properties";

	private static String alarmFileName = "alarm.properties";
	private static String locateCfgFileName = "locate-config.properties";
	private static String locCfgFileName = "locConfig.properties";
	private static String proxyFileName = "proxy.properties";
	private static String smsFileName = "sms.properties";

	private static String webappFileName = "webapp.properties";
	private static String scheduleFileName = "schedule.properties";
	private static String jmsFileName = "jms.properties";
	private static String naviserverFileName = "naviserver.properties";
	private static String pdcwztFileName = "pdc-wzt.properties";
	private static String qqtFileName = "qqt.properties";
	private static String tobacoFileName = "tobaco.properties";

	private static String nonMtCmdService = "non-mtcmd-service.properties";

	private static HashMap<String, String> serviceMap;

	private static HashMap<String, String> serviceKeyMap;

	private static HashMap<String, String> configMap;

	private static HashMap<String, String> loadMap;

	private static HashMap<String, String> locateConfigMap;

	private static HashMap<String, String> alarmMap;

	private static HashMap<String, String> locConfigMap;

	private static HashMap<String, String> proxyMap;

	private static HashMap<String, String> scheduleMap;

	private static HashMap<String, String> smsMap;

	private static HashMap<String, String> webappMap;

	private static HashMap<String, String> jmsMap;

	private static HashMap<String, String> naviserverMap;

	private static HashMap<String, String> pdcwztMap;

	private static HashMap<String, String> qqtMap;

	private static HashMap<String, String> tobacoMap;

	private static HashMap<String, String> nonMtCmdServiceMap;

	public static AllConfigCache getInstance() {
		if (instance == null) {
			instance = new AllConfigCache();
		}
		return instance;
	}

	private AllConfigCache() {
	}

	/**
	 * 加载所有配置信息
	 * 
	 * @author 
	 */
	public static void loadAllConfig() {

		PropertyReader p = null;
		try {
			p = new PropertyReader(serviceFileName);
			AllConfigCache.serviceMap = p.getAllValue();
			log.info(serviceFileName + " size:" + instance.getServiceMap().size());
		} catch (Exception e) {
			log.error("加载" + serviceFileName + "异常:" + e.getMessage(), null);
		}
		try {
			PropertyReader p1 = new PropertyReader(servicekeyFileName);
			AllConfigCache.serviceKeyMap = p1.getAllValue();
			log.info(servicekeyFileName + "  size:" + instance.getServiceKeyMap().size());
		} catch (Exception e) {
			log.error("加载" + servicekeyFileName + "异常:" + e.getMessage(), null);
		}
		try {
			p = new PropertyReader(configFileName);
			AllConfigCache.configMap = p.getAllValue();
			log.info(configFileName + "  size:" + instance.getConfigMap().size());
		} catch (Exception e) {
			log.error("加载" + configFileName + "异常:" + e.getMessage(), null);
		}
		try {
			p = new PropertyReader(loadFileName);
			AllConfigCache.loadMap = p.getAllValue();
			log.info(loadFileName + "  size:" + instance.getLoadMap().size());
		} catch (Exception e) {
			log.error("加载" + loadFileName + "异常:" + e.getMessage(), null);
		}
		try {
			p = new PropertyReader(alarmFileName);
			AllConfigCache.alarmMap = p.getAllValue();
			log.info(alarmFileName + "  size:" + instance.getAlarmMap().size());
		} catch (Exception e) {
			// log.error("加载" + alarmFileName + "异常:"+e.getMessage(), null);
		}
		try {
			p = new PropertyReader(locCfgFileName);
			AllConfigCache.locConfigMap = p.getAllValue();
			log.info(locCfgFileName + "  size:" + instance.getLocConfigMap().size());
		} catch (Exception e) {
			// log.error("加载" + locCfgFileName + "异常:"+e.getMessage(), null);
		}
		try {
			p = new PropertyReader(proxyFileName);
			AllConfigCache.proxyMap = p.getAllValue();
			log.info(proxyFileName + "  size:" + instance.getProxyMap().size());
		} catch (Exception e) {
			// log.error("加载" + proxyFileName + "异常:"+e.getMessage(), null);
		}
		try {
			p = new PropertyReader(scheduleFileName);
			AllConfigCache.scheduleMap = p.getAllValue();
			log.info(scheduleFileName + "  size:" + instance.getScheduleMap().size());
		} catch (Exception e) {
			log.error("加载" + scheduleFileName + "异常:" + e.getMessage(), null);
		}
		try {
			p = new PropertyReader(smsFileName);
			AllConfigCache.smsMap = p.getAllValue();
			log.info(smsFileName + "  size:" + instance.getSmsMap().size());
		} catch (Exception e) {
			// log.error("加载" + smsFileName + "异常:"+e.getMessage(), null);
		}
		try {
			p = new PropertyReader(webappFileName);
			AllConfigCache.webappMap = p.getAllValue();
			log.info(webappFileName + "  size:" + instance.getWebappMap().size());
		} catch (Exception e) {
			log.error("加载" + webappFileName + "异常:" + e.getMessage(), null);
		}
		try {
			p = new PropertyReader(locateCfgFileName);
			AllConfigCache.locateConfigMap = p.getAllValue();
			log.info(locateCfgFileName + " size:" + instance.getLocateConfigMap().size());
		} catch (Exception e) {
			// log.error("加载" + locateCfgFileName + "异常:"+e.getMessage(), null);
		}
		try {
			p = new PropertyReader(jmsFileName);
			AllConfigCache.jmsMap = p.getAllValue();
			log.info(jmsFileName + " size:" + AllConfigCache.jmsMap.size());
		} catch (Exception e) {
			// log.error("加载" + jmsFileName + "异常:"+e.getMessage(), null);
		}
		try {
			p = new PropertyReader(naviserverFileName);
			AllConfigCache.naviserverMap = p.getAllValue();
			log.info(naviserverFileName + " size:" + AllConfigCache.naviserverMap.size());
		} catch (Exception e) {
			// log.error("加载" + naviserverFileName + "异常:"+e.getMessage(),
			// null);
		}
		try {
			p = new PropertyReader(pdcwztFileName);
			AllConfigCache.pdcwztMap = p.getAllValue();
			log.info(pdcwztFileName + " size:" + AllConfigCache.pdcwztMap.size());
		} catch (Exception e) {
			// log.error("加载" + pdcwztFileName + "异常:"+e.getMessage(), null);
		}
		try {
			p = new PropertyReader(qqtFileName);
			AllConfigCache.qqtMap = p.getAllValue();
			log.info(qqtFileName + " size:" + AllConfigCache.qqtMap.size());
		} catch (Exception e) {
			// log.error("加载" + qqtFileName + "异常:"+e.getMessage(), null);
		}
		try {
			p = new PropertyReader(tobacoFileName);
			AllConfigCache.tobacoMap = p.getAllValue();
			log.info(tobacoFileName + " size:" + AllConfigCache.tobacoMap.size());
		} catch (Exception e) {
			// log.error("加载" + tobacoFileName + "异常:"+e.getMessage(), null);
		}

		try {
			p = new PropertyReader(nonMtCmdService);
			AllConfigCache.nonMtCmdServiceMap = p.getAllValue();
			log.info(nonMtCmdService + " size:" + AllConfigCache.nonMtCmdServiceMap.size());
		} catch (Exception e) {
			// log.error("加载" + nonMtCmdService + "异常:"+e.getMessage(), null);
		}
	}

	/**
	 * 获取src下service.properties配置内容
	 */
	public HashMap<String, String> getServiceMap() {
		return AllConfigCache.serviceMap;
	}

	/**
	 * 获取src下servicekey.properties配置内容
	 */
	public HashMap<String, String> getServiceKeyMap() {
		return AllConfigCache.serviceKeyMap;
	}

	/**
	 * 获取src下config.properties配置内容
	 */
	public HashMap<String, String> getConfigMap() {
		return AllConfigCache.configMap;
	}

	/**
	 * 获取src下load.properties配置内容
	 */
	public HashMap<String, String> getLoadMap() {
		return AllConfigCache.loadMap;
	}

	/**
	 * 获取src下locate-config.properties配置内容
	 */
	public HashMap<String, String> getLocateConfigMap() {
		return AllConfigCache.locateConfigMap;
	}

	/**
	 * 获取src下alarm.properties配置内容
	 */
	public HashMap<String, String> getAlarmMap() {
		return AllConfigCache.alarmMap;
	}

	/**
	 * 获取src下locConfig.properties配置内容
	 */
	public HashMap<String, String> getLocConfigMap() {
		return AllConfigCache.locConfigMap;
	}

	/**
	 * 获取src下 proxy.properties配置内容
	 */
	public HashMap<String, String> getProxyMap() {
		return AllConfigCache.proxyMap;
	}

	/**
	 * 获取src下schedule.properties配置内容
	 */
	public HashMap<String, String> getScheduleMap() {
		return AllConfigCache.scheduleMap;
	}

	/**
	 * 获取src下sms.properties配置内容
	 */
	public HashMap<String, String> getSmsMap() {
		return AllConfigCache.smsMap;
	}

	/**
	 * 获取src下webapp.properties配置内容
	 */
	public HashMap<String, String> getWebappMap() {
		return AllConfigCache.webappMap;
	}

	/**
	 * 获取src下jms.properties配置内容
	 */
	public static HashMap<String, String> getJmsMap() {
		return jmsMap;
	}

	/**
	 * 获取src下naviserver.properties配置内容
	 */
	public static HashMap<String, String> getNaviserverMap() {
		return naviserverMap;
	}

	/**
	 * 获取src下pdc-wzt.properties配置内容
	 */
	public static HashMap<String, String> getPdcwztMap() {
		return pdcwztMap;
	}

	/**
	 * 获取src下qqt.properties配置内容
	 */
	public static HashMap<String, String> getQqtMap() {
		return qqtMap;
	}

	/**
	 * 获取src下发tobaco.properties配置
	 */
	public static HashMap<String, String> getTobacoMap() {
		return tobacoMap;
	}

	/**
	 * 获取src下发non-mtcmd-service.properties配置
	 */
	public static HashMap<String, String> getNonMtCmdServiceMap() {
		return nonMtCmdServiceMap;
	}
}
