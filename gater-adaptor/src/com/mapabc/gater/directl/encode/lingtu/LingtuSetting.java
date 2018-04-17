/**
 * 
 */
package com.mapabc.gater.directl.encode.lingtu;

import java.io.UnsupportedEncodingException;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;
import com.mapabc.gater.directl.encode.SettingAdaptor;


/**
 * @author shiguang.zhou
 * 
 */
public class LingtuSetting extends SettingAdaptor {
	String head = "C M ";
	String end = "\r\n";
	String cmdSeq = Tools.getRandomString(4);

	public String camera(Request req) {
		String ret = "";
		// C M 1 4C54:1001|301|1 3B9
		String deviceid = req.getDeviceId();
		RequestUtil.getDealRequest(req, "518", cmdSeq);

		head = head + cmdSeq + " ";
		String no = RequestUtil.getReqData(req, "chanel");

		String cmd = LingtuUtil.createOemCodeBySn(deviceid) + ":" + deviceid
				+ "|301|" + no + " ";
		String vcode = Tools.getVerfyCode(cmd.getBytes());
		ret = head + cmd + vcode;
		String hex = Tools.bytesToHexString(ret.getBytes());

		return hex;
	}

	public String oilInterSetting(Request req) {

		String ret = "";
		String cmd = "";
		RequestUtil.getDealRequest(req, "510", cmdSeq);
		String type = RequestUtil.getReqData(req, "type");
		String time = RequestUtil.getReqData(req, "interval");
		head = "C M " + cmdSeq + " ";

		if (type.equals("0")) {// 取消设置
			cmd = LingtuUtil.createOemCodeBySn(req.getDeviceId()) + ":"
					+ req.getDeviceId() + "|510|0 ";
		} else if (type.equals("1")) {

			String hexVal = Integer.toHexString(Integer.parseInt(time));// 单位分钟
			cmd = LingtuUtil.createOemCodeBySn(req.getDeviceId()) + ":"
					+ req.getDeviceId() + "|510|1;" + "1" + ";" + hexVal + " ";
		}

		String vcode = Tools.getVerfyCode(cmd.getBytes());
		ret = head + cmd + vcode;
		ret = Tools.bytesToHexString(ret.getBytes());
		return ret;

	}

	// 疲劳驾驶设置
	public String fatigueDriveSetting(Request req) {
		String ret = "";
		String cmd = "";

		head = "C M " + cmdSeq + " ";
		String type = RequestUtil.getReqData(req, "type");
		String maxTime = RequestUtil.getReqData(req, "maxDriveTime");
		String restTime = RequestUtil.getReqData(req, "restTime");

		if (type.equals("0") || maxTime.equals("0")) {// 取消设置
			cmd = LingtuUtil.createOemCodeBySn(req.getDeviceId()) + ":"
					+ req.getDeviceId() + "|212|0 ";
		} else {
			String hexMaxTime = Integer.toHexString(Integer.parseInt(maxTime));
			String hexRestTime = Integer
					.toHexString(Integer.parseInt(restTime));

			cmd = LingtuUtil.createOemCodeBySn(req.getDeviceId()) + ":"
					+ req.getDeviceId() + "|212|1;" + hexMaxTime + ";"
					+ hexRestTime + " ";
		}

		String vcode = Tools.getVerfyCode(cmd.getBytes());
		ret = head + cmd + vcode;
		ret = Tools.bytesToHexString(ret.getBytes());
		return ret;
	}

	public String deleteLedMsg(Request req) {
		String ret = "";
		String cmd = "";

		head = "C M " + cmdSeq + " ";
		String index = RequestUtil.getReqData(req, "msgIndex");
		String hexVal = Integer.toHexString(Integer.parseInt(index));

		cmd = LingtuUtil.createOemCodeBySn(req.getDeviceId()) + ":"
				+ req.getDeviceId() + "|E03|" + hexVal + " ";

		String vcode = Tools.getVerfyCode(cmd.getBytes());
		ret = head + cmd + vcode;
		ret = Tools.bytesToHexString(ret.getBytes());
		return ret;
	}

	public String sendLedMsg(Request req) {
		String ret = "";
		String cmd = "";

		head = "C M " + cmdSeq + " ";
		String index = RequestUtil.getReqData(req, "msgIndex");
		String type = RequestUtil.getReqData(req, "type");
		String msg = RequestUtil.getReqData(req, "ledMsg");
		String hexVal = Integer.toHexString(Integer.parseInt(index));
		try {

			byte[] msgBytes = msg.getBytes("GB2312");

			String hexMsg = Tools.bytesToHexString(msgBytes);

			cmd = LingtuUtil.createOemCodeBySn(req.getDeviceId()) + ":"
					+ req.getDeviceId() + "|E02|" + hexVal + ";" + type + ";"
					+ hexMsg + " ";

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String vcode = Tools.getVerfyCode(cmd.getBytes());
		ret = head + cmd + vcode;
		ret = Tools.bytesToHexString(ret.getBytes());
		return ret;
	}

	public String setLedMsgProperty(Request req) {
		String ret = "";
		String cmd = "";

		head = "C M " + cmdSeq + " ";

		int paramNum = 0;
		String params = "";
		String index = RequestUtil.getReqData(req, "msgIndex");
		String hexVal = Integer.toHexString(Integer.parseInt(index));
		String displayType = RequestUtil.getReqData(req, "displayType");
		String scrollType = RequestUtil.getReqData(req, "scrollType");
		String displayFont = RequestUtil.getReqData(req, "displayFont");
		String displaySpeed = RequestUtil.getReqData(req, "displaySpeed");
		String scrollInterval = RequestUtil.getReqData(req, "scrollInterval");
		String displayTimes = RequestUtil.getReqData(req, "displayTimes");
		String displayTime = RequestUtil.getReqData(req, "stayTime");
		String displayBrt = RequestUtil.getReqData(req, "displayBright");
		String sectime1 = RequestUtil.getReqData(req, "singleSecTime");
		String sectime2 = RequestUtil.getReqData(req, "moreSecTime");

		if (displayType != null && displayType.trim().length() > 0
				&& scrollType != null && scrollType.trim().length() > 0) {
			paramNum += 1;
			params += "A" + displayType + scrollType + ";";
		} else {// 默认停开车、左滚显示
			paramNum += 1;
			params += "A20;";
		}
		if (displayFont != null && displayFont.trim().length() > 0) {
			paramNum += 1;
			params += "B" + intStrToHexString(displayFont.trim()) + ";";

		} else {// 默认设置

		}
		if (displaySpeed != null && displaySpeed.trim().length() > 0) {
			paramNum += 1;
			params += "C" + intStrToHexString(displaySpeed.trim()) + ";";
		}
		if (scrollInterval != null && scrollInterval.trim().length() > 0) {
			paramNum += 1;
			params += "D" + intStrToHexString(scrollInterval.trim()) + ";";
		}
		if (displayTimes != null && displayTimes.trim().length() > 0) {
			paramNum += 1;
			params += "E" + intStrToHexString(displayTimes.trim()) + ";";
		}
		if (displayTime != null && displayTime.trim().length() > 0) {
			paramNum += 1;
			params += "F" + intStrToHexString(displayTime.trim()) + ";";
		}
		if (displayBrt != null && displayBrt.trim().length() > 0) {
			paramNum += 1;
			params += "G" + intStrToHexString(displayBrt.trim()) + ";";
		} else {
			paramNum += 1;
			params += "G7";// 默认亮度为3
		}
		if (sectime1 != null && sectime1.trim().length() > 0) {// 时间段是否需要转换成16进制
			paramNum += 1;
			params += "H" + sectime1.trim() + ";";
		}
		if (sectime2 != null && sectime2.trim().length() > 0) {// 时间段是否需要转换成16进制
			paramNum += 1;
			params += "I" + sectime2.trim() + ";";

		}

		if (params.indexOf(";", params.length() - 1) != -1) {
			params = params.substring(0, params.length() - 1);
		}

		cmd = LingtuUtil.createOemCodeBySn(req.getDeviceId()) + ":"
				+ req.getDeviceId() + "|E04|" + hexVal + ";"
				+ Integer.toHexString(paramNum) + ";" + params + " ";

		String vcode = Tools.getVerfyCode(cmd.getBytes());
		ret = head + cmd + vcode;
		ret = Tools.bytesToHexString(ret.getBytes());
		return ret;
	}

	public String setLedScreenBright(Request req) {
		String ret = "";
		String cmd = "";

		head = "C M " + cmdSeq + " ";
		String sctBrts = "";
		String sectime1 = RequestUtil.getReqData(req, "secTime1Brt");
		String sectime2 = RequestUtil.getReqData(req, "secTime2Brt");
		String sectime3 = RequestUtil.getReqData(req, "secTime3Brt");
		String sectime4 = RequestUtil.getReqData(req, "secTime4Brt");
		String[] sectimeBrt = new String[] { sectime1, sectime2, sectime3,
				sectime4 };

		if (sectimeBrt != null) {
			if (sectimeBrt.length > 4) {
				return null;
			}
			for (int i = 0; i < sectimeBrt.length; i++) {
				String secBrt = sectimeBrt[i];
				int index1 = secBrt.indexOf("!");
				int index2 = secBrt.indexOf(";");
				String sec1 = secBrt.substring(0, index1);
				sec1 = Integer.toHexString(Integer.parseInt(sec1));// Tools.convertToHex(sec1.substring(0,
																	// 2),
																	// 2)+Tools.convertToHex(sec1.substring(2,
																	// 4),
																	// 2)+Tools.convertToHex(sec1.substring(4,
																	// 6), 2);
				String sec2 = secBrt.substring(index1 + 1, index2);
				sec2 = Integer.toHexString(Integer.parseInt(sec2));// Tools.convertToHex(sec2.substring(0,
																	// 2),
																	// 2)+Tools.convertToHex(sec2.substring(2,
																	// 4),
																	// 2)+Tools.convertToHex(sec2.substring(4,
																	// 6), 2);

				String brt = secBrt.substring(index2 + 1);

				sctBrts += sec1 + "!" + sec2 + ";"
						+ Integer.toHexString(Integer.parseInt(brt)) + ",";
			}
		}
		if (sctBrts.indexOf(",", sctBrts.length() - 1) != -1) {
			sctBrts = sctBrts.substring(0, sctBrts.length() - 1);
		}
		cmd = LingtuUtil.createOemCodeBySn(req.getDeviceId()) + ":"
				+ req.getDeviceId() + "|E06|" + sctBrts + " ";

		String vcode = Tools.getVerfyCode(cmd.getBytes());
		ret = head + cmd + vcode + end;
		ret = Tools.bytesToHexString(ret.getBytes());
		return ret;
	}

	public String setLedSwitch(Request req) {
		String ret = "";
		String cmd = "";
		String onoff = "";
		String switchLed = "";
		String type = RequestUtil.getReqData(req, "type");
		if (type.equals("0")) {
			onoff = "1";
		} else if (type.equals("1")) {
			onoff = "0";
		}
		head = "C M " + cmdSeq + " ";
		String date1 = RequestUtil.getReqData(req, "startTime");
		String date2 = RequestUtil.getReqData(req, "endTime");
		String bright = RequestUtil.getReqData(req, "bright");

		switchLed = onoff + ";" + date1 + ";" + date2 + ";" + bright;

		cmd = LingtuUtil.createOemCodeBySn(req.getDeviceId()) + ":"
				+ req.getDeviceId() + "|E01|" + switchLed + " ";

		String vcode = Tools.getVerfyCode(cmd.getBytes());
		ret = head + cmd + vcode+ end;
		ret = Tools.bytesToHexString(ret.getBytes());
		return ret;
	}

	public String intStrToHexString(String num) {
		int number = Integer.parseInt(num);
		String ret = Integer.toHexString(number).toUpperCase();
		return ret;
	}

}
