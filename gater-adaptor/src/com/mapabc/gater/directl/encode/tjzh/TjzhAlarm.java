/**
 * 
 */
package com.mapabc.gater.directl.encode.tjzh;

import java.util.Date;


import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.AlarmAdaptor;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;


/**
 * @author shiguang.zhou
 * 
 */
public class TjzhAlarm extends AlarmAdaptor {
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

	// 超速设置
	public String overspeedAlarm(Request req) {

		StringBuffer buffer = new StringBuffer();

		buffer.append(head);
		buffer.append("3f");
		buffer.append("0007");
		buffer.append(this.getFormatDeSN(req.getDeviceId()));

		String speed = RequestUtil.getReqData(req, "max");
		if (speed.trim().length() == 0)
			speed = "0";
		int fs = 0;
		try {
			fs = (int) Float.parseFloat(speed);
		} catch (Exception e) {
			fs = 0;
			e.printStackTrace();
		}

		if (fs < 0)
			fs = 0;
		if (fs > 255)
			fs = 255;
		String hexSpeed = Tools.int2Hexstring(fs, 2);

		buffer.append(hexSpeed);
		byte vcode = Tools.checkData(Tools.fromHexString(buffer.toString()));
		buffer.append(Tools.bytesToHexString(new byte[] { vcode }));
		buffer.append(end);

		byte[] cmd = Tools.fromHexString(buffer.toString().toUpperCase());
		String ret = buffer.toString().toUpperCase();// new String(cmd);

		return ret;
	}

	// 取消指定车辆的报警位
	public String cancelAlarm(Request req) {
		StringBuffer buffer = new StringBuffer();

		buffer.append(head);
		buffer.append("37");
		buffer.append("0006");
		buffer.append(this.getFormatDeSN(req.getDeviceId()));
		byte vcode = Tools.checkData(Tools.fromHexString(buffer.toString()));
		buffer.append(Tools.bytesToHexString(new byte[] { vcode }));
		buffer.append(end);

		byte[] cmd = Tools.fromHexString(buffer.toString().toUpperCase());
		String ret = buffer.toString().toUpperCase();// new String(cmd);

		return ret;
	}

	// 区域报警参数设置
	public String alarmParams(Request req) {
		StringBuffer buffer = new StringBuffer();

		buffer.append(head);
		buffer.append("80");
		buffer.append("0009");
		buffer.append(this.getFormatDeSN(req.getDeviceId()));
		String timelen = RequestUtil.getReqData(req, "timelen"); // 持续触发报警时间
		if (timelen.length() == 0)
			timelen = "0";
		int itimelen = Integer.parseInt(timelen);
		if (itimelen < 0)
			timelen = "0";
		if (itimelen > 255)
			timelen = "255";

		String interval = RequestUtil.getReqData(req, "interval"); // 报警间隔
		if (interval.length() == 0)
			interval = "0";
		int iinterval = Integer.parseInt(interval);
		if (iinterval < 0)
			interval = "0";
		if (iinterval > 255)
			interval = "255";

		String times = RequestUtil.getReqData(req, "times");
		if (times.length() == 0)
			times = "0";
		int itimes = Integer.parseInt(times);
		if (itimes < 0)
			times = "0";
		if (itimes > 255)
			times = "255";
		buffer.append(Tools.convertToHex(times, 2));
		buffer.append(Tools.convertToHex(interval, 2));
		buffer.append(Tools.convertToHex(timelen, 2));

		byte vcode = Tools.checkData(Tools.fromHexString(buffer.toString()));
		buffer.append(Tools.bytesToHexString(new byte[] { vcode }));
		buffer.append(end);

		byte[] cmd = Tools.fromHexString(buffer.toString().toUpperCase());
		String ret = buffer.toString().toUpperCase();// new String(cmd);

		return ret;
	}

	// 区域报警开关
	public String switchAlarm(Request req) {
		StringBuffer buffer = new StringBuffer();

		buffer.append(head);
		buffer.append("81");
		buffer.append("0009");
		buffer.append(this.getFormatDeSN(req.getDeviceId()));

		String off = RequestUtil.getReqData(req, "off");
		buffer.append(Tools.convertToHex(off, 2));
		byte vcode = Tools.checkData(Tools.fromHexString(buffer.toString()));
		buffer.append(Tools.bytesToHexString(new byte[] { vcode }));
		buffer.append(end);

		byte[] cmd = Tools.fromHexString(buffer.toString().toUpperCase());
		String ret = buffer.toString().toUpperCase();// new String(cmd);

		return ret;
	}

	public String stopCarAlarm(Request req) {
		String ret = null;
		String deviceId = req.getDeviceId();
		String time = RequestUtil.getReqData(req, "time");// 单位，分钟
		ret = head + "400007" + this.getFormatDeSN(deviceId)
				+ Tools.convertToHex(time, 2);
		byte verfyCode = Tools.checkData(Tools.fromHexString(ret));
		String vc = Tools.bytesToHexString(new byte[] { verfyCode });
		ret += vc + end;
		return ret;
	}

}
