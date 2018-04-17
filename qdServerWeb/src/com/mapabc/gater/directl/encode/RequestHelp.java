package com.mapabc.gater.directl.encode;

import java.util.HashMap;

public class RequestHelp {
	public static Request generateRequestByOverspeedAlarm(String deviceId,
			String max) {
		Request req = new Request();
		String userId = "-1";
		String serviceName = "overspeedAlarm";
		String serviceKey = "createAlarm";
		String deviceType = "GP-PND-GPRS";
		HashMap<String, Object> datas = new HashMap<String, Object>();
		datas.put("max", killNull(max));
		datas.put("duration", "null");

		req.setUserId(userId);
		req.setServiceName(serviceName);
		req.setCmdType(serviceName);
		req.setDatas(datas);
		req.setDeviceId(deviceId);
		req.setServiceKey(serviceKey);
		req.setDeviceType(deviceType);
		return req;
	}

	public static Request generateRequestByAreaAlarm(String deviceId,
			String areaNo, String alarmType, String maxSpeed, String points) {
		Request req = new Request();
		String userId = "-1";
		String serviceName = "overspeedAlarm";
		String serviceKey = "createAlarm";
		String deviceType = "GP-PND-GPRS";

		String areaType = "2";
		String radius = "";
		HashMap<String, Object> datas = new HashMap<String, Object>();
		datas.put("areaNo", killNull(areaNo));
		datas.put("areaType", killNull(areaType));
		datas.put("alarmType", killNull(alarmType));
		datas.put("maxSpeed", killNull(maxSpeed));
		datas.put("radius", radius);
		datas.put("points", killNull(points));

		req.setUserId(userId);
		req.setServiceName(serviceName);
		req.setCmdType(serviceName);
		req.setDatas(datas);
		req.setDeviceId(deviceId);
		req.setServiceKey(serviceKey);
		req.setDeviceType(deviceType);
		return req;
	}

	private static String killNull(String s) {
		if (s == null || s.trim().equals("")) {
			s = "";
		}
		return s;
	}
}
