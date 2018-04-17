/**
 * 
 */
package com.mapabc.gater.directl.encode.ucst;

import java.io.UnsupportedEncodingException;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;
import com.mapabc.gater.directl.encode.SettingAdaptor;
 

/**
 * @author shiguang.zhou
 * 
 */
public class UcstSetting extends SettingAdaptor {
	private String centerId = "1";
	private String centerPwd = "";
	private String oemCode = "1";

	public String heartSetting(Request req) {
		return null;
	}

	public String apnSetting(Request req) {
	 

		String deviceId = req.getDeviceId();
		String seq = Tools.getRandomString(2);
		String apn = RequestUtil.getReqData(req, "apn");
		while (apn.length() < 40) {
			apn =   apn+"\0";
		}

		String ptlCont = "01"+Tools.convertToHex("21", 2)+Tools.int2Hexstring(apn.getBytes().length, 2)
				+ Tools.bytesToHexString(apn.getBytes());
		byte[] ptlByte = Tools.fromHexString(ptlCont);

		String hex = UcstProtocalUtil.createMtProtocal(seq, oemCode, deviceId,
				centerId, deviceId, "1b", ptlByte);

		//Log.getInstance().ucstLog(deviceId + " 设置APN：" + hex);
		return hex;
	}

	public String camera(Request req) {
		 
		 
		String hex = "";
		String deviceId = req.getDeviceId();
		String seq = Tools.getRandomString(2);
		String funtype = RequestUtil.getReqData(req, "funType");
		String type = RequestUtil.getReqData(req, "cameraType");
		String channel = RequestUtil.getReqData(req, "chanel");
		String interval = RequestUtil.getReqData(req, "interval");
		String count = RequestUtil.getReqData(req, "count");
		String channelCnt = RequestUtil.getReqData(req, "chanelCnt");
		String condition = RequestUtil.getReqData(req, "condition");
		String isupload = RequestUtil.getReqData(req, "isUpload");
		String ptlno = "f5";

		if (funtype.equals("1")) {// 拍照设置
			//req.setCmdType("518");
			String ptlcont = "";
			String ptlNo = "";
			if (type.equals("3")) {//报警拍照张数
				if (condition.equals("2") && count != "") {
					//req.setCmdId("1b" + Tools.convertToHex("24", 2));
					ptlcont = Tools.convertToHex("24", 2)
							+ Tools.convertToHex(count, 2);
					ptlNo ="1b";
				} else if (condition.equals("2") && interval != "") {//报警拍照间隔
					//req.setCmdId("1b" + Tools.convertToHex("25", 2));
					ptlcont = Tools.convertToHex("25", 2)
							+ Tools.convertToHex(interval, 2);
					ptlNo ="1b";
				}
				
			}else if(type.equals("1")){//拍照立即上传
				 
				ptlNo ="f7";
				ptlcont = Tools.convertToHex(isupload, 2);
			}
			//req.setCmdId(ptlNo);
			byte[] ptlbytes = Tools.fromHexString(ptlcont);
			hex = UcstProtocalUtil.createMtProtocal(seq, oemCode, deviceId,
					centerId, centerPwd, ptlNo, ptlbytes);

		}

		//Log.getInstance().ucstLog(deviceId + " 拍照指令：" + hex);
		return hex;

	}

	public String addrSetting(Request req) {
		String seqs = RequestUtil.getReqData(req, "sequence");
		 

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
 		 

		String ptlCont = "01" + Tools.convertToHex("10", 2) + Tools.int2Hexstring((sip+sport).length()/2, 2)+sip + sport ;
		byte[] ptlByte = Tools.fromHexString(ptlCont);

		String hex = UcstProtocalUtil.createMtProtocal(seq, oemCode, deviceId,
				centerId, deviceId, "1b", ptlByte);

		//Log.getInstance().ucstLog(deviceId + " 通讯地址设置指令：" + hex);
		return hex;
	}

	public String clockSetting(Request req) {
		//Log.getInstance().ucstLog(req.getDeviceId()+" 时钟校对为只读功能，不能设置。");
		return null;
	}

	public String driverCodeSetting(Request req) {
		String seqs = RequestUtil.getReqData(req, "sequence");
		String typeCode = req.getDeviceType();

		 

		String deviceId = req.getDeviceId();
		String seq = Tools.getRandomString(2);
		String code = RequestUtil.getReqData(req, "code");
		 
			while (code.length() < 12) {
				code = "0" + code;
			}
		 
		// if (code.length()>12){
		// code = code.substring(0, 13);
		// }
		String ptlCont = "01"+Tools.convertToHex("4", 2) +"0c"+ code;// Tools.convertToHex(code,
		// 12);
		//Log.getInstance().ucstLog("driverCode:" + ptlCont);
		byte[] ptlByte = Tools.fromHexString(ptlCont);

		String hex = UcstProtocalUtil.createMtProtocal(seq, oemCode, deviceId,
				centerId, deviceId, "1b", ptlByte);

		//Log.getInstance().ucstLog(deviceId + " 驾驶员代码设置指令：" + hex);
		return hex;
	}

	public String driverLicenseSetting(Request req) {
		String seqs = RequestUtil.getReqData(req, "sequence");
		 
		//req.setCmdType("513");
		//req.setCmdId("1b");
		String deviceId = req.getDeviceId();
		String seq = Tools.getRandomString(2);
		String license = RequestUtil.getReqData(req, "license");
		while (license.getBytes().length < 18) {
			license = license + "\0";
		}
		String ptlCont = "01"+Tools.convertToHex("5", 2)+Tools.int2Hexstring(license.getBytes().length, 2)
				+ Tools.bytesToHexString(license.getBytes());
		byte[] ptlByte = Tools.fromHexString(ptlCont);

		String hex = UcstProtocalUtil.createMtProtocal(seq, oemCode, deviceId,
				centerId, deviceId, "1b", ptlByte);

		//Log.getInstance().ucstLog(deviceId + " 驾驶证号码置指令：" + hex);
		return hex;
	}

	// 疲劳驾驶设置
	public String fatigueDriveSetting(Request req) {
		String seqs = RequestUtil.getReqData(req, "sequence");
		 

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

		String hex = UcstProtocalUtil.createMtProtocal(seq, oemCode, deviceId,
				centerId, deviceId, "14", ptlByte);

		 

		return hex;
	}

	public String installDateSetting(Request req) {
  		return null;
	}

	public String smsCenterSetting(Request req) {
		String seqs = RequestUtil.getReqData(req, "sequence");
		 

		String deviceId = req.getDeviceId();
		String seq = Tools.getRandomString(2);
		String number = RequestUtil.getReqData(req, "number");
		while (number.getBytes().length < 15) {
			number = number + "\0";
		}
		String ptlCont = "01"+Tools.convertToHex("12", 2)+"0f"
				+ Tools.bytesToHexString(number.getBytes());
		byte[] ptlByte = Tools.fromHexString(ptlCont);

		String hex = UcstProtocalUtil.createMtProtocal(seq, oemCode, deviceId,
				centerId, deviceId, "1b", ptlByte);

		//Log.getInstance().ucstLog(deviceId + " 短信中心号码设置指令：" + hex);
		return hex;
	}

	public String vehicleCardTypeSetting(Request req) {
		String seqs = RequestUtil.getReqData(req, "sequence");
		 

		String deviceId = req.getDeviceId();
		String seq = Tools.getRandomString(2);
		String category = RequestUtil.getReqData(req, "category");
		while (category.getBytes().length < 12) {
			category = category + "\0";
		}
		String ptlCont = "";

		try {
			ptlCont = "01"+Tools.convertToHex("3", 2)+Tools.int2Hexstring(category.getBytes("GB2312").length, 2)
					+ Tools.bytesToHexString(category.getBytes("GB2312"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		byte[] ptlByte = Tools.fromHexString(ptlCont);

		String hex = UcstProtocalUtil.createMtProtocal(seq, oemCode, deviceId,
				centerId, deviceId, "1b", ptlByte);

		//Log.getInstance().ucstLog(deviceId + " 车牌类别设置指令：" + hex);
		return hex;
	}
	public String carIdSetting(Request req) {
		String seqs = RequestUtil.getReqData(req, "sequence");
		 
		String deviceId = req.getDeviceId();
		String seq = Tools.getRandomString(2);
		String id = RequestUtil.getReqData(req, "newId");
		while (id.getBytes().length < 17) {
			id = id + "\0";
		}

		String contHex = "01"+Tools.convertToHex("1", 2)+Tools.int2Hexstring(id.getBytes().length, 2)
				+ Tools.bytesToHexString(id.getBytes());
		byte[] ptlByte = Tools.fromHexString(contHex);

		String hex = UcstProtocalUtil.createMtProtocal(seq, oemCode, deviceId,
				centerId, deviceId, "1b", ptlByte);

		//Log.getInstance().ucstLog(deviceId + " 车辆识别代码设置指令：" + hex);
		return hex;
	}
	public String vehicleIdSetting(Request req) {
		String seqs = RequestUtil.getReqData(req, "sequence");
		 
		String deviceId = req.getDeviceId();
		String seq = Tools.getRandomString(2);
		String id = RequestUtil.getReqData(req, "id");
		while (id.getBytes().length < 17) {
			id = id + "\0";
		}

		String contHex = "01"+Tools.convertToHex("1", 2)+Tools.int2Hexstring(id.getBytes().length, 2)
				+ Tools.bytesToHexString(id.getBytes());
		byte[] ptlByte = Tools.fromHexString(contHex);

		String hex = UcstProtocalUtil.createMtProtocal(seq, oemCode, deviceId,
				centerId, deviceId, "1b", ptlByte);

		//Log.getInstance().ucstLog(deviceId + " 车辆识别代码设置指令：" + hex);
		return hex;
	}

	public String vehicleCardSetting(Request req) {
		 

		String deviceId = req.getDeviceId();
		String seq = Tools.getRandomString(2);
		String cardName = RequestUtil.getReqData(req, "number");

		while (cardName.getBytes().length < 12) {
			cardName = cardName + "\0";
		}

		String contHex = "";
		try {
			contHex = "01"+Tools.convertToHex("2", 2)+Tools.int2Hexstring(cardName.getBytes("GB2312").length, 2)
					+ Tools.bytesToHexString(cardName.getBytes("GB2312"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		byte[] ptlByte = Tools.fromHexString(contHex);

		String hex = UcstProtocalUtil.createMtProtocal(seq, oemCode, deviceId,
				centerId, deviceId, "1b", ptlByte);

		//Log.getInstance().ucstLog(deviceId + " 车辆号码设置指令：" + hex);
		return hex;
	}

	 

	public static void main(String[] args) {
		System.out.println(Integer.parseInt("0001C2", 16));
		System.out.println(Tools.convertToHex("450", 6));
	}
}
