package com.mapabc.gater.directl.encode.OBU;

/**
 * 
 */

import java.nio.ByteBuffer;
import java.util.HashMap;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;
import com.mapabc.gater.directl.encode.SettingAdaptor;



/**
 * @author peng.chen
 * 
 */
public class OBUSetting extends SettingAdaptor {

	public String addrSetting(Request req) {
		String cmd = "";

		String ip = RequestUtil.getReqData(req, "ip");
		String port = RequestUtil.getReqData(req, "port");
		String param1 = "20" + Tools.int2Hexstring(ip.getBytes().length, 2)
				+ Tools.bytesToHexString(ip.getBytes());
		String param2 = "2102"
				+ Tools.bytesToHexString(Tools.convertBytePos(Tools
						.fromHexString(Tools.convertToHex(port, 4))));

		String parameter =param1+ param2;

		cmd = OBUUtil.downParameterSet(req.getDeviceId(), 2, parameter);
		return cmd;

	}

	public String apnSetting(Request req) {
		String cmd = "";
		String apns = RequestUtil.getReqData(req, "apn");
		if (apns == "") {
			//Log.getInstance().obitsLog(req.getDeviceId() + " 输入的APN不允许为空。");
			return null;
		}
		String[] apnSplit = apns.split(";");
		String param1 = null;
		String param2 = null;
		if (apnSplit.length == 2) {
			param1 = "81"
					+ Tools.int2Hexstring(apnSplit[0].getBytes().length, 2)
					+ Tools.bytesToHexString(apnSplit[0].getBytes());
			param2 = "98"
					+ Tools.int2Hexstring(apnSplit[1].getBytes().length, 2)
					+ Tools.bytesToHexString(apnSplit[1].getBytes());
		}
		if (apnSplit.length == 1) {
			param1 = "81"
					+ Tools.int2Hexstring(apnSplit[0].getBytes().length, 2)
					+ Tools.bytesToHexString(apnSplit[0].getBytes());
			param2 = "98"
					+ Tools.int2Hexstring(apnSplit[0].getBytes().length, 2)
					+ Tools.bytesToHexString(apnSplit[0].getBytes());

		}

		String parameter = param1 + param2;
 

		cmd = OBUUtil.downParameterSet(req.getDeviceId(), 2, parameter);
		return cmd;
	}

	@Override
	public String modeSetting(Request req) {
		return null;
	}

	public String vehicleIdSetting(Request req) { 
		String cmd = "";

		String ip = RequestUtil.getReqData(req, "id"); 
		 
		String param1 = "00" + Tools.int2Hexstring(ip.getBytes().length, 2)
		+ Tools.bytesToHexString(Tools.convertBytePos(ip.getBytes()));

		String parameter = param1; 
		cmd = OBUUtil.downParameterSet(req.getDeviceId(), 2, parameter);
		return cmd;
	}

	public String temperatureInterSetting(Request req) {
		return null;
	}

	public String oilInterSetting(Request req) {
		return null;
	}

	@Override
	public String camera(Request req) {
		return null;
	}
	
	public String carIdSetting(Request req) { 
		String cmd = "";

		String ip = RequestUtil.getReqData(req, "newId"); 
		String idHex = Tools.convertToHex(ip, 8);
		 
		String param1 = "0004"
		+ Tools.bytesToHexString(Tools.convertBytePos(Tools.fromHexString(idHex)));

		String parameter = param1; 

		cmd = OBUUtil.downParameterSet(req.getDeviceId(), 2, parameter);
		return cmd;
	}

	 

	public static void main(String[] args){
		String s = "sd";
		System.out.println(s.split(";")[0]);
	}
}
