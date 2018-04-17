/**
 * 
 */
package com.mapabc.gater.directl.encode.swyj;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;
import com.mapabc.gater.directl.encode.SettingAdaptor;

/**
 * @author shiguang.zhou
 * 
 */
public class SwyjSetting extends SettingAdaptor {
	private String head = "*HQ,";
	private String end = "#";
	private String gpssn;

	public String addrSetting(Request req) {// GPRS设置无应答，短信设置成功
		// *XX,YYYYYYYYYY,S23,HHMMSS,IP_addr,Port,Redial_Times#


		String deviceid = req.getDeviceId();
		String ip = RequestUtil.getReqData(req, "ip").replaceAll("\\.", ",");
		String port = RequestUtil.getReqData(req, "port");
		String ret = head + deviceid + ",S23," + Tools.getCurHMS() + "," + ip
				+ "," + port + ",255" + end;
		String hex = Tools.bytesToHexString(ret.getBytes());
		//Log.getInstance().tianheLog(ret);
		return hex;
	}

	public String apnSetting(Request req) {// //GPRS设置无应答，短信设置成功
		// *XX,YYYYYYYYYY,S24,HHMMSS,M,APN#


		String deviceid = req.getDeviceId();
		String apn = RequestUtil.getReqData(req, "apn");
		String ret = head + deviceid + ",S24," + Tools.getCurHMS() + ",2,"
				+ apn + end;
		String hex = Tools.bytesToHexString(ret.getBytes());
		//Log.getInstance().tianheLog(ret);
		return hex;
	}

	public String fatigueDriveSetting(Request req) {
		// *XX,YYYYYYYYYY,S40,HHMMSS,Speed,T_work,T_rest,T_on,T_off,Para#

		String deviceid = req.getDeviceId();
		String maxtime = RequestUtil.getReqData(req, "maxDriveTime");
		String resttime = RequestUtil.getReqData(req, "restTime");
		String speed = "2";// 单位节
		int intMaxTime = 0;
		int intRestTime = 0;

		try {
			intMaxTime = Integer.parseInt(maxtime) * 60; // 转为秒
			intRestTime = Integer.parseInt(resttime) * 60;// 转为秒
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (intMaxTime == 0) {// 取消
			speed = "0";
		}
		String ret = head + deviceid + ",S40," + Tools.getCurHMS() + ","
				+ speed + "," + intMaxTime + "," + intRestTime + ",180,180,FF"
				+ end;
		String hex = Tools.bytesToHexString(ret.getBytes());
		//Log.getInstance().tianheLog(ret);
		return hex;
	}

	public String tcpRecordSetting(Request req) {
		// *XX,YYYYYYYYYY,S34,HHMMSS,M#

		String deviceid = req.getDeviceId();
		String type = RequestUtil.getReqData(req, "type");
		String ret = head + deviceid + ",S34," + Tools.getCurHMS() + "," + type
				+ end;
		String hex = Tools.bytesToHexString(ret.getBytes());
		//Log.getInstance().tianheLog(ret);
		return hex;
	}

	// 即时拍照
	public String camera(Request req) {// 测试：无应答，无摄像头测试


		String deviceId = req.getDeviceId();
		String channel = RequestUtil.getReqData(req, "chanel"); // 0-7
		int ichannel = Integer.parseInt(channel);
		if (ichannel > 8) {
			ichannel = ichannel % 8;
		}
		String pixel = RequestUtil.getReqData(req, "pixel");// 分辨率
		String ret = head + deviceId + ",S39," + Tools.getCurHMS() + ",3,"
				+ ichannel + ",0," + pixel + end;
		String hex = Tools.bytesToHexString(ret.getBytes());
		//Log.getInstance().tianheLog(ret);
		return hex;
	}

	public String fortificationSetting(Request req) {
		// *XX,YYYYYYYYYY,S6,HHMMSS,M#
		String ret = "";
		String mtype = "";
		String deviceId = req.getDeviceId();
		String type = RequestUtil.getReqData(req, "type");
		if (type.equals("0")) {
			mtype = "2";
		}
		if (type.equals("1")) {
			mtype = "3";
		}
		ret = head + deviceId + ",S6," + Tools.getCurHMS() + "," + mtype 
				+ end;
		//Log.getInstance().tianheLog(ret);
		return Tools.bytesToHexString(ret.getBytes());
	}

}
