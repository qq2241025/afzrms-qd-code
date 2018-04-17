/**
 * 
 */
package com.mapabc.gater.directl.encode.lingtu;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.NumberFormat;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.AlarmAdaptor;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;
import com.mapabc.gater.lbmp.service.CoordinateConvertService;
import com.mapabc.gater.lbmp.service.impl.MapabcCoordServiceImpl;
import com.mapabc.geom.DPoint;

/**
 * @author shiguang.zhou
 * 
 */
public class LingTuAlarm extends AlarmAdaptor {

	String head = "C M ";
	String end = "\r\n";
	String cmdSeq = Tools.getRandomString(4);

	public String overspeedAlarm(Request req) {
		String ret = "";
		String time = "";
		
		
		String maxSpeed = RequestUtil.getReqData(req, "max");
		int maxS = Integer.parseInt(maxSpeed);

		RequestUtil.getDealRequest(req, "204", cmdSeq);

		head = head + cmdSeq + " ";

		String hexS = Integer.toHexString(maxS);
		if (maxS == 0) {
			hexS = "000";
		}
		if (maxS > 255) {
			maxS = 255;
		}
		String cmd = LingtuUtil.createOemCodeBySn(req.getDeviceId()) + ":"
				+ req.getDeviceId() + "|202|" + hexS + " ";
		String vcode = Tools.getVerfyCode(cmd.getBytes());
		ret = head + cmd + vcode + end;
		 
		String hex = Tools.bytesToHexString(ret.getBytes());

		return hex;
	}

	/*
	 * 取消报警，没有定义的报警用取消当前报警实现即 v=0 @param seq:序列号 @param v:0取消当前报警 1紧急报警 2劫警
	 * 3区域报警 4超速报警
	 */
	public String cancelAlarm(Request req) {
		String ret = "";
		// C M c 4C54:1001|60|1 38B
		RequestUtil.getDealRequest(req, "206",  cmdSeq);
		String type = RequestUtil.getReqData(req, "type");
		String gsn = req.getDeviceId();

		head = head + cmdSeq + " ";

		String cmd = "";

		if (type.equals("3")) {
			cmd = LingtuUtil.createOemCodeBySn(gsn) + ":" + gsn + "|201| ";
		} else if (type.equals("4")) {
			cmd = LingtuUtil.createOemCodeBySn(gsn) + ":" + gsn + "|202|000 ";
		} else {
			cmd = LingtuUtil.createOemCodeBySn(gsn) + ":" + gsn + "|60|" + type
					+ " ";
		}
		String vcode = Tools.getVerfyCode(cmd.getBytes());
		ret = head + cmd + vcode + end;
 		String hex = Tools.bytesToHexString(ret.getBytes());

		return hex;
	}

	/**
	 * 多边形区域报警设置(矩形区域也可以用此接口)
	 * 
	 * @param seq：指令序列号
	 * @param alarmType：类型
	 *            0出 1进 2取消
	 * @param rangeId：区域编号
	 * @param startDate:区域生效的开始时间
	 * @param endDate:区域生效的结束时间
	 * @param points：顶点数组
	 * @return
	 */
	public String areaAlarm(Request req) {// 坐标要反偏转发到终端
		String ret = "";
		// C M 13 4C54:1001|200|2;15752A00!8954400,1BE51D00!44AA200;1 B1E

		RequestUtil.getDealRequest(req, "200",   cmdSeq);

		String alarmType = RequestUtil.getReqData(req, "alarmType");
		String gsn = req.getDeviceId();

		head = head + cmdSeq + " ";

		String pointstr = (String) req.getDatas().get("points");
		String[] points = pointstr.split(";");

		String xys = "";
		String[] maxLngLat = Tools.getRecMaxLntLat(points[0], points[1]);
		String[] minLngLat = Tools.getRecMinLntLat(points[0], points[1]);

		double maxLongitude = Double.parseDouble(maxLngLat[0]);
		double maxLatitude = Double.parseDouble(maxLngLat[1]);
		
		double minLongitude = Double.parseDouble(minLngLat[0]);
		double minLatitude = Double.parseDouble(minLngLat[1]);

		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(6);
		String lux = nf.format(minLongitude);
		String luy = nf.format(maxLatitude);
		
		String rdx = nf.format(maxLongitude);
		String rdy = nf.format(minLatitude);

		String slux = Tools.Du2Mills(lux);
		String sluy = Tools.Du2Mills(luy);
		String srdx = Tools.Du2Mills(rdx);
		String srdy = Tools.Du2Mills(rdy);

		xys += slux + "!" + sluy + "," + srdx + "!" + srdy;

		String cmd = LingtuUtil.createOemCodeBySn(gsn) + ":" + gsn + "|200|2;"
				+ xys + ";" + alarmType + " ";
		String vcode = Tools.getVerfyCode(cmd.getBytes());
		ret = head + cmd + vcode + end;
		String hex = Tools.bytesToHexString(ret.getBytes());

		return hex;

	}

	// 偏航报警
	public String lineAlarm(Request req) {
		String ret = "";
		RequestUtil.getDealRequest(req, "202",   cmdSeq);

		String distance = RequestUtil.getReqData(req, "offset");
		String gsn = req.getDeviceId();

		head = head + cmdSeq + " ";

		int maxDis = Integer.parseInt("ffff", 16);
		int curDis = Integer.parseInt(distance);
		if (curDis > maxDis) {
			curDis = maxDis;
		}
		String hexDis = Integer.toHexString(curDis);

		String pointstr = (String) req.getDatas().get("points");
		String[] points = pointstr.split(";");
		CoordinateConvertService coordService = new MapabcCoordServiceImpl();
		DPoint pointXY = null;

		String xys = "";
		for (int i = 0; i < points.length; i++) {
			String[] xy = points[i].split(",");
			double x = 0;
			double y = 0;
			try {
				x = Double.parseDouble(xy[0]);
				y = Double.parseDouble(xy[1]);
				if (x == 0 || y == 0)
					continue;
				pointXY = coordService.reverseDeflect(x, y);

			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			NumberFormat nf = NumberFormat.getInstance();
			nf.setMaximumFractionDigits(6);
			String fx = nf.format(pointXY.x);
			String fy = nf.format(pointXY.y);
			String sx = Tools.Du2Mills(fx);
			String sy = Tools.Du2Mills(fy);

			if (i == points.length - 1) {
				xys += sx + "!" + sy;
			} else {
				xys += sx + "!" + sy + ",";
			}
		}
		String cmd = LingtuUtil.createOemCodeBySn(gsn) + ":" + gsn + "|206|1;"
				+ points.length + ";" + hexDis + ";" + xys + " ";
		String vcode = Tools.getVerfyCode(cmd.getBytes());
		ret = head + cmd + vcode + end;
		String hex = Tools.bytesToHexString(ret.getBytes());

		return hex;
	}

	public String cancleArea(Request req) {
		String ret = "";
		RequestUtil.getDealRequest(req, "201",   cmdSeq);
		String alarmType = RequestUtil.getReqData(req, "alarmType");
		String gsn = req.getDeviceId();

		head = head + cmdSeq + " ";

		// 取消终端区域设置C M 14 4C54:1001|201| 387
		String cancelCmd = LingtuUtil.createOemCodeBySn(gsn) + ":" + gsn
				+ "|201| ";
		ret = head + cancelCmd + Tools.getVerfyCode(cancelCmd.getBytes()) + end;
		String hex = Tools.bytesToHexString(ret.getBytes());

		return hex;

	}

	public String cancleLine(Request req) {
		String ret = "";
		RequestUtil.getDealRequest(req, "203",   cmdSeq);

		String gsn = req.getDeviceId();
		head = head + cmdSeq + " ";

		String cmd = LingtuUtil.createOemCodeBySn(gsn) + ":" + gsn + "|207|1 ";
		String vcode = Tools.getVerfyCode(cmd.getBytes());
		ret = head + cmd + vcode + end;

		String hex = Tools.bytesToHexString(ret.getBytes());

		return hex;

	}

	// 报警参数设置
	public String alarmParams(Request req) {
		// C M 1 4C54:1001|219|202;5;2;5 571
		// C M 223 4C54:13455141504|219|202;2;2;5 6DD

		String ret = "";
		RequestUtil.getDealRequest(req, "207",   cmdSeq);

		String gsn = req.getDeviceId();
		String timelen = RequestUtil.getReqData(req, "timelen");
		String interval = RequestUtil.getReqData(req, "interval");
		String times = RequestUtil.getReqData(req, "times");
		String type = RequestUtil.getReqData(req, "type");

		head = head + cmdSeq + " ";

		String cmd = LingtuUtil.createOemCodeBySn(gsn) + ":" + gsn + "|219|"
				+ type + ";" + timelen + ";" + interval + ";" + times + " ";
		String vcode = Tools.getVerfyCode(cmd.getBytes());
		ret = head + cmd + vcode+end;
		String hex = Tools.bytesToHexString(ret.getBytes());

		return hex;
	}
	
	public String temperatureSetting(Request req) {

		String ret = "";
	 
		head = head + cmdSeq + " ";

		String cmd = "";
		String type = RequestUtil.getReqData(req, "type");
		String gsn  = req.getDeviceId();
		String up = RequestUtil.getReqData(req, "highTemp");
		String down = RequestUtil.getReqData(req, "lowTemp");

		if (type.equals("0")) {
			// 取消温度检测
			cmd = LingtuUtil.createOemCodeBySn(gsn) + ":" + gsn + "|208|" + type + " ";

		} else {
			int upt = Integer.parseInt(up) & 0xffff;
			String upHex = Integer.toHexString(upt);
			 

			int donwt = Integer.parseInt(down) & 0xffff;
			String donwHex = Integer.toHexString(donwt);
			 

			cmd = LingtuUtil.createOemCodeBySn(gsn) + ":" + gsn + "|208|" + type + ";"+donwHex+";"+upHex+" ";
		}
		String vcode = Tools.getVerfyCode(cmd.getBytes());
		ret = head + cmd + vcode+end;
		ret = Tools.bytesToHexString(ret.getBytes());
		return ret;
	
	}
	
	public String tirePressureAlarm(Request req) {
		String ret = "";
		String cmd = "";

		head = "C M " + cmdSeq + " ";
		String gsn = req.getDeviceId();
		String type = RequestUtil.getReqData(req, "type");
		String kpa = RequestUtil.getReqData(req, "pressureValue");

		if (type.equals("0")) {// 取消设置
			cmd = LingtuUtil.createOemCodeBySn(gsn) + ":" + gsn + "|213|0;FFFFFFFF ";
		} else if (type.equals("1")) {
			String hexPressure = Integer.toHexString(Integer.parseInt(kpa));// 千帕
			cmd = LingtuUtil.createOemCodeBySn(gsn) + ":" + gsn + "|213|1;" + hexPressure
					+ " ";
		}

		String vcode = Tools.getVerfyCode(cmd.getBytes());
		ret = head + cmd + vcode+end;
		ret = Tools.bytesToHexString(ret.getBytes());
		return ret;
	}
	
	 public String timeoutStopCarAlarm(Request req){

			String ret = "";
			String cmd = "";

			head = "C M " + cmdSeq + " ";
			String gsn = req.getDeviceId();
			String type = RequestUtil.getReqData(req, "type");
			String maxStopTime  = RequestUtil.getReqData(req, "maxStopTime");
			
			if (type.equals("0") || maxStopTime.equals("0")) {// 取消设置
				cmd = LingtuUtil.createOemCodeBySn(gsn) + ":" + gsn  + "|215|0 ";
			} else if (type.equals("1") ) {
				String hexRest = Integer.toHexString(Integer.parseInt(maxStopTime ));
				cmd = LingtuUtil.createOemCodeBySn(gsn) + ":" + gsn  + "|215|1;" + hexRest
						+ " ";
			}

			String vcode = Tools.getVerfyCode(cmd.getBytes());
			ret = head + cmd + vcode+end;
			ret = Tools.bytesToHexString(ret.getBytes());
			return ret;
		
	 }
}
