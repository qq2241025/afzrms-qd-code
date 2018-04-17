package com.mapabc.gater.directl.encode.wxp;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.QueryAdaptor;
import com.mapabc.gater.directl.encode.Request;



public class WxpQuery extends QueryAdaptor {
	
	public String queryVersion(Request req) {
		String deviceId = req.getDeviceId();
		while (deviceId.length() < 15) {
			deviceId += " ";
		}
		// 源手机号码
		String ysim = "00000000000    ";
 		
		String data = Tools.bytesToHexString((deviceId + ysim).getBytes());
		 
		String userData = WxpUtil.crtSmsPtl("2700", "1070", "10", "22", data);
		String ret = WxpUtil.crtProtocal("03", "04", "83", userData);
		return ret;
	}
	
	public String workStatus(Request req) {
		String deviceId = req.getDeviceId();
		while (deviceId.length() < 15) {
			deviceId += " ";
		}
		// 源手机号码
		String ysim = "00000000000    ";
 		
		String data = Tools.bytesToHexString((deviceId + ysim).getBytes());
		 
		String userData = WxpUtil.crtSmsPtl("2700", "1070", "10", "1a", data);
		String ret = WxpUtil.crtProtocal("03", "04", "83", userData);
		return ret;
	}
	

}
