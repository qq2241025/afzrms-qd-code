package com.mapabc.gater.lbsgateway;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.LogFactory;

import com.mapabc.AppCtxServer;
import com.mapabc.gater.common.Const;
import com.mapabc.gater.directl.AllConfigCache;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.bean.command.TStructions;
import com.mapabc.gater.directl.dbutil.service.DBService;
import com.mapabc.gater.directl.encode.RequestUtil;
import com.mapabc.gater.lbsgateway.bean.TerminalTCPAddress;

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
public class GprsTcpThreadList {
	private static org.apache.commons.logging.Log log = LogFactory
			.getLog(GprsTcpThreadList.class);

	static GprsTcpThreadList instance = null;
	private static HashMap<String, String> configMap;
	private static HashMap<String, String> loadMap;

	// private static IMemcachedCache cache;
	private static String isload = null;
	private static String localAddress = null;

	private static ConcurrentMap<String, TerminalTCPAddress> tcpMap = new ConcurrentHashMap<String, TerminalTCPAddress>();
	private static ConcurrentHashMap<ClientSocket, String> tcpLinkList = new ConcurrentHashMap<ClientSocket, String>();

	private static String isReSend;
	private static String localMtAddr;

	public synchronized static GprsTcpThreadList getInstance() {
		if (instance == null) {
			instance = new GprsTcpThreadList();
		}

		return instance;
	}

	private GprsTcpThreadList() {
		configMap = AllConfigCache.getInstance().getConfigMap();
		isReSend = configMap.get("isReSendIns");

		loadMap = AllConfigCache.getInstance().getLoadMap();
		if (loadMap != null) {
			isload = loadMap.get("isOverLoad");
			localAddress = loadMap.get("localTcpAddr");
			localMtAddr = loadMap.get("localMtUrl");
		}

		// if (isload != null && isload.equals("1")) {
		// cache = MemCacheUtil.getInstance().getCache("gaterClient");
		// }
	}

	public ConcurrentMap<String, TerminalTCPAddress> getTcpLink() {
		// try {
		// if (isload != null && isload.equals("1") && cache != null) {
		//
		// return (ConcurrentMap) cache.get(KeyConstant.TcpLinkCacheKey);
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		return tcpMap;
	}

	/**
	 * 添加一个新的TCP连接
	 * 
	 * @param key
	 *            String
	 * @param gpsClient
	 *            GPSClient
	 */
	public /**synchronized*/ void add(String key, TerminalTCPAddress newClient) {

		if (key == null || newClient == null)
			return;

		ConcurrentMap<String, TerminalTCPAddress> tcpLink = getTcpLink();
		ClientSocket clientSocketTmp = new ClientSocket();
		TerminalTCPAddress oldClient = null;
		Socket oldSocket = null;
		Socket newSocket = null;
		String oldAddress = null;
		String newAddress = null;
		SocketChannel oldSocketChannel = null;
		SocketChannel newSocketChannel = null;

		if (tcpLink != null) {
			oldClient = tcpLink.get(key);
		}
		if (newClient != null) {
			newSocketChannel = newClient.getSocketChannel();
			// 新的连接链路
			if (newSocketChannel != null) {

				if (newSocketChannel != null) {
					newSocket = newSocketChannel.socket();
					clientSocketTmp.setSocket(newSocket);
					clientSocketTmp.setDate(new Date());

					if (newSocket != null) {
						SocketAddress newSocketAddress = newSocket
								.getRemoteSocketAddress();
						if (newSocketAddress != null) {
							newAddress = newSocketAddress.toString();

							Set tmpTcpSet = tcpLinkList.entrySet();
							List keyList = new ArrayList(tmpTcpSet);
							Iterator iterator = keyList.iterator();
							boolean isExit = false;
							ClientSocket client = null;
							while (iterator.hasNext()) {
								try {
									Map.Entry met = (Map.Entry) iterator.next();
									client = (ClientSocket) met.getKey();
									String value = (String) met.getValue();

									if (value != null && value.equals(key)) {
										if (client != null
												&& client.getSocket().equals(
														newSocket)) {
											isExit = true;
											break;
										}
									}

								} catch (Exception e) {
									log.error("check tcplink error", e);
								}
							}
							if (!isExit) {
								tcpLinkList.put(clientSocketTmp, key);
							} else {
								client.setDate(new Date());
								tcpLinkList.put(client, key);
							}
							TcpLinkCache.getInstance().addToTcpCache(newSocket,
									new Date());
							if (log.isDebugEnabled())
								log.debug(key + " tcp new 客户端连接地址："
										+ newAddress + ",tcpLinkList size="
										+ instance.tcpLinkList.size()
										+ "     isexist:" + isExit);
							if (log.isDebugEnabled())
								log.debug(" tcp链路地址："
										+ newAddress
										+ "，对应终端序列号："
										+ key
										+ ",date："
										+ Tools.formatDate2Str(new Date(),
												"yyyy-MM-dd HH:mm:ss"));

						} else {
							if (log.isDebugEnabled())
								log.debug(key + " tcp new 链路客户端地址为空!");

							// try {
							// //newSocket.close();
							// //newSocketChannel.close();
							// Log
							// .getInstance()
							// .outLog(
							// key
							// + " tcp new 链路客户端地址为空!");
							// } catch (IOException e) {
							//
							// e.printStackTrace();
							// }
						}
					}
				}
			}
		}

		if (oldClient != null) {
			try {
				oldSocketChannel = oldClient.getSocketChannel();
				// 旧的连接链路
				if (oldSocketChannel != null) {

					if (oldSocketChannel != null) {
						oldSocket = oldSocketChannel.socket();
						if (oldSocket != null) {
							SocketAddress oldSocketAddress = oldSocket
									.getRemoteSocketAddress();
							if (oldSocketAddress != null) {
								oldAddress = oldSocketAddress.toString();
								if (log.isDebugEnabled())
									log.debug(key + " tcp OLD 客户端连接地址："
											+ oldAddress);
							} else {

								try {
									removeChangeOldLink(oldSocket);
								} catch (Exception e) {
								}
								try {
									oldSocket.close();
									oldSocketChannel.close();
									if (log.isDebugEnabled())
										log
												.debug(key
														+ " tcp 旧链路客户端地址为空，删除old channel!");
								} catch (IOException e) {

									e.printStackTrace();
								}
							}
						}
					}
				}

				if (oldAddress != null && newAddress != null
						&& !oldAddress.equals(newAddress)) {

					try {
						removeChangeOldLink(oldSocket);
					} catch (Exception e) {
					}
					if (oldSocketChannel != null) {
						oldSocketChannel.close();
						oldSocketChannel = null;
					}
					if (oldSocket != null) {
						oldSocket.close();
						oldSocket = null;
						if (log.isDebugEnabled())
							log
									.debug(key + "  tcp 链接发生变化，删除旧的连接:"
											+ oldAddress);
					}
				}
			} catch (IOException e) {

				e.printStackTrace();
				log.error(key + "  关闭旧连接异常", e);

			}

		}

		try {
			if (newSocketChannel != null) {
				if (newSocketChannel.isOpen()) {
					newClient.setLocalAddr(localAddress);
					try {
					if (tcpMap.get(key) == null) {// 登记首次登陆记录
						DBService service = (DBService) AppCtxServer.getInstance().getBean(Const.SPRING_BEAN_NAME_DBSERVICE);
						service.saveTermOnlineStatus(key, localMtAddr, "1",
								"0", true);

						// 补发失败指令
						if (isReSend != null && isReSend.equals("1")) {
							TStructions[] insList = service
									.getStructionBySendStatus(key, "1");
							RequestUtil.reSendIns(insList, newClient);
						}
						}
					}catch (Exception e){}
					tcpMap.put(key, newClient);

					// if (isload != null && isload.equals("1"))
					// cache.put(KeyConstant.TcpLinkCacheKey, tcpMap);

					log.info(" 共有" + tcpMap.size() + " 个TCP终端进入 GPRS");
				} else {
					// newSocket.close();
					// newSocketChannel.close();
					if (log.isDebugEnabled())
						log.debug(key + " tcp newSocketChannel.isOpen()="
								+ newSocketChannel.isOpen());
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(" 增加新连接异常", e);
		}

	}

	private void removeChangeOldLink(Socket socket) {

		Set tmpTcpSet = tcpLinkList.entrySet();
		List keyList = new ArrayList(tmpTcpSet);
		Iterator iterator = keyList.iterator();
		boolean isExit = false;

		while (iterator.hasNext()) {
			try {
				Map.Entry met = (Map.Entry) iterator.next();
				ClientSocket client = (ClientSocket) met.getKey();
				String value = (String) met.getValue();
				if (client == null) {
					tcpLinkList.remove(client);
					tcpMap.remove(value);
				} else if (client.getSocket().equals(socket)) {
					tcpLinkList.remove(client);
					tcpMap.remove(value);
					
					if (log.isDebugEnabled())
						log.debug("removeChangeOldLink:" + client.toString()
								+ ",tcplink size:" + tcpLinkList.size());
					break;
				}

			} catch (Exception e) {
				log.error("check tcplink error", e);
			}
		}
	}

	/**
	 * 判断是否已经存在
	 * 
	 * @param key
	 *            String
	 * @return boolean
	 */
	public synchronized boolean isExist(String key) {

		if (tcpMap.get(key) == null)
			return false;
		return true;
	}

	public void checkTcpLinkList() {

		log.info("Starting check tcp link!");
		Date curDate = new Date();
		Calendar curcal = Calendar.getInstance();
		curcal.setTime(curDate);

		// 从配置文件获取TCP连接扫描频率
		String chkTcp = configMap.get("checkTcpTime");
		String checkTcpTime = (chkTcp == null || chkTcp.length() <= 0) ? "5"
				: chkTcp;
		int tcpTime = Integer.parseInt(checkTcpTime);

		Set tmpTcpSet = tcpLinkList.entrySet();
		List keyList = new ArrayList(tmpTcpSet);
		Iterator iterator = keyList.iterator();
		Calendar conncal = Calendar.getInstance();

		while (iterator.hasNext()) {
			try {
				Map.Entry met = (Map.Entry) iterator.next();
				ClientSocket client = (ClientSocket) met.getKey();
				String value = (String) met.getValue();

				if (client != null) {
					Socket socket = client.getSocket();
					if (log.isDebugEnabled())
						log.debug(value
								+ " checking => TerminalTCPAddress isclosed:"
								+ socket.isClosed() + ",isConnected="
								+ socket.isConnected() + ",isInputShutdown="
								+ socket.isInputShutdown() + ",isOutShutdown="
								+ socket.isOutputShutdown() + ",addr="
								+ socket.getRemoteSocketAddress());

					if (socket == null || !socket.isConnected()
							|| socket.isClosed()
							|| socket.getRemoteSocketAddress() == null) {

						tcpLinkList.remove(client);
						tcpMap.remove(value);
						DBService service = (DBService) AppCtxServer.getInstance().getBean(Const.SPRING_BEAN_NAME_DBSERVICE);
						service.saveTermOnlineStatus(value, localMtAddr, "0",
								"0", false);
						continue;
					}
				}

				Date connDate = client.getDate();
				conncal.setTime(connDate);
				conncal.add(Calendar.SECOND, tcpTime);

				if (log.isDebugEnabled())
					log.debug(value
							+ " old tcplink time:"
							+ Tools.formatDate2Str(connDate,
									"yyyy-MM-dd HH:mm:ss")
							+ ",new tcplink time:"
							+ Tools.formatDate2Str(curDate,
									"yyyy-MM-dd HH:mm:ss") + ",时间差："
							+ (curDate.getTime() - connDate.getTime()) / 1000
							+ " 秒。");

				if (curcal.compareTo(conncal) > 0) {// tcpTime分钟内无新数据则从列表中剔除
					tcpLinkList.remove(client);

					if (client.getSocket() != null
							&& !client.getSocket().isClosed()) {
						client.getSocket().close();

						log.info(value + " 超过" + tcpTime
								+ "s 不活动连接，删除.当前 tcplinklist size:"
								+ instance.tcpLinkList.size()
								+ ",delete socket:"
								+ client.getSocket().getRemoteSocketAddress());

					}
					if (value != null) {
						tcpMap.remove(value);
						// 更新TCP数据库为离线
						DBService service = (DBService) AppCtxServer.getInstance().getBean(Const.SPRING_BEAN_NAME_DBSERVICE);
						service.saveTermOnlineStatus(value, localMtAddr, "0",
								"0", false);
					}

				}

			} catch (Exception e) {
				log.error("check tcplink error", e);
			}
		}

	}

	/**
	 * 通过DEVICEID得到Tcp Socket
	 * 
	 * @param simCard
	 *            String
	 * @return Socket
	 */
	public synchronized TerminalTCPAddress getGpsTcpThreadBySim(String deviceid) {
		TerminalTCPAddress gpsClient = null;
		boolean flag = false;
		ConcurrentMap<String, TerminalTCPAddress> tcpLinkList = this
				.getTcpLink();
		Iterator it = tcpLinkList.keySet().iterator();
		while (it.hasNext()) {
			try {
				Object obj = tcpLinkList.get((String) it.next());
				if (obj instanceof TerminalTCPAddress) {

					gpsClient = (TerminalTCPAddress) obj;

					if (gpsClient.getDeviceSN().equalsIgnoreCase(deviceid)) {
						flag = true;

						break;
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				log.error("缓存通过DEVICEID获取TCP处理线程异常", ex);
				log.debug("缓存通过DEVICEID获取TCP处理线程异常：" + ex.getMessage());
			}
		}
		if (flag) {
			return gpsClient;
		} else {
			return null;
		}
	}

	// 通过DEVICEID获取TCP的连接线程
	public synchronized Object getGprsThreadBySim(String deviceid) {
		Object retObj = null;
		boolean flag = false;
		ConcurrentMap<String, TerminalTCPAddress> tcpLinkList = this
				.getTcpLink();
		Iterator it = tcpLinkList.keySet().iterator();
		while (it.hasNext()) {
			try {
				Object obj = tcpLinkList.get((String) it.next());
				if (obj instanceof TerminalTCPAddress) {

					TerminalTCPAddress gpsClient = (TerminalTCPAddress) obj;
					if (gpsClient.getDeviceSN().equalsIgnoreCase(deviceid)) {
						flag = true;
						retObj = gpsClient;
						break;
					}
				}
			} catch (Exception ex) {
				log.debug(ex.getMessage());
				ex.printStackTrace();
			}
		}
		if (flag) {
			return retObj;
		} else {
			return null;
		}
	}

	// 删除TCP连接
	public synchronized void remove2(String key) {

		Iterator it = tcpMap.keySet().iterator();

		TerminalTCPAddress gpsClient = (TerminalTCPAddress) tcpMap.get(key);
		if (gpsClient != null) {
			// GBLTerminalList.getInstance().removeTerminal(key);
			tcpMap.remove(key);

			// if (isload != null && isload.equals("1"))
			// cache.put(KeyConstant.TcpLinkCacheKey, tcpMap);

			if (gpsClient != null) {
				SocketChannel socket = gpsClient.getSocketChannel();

				if (socket != null) {
					Set tmpTcpSet = this.tcpLinkList.entrySet();
					List keyList = new ArrayList(tmpTcpSet);
					Iterator iterator = keyList.iterator();

					while (iterator.hasNext()) {
						try {
							Map.Entry met = (Map.Entry) iterator.next();
							ClientSocket client = (ClientSocket) met.getKey();
							String value = (String) met.getValue();
							Socket tmpSocket = socket.socket();
							ClientSocket tmpCs = new ClientSocket();
							tmpCs.setSocket(tmpSocket);

							if (client != null && tmpCs != null
									&& client.equals(tmpCs)) {
								this.tcpLinkList.remove(client);
								socket.socket().close();
								socket.close();
								socket = null;
							}

						} catch (Exception e) {
							log.error("remove old tcplink error", e);
						}

					}
				}

			}

			// 更新TCP数据库为离线
			DBService service;
			try {
				service = (DBService) AppCtxServer.getInstance().getBean(Const.SPRING_BEAN_NAME_DBSERVICE);
				service.saveTermOnlineStatus(key, localMtAddr, "0", "0", false);
			} catch (Exception e) {
				log.error("get DBService bean error", e);
			}

			 
			log.debug(gpsClient.getDeviceSN() + "缓存退出了TCP GPRS");
		}
		// if (isload != null && isload.equals("1") && cache != null)
		// cache.put(KeyConstant.TcpLinkCacheKey, tcpLinkList);
	}

	/**
	 * 通过TCP地址获取设备ID
	 * 
	 * @return the tcpLinkList
	 */
	public String getDeviceIdByTcpAddress(Socket socketAddress) {

		ClientSocket tmpCs = new ClientSocket();
		tmpCs.setSocket(socketAddress);

		return this.tcpLinkList.get(tmpCs);
	}

	/**
	 * @param tcpLinkList
	 *            the tcpLinkList to set
	 */
	public void setTcpLinkList(
			ConcurrentHashMap<ClientSocket, String> tcpLinkList) {
		this.tcpLinkList = tcpLinkList;
	}

	/**
	 * @return the tcpLinkList
	 */
	public ConcurrentHashMap<ClientSocket, String> getTcpLinkList() {
		return this.tcpLinkList;
	}

}
