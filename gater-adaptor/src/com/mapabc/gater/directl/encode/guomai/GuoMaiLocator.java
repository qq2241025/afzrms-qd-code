/**
 * 
 */
package com.mapabc.gater.directl.encode.guomai;

import java.util.HashMap;

import com.mapabc.gater.directl.Log;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.LocatorAdapter;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;

/**
 * @author shiguang.zhou
 * 
 */
public class GuoMaiLocator extends LocatorAdapter {

	private String centerId = "1";
	private String centerPwd = "";
	private String oemCode = "1";

	public GuoMaiLocator() {

	}

	public String distanceInter(Request req) {
		String seqs = RequestUtil.getReqData(req, "sequence");
		 
		
		String interval = (String) req.getDatas().get("interval"); // 50-9999m
		String count = RequestUtil.getReqData(req, "count");
		int icnt = 0;
		int ival = 0;
		String hexval = "";
 		String cntHex = "";
 		
		if (count !="" && count !="0")
			icnt=Integer.parseInt(count);
		else 
			icnt = 65535;
		
		if (interval !="")
		  ival = Integer.parseInt(interval);
		

		if (ival < 50) {//最小50m
			ival = 50;
		} else if (ival > 9999) {
			ival = 9999;
		}  
 
		String seq = Tools.getRandomString(2); 
 		String deviceId = req.getDeviceId();
 		hexval = Tools.int2Hexstring(ival, 4);
 		cntHex = Tools.int2Hexstring(icnt, 4);
 		
		String protocalNo = "04";
 		String protocalCont = hexval +"00"+ cntHex  ;
 		byte[] ptlByte = Tools.fromHexString(protocalCont);
 		

		String typeCode = req.getDeviceType();

		if (typeCode.equals("GP-UCSTC-GPRS")){
			centerPwd = Integer.parseInt("000007D9", 16)+"";
		}else {
			centerPwd = deviceId;
		}

		String hex = GuoMaiProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, centerPwd, protocalNo, ptlByte);
		//Log.getInstance().guomaiLog(deviceId + " 设置距离回传指令：" + hex);

		return hex;
	}

	public String locate(Request req) {
		String seqs = RequestUtil.getReqData(req, "sequence");
	 
		String seq = Tools.getRandomString(2);// RequestUtil.getReqData(req,
												// "sequence");
		String protocalNo = "01";
		String deviceId = req.getDeviceId();
		String typeCode = req.getDeviceType();

		
		if (typeCode.equals("GP-UCSTC-GPRS")){
			centerPwd = Integer.parseInt("000007D9", 16)+"";
		}else {
			centerPwd = deviceId;
		}

		String hex = GuoMaiProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, centerPwd, protocalNo, null);

		//Log.getInstance().guomaiLog(deviceId + " 点名指令：" + hex);

		return hex;
	}

	public String timeInter(Request req) {
		  
				
		String interval = (String) req.getDatas().get("interval"); // 1-255s
		String count = RequestUtil.getReqData(req, "count");
		int icnt = 0;
		int ival = 0;
		String hexval = "";
 		String cntHex = "";
 		
 		if (count != "")
			icnt = Integer.parseInt(count);//次数
 		else
 			icnt = 65535;//按频率连续报
 		
 		if (interval != "")
			ival = Integer.parseInt(interval);//间隔
 		else 
 			ival = 0;
 		
 		 if (ival < 1) {
 			ival = 0;
 			icnt = 0;
		} 
		if (ival > 255) {
			ival = 255;
 		}  
		if (icnt <= 0)
			icnt = 65535;//按频率连续报
		
		String typeCode = req.getDeviceType();
		String deviceId = req.getDeviceId();
		if (typeCode.equals("GP-UCSTC-GPRS")){
			centerPwd = Integer.parseInt("000007D9", 16)+"";
		}else{
			centerPwd = deviceId;
		}
 	    hexval = Tools.int2Hexstring(ival, 2);
  		cntHex = Tools.int2Hexstring(icnt, 4);
  		String seq = Tools.getRandomString(2); 

 		String protocalNo = "02";
 		String protocalCont = hexval + "00"+ cntHex ;
 		byte[] ptlByte = Tools.fromHexString(protocalCont);
		String hex = GuoMaiProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, centerPwd, protocalNo, ptlByte);

		//Log.getInstance().guomaiLog(deviceId + " 频率设置指令：" + hex);

		return hex;

	}

	public static void main(String[] args) { }

}
