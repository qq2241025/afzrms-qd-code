package com.mapabc.gater.lbsgateway.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.TimerTask;

import org.apache.commons.logging.LogFactory;

import com.mapabc.gater.directl.ParseConfigParamUtil;

import com.mapabc.gater.lbsgateway.TcpLinkCache;
import com.mapabc.gater.lbsgateway.bean.TerminalUDPAddress;

public class TermStateService extends TimerTask {
	private static org.apache.commons.logging.Log log=LogFactory.getLog(TermStateService.class);

	public TermStateService() {
		log.info("启动终端状态检测服务！");
	}

	@Override
	public void run() {
		try{
		TcpLinkCache.getInstance().checkTcpCache();
		com.mapabc.gater.lbsgateway.GprsTcpThreadList.getInstance().checkTcpLinkList();
//
//		com.mapabc.gater.lbsgateway.GprsTcpThreadCsList.getInstance()
//				.checkTCPState();

		com.mapabc.gater.lbsgateway.GprsUdpThreadList.getInstance().checkUDPState();
		}catch(Exception e){
			log.error("timetask error", e);
			e.printStackTrace();
		}

	}

}
