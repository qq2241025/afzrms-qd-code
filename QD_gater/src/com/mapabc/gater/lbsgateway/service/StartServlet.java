package com.mapabc.gater.lbsgateway.service;

import java.util.Date;
import java.util.Timer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.LogFactory;

import com.mapabc.AppCtxServer;
import com.mapabc.gater.directl.AllConfigCache;
import com.mapabc.gater.directl.encode.Controller;
import com.mapabc.gater.lbsgateway.GBLTerminalList;
import com.mapabc.gater.lbsgateway.PrepareParse;
import com.mapabc.gater.lbsgateway.alarmpool.GpsAlarmServer;
import com.mapabc.gater.lbsgateway.bean.ConnectionInfo;
import com.mapabc.gater.lbsgateway.gprsserver.StartServer;
import com.mapabc.gater.lbsgateway.gprsserver.tcp.GprsTcpServer;
import com.mapabc.gater.lbsgateway.gprsserver.udp.GprsServer;
import com.mapabc.gater.lbsgateway.service.flowvolume.StartFlowServer;
import com.mapabc.gater.lbsgateway.service.online.StartInitOlineService;
import com.mapabc.gater.util.DBTableConfig;

/**
 * <p>
 * Title: 服务管理Servlet
 * </p>
 * 
 * <p>
 * Description: 本类负责通讯网关的初始化，实现了从数据库中加载终端信息和负责内存的垃圾回收
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author
 * @version 1.0
 */

public class StartServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static org.apache.commons.logging.Log log = LogFactory.getLog(StartServlet.class);

	// private ArrayList<SimpleRead> srList = new ArrayList<SimpleRead>();

	/**
	 * 
	 * 通讯启动入口
	 * 
	 * @throws ServletException
	 */
	public void init() throws ServletException {
		// new TrimTrackServer().start();
		// 加载所有property配置文件信息到缓存
		try {
			AllConfigCache.getInstance();
			AllConfigCache.loadAllConfig();
		} catch (Exception e) {
		}
		try {
			try {
				AppCtxServer.getInstance().initSystem(getServletContext());
				String dbOff = AllConfigCache.getInstance().getConfigMap().get("dbSaveOff");
				// 加载DB结构配置信息
				if (dbOff != null && dbOff.equals("0")) {
					log.info("-------------------DBTableConfig loadXml---------------");
					DBTableConfig.getInstance().loadXml();
				}
				GBLTerminalList.getInstance().loadTerminals(); // 把所有终端信息加载到内存
			} catch (Exception e) {
			}

			ConnectionInfo[] coninfs = PrepareParse.getInstance().loadTermConfig();
			for (int i = 0; i < coninfs.length; i++) {
				ConnectionInfo info = coninfs[i];
				String port = info.getPort();
				String type = info.getType();
				if (port == null || port.trim().length() <= 0 || type == null || type.trim().length() <= 0) {
					continue;
				}

				if (type.equals("0")) {// TCP
					StartServer tcpServer = new GprsTcpServer(Integer.parseInt(port), info.getParsePoolNum(),
							info.getDbPoolNum(), info.getReadDataPoolNum(), null);
					tcpServer.start();
				} else if (type.equals("1")) {// UDP
					StartServer udpServer = new GprsServer(Integer.parseInt(port), info.getParsePoolNum(),
							info.getDbPoolNum(), info.getReadDataPoolNum(), null);
					udpServer.start();
				} else if (type.equals("2")) {// Serial
					// SimpleRead sr = new SimpleRead(port,40,20);
					// Thread thread = new Thread(sr);
					// thread.start();
					// srList.add(sr);
				}
			}
			try {
				// 报警入库服务
				GpsAlarmServer.start();
			} catch (Exception e) {
			}
			try {
				// 重载终端信息定时器
				String isReload = AllConfigCache.getInstance().getConfigMap().get("isReloadTerm");
				if (isReload != null && isReload.equals("1")) {
					String sval = AllConfigCache.getInstance().getConfigMap().get("reloadTerminalInterval");
					int ival = Integer.parseInt(sval);

					Timer timerReload = new Timer();
					timerReload.schedule(new DynamicLoadTermList(), new Date(System.currentTimeMillis() + 60 * 1000),
							ival * 1000);
				}
			} catch (Exception e) {
			}
			/*
			try {
				// 流量入库
				StartFlowServer.start();
			} catch (Exception e) {
			}

			try {
				// 初始化终端在线状态表
				StartInitOlineService.start();
			} catch (Exception e) {
			}
			try {
				// 下行缓存服务
				Controller.startOrderListen();
			} catch (Exception e) {
			}
			// try {
			// // 加载数据库中最近位置表到缓存
			// GBLTerminalList.getInstance().loadAllLastLoc();
			// } catch (Exception e) {
			// }
			// try {
			// // 加载数据库中最近状态表到缓存
			// GBLTerminalList.getInstance().loadAllLastStatus();
			// } catch (Exception e) {
			// }
*/
		} catch (Exception e) {
			e.printStackTrace();
			log.error("启动通讯服务异常", e);
		}
		System.out.println("StartServlet load finished!");
	}

	// Clean up resources
	public void destroy() {

	}

}
