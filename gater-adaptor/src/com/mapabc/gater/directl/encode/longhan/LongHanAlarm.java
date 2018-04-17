/**
 * 
 */
package com.mapabc.gater.directl.encode.longhan;

import java.util.Date;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.AlarmAdaptor;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;


/**
 * @author shiguang.zhou
 * 
 */
public class LongHanAlarm extends AlarmAdaptor {
	private String head = "2929";
	private String end = "0d";

	// 超速设置
	public String overspeedAlarm(Request req) { 

		String speed = RequestUtil.getReqData(req, "max");// 终端默认至少10公里/小时
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

		String ret = LongHanUtil.makeProtocal(req.getDeviceId(), "3f", 7,
				hexSpeed);
		return ret;
	}

	// 取消指定车辆的报警位
	public String cancelAlarm(Request req) { 
		String ret = LongHanUtil.makeProtocal(req.getDeviceId(), "37", 6, null);
		return ret;
	}

	// 区域报警参数设置
	public String alarmParams(Request req) {
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

		String ptlCont = Tools.convertToHex(times, 2)
				+ Tools.convertToHex(interval, 2)
				+ Tools.convertToHex(timelen, 2);

		String ret = LongHanUtil.makeProtocal(req.getDeviceId(), "80", 9,
				ptlCont);

		return ret;
	}

	// 区域报警开关
	public String switchAlarm(Request req) {
		 
		String off = RequestUtil.getReqData(req, "off");
		String ptlCont = Tools.convertToHex(off, 2);
		String ret = LongHanUtil.makeProtocal(req.getDeviceId(), "81", 9,
				ptlCont);

		return ret;
	}

	public String timeoutStopCarAlarm(Request req) {
	    String ret = null;

		String time = RequestUtil.getReqData(req, "maxStopTime");// 单位，分钟
		String ptlCont = Tools.convertToHex(time, 2);
		ret = LongHanUtil.makeProtocal(req.getDeviceId(), "40", 7, ptlCont);

		return ret;
	}

	public String areaAlarm(Request req) {
		 

		String hex = "";
		String deviceId = req.getDeviceId();
		String points = RequestUtil.getReqData(req, "points");
		String alarmType = RequestUtil.getReqData(req, "alarmType");
		String areaNo = RequestUtil.getReqData(req, "areaNo");
		String isMore = RequestUtil.getReqData(req, "isMore");

		String[] m_points = points.split("\\|");// 以|分隔多区域
		int areaCount = m_points.length;
		String areaCont = "";
		String type = "";

		if (isMore != null && isMore.equals("1")) {// 一次性设置多个区域
			for (int i = 0; i < m_points.length; i++) {
				String s_point = m_points[i];

				String[] recPoints = s_point.split(";");
				if (recPoints.length < 3) {
					//Log.getInstance().longhanLog("矩形区域格式必须为：x,y;x,y;alarmtype");
					continue;
				}

				String alarmtype = recPoints[2];

				if (alarmtype.equals("0")) {
					type = "01";
				} else if (alarmtype.equals("1")) {
					type = "00";
				} else if (alarmtype.equals("2")) {
					type = "02";
				} else {
					type = "03";
				}
				areaNo = Tools.int2Hexstring(i + 1, 2);
				areaCont += this.getAreaCont(s_point) + areaNo + type;
			}
		} else {
			if (alarmType.equals("0")) {
				type = "01";
			} else if (alarmType.equals("1")) {
				type = "00";
			} else if (alarmType.equals("2")) {
				type = "02";
			} else {
				type = "03";
			}
			areaCont = this.getAreaCont(points) + Tools.convertToHex(areaNo, 2)
					+ type;
		}

		int leng = 7 + areaCont.length() / 2;
		String ptlCont = Tools.int2Hexstring(areaCount, 2) + areaCont;
		String ret = LongHanUtil.makeProtocal(req.getDeviceId(), "46", leng,
				ptlCont);

		return ret;
	}

	private String getAreaCont(String xyPoint) {
		String ret = "";
		String[] recPoints = xyPoint.split(";");
		String[] maxLngLat = Tools.getRecMaxLntLat(recPoints[0], recPoints[1]);
		String[] minLngLat = Tools.getRecMinLntLat(recPoints[0], recPoints[1]);
		String maxLng = maxLngLat[0];
		String maxLat = maxLngLat[1];
		String minLng = minLngLat[0];
		String minLat = minLngLat[1];
		String x1 = LongHanUtil.formatLngLat2DF(maxLng, 8, 1);
		String y1 = LongHanUtil.formatLngLat2DF(maxLat, 8, 0);
		String x2 = LongHanUtil.formatLngLat2DF(minLng, 8, 1);
		String y2 = LongHanUtil.formatLngLat2DF(minLat, 8, 0);
		ret += y2 + x2 + y1 + x1;
		return ret;
	}

	public String cancleArea(Request req) {
		String ret = "";
		ret = LongHanUtil.makeProtocal(req.getDeviceId(), "47", 6, "");

		return ret;
	}

	public String viewAreas(Request req) {
		String ret = "";
		ret = LongHanUtil.makeProtocal(req.getDeviceId(), "48", 6, "");
		return ret;
	}

	public String oilReduceAlarm(Request req) {
		 

		String deviceid = req.getDeviceId();
		String minOil = RequestUtil.getReqData(req, "minOilValue");
		String maxOil = RequestUtil.getReqData(req, "maxOilValue");
		String ratio = RequestUtil.getReqData(req, "reduceRatio");
		String relation = RequestUtil.getReqData(req, "relation");
		 
		if (relation.equals("0"))
			relation="1";
		else
			relation = "0";
		
		String hexCont = Tools.convertToHex(minOil, 4)
				+ Tools.convertToHex(maxOil, 4) + Tools.convertToHex(ratio, 2)
				+ Tools.convertToHex(relation, 2);

		String ret = "";
		ret = LongHanUtil.makeProtocal(req.getDeviceId(), "fb", 6 + 6, hexCont);
		return ret;
	}

	public String oilReduceAlarmQuery(Request req) {
		 
		String ret = "";
		ret = LongHanUtil.makeProtocal(req.getDeviceId(), "fc", 6, "");
		return ret;
	}

	public static void main(String[] args) {
		String points = "113.18389892578125,23.046248208393926;113.41529846191406,23.18013237070759";
		LongHanAlarm a = new LongHanAlarm();
		a.oilReduceAlarm(new Request());
	}

}
