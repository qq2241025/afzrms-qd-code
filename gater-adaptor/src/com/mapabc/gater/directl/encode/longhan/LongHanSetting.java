/**
 * 
 */
package com.mapabc.gater.directl.encode.longhan;

import java.util.Date;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;
import com.mapabc.gater.directl.encode.SettingAdaptor;

/**
 * @author shiguang.zhou
 * 
 */
public class LongHanSetting extends SettingAdaptor {

	// 通讯地址设置,终端有应得，但终端没设置成功
	public String addrSetting(Request req) {

		String cmdType = "";

		String type = RequestUtil.getReqData(req, "type");
		if (type.trim().length() > 0) {
			if (type.trim().equals("0")) {
				cmdType = "76";
			} else {
				cmdType = "69";
			}
		}
		String ip = RequestUtil.getReqData(req, "ip");
		String ips[] = ip.split("\\.");
		String port = RequestUtil.getReqData(req, "port");
		String addr = "";
		for (int i = 0; i < ips.length; i++) {
			String s = ips[i];
			while (s.length() < 3) {
				s = "0" + s;
			}
			if (i == ips.length - 1) {
				addr += s;
			} else {
				addr += s + ".";
			}
		}
		String deviceType = req.getDeviceType();
		String deviceid = LongHanUtil.simToIp(req.getDeviceId());
//		if (deviceType != null && deviceType.equals("GP-LONGHAN-GPRS")){
//			deviceid = "00000000000";
//		}else{
			deviceid=req.getDeviceId();
//		}
		addr = "\"" + addr + "\"," + port;
 

		String ret = LongHanUtil
				.makeProtocal(deviceid, cmdType,
						addr.getBytes().length + 6, Tools.bytesToHexString(addr
								.getBytes()));
		return ret;
	}

	public String camera(Request req) {
		String deviceId = req.getDeviceId();
		String condition = RequestUtil.getReqData(req, "condition");
		String funType = RequestUtil.getReqData(req, "funType");
		String cameraType = RequestUtil.getReqData(req, "cameraType");
		String chanel = RequestUtil.getReqData(req, "chanel");
		String pixel = RequestUtil.getReqData(req, "pixel");

		String ret = null;
		// if (condition != "") {// 拍照触发条件设置
		// String bit = "";
		// if (condition.equals("2")) {
		// bit += "1";
		// }
		// while (bit.length() < 8) {
		// bit = bit + "0";
		// }
		// int ibit = Integer.parseInt(bit, 2);
		// String hexBit = Tools.int2Hexstring(ibit, 2);
		//
		// RequestUtil.getDealRequest(req,
		// StructionType.CAMERA_SET_COMMOND_TYPE, "26");
		//
		// ret = LongHanUtil.makeProtocal(req.getDeviceId(), "26", 7, hexBit);
		// return ret;
		// } else
		if (funType != "" && funType.equals("1")) {

			if (cameraType.equals("1")) {// 即时拍照

				int ichanel = Integer.parseInt(chanel);
				ichanel = ichanel + 1;

				if (ichanel > 4) {
					ichanel = ichanel % 4;

 
				}

				if (pixel.equals("5")) {
					pixel = "00";
				} else if (pixel.equals("6")) {
					pixel = "01";
				} else {
					pixel = "00";
				}
				String hexCont = Tools.int2Hexstring(ichanel, 2) + pixel; 

				ret = LongHanUtil.makeProtocal(req.getDeviceId(), "28", 8,
						hexCont);
				return ret;
			} else if (cameraType.equals("2")) {// 定时拍照指令
				String interval = RequestUtil.getReqData(req, "interval");
				int inval = Integer.parseInt(interval);

				if (req.getDeviceType() != null
						&& req.getDeviceType().equals("GP-LH-HK-GPRS")) {
					// 香港版定时拍照
					String hexCont = "01";
					String count = RequestUtil.getReqData(req, "count");
					int cnt = Integer.parseInt(count);
					if (cnt > 10)
						count = "10";
					if (inval > 255)
						interval = "255";

					hexCont += Tools.convertToHex(count, 2)
							+ Tools.convertToHex(interval, 2) + "02";
					if (pixel.equals("5")) {
						pixel = "02";
					} else if (pixel.equals("6")) {
						pixel = "01";
					} else {
						pixel = "03";
					}
					hexCont += pixel + "C00103E8";
 

					ret = LongHanUtil.makeProtocal(req.getDeviceId(), "2a", 21,
							hexCont);
					return ret;

				} else {
					String chanelCount = RequestUtil.getReqData(req, "chanelCnt");
					int ichlCnt = Integer.parseInt(chanelCount);

					if (inval == 255 || inval > 255 * 30) {// 停止定时拍照
						interval = "0";
					} else if (inval < 30) {
						interval = "1";
					} else if (inval > 30) {
						interval = inval / 30 + "";
					}
					switch (ichlCnt) {
					case 1:
						chanelCount = "08";
						break;
					case 2:
						chanelCount = "0c";
						break;
					case 3:
						chanelCount = "0e";
						break;
					case 4:
						chanelCount = "0f";
						break;
					}
					String hexCont = Tools.convertToHex(interval, 2)
							+ chanelCount; 
					ret = LongHanUtil.makeProtocal(req.getDeviceId(), "65", 8,
							hexCont);
				}
				return ret;

			}

		}

		return ret;
	}

	public String vehicleIdSetting(Request req) {
		String ret = ""; 
		String deviceId = req.getDeviceId();
		String id = RequestUtil.getReqData(req, "id");
		ret = LongHanUtil.makeProtocal(req.getDeviceId(), "20", 6 + id
				.getBytes().length, Tools.bytesToHexString(id.getBytes()));
		return ret;
	}

	public String carIdSetting(Request req) {
		String ret = ""; 
		String deviceId = req.getDeviceId();
		String deviceType = req.getDeviceType();
		String pre = "13995208";

		if (deviceType != null && deviceType.equals("GP-LH-HK-GPRS")) {
			deviceId = "00000000000";
		}else{
			pre = LongHanUtil.simToIp(deviceId);
		}
		String id = RequestUtil.getReqData(req, "newId");
		
		ret = LongHanUtil
				.makeProtocal(deviceId, "20", 10 + id.getBytes().length, pre+Tools
						.bytesToHexString(id.getBytes()));
		//Log.getInstance().longhanLog(req.getDeviceId()+" 设置车台编号指令："+ret);
		return ret;
	}

	public String smsCenterSetting(Request req) {
		String ret = ""; 
		String deviceId = req.getDeviceId();
		String number = RequestUtil.getReqData(req, "number");
		ret = LongHanUtil.makeProtocal(req.getDeviceId(), "77", 6 + number
				.getBytes().length, Tools.bytesToHexString(number.getBytes()));
		return ret;
	}

}
