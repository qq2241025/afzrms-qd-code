package com.mapabc.gater.directl.encode.doog;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.AlarmAdaptor;
import com.mapabc.gater.directl.encode.Request;

public class GAODEAlarm extends AlarmAdaptor {
	public String areaAlarm(Request req) {

		StringBuffer buffer = new StringBuffer();

		buffer.append("$WZTREQ,");
		buffer.append("21,");
		buffer.append(req.getDeviceId() + ",");
		buffer.append("0,");

		buffer.append("(");
		String deviceType = req.getDeviceType();
		if (deviceType != null && deviceType.equals("GP-QINGTAN-GPRS")) {
			buffer.append("1,");
		} else {
			buffer.append((String) req.getDatas().get("areaNo") + ",");
		}
		buffer.append((String) req.getDatas().get("areaType") + ",");
		buffer.append((String) req.getDatas().get("alarmType") + ",");
		buffer.append((String) req.getDatas().get("maxSpeed") + ",");
		buffer.append((String) req.getDatas().get("radius") + ",");
		String points = (String) req.getDatas().get("points");
		points = points.replace(",", "!");
		points = points.replace(";", ",");
		buffer.append(points);
		buffer.append(")");

		buffer.append("#");

		String hex = Tools.bytesToHexString(buffer.toString().getBytes());

		return hex;
	}

	public String lineAlarm(Request req) {

		StringBuffer buffer = new StringBuffer();

		buffer.append("$WZTREQ,");
		buffer.append("23,");
		buffer.append(req.getDeviceId() + ",");
		buffer.append("0,");

		buffer.append("(");
		// buffer.append((String)req.getDatas().get("lineNo") + ",");
		String deviceType = req.getDeviceType();
		if (deviceType != null && deviceType.equals("GP-QINGTAN-GPRS")) {
			buffer.append("1,");
		} else {
			buffer.append((String) req.getDatas().get("lineNo") + ",");
		}
		// buffer.append("1,");
		buffer.append((String) req.getDatas().get("alarmType") + ",");
		buffer.append((String) req.getDatas().get("offset") + ",");
		String points = (String) req.getDatas().get("points");
		points = points.replace(",", "!");
		points = points.replace(";", ",");
		buffer.append(points);
		buffer.append(")");

		buffer.append("#");

		String hex = Tools.bytesToHexString(buffer.toString().getBytes());

		return hex;
	}

	public String overspeedAlarm(Request req) {

		StringBuffer buffer = new StringBuffer();

		buffer.append("$WZTREQ,");
		buffer.append("20,");
		buffer.append(req.getDeviceId() + ",");
		buffer.append("0,");

		buffer.append("(");
		buffer.append((String) req.getDatas().get("max") + ",");
		buffer.append((String) req.getDatas().get("duration"));
		buffer.append(")");

		buffer.append("#");

		String hex = Tools.bytesToHexString(buffer.toString().getBytes());

		return hex;
	}

	private String cancelOverspeedAlarm(Request req) {

		StringBuffer buffer = new StringBuffer();

		buffer.append("$WZTREQ,");
		buffer.append("20,");
		buffer.append(req.getDeviceId() + ",");
		buffer.append("0,");

		buffer.append("(");
		buffer.append("0,");
		buffer.append("");
		buffer.append(")");

		buffer.append("#");

		String hex = Tools.bytesToHexString(buffer.toString().getBytes());

		return hex;
	}

	public String cancleArea(Request req) {

		StringBuffer buffer = new StringBuffer();

		buffer.append("$WZTREQ,");
		buffer.append("22,");
		buffer.append(req.getDeviceId() + ",");
		buffer.append("0,");

		buffer.append("(");
		String deviceType = req.getDeviceType();
		if (deviceType != null && deviceType.equals("GP-QINGTAN-GPRS")) {
			buffer.append("1");
		} else {
			buffer.append((String) req.getDatas().get("areaNo") + ",");
			buffer.append((String) req.getDatas().get("alarmType"));
		}

		// buffer.append("1");
		buffer.append(")");

		buffer.append("#");

		String hex = Tools.bytesToHexString(buffer.toString().getBytes());

		return hex;
	}

	public String cancleLine(Request req) {

		StringBuffer buffer = new StringBuffer();

		buffer.append("$WZTREQ,");
		buffer.append("24,");
		buffer.append(req.getDeviceId() + ",");
		buffer.append("0,");

		buffer.append("(");
		String deviceType = req.getDeviceType();
		if (deviceType != null && deviceType.equals("GP-QINGTAN-GPRS")) {
			buffer.append("1");
		} else {
			buffer.append((String) req.getDatas().get("lineNo"));
		}
//		buffer.append("1");
		buffer.append(")");

		buffer.append("#");

		String hex = Tools.bytesToHexString(buffer.toString().getBytes());

		return hex;
	}

	public String cancelAlarm(Request req) {
		String type = (String) req.getDatas().get("type");

		if (type.equals("1")) {
			return cancelOverspeedAlarm(req);
		} else if (type.equals("2")) {
			return cancleArea(req);
		} else if (type.equals("6")) {
			return cancleLine(req);
		} else {
			String allAlarm = "$WZTREQ,25," + req.getDeviceId() + ",0,0#";
			return Tools.bytesToHexString(allAlarm.getBytes());
		}

	}

}
