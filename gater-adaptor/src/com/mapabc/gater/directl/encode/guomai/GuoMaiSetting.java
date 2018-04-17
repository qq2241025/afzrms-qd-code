/**
 * 
 */
package com.mapabc.gater.directl.encode.guomai;

import java.io.UnsupportedEncodingException;

import com.mapabc.gater.directl.Log;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.constant.StructionType;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;
import com.mapabc.gater.directl.encode.SettingAdaptor;

/**
 * @author shiguang.zhou
 * 
 */
public class GuoMaiSetting extends SettingAdaptor {
	private String centerId = "1";
	private String centerPwd = "";
	private String oemCode = "1";

	public String heartSetting(Request req) { 
		//req.setCmdType("507");
		//req.setCmdId("f8");

		String deviceId = req.getDeviceId();
		String seq = Tools.getRandomString(2);
		String interval = RequestUtil.getReqData(req, "interval");
		String type = RequestUtil.getReqData(req, "type");
		String protocalNo = "f8";
		String stype = Tools.convertToHex(type, 2);
		String ptlCont = stype + Tools.convertToHex(interval, 4);
		byte[] ptlByte = Tools.fromHexString(ptlCont);
		String typeCode = req.getDeviceType();

		if (typeCode.equals("GP-UCSTC-GPRS")) {
			centerPwd = Integer.parseInt("000007D9", 16) + "";
		} else {
			centerPwd = deviceId;
		}
		String hex = GuoMaiProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, centerPwd, protocalNo, ptlByte);

 
		return hex;
	}

	public String camera(Request req) {
		String seqs = RequestUtil.getReqData(req, "sequence");
		 
		String hex = "";
		String deviceId = req.getDeviceId();
		String seq = Tools.getRandomString(2);
		String funtype = RequestUtil.getReqData(req, "funType");
		String type = RequestUtil.getReqData(req, "cameraType");
		String channel = RequestUtil.getReqData(req, "chanel");
		String interval = RequestUtil.getReqData(req, "interval");
		String count = RequestUtil.getReqData(req, "count");
		String channelCnt = "1";// RequestUtil.getReqData(req, "chanelCnt");
		String ptlno = "f5";

		if (funtype.equals("1")) {// 拍照设置
			//req.setCmdType("518");
			//req.setCmdId("f5");
			if (channel == "")
				channel = "0";
			if (count == "")
				count = "1";
			String ptlcont = "";

			if (type.equals("1")) {// 单拍

				ptlcont = Tools.convertToHex(channel, 2)
						+ Tools.convertToHex(count, 2)
						+ Tools.convertToHex(channelCnt, 2);
				ptlcont = "01" + ptlcont + "00";
			} else if (type.equals("2")) {// 持续拍照
				int val = Integer.parseInt(interval) / 60;
				
				if (val <= 0)
					interval = "1";
				else
					interval = val + "";

				ptlcont = Tools.convertToHex(channel, 2)
						+ Tools.convertToHex(interval, 2)
						+ Tools.convertToHex(channelCnt, 2);
				ptlcont = "05" + ptlcont + "01";
			}
			byte[] ptlbytes = Tools.fromHexString(ptlcont);
			String typeCode = req.getDeviceType();

			if (typeCode.equals("GP-UCSTC-GPRS")) {
				centerPwd = Integer.parseInt("000007D9", 16) + "";
			} else {
				centerPwd = deviceId;
			}
			hex = GuoMaiProtocalUtil.createMtProtocal(seq, oemCode, deviceId,
					centerId, centerPwd, ptlno, ptlbytes);

		}

		//Log.getInstance().guomaiLog(deviceId + " 拍照指令：" + hex);
		return hex;

	}

	public String addrSetting(Request req) { 

		String deviceId = req.getDeviceId();
		String seq = Tools.getRandomString(2);
		String ip = RequestUtil.getReqData(req, "ip");
		String[] ips = ip.split("\\.");
		String sip = "";
		int i = 0;
		while (i < ips.length) {
			sip += Tools.convertToHex(ips[i], 2);
			i++;
		}

		String port = RequestUtil.getReqData(req, "port");
		String sport = Tools.convertToHex(port, 4);

		String ptlCont = "";
		if (req.getDeviceType().equals("GP-UCSTC-GPRS")) {
			ptlCont = Tools.convertToHex("10", 2) + sip + sport;
		} else {
			String large = RequestUtil.getReqData(req, "largeImagePort");
			String slarge = Tools.convertToHex(large, 4);

			String little = RequestUtil.getReqData(req, "littleImagePort");
			String slittle = Tools.convertToHex(little, 4);
			ptlCont = Tools.convertToHex("10", 2) + sip + sport + slarge
					+ slittle;
		}

		byte[] ptlByte = Tools.fromHexString(ptlCont);
		String typeCode = req.getDeviceType();

		if (typeCode.equals("GP-UCSTC-GPRS")) {
			centerPwd = Integer.parseInt("000007D9", 16) + "";
		} else {
			centerPwd = deviceId;
		}
		String hex = GuoMaiProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, centerPwd, "1b", ptlByte);

		//Log.getInstance().guomaiLog(deviceId + " 通讯地址设置指令：" + hex);
		return hex;
	}

	// 时钟校对设置指令
	public String clockSetting(Request req) {
		 

		String deviceId = req.getDeviceId();
		String seq = Tools.getRandomString(2);
		String date = RequestUtil.getReqData(req, "date");

		String ptlCont = Tools.convertToHex("9", 2) + date;
		byte[] ptlByte = Tools.fromHexString(ptlCont);
		String typeCode = req.getDeviceType();

		if (typeCode.equals("GP-UCSTC-GPRS")) {
			centerPwd = Integer.parseInt("000007D9", 16) + "";
		} else {
			centerPwd = deviceId;
		}
		String hex = GuoMaiProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, centerPwd, "1b", ptlByte);

		//Log.getInstance().guomaiLog(deviceId + " 时钟校对设置指令：" + hex);
		return hex;
	}

	// 驾驶员代码设置
	public String driverCodeSetting(Request req) { 
		String typeCode = req.getDeviceType();
 
		String deviceId = req.getDeviceId();
		String seq = Tools.getRandomString(2);
		String code = RequestUtil.getReqData(req, "code");
		if (typeCode.equals("GP-GUOMAIC-GPRS")) {
			while (code.length() < 12) {
				code = "0" + code;
			}
			//req.setCmdId("1b09");
		} else if (typeCode.equals("GP-GUOMAIP-GPRS")) {
			code = Tools.convertToHex(code, 12);
			//req.setCmdId("1b09");
		} else if (typeCode.equals("GP-UCSTC-GPRS")) {
			while (code.length() < 12) {
				code = "0" + code;
			}
			//req.setCmdId("1b04");
		}
		// if (code.length()>12){
		// code = code.substring(0, 13);
		// }
		String ptlCont = Tools.convertToHex("4", 2) + code;// Tools.convertToHex(code,
		// 12);
		//Log.getInstance().guomaiLog("driverCode:" + ptlCont);
		byte[] ptlByte = Tools.fromHexString(ptlCont);

		if (typeCode.equals("GP-UCSTC-GPRS")) {
			centerPwd = Integer.parseInt("000007D9", 16) + "";
		} else {
			centerPwd = deviceId;
		}
		String hex = GuoMaiProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, centerPwd, "1b", ptlByte);

		//Log.getInstance().guomaiLog(deviceId + " 驾驶员代码设置指令：" + hex);
		return hex;
	}

	// 驾驶证号设置
	public String driverLicenseSetting(Request req) {
		 
		String deviceId = req.getDeviceId();
		String seq = Tools.getRandomString(2);
		String license = RequestUtil.getReqData(req, "license");
		while (license.getBytes().length < 18) {
			license = license + "\0";
		}
		String ptlCont = Tools.convertToHex("5", 2)
				+ Tools.bytesToHexString(license.getBytes());
		byte[] ptlByte = Tools.fromHexString(ptlCont);
		String typeCode = req.getDeviceType();

		if (typeCode.equals("GP-UCSTC-GPRS")) {
			centerPwd = Integer.parseInt("000007D9", 16) + "";
		} else {
			centerPwd = deviceId;
		}
		String hex = GuoMaiProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, centerPwd, "1b", ptlByte);

		//Log.getInstance().guomaiLog(deviceId + " 驾驶证号码置指令：" + hex);
		return hex;
	}

	// 疲劳驾驶设置
	public String fatigueDriveSetting(Request req) {
		 

		String deviceId = req.getDeviceId();
		String seq = Tools.getRandomString(2);
		String interval = RequestUtil.getReqData(req, "maxDriveTime");// X*5MIN

		if (interval == "" || Integer.parseInt(interval) < 30) {
			if (interval.equals("1"))
				interval = "5";
			else
				interval = "30";
		} else if (Integer.parseInt(interval) > 240) {
			interval = "240";
		}
		interval = Integer.parseInt(interval) / 5 + "";

		String restTime = RequestUtil.getReqData(req, "restTime");

		String ptlCont = Tools.convertToHex(interval, 2)
				+ Tools.convertToHex(restTime, 2);
		byte[] ptlByte = Tools.fromHexString(ptlCont);
		String typeCode = req.getDeviceType();

		if (typeCode.equals("GP-UCSTC-GPRS")) {
			centerPwd = Integer.parseInt("000007D9", 16) + "";
		} else {
			centerPwd = deviceId;
		}
		String hex = GuoMaiProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, centerPwd, "14", ptlByte);
 
		return hex;
	}

	// 初次安装日期
	public String installDateSetting(Request req) {
		 
		String deviceId = req.getDeviceId();
		String seq = Tools.getRandomString(2);
		String date = RequestUtil.getReqData(req, "date");

		String ptlCont = Tools.convertToHex("8", 2) + date;
		byte[] ptlByte = Tools.fromHexString(ptlCont);
		String typeCode = req.getDeviceType();

		if (typeCode.equals("GP-UCSTC-GPRS")) {
			centerPwd = Integer.parseInt("000007D9", 16) + "";
		} else {
			centerPwd = deviceId;
		}
		String hex = GuoMaiProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, centerPwd, "1b", ptlByte);

		//Log.getInstance().guomaiLog(deviceId + " 初装时间设置指令：" + hex);
		return hex;
	}

	// 短消息服务中心号码
	public String smsCenterSetting(Request req) {
		 

		String deviceId = req.getDeviceId();
		String seq = Tools.getRandomString(2);
		String number = RequestUtil.getReqData(req, "number");
		while (number.getBytes().length < 15) {
			number = number + "\0";
		}
		String ptlCont = Tools.convertToHex("12", 2)
				+ Tools.bytesToHexString(number.getBytes());
		byte[] ptlByte = Tools.fromHexString(ptlCont);
		String typeCode = req.getDeviceType();

		if (typeCode.equals("GP-UCSTC-GPRS")) {
			centerPwd = Integer.parseInt("000007D9", 16) + "";
		} else {
			centerPwd = deviceId;
		}
		String hex = GuoMaiProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, centerPwd, "1b", ptlByte);

		//Log.getInstance().guomaiLog(deviceId + " 短信中心号码设置指令：" + hex);
		return hex;
	}

	// 车牌分类
	public String vehicleCardTypeSetting(Request req) {
		 

		String deviceId = req.getDeviceId();
		String seq = Tools.getRandomString(2);
		String category = RequestUtil.getReqData(req, "category");
		while (category.getBytes().length < 12) {
			category = category + "\0";
		}
		String ptlCont = "";

		try {
			ptlCont = Tools.convertToHex("3", 2)
					+ Tools.bytesToHexString(category.getBytes("GB2312"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		byte[] ptlByte = Tools.fromHexString(ptlCont);
		String typeCode = req.getDeviceType();

		if (typeCode.equals("GP-UCSTC-GPRS")) {
			centerPwd = Integer.parseInt("000007D9", 16) + "";
		} else {
			centerPwd = deviceId;
		}
		String hex = GuoMaiProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, centerPwd, "1b", ptlByte);

		//Log.getInstance().guomaiLog(deviceId + " 车牌类别设置指令：" + hex);
		return hex;
	}
	
	public String carIdSetting(Request req) {
		 
		String deviceId = req.getDeviceId();
		String seq = Tools.getRandomString(2);
		String id = RequestUtil.getReqData(req, "newId");
		while (id.getBytes().length < 17) {
			id = id + "\0";
		}

		String contHex = Tools.convertToHex("1", 2)
				+ Tools.bytesToHexString(id.getBytes());
		byte[] ptlByte = Tools.fromHexString(contHex);
		String typeCode = req.getDeviceType();

		if (typeCode.equals("GP-UCSTC-GPRS")) {
			centerPwd = Integer.parseInt("000007D9", 16) + "";
		} else {
			centerPwd = deviceId;
		}
		String hex = GuoMaiProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, centerPwd, "1b", ptlByte);

		//Log.getInstance().guomaiLog(deviceId + " 车辆识别代码设置指令：" + hex);
		return hex;
	}
	
	// 车辆识别代号设置
	public String vehicleIdSetting(Request req) {
		String seqs = RequestUtil.getReqData(req, "sequence");
		 
		String deviceId = req.getDeviceId();
		String seq = Tools.getRandomString(2);
		String id = RequestUtil.getReqData(req, "id");
		while (id.getBytes().length < 17) {
			id = id + "\0";
		}

		String contHex = Tools.convertToHex("1", 2)
				+ Tools.bytesToHexString(id.getBytes());
		byte[] ptlByte = Tools.fromHexString(contHex);
		String typeCode = req.getDeviceType();

		if (typeCode.equals("GP-UCSTC-GPRS")) {
			centerPwd = Integer.parseInt("000007D9", 16) + "";
		} else {
			centerPwd = deviceId;
		}
		String hex = GuoMaiProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, centerPwd, "1b", ptlByte);

		//Log.getInstance().guomaiLog(deviceId + " 车辆识别代码设置指令：" + hex);
		return hex;
	}

	// 车牌号码
	public String vehicleCardSetting(Request req) {
		 
		String deviceId = req.getDeviceId();
		String seq = Tools.getRandomString(2);
		String cardName = RequestUtil.getReqData(req, "number");

		while (cardName.getBytes().length < 12) {
			cardName = cardName + "\0";
		}

		String contHex = "";
		try {
			contHex = Tools.convertToHex("2", 2)
					+ Tools.bytesToHexString(cardName.getBytes("GB2312"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		byte[] ptlByte = Tools.fromHexString(contHex);
		String typeCode = req.getDeviceType();

		if (typeCode.equals("GP-UCSTC-GPRS")) {
			centerPwd = Integer.parseInt("000007D9", 16) + "";
		} else {
			centerPwd = deviceId;
		}
		String hex = GuoMaiProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, centerPwd, "1b", ptlByte);

		//Log.getInstance().guomaiLog(deviceId + " 车辆号码设置指令：" + hex);
		return hex;
	}

	// 特征系数设置
	public String featureCofficient(Request req) {
		 

		String deviceId = req.getDeviceId();
		String seq = Tools.getRandomString(2);
		String cofficient = RequestUtil.getReqData(req, "cofficient");

		String contHex = "";

		contHex = Tools.convertToHex("15", 2)
				+ Tools.convertToHex(cofficient, 6);

		byte[] ptlByte = Tools.fromHexString(contHex);
		String typeCode = req.getDeviceType();

		if (typeCode.equals("GP-UCSTC-GPRS")) {
			centerPwd = Integer.parseInt("000007D9", 16) + "";
		} else {
			centerPwd = deviceId;
		}
		String hex = GuoMaiProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, centerPwd, "1b", ptlByte);

		//Log.getInstance().guomaiLog(deviceId + " 特征系数设置指令：" + hex);
		return hex;
	}

	// 熄火上报间隔设置
	public String flameOutInterSetting(Request req) {
		 
		String deviceId = req.getDeviceId();
		String seq = Tools.getRandomString(2);
		String interval = RequestUtil.getReqData(req, "interval");

		String ptlCont = Tools.convertToHex("0", 2)
				+ Tools.convertToHex(interval, 4);
		byte[] ptlByte = Tools.fromHexString(ptlCont);
		String typeCode = req.getDeviceType();

		if (typeCode.equals("GP-UCSTC-GPRS")) {
			centerPwd = Integer.parseInt("000007D9", 16) + "";
		} else {
			centerPwd = deviceId;
		}
		String hex = GuoMaiProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, centerPwd, "f8", ptlByte);

	 
		return hex;

	}

	// 设置当前GPS的总里程
	public String totalMileageSetting(Request req) {

		 
		String deviceId = req.getDeviceId();
		String seq = Tools.getRandomString(2);
		String distance = RequestUtil.getReqData(req, "totalNum");

		String ptlCont = Tools.convertToHex("2", 2)
				+ Tools.convertToHex(distance, 8);
		byte[] ptlByte = Tools.fromHexString(ptlCont);
		String typeCode = req.getDeviceType();

		if (typeCode.equals("GP-UCSTC-GPRS")) {
			centerPwd = Integer.parseInt("000007D9", 16) + "";
		} else {
			centerPwd = deviceId;
		}
		String hex = GuoMaiProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, centerPwd, "f8", ptlByte);

		 
		return hex;

	}

	// 设置普通定位包中里程的上报类型
	public String mileageTypeSetting(Request req) {

		 
		String deviceId = req.getDeviceId();
		String seq = Tools.getRandomString(2);
		String type = RequestUtil.getReqData(req, "type");

		String hexType = "";
		if (type.equals("0")) {
			hexType = "AA";
		} else {
			hexType = "55";
		}

		String ptlCont = Tools.convertToHex("3", 2) + hexType;
		byte[] ptlByte = Tools.fromHexString(ptlCont);
		String typeCode = req.getDeviceType();

		if (typeCode.equals("GP-UCSTC-GPRS")) {
			centerPwd = Integer.parseInt("000007D9", 16) + "";
		} else {
			centerPwd = deviceId;
		}
		String hex = GuoMaiProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, centerPwd, "f8", ptlByte);

		 
		return hex;

	}

	public static void main(String[] args) {
		System.out.println(Integer.parseInt("0001C2", 16));
		System.out.println(Tools.convertToHex("450", 6));
	}
}
