package com.mapabc.gater.lbsgateway.gprsserver;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.logging.LogFactory;
import com.mapabc.gater.directl.AllConfigCache;
import com.mapabc.gater.directl.ParseConfigParamUtil;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.parse.ParseBase;
import com.mapabc.gater.lbsgateway.GBLTerminalList;
import com.mapabc.gater.lbsgateway.bean.TTerminal;
import com.mapabc.gater.lbsgateway.poolsave.BatchSaveUtil;
import com.mapabc.gater.lbsgateway.poolsave.GpsData;

public class ForwardServer {
	
	private static org.apache.commons.logging.Log log=LogFactory.getLog(ForwardServer.class);


	public static void forward(byte[] msg, ParseBase pb) {
//		if (!com.mapabc.proxy.config.Config.isStart) {
//			return;
//		}
//		String deviceId = pb.getDeviceSN();
//		TTerminal terminal = GBLTerminalList.getInstance().getTerminaInfo(deviceId);
//		String eid = terminal.getEntCode();
//		
//		GpsData gpsData = BatchSaveUtil.getGpsDataFromParseBase(pb);
//		GpsInfo gps = getGpsInfo(gpsData);
//		try {
//			ProxyForwardService.receive(msg, gps, deviceId, eid);
//			 
//			log.info(
//					"发送信息到代理转发服务成功, msg:" + Tools.bytesToHexString(msg) + ", deviceId:" + deviceId
//							+ ", eid:" + eid);
//		} catch (Exception e) {
//			log.error(
//					"发送信息到代理转发服务失败, msg:" + Tools.bytesToHexString(msg) + ", deviceId:" + deviceId
//							+ ", eid:" + eid, e);
//		}
	}

//	private static GpsInfo getGpsInfo(GpsData gpsData) {
//		GpsInfo gps = new GpsInfo();
//		if(null == gpsData){
//			return gps;
//		}
//		
//		gps.setDeviceId(gpsData.getDEVICE_ID());
//		gps.setSimcard(gpsData.getSIMCARD());
//		gps.setId(gpsData.getId());
//		gps.setX(gpsData.getX());
//		gps.setY(gpsData.getY());
//		gps.setSpeed(gpsData.getS());
//		gps.setDirection(gpsData.getDirection());
//		gps.setHeight(gpsData.getH());
//		gps.setMile(gpsData.getMileage());
//		gps.setTime(gpsData.getTIME());
//		//gps.setSatelliteNum(gpsData.getStarSum());
//		gps.setAddress(gpsData.getAdress());
//		gps.setStatus(gpsData.getStatus());
//		
//		return gps;
//	}
	
	

	
	
}
