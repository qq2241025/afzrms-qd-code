package com.mapabc.gater.lbsgateway;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.LogFactory;

import com.mapabc.AppCtxServer;
import com.mapabc.gater.common.Const;
import com.mapabc.gater.directl.AllConfigCache;
import com.mapabc.gater.directl.bean.command.TStructions;
import com.mapabc.gater.directl.dbutil.service.DBService;
import com.mapabc.gater.directl.encode.RequestUtil;
import com.mapabc.gater.lbsgateway.bean.TerminalUDPAddress;

/**
 * 
 * 用来保存已经连立GPRS连接的终端
 * 
 * 以终端系列号为key
 * 
 * 终端系列号与TLGPRSThread一一对应
 * 
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * 
 * @version 1.0
 */
public class GprsUdpThreadList {
	static GprsUdpThreadList instance = null;
	private static org.apache.commons.logging.Log log=LogFactory.getLog(GprsUdpThreadList.class);


	private static HashMap<String, String> configMap = AllConfigCache
			.getInstance().getConfigMap();
	private static HashMap<String, String> loadMap = null;

	private static String isLoad = null;
	private static String localAddress;
	private static String localMtAddr ;
	private static String isReSend;
	
//	private static IMemcachedCache cache = null;
	private ConcurrentMap<String, TerminalUDPAddress> udpMap = new ConcurrentHashMap<String, TerminalUDPAddress>();

	public GprsUdpThreadList() {
		try {
			isReSend = configMap.get("isReSendIns");
			loadMap = AllConfigCache.getInstance().getLoadMap();
			if (loadMap != null){
			isLoad = loadMap.get("isOverLoad");
			localAddress = loadMap.get("localUdpAddr");
			localMtAddr = loadMap.get("localMtUrl");
			}
//			if (isLoad != null && isLoad.equals("1"))
//				cache = MemCacheUtil.getInstance().getCache("gaterClient");
		} catch (Exception e) {

		}
	}

	public synchronized static GprsUdpThreadList getInstance() {
		if (instance == null) {
			instance = new GprsUdpThreadList();
		}
		return instance;
	}

	/**
	 * 添加一个新的UDP连接
	 * 
	 * @param key
	 *            String
	 * @param gpsClient
	 *            GPSClient
	 */
	public /** synchronized */ void add(String key, TerminalUDPAddress gpsClient) {

		try {
			if (key == null || gpsClient==null)
				return;
			if (udpMap.get(key) == null) {// 登记第一次上线记录
				DBService service = (DBService) AppCtxServer.getInstance().getBean(Const.SPRING_BEAN_NAME_DBSERVICE);
				service.saveTermOnlineStatus(key, localMtAddr, "1", "1", true);
				
				//补发失败指令
				if (isReSend != null && isReSend.equals("1")){
//					TStructions[] insList = service.getStructionBySendStatus(key, "1");
//					RequestUtil.reSendIns(insList, gpsClient);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		udpMap.put(key, gpsClient);
//		if (isLoad != null && isLoad.equals("1"))// 有负载时把链路加到memcache
//			cache.put(KeyConstant.UdpLinkCacheKey, udpMap);

		if (log.isDebugEnabled())
			log.debug(
				key + "  udp 链路客户端地址：" + gpsClient.getSocketAddress());
		if (log.isDebugEnabled())
			log.debug(" 共有" + udpMap.size() + " 个UDP终端进入 GPRS");

	}

	public ConcurrentMap<String, TerminalUDPAddress> getUdpLink() {
//		try {
//			if (isLoad != null && isLoad.equals("1")) {// 有负载时从memcache获取
//				if (cache != null) {
//					return (ConcurrentMap) cache
//							.get(KeyConstant.UdpLinkCacheKey);
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		return udpMap;
	}

	/**
	 * 判断是否已经存在
	 * 
	 * @param key
	 *            String
	 * @return boolean
	 */
	public synchronized boolean isExist(String key) {

		if (udpMap.get(key) == null)
			return false;
		return true;
	}

	/**
	 * 检查终端连接状态,
	 * 
	 * @param simCard
	 *            String
	 * @return Socket
	 */
	public void checkUDPState() {

		Object obj = null;
		boolean flag = false; 
			log.info("Starting check udp link!");
		if (null != udpMap && udpMap.size() > 0) {
			if (log.isDebugEnabled())
				log.debug(" 检测UDP连接数：" + udpMap.size());
		}

		Iterator it = udpMap.keySet().iterator();

		while (it.hasNext()) {

			Date curDate = new Date();
			Calendar curcal = Calendar.getInstance();
			curcal.setTime(curDate);
			String key = (String) it.next();
			obj = udpMap.get(key);

			if (obj instanceof TerminalUDPAddress) {

				// 从配置文件获取UDP连接扫描频率
				String chkUdp = instance.configMap.get("checkUdpTime");
				String checkUdpTime = (chkUdp == null || chkUdp.length() <= 0) ? "1"
						: chkUdp;
				int udpTime = Integer.parseInt(checkUdpTime);
				// log.info("UDP链接扫描频率为：" + checkUdpTime);

				TerminalUDPAddress gpsClient = (TerminalUDPAddress) obj;

				Date connDate = gpsClient.getDate();
				Calendar conncal = Calendar.getInstance();
				conncal.setTime(connDate);
				conncal.add(Calendar.SECOND, udpTime);

				if (curcal.compareTo(conncal) > 0) {// udpTime分钟内无新数据则从列表中剔除
					it.remove();

					udpMap.remove(gpsClient.getDeviceSN());
					// GBLTerminalList.getInstance().removeTerminal(
					// gpsClient.getDeviceSN());

					// 更新UDP数据库为离线
					DBService service;
					try {
						service = (DBService) AppCtxServer.getInstance().getBean(Const.SPRING_BEAN_NAME_DBSERVICE);
						service.saveTermOnlineStatus(key, localMtAddr, "0", "1", false);
					} catch (Exception e) {
						log.error("get DBService bean error", e);
					}
					if (log.isDebugEnabled())
						log.debug(
							gpsClient.getDeviceSN() + "缓存退出了UDP连接," + "当前连接数="
									+ udpMap.size());

					// 离线通知报警服务
//					String alarmServiceType = instance.configMap
//							.get("alarmServiceType");
//					 
//					if (alarmServiceType != null
//							&& alarmServiceType.equals("1")) {
//						AlarmService alarm = new AlarmServiceLocalImpl();
//						alarm = new AlarmServiceRemoteImpl(instance.configMap
//								.get("alarmServiceUrl"), "utf-8");
//
//						SimpleDateFormat date = new SimpleDateFormat(
//								"yyyy-MM-dd HH:mm:ss");
//						String time = date.format(new Date());
//						String deviceId = gpsClient.getDeviceSN();
//						try {
//							alarm.offline(deviceId, time);
//							if (log.isDebugEnabled())
//								log.debug(
//									"缓存离线通知报警信息成功, deviceId:" + deviceId
//											+ ", time:" + time);
//						} catch (Exception e) {
//							e.printStackTrace();
//							log.error("缓存离线通知报警失败！", e);
//						}
//					}
				}

			}
		}
//		if (isLoad != null && isLoad.equals("1"))// 有负载时更新memcache链路缓存
//			cache.put(KeyConstant.UdpLinkCacheKey, udpMap);

	}

	/**
	 * 通过手机号得到UDP线程
	 * 
	 * @param simCard
	 *            String
	 * @return Socket
	 */
	public synchronized TerminalUDPAddress getGpsThreadBySim(String simCard) {
		TerminalUDPAddress gpsClient = null;

		boolean flag = false;
		ConcurrentMap<String, TerminalUDPAddress> udpList = this.getUdpLink();
		Iterator it = udpList.keySet().iterator();

		while (it.hasNext()) {
			try {
				Object obj = udpList.get((String) it.next());
				if (obj instanceof TerminalUDPAddress) {

					gpsClient = (TerminalUDPAddress) obj;
					if (gpsClient.getDeviceSN().equalsIgnoreCase(simCard)) {
						flag = true;
						break;
					}
				}
			} catch (Exception ex) {
				log.error("通过手机号获取UDP连接对象异常：" + ex.getMessage());
			}
		}
		if (flag) {
			return gpsClient;
		} else {
			return null;
		}
	}

	// 通过DEVICEID获取UDP的连接线程
	public synchronized Object getGprsThreadBySim(String deviceid) {
		Object retObj = null;
		boolean flag = false;
		ConcurrentMap<String, TerminalUDPAddress> udpList = this.getUdpLink();
		Iterator it = udpList.keySet().iterator();

		while (it.hasNext()) {
			try {
				Object obj = udpList.get((String) it.next());
				if (obj instanceof TerminalUDPAddress) {
					TerminalUDPAddress udpObj = (TerminalUDPAddress) obj;
					if (udpObj.getDeviceSN().equalsIgnoreCase(deviceid)) {
						flag = true;
						retObj = udpObj;
						break;
					}
				}
			} catch (Exception ex) {
				log.info(ex.getMessage());
				ex.printStackTrace();
			}
		}
		if (flag) {
			return retObj;
		} else {
			return null;
		}
	}

	/**
	 * 删除UDP连接
	 * 
	 * @param key
	 *            String
	 */
	public synchronized void removeUDP(String key) {

		TerminalUDPAddress gpsClient = (TerminalUDPAddress) udpMap.get(key);
		if (gpsClient != null) {
			// GBLTerminalList.getInstance().removeTerminal(key);
			udpMap.remove(key);
//			if (isLoad != null && isLoad.equals("1") && cache != null)
//				cache.put(KeyConstant.UdpLinkCacheKey, udpMap);

			// 更新UDP数据库为离线
			DBService service;
			try {
				service = (DBService) AppCtxServer.getInstance().getBean(Const.SPRING_BEAN_NAME_DBSERVICE);
				service.saveTermOnlineStatus(key, localMtAddr, "0", "1", false);
			} catch (Exception e) {
				log.error("get DBService bean error", e);
			}
			if (log.isDebugEnabled())
				log.debug(gpsClient.getDeviceSN() + "缓存退出了UDP GPRS");

			// 离线通知报警服务
//			String alarmServiceType = instance.configMap
//					.get("alarmServiceType");
//			if (alarmServiceType != null && alarmServiceType.trim().equals("1")) {
//				AlarmService alarm = new AlarmServiceLocalImpl();
//				alarm = new AlarmServiceRemoteImpl(alarmServiceType, "utf-8");
//
//				SimpleDateFormat date = new SimpleDateFormat(
//						"yyyy-MM-dd HH:mm:ss");
//				String time = date.format(new Date());
//				String deviceId = gpsClient.getDeviceSN();
//				try {
//					alarm.offline(deviceId, time);
//					if (log.isDebugEnabled())
//						log.debug(
//							"缓存离线通知报警信息成功, deviceId:" + deviceId + ", time:"
//									+ time);
//				} catch (Exception e) {
//					e.printStackTrace();
//					log.error("缓存离线通知报警失败！", e);
//				}
//			}
		}
	}

	public static void main(String[] args) {

	}

}
