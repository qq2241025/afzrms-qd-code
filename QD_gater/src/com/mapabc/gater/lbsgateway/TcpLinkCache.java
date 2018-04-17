/**
 * 
 */
package com.mapabc.gater.lbsgateway;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.LogFactory;

import com.mapabc.AppCtxServer;
import com.mapabc.gater.common.Const;
import com.mapabc.gater.directl.AllConfigCache;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.dbutil.service.DBService;

/**
 * @author 
 * 
 */
public class TcpLinkCache {

	static TcpLinkCache instance = null;
	private static String localMtAddr;
	private static HashMap<String, String> loadMap;
	
	private static org.apache.commons.logging.Log log = LogFactory
			.getLog(TcpLinkCache.class);

	private static ConcurrentHashMap<Socket, Date> tcpAllCache = new ConcurrentHashMap<Socket, Date>();

	public synchronized static TcpLinkCache getInstance() {
		if (instance == null) {
			instance = new TcpLinkCache();
		}
		return instance;
	}

	private TcpLinkCache(){
		loadMap = AllConfigCache.getInstance().getLoadMap();
		if (loadMap != null) { 
			localMtAddr = loadMap.get("localMtUrl");
		}
	}
	/**
	 * @param tcpCache
	 *            the tcpCache to set
	 */
	public void addToTcpCache(Socket socket, Date date) {
		if (this.tcpAllCache.get(socket) != null) {
			if (log.isDebugEnabled()) {
				if (socket.getRemoteSocketAddress() != null)
					log
							.debug("更新tcp链路时间："
									+ socket.getRemoteSocketAddress()
											.toString()
									+ ",new date:"
									+ Tools.formatDate2Str(date,
											"yyyy-MM-dd HH:mm:ss"));
			}
		} else {
			if (log.isDebugEnabled()) {
				if (socket.getRemoteSocketAddress() != null)
					log
							.debug("tcp缓存新增链路："
									+ socket.getRemoteSocketAddress()
											.toString()
									+ ",new date:"
									+ Tools.formatDate2Str(date,
											"yyyy-MM-dd HH:mm:ss"));
			}
		}
		this.tcpAllCache.put(socket, date);
		if (log.isDebugEnabled())
			log.debug("tcp缓存链路总数：" + this.tcpAllCache.size());

	}

	public void checkTcpCache() {
		log.info("Starting check tcpCache!");
		Date curDate = new Date();
		Calendar curcal = Calendar.getInstance();
		curcal.setTime(curDate);

		Set tmpTcpSet = this.tcpAllCache.entrySet();
		List keyList = new ArrayList(tmpTcpSet);
		Iterator iterator = keyList.iterator();
		Calendar conncal = Calendar.getInstance();
		String chkTcp = AllConfigCache.getInstance().getConfigMap().get(
				"checkTcpTime");
		String checkTcpTime = (chkTcp == null || chkTcp.length() <= 0) ? "60"
				: chkTcp;
		int tcpTime = Integer.parseInt(checkTcpTime);

		while (iterator.hasNext()) {
			try {
				Map.Entry met = (Map.Entry) iterator.next();
				Socket client = (Socket) met.getKey();
				Date indate = (Date) met.getValue();

				conncal.setTime(indate);
				conncal.add(Calendar.SECOND, tcpTime);

				if (client.getRemoteSocketAddress() != null) {
					if (log.isDebugEnabled())
						log.debug(client.getRemoteSocketAddress()
								+ " tcpcache socket old  time:"
								+ Tools.formatDate2Str(indate,
										"yyyy-MM-dd HH:mm:ss")
								+ ",tcpcache socket new  time:"
								+ Tools.formatDate2Str(curDate,
										"yyyy-MM-dd HH:mm:ss") + ",时间差："
								+ (curDate.getTime() - indate.getTime()) / 1000
								+ " 秒。");
				}
				
				
				

				if (curcal.compareTo(conncal) > 0) {// tcpTime分钟内无新数据则从列表中剔除

					this.tcpAllCache.remove(client);
					
					if (client != null && !client.isClosed()) {
						if (log.isDebugEnabled())
							log.debug(client.getRemoteSocketAddress()
									+ " 在tcpcache中已" + tcpTime
									+ "秒不活动，从缓存中删除并关闭该链路。");
						
						try {
							String deviceId = GprsTcpThreadList.getInstance()
									.getTcpLinkList().get(client);
							if (deviceId != null){
								GprsTcpThreadList.getInstance().getTcpLink()
										.remove(deviceId);
								DBService service = (DBService) AppCtxServer.getInstance().getBean(Const.SPRING_BEAN_NAME_DBSERVICE);
								service.saveTermOnlineStatus(deviceId, localMtAddr, "0",
										"0", false);
								
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						client.close();
					}

				}

			} catch (Exception e) {
				log.error("check tcplink error", e);
			}
		}

	}

	public static void main(String[] args) {
		ConcurrentHashMap<Socket, Date> tcpAllCache = new ConcurrentHashMap<Socket, Date>();
		Socket socket = new Socket();
		try {
			socket.setSoTimeout(300);
			tcpAllCache.put(socket, new Date());
			Thread.sleep(5000);
			socket.setSoTimeout(800);
			tcpAllCache.put(socket, new Date());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
