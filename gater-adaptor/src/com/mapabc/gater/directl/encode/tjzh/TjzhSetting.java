/**
 * 
 */
package com.mapabc.gater.directl.encode.tjzh;

import java.util.Date;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;
import com.mapabc.gater.directl.encode.SettingAdaptor;
 

/**
 * @author shiguang.zhou
 * 
 */
public class TjzhSetting extends SettingAdaptor {

	private String head = "2929";
	private String end = "0d";

	private String getFormatDeSN(String deviceid) {
		String dsn = "";
		if (deviceid != null && deviceid.trim().length() > 0) {// 此处待定
			String[] dsns = deviceid.split("\\.");
			for (int i = 0; i < dsns.length; i++) {
				dsn += Tools.convertToHex(dsns[i], 2);
			}
		}
		return dsn;
	}

	// 通讯地址设置
	public String addrSetting(Request req) {

		StringBuffer buffer = new StringBuffer();

		buffer.append(head);

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
		String deviceid = this.getFormatDeSN(req.getDeviceId());
		addr = "\"" + addr + "\"," + port;
		buffer.append(cmdType);
		buffer.append(Tools.convertToHex(addr.getBytes().length + 6 + "", 4));
		buffer.append(deviceid);
		buffer.append(Tools.bytesToHexString(addr.getBytes()));

		byte verfyCode = Tools
				.checkData(Tools.fromHexString(buffer.toString()));
		String vc = Tools.bytesToHexString(new byte[] { verfyCode });
		buffer.append(vc);

		buffer.append(end);
		byte[] cmd = Tools.fromHexString(buffer.toString().toUpperCase());
		String ret = buffer.toString().toUpperCase();

		return ret;
	}

	public String camera(Request req) {
		String deviceId = req.getDeviceId();
		String condition = RequestUtil.getReqData(req, "condition");
		String ret = null;
		if (condition != "") {//拍照触发条件设置
			String bit = "";
			if (condition.equals("2")) {
				bit += "1";
			}
			while (bit.length() < 8) {
				bit = bit + "0";
			}
			int ibit = Integer.parseInt(bit, 2);
			String hexBit = Tools.int2Hexstring(ibit, 2);
			ret = head + "260007" + this.getFormatDeSN(deviceId) + hexBit;
			byte verfyCode = Tools.checkData(Tools.fromHexString(ret));
			String vc = Tools.bytesToHexString(new byte[] { verfyCode });
			ret += vc+end;
		}

		return ret;
	}

}
