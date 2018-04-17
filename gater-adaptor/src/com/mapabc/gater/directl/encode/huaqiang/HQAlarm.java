/**
 * 
 */
package com.mapabc.gater.directl.encode.huaqiang;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.NumberFormat;

 
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.AlarmAdaptor;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;
import com.mapabc.geom.DPoint;
 
 
 

/**
 * @author shiguang.zhou
 * 
 */
public class HQAlarm extends AlarmAdaptor {

	 

	public String overspeedAlarm(Request req) {
		String hex = "";
		String speed = RequestUtil.getReqData(req, "max");
		int dSpeed = Integer.parseInt(speed);
		if (dSpeed>255 || dSpeed==0){//取消超速设置
			String cancelCmd = "AH(1FF)";
			cancelCmd = HQ20Util.makeCommandStr(cancelCmd, true, true);
			return cancelCmd;
		}
		StringBuffer buf = new StringBuffer();
		buf.append("AH(1");
		double knotSpeed = HQ20Util.KMHOUR2Knot(dSpeed);
		String knotSpeedStr = HQ20Util.extentString(Integer
				.toHexString((int) knotSpeed), 2);
		buf.append(knotSpeedStr);
		buf.append(")");
		String cmdStr = HQ20Util.makeCommandStr(buf.toString(), true, true);
		 
		hex = Tools.bytesToHexString(cmdStr.getBytes());

		return hex;
	}

 
	public String cancelAlarm(Request req) {
		String hex = "";
		String type = RequestUtil.getReqData(req, "type");
		if (type.equals("3")) {
			String head = Tools.bytesToHexString("[HQ2000AO".getBytes());
			String cancelcmd = "";
			cancelcmd += head + "0004";
			cancelcmd += "01"+Tools.bytesToHexString("D".getBytes());
			cancelcmd += "0004"+Tools.bytesToHexString("]".getBytes());
		    hex = Tools.bytesToHexString(cancelcmd.getBytes());
		} else if (type.equals("4")) {			 
			StringBuffer buf = new StringBuffer();
			buf.append("AH(1");
			buf.append("FF");
			buf.append(")");
			String  cmdStr = HQ20Util.makeCommandStr(buf.toString(), true, true);
			 hex = Tools.bytesToHexString(cmdStr.getBytes());
		} else {
			String ret = "";
			String cmd = "BC";
			ret = HQ20Util.makeCommandStr(cmd, false, true);
			 hex = Tools.bytesToHexString(ret.getBytes());
		}
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
	public String areaAlarm(Request req) {//坐标要反偏转发到终端
		String hex = "";

		StringBuffer buf = new StringBuffer();
		String alarmType = RequestUtil.getReqData(req, "alarmType");
		buf.append("AD");
		if (alarmType.equals("0")) {
			buf.append("O(");
		} else if (alarmType.equals("1")) {
			buf.append("I(");
		} else if (alarmType.equals("2")) {
			buf.append("A(");
		} else if (alarmType.equals("3")) {
			buf.append("D(");
		}
		String dots = RequestUtil.getReqData(req, "points");
		String[] xys = dots.split(";");
		 
		if (xys==null || xys.length<=1){
			//log.info(req.getDeviceId()+"HQ围栏至少需要两个点！");
			return null;
		}
//		String[] leftUp = xys[0].split(",");
//		String[] rightDown = xys[1].split(",");
		
		String[] maxLngLat = Tools.getRecMaxLntLat(xys[0],
				xys[1]);
		String[] minLngLat = Tools.getRecMinLntLat(xys[0],
				xys[1]);
		
		
		double maxLongitude = Double.parseDouble(maxLngLat[0]);
		double maxLatitude = Double.parseDouble(maxLngLat[1]);
		double minLongitude = Double.parseDouble(minLngLat[0]);
		double minLatitude = Double.parseDouble(minLngLat[1]);

		buf.append(HQ20Util.getLatLongString(maxLatitude, 1));
		buf.append(HQ20Util.getLatLongString(maxLongitude, 0));
		buf.append(HQ20Util.getLatLongString(minLatitude, 1));
		buf.append(HQ20Util.getLatLongString(minLongitude, 0));
		buf.append(")");

		String cmd = HQ20Util.makeCommandStr(buf.toString(), true, true);

		hex = Tools.bytesToHexString(cmd.getBytes());
		
		return hex;

	}

	// 偏航报警
	public String setLineAlarm(Request req) {
		String hex = "";

		return hex;
	}

	public String cancleArea(Request req) {
		String hex = "";

		return hex;

	}

	public String cancleLine(Request req) {
		String hex = "";

		return hex;

	}

	// 报警参数设置
	public String alarmParams(Request req) {

		String hex = "";

		return hex;
	}

}
