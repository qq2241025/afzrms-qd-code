/**
 * 
 */
package com.mapabc.gater.directl.encode.wxp;

import java.util.Date;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.LocatorAdapter;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;


/**
 * @author shiguang.zhou
 * 
 */
public class WxpLocator extends LocatorAdapter {
	// 点名
	public String locate(Request req) {
		String deviceId = req.getDeviceId();
		while (deviceId.length() < 15) {
			deviceId += " ";
		}
		// 源手机号码
		String ysim = "00000000000    ";

		String interHex = Tools.convertToHex("0", 4);
		String data = Tools.bytesToHexString((deviceId + ysim).getBytes());

		String userData = WxpUtil.crtSmsPtl("2700", "1070", "01", "11", data);
		String ret = WxpUtil.crtProtocal("03", "04", "83", userData);
		return ret;
	}

	// 定时定位
	public String timeInter(Request req) {

		// String s = this.defaultSet(req);
		// if (s != null)
		// return s;

		String deviceId = req.getDeviceId();
		while (deviceId.length() < 15) {
			deviceId += " ";
		}
		// 源手机号码
		String ysim = "00000000000    ";
		String interval = RequestUtil.getReqData(req, "interval");
		String count = RequestUtil.getReqData(req, "count");
		int cnt = Integer.parseInt(count);
		if (cnt <= 0)
			count = "0";

		String interHex = Tools.convertToHex(interval, 4);
		String data = Tools.bytesToHexString((deviceId + ysim).getBytes());
		data += "420000" + interHex + "0000" + Tools.convertToHex(count, 4);
		// data += Tools.bytesToHexString("111111".getBytes())+"01020e";
		// String param = "0101"+"00000000"+"02"+"42"+"0000" + interHex + "0000"
		// ;
		// data += param;
		String userData = WxpUtil.crtSmsPtl("2700", "1070", "01", "01", data);
		String ret = WxpUtil.crtProtocal("03", "04", "83", userData);
		return ret;

	}

	public String blindSampleInter(Request req) {
	

		String deviceId = req.getDeviceId();
		while (deviceId.length() < 15) {
			deviceId += " ";
		}
		// 源手机号码
		String ysim = "00000000000    ";
		String interval = RequestUtil.getReqData(req, "interval");
 
		 

		String interHex = Tools.convertToHex(interval, 4);
		String data = Tools.bytesToHexString((deviceId + ysim).getBytes());
		data +=interHex  ;
		 
		String userData = WxpUtil.crtSmsPtl("2700", "1070", "10", "12", data);
		String ret = WxpUtil.crtProtocal("03", "04", "83", userData);
		return ret;
	}

	public static void main(String[] args) {
		WxpLocator l = new WxpLocator();
		System.out.println(l.timeLocate(null));
	}

}
