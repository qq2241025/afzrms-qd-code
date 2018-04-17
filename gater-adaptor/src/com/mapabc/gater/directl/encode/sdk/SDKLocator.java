/**
 * 
 */
package com.mapabc.gater.directl.encode.sdk;

import java.util.Arrays;
import java.util.Date;
 
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.LocatorAdapter;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;

/**
 * @author shiguang.zhou
 * 
 */
public class SDKLocator extends LocatorAdapter {

	public String distanceInter(Request req) {
		String deviceId = req.getDeviceId();
		String interval = RequestUtil.getReqData(req, "interval");
		String count = RequestUtil.getReqData(req, "count");
		String params = Tools
		.convertToHex(interval, 4)
				+ Tools.convertToHex(count, 2);

		String cmd = SdkUtil.crtProtocal(deviceId, "11", params);
		return cmd;
	}

	public String locate(Request req) {
		// TODO Auto-generated method stub
		String deviceId = req.getDeviceId();
		String cmd = SdkUtil.crtProtocal(deviceId, "14", "");
		return cmd;
	}

	public String locateRecoup(Request req) {
		String deviceId = req.getDeviceId();
 
		String date = this.getReqData(req, "date");
		String time = this.getReqData(req, "time");
		String y = this.getReqData(req, "y");
		String x = this.getReqData(req, "x");
		String posdesc = this.getReqData(req, "posdesc");
		String fy = SdkUtil.formatXY(y);
		String fx = SdkUtil.formatXY(x);
		String params = date+time+fy+fx+Tools.bytesToHexString(posdesc.getBytes())+"01";
		String cmd = SdkUtil.crtProtocal(deviceId, "13", params);
		
		
		return cmd;
	}

	public String timeInter(Request req) {
		// TODO Auto-generated method stub
		String deviceId = req.getDeviceId();
		String interval = RequestUtil.getReqData(req, "interval");
		String count = RequestUtil.getReqData(req, "count");
		String params = Tools.convertToHex(interval, 2)
				+ Tools.convertToHex(count, 2);
		String cmd = SdkUtil.crtProtocal(deviceId, "10", params);
		return cmd;
	}

	public String timeLocate(Request req) {
		// TODO Auto-generated method stub
		String deviceId = req.getDeviceId();
		String startTime = RequestUtil.getReqData(req, "startTime");

		String deadTime = RequestUtil.getReqData(req, "deadTime");

		String reportWeek = RequestUtil.getReqData(req, "reportWeek");
		String[] weeks = reportWeek.split(";");
		String[] bWeeks = new String[] { "0", "0", "0", "0", "0", "0", "0", "0" };

		for (int i = 0; i < weeks.length; i++) {
			if (weeks[i].equals("7")) {
				bWeeks[0] = "1";
			} else if (weeks[i].equals("1")) {
				bWeeks[1] = "1";
			} else if (weeks[i].equals("2")) {
				bWeeks[2] = "1";
			} else if (weeks[i].equals("3")) {
				bWeeks[3] = "1";
			} else if (weeks[i].equals("4")) {
				bWeeks[4] = "1";
			} else if (weeks[i].equals("5")) {
				bWeeks[5] = "1";
			} else if (weeks[i].equals("6")) {
				bWeeks[6] = "1";
			}
		}
		String swk = Arrays.toString(bWeeks).replaceAll(",", "").replaceAll(
				"[", "").replaceAll("]", "");
		byte bwk = Byte.parseByte(swk, 2);
		swk = Tools.int2Hexstring((int) bwk, 2);

		String reportTime = RequestUtil.getReqData(req, "reportTime");
		reportTime = reportTime.replaceAll(":", "");

		String params = startTime + reportTime + deadTime + swk;

		String cmd = SdkUtil.crtProtocal(deviceId, "12", params);

		return cmd;
	}

	public String encode(Request req) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getReqData(Request req, String key) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] args) {

		String[] bWeeks = new String[] { "0", "0", "0", "0", "0", "0", "0", "0" };
		System.out.println(Arrays.toString(bWeeks));

	}

}
