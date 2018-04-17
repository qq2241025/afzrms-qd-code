/**
 * 
 */
package com.mapabc.gater.directl.encode.swyj;

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
public class SwyjAlarm extends AlarmAdaptor {

	private String head = "*HQ,";
	private String end = "#";

	public String overspeedAlarm(Request req) {
		String hex = "";
		String ret = "";
		String spd = "";
		String speed = RequestUtil.getReqData(req, "max");
		String duration = RequestUtil.getReqData(req, "duration");
		String gpssn = req.getDeviceId();
		 
		
		try {
			double dspd = Double.parseDouble(speed) / 1.852;
			spd = (int) dspd + "";
			if (dspd == 0 || duration == "")
				duration = "0";
		} catch (Exception e) {
			spd = "0";// 为取消
			duration = "0";
			e.printStackTrace();
		}
		// *XX,YYYYYYYYYY,S14,HHMMSS,Max_speed,Min_speed,M,countinue #
		// GPS数据有效时才触发报警
		ret = head + gpssn + ",S14," + Tools.getCurHMS() + "," + spd + ",0,1,"
				+ duration + end;
		//Log.getInstance().tianheLog(ret);
		hex = Tools.bytesToHexString(ret.getBytes());
		return hex;
	}

	/*
	 * 取消报警，没有定义的报警用取消当前报警实现即 v=0 @param seq:序列号 @param v:0取消当前报警 1紧急报警 2劫警
	 * 3区域报警 4超速报警
	 */
	public String cancelAlarm(Request req) {
		String hex = "";
		String ret = null;
		String type = RequestUtil.getReqData(req, "type");
		String gpssn = req.getDeviceId();
		 
		// if (type.equals("3")) {
		// ret = head + gpssn + ",S14," + Tools.getCurHMS() + ",0,"
		// + ",0,1,10" + end;
		// } else if (type.equals("4")) {
		// ret = head + gpssn + ",S21," + Tools.getCurHMS() + ",0,0" + end;
		// } else {
		ret = head + gpssn + ",R7," + Tools.getCurHMS() + end;
		// }
		//Log.getInstance().tianheLog(ret);
		hex = Tools.bytesToHexString(ret.getBytes());
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
		String ret = "";
		// *XX,YYYYYYYYYY,S21,HHMMSS,Lx,M,D,Minlatitude,Maxlatitude,G,Minlongitude,Maxlongitude#
		String type = "";
		String alarmType = RequestUtil.getReqData(req, "alarmType");
		String areaNo = RequestUtil.getReqData(req, "areaNo");
		String gpssn = req.getDeviceId();
		 
		
		if (alarmType.equals("0"))
			type = "3"; // 出区域
		if (alarmType.equals("1")) {
			type = "1";// 进区域
		}
		if (alarmType.equals("2")) {
			type = "0";
		}
		String recDot = RequestUtil.getReqData(req, "points");
		String[] xys = recDot.split(";");
		if (xys == null || xys.length < 2) {
			//Log.getInstance().tianheLog(gpssn + "围栏点个数必须>=2");
			return null;
		}
//		String[] leftUp = xys[0].split(",");
//		String[] rightDown = xys[1].split(",");
//
//		double leftUpX = Double.parseDouble(leftUp[0]);
//		double leftUpY = Double.parseDouble(leftUp[1]);
//		double rightDownX = Double.parseDouble(rightDown[0]);
//		double rightDownY = Double.parseDouble(rightDown[1]);
//		double leftDownX = Double.parseDouble(leftUp[0]);
//		double leftDownY = Double.parseDouble(rightDown[1]);
//		double rightUpX = Double.parseDouble(rightDown[0]);
//		double rightUpY = Double.parseDouble(leftUp[1]);
		String[] maxLngLat = Tools.getRecMaxLntLat(xys[0],
				xys[1]);
		String[] minLngLat = Tools.getRecMinLntLat(xys[0],
				xys[1]);
		
		
		double maxLongitude = Double.parseDouble(maxLngLat[0]);
		double maxLatitude = Double.parseDouble(maxLngLat[1]);
		double minLongitude = Double.parseDouble(minLngLat[0]);
		double minLatitude = Double.parseDouble(minLngLat[1]);

		String maxx = getLatLongString(maxLongitude, 0);
		String maxy = getLatLongString(maxLatitude, 1);
		String minx = getLatLongString(minLongitude, 0);
		String miny = getLatLongString(minLatitude, 1);

		String lux = getLatLongString(minLongitude, 0);
		String luy = getLatLongString(maxLatitude, 1);
		String rux = getLatLongString(maxLongitude, 0);
		String ruy = getLatLongString(maxLatitude, 1);

		String rdx = getLatLongString(maxLongitude, 0);
		String rdy = getLatLongString(minLatitude, 1);
		String ldx = getLatLongString(minLongitude, 0);
		String ldy = getLatLongString(minLatitude, 1);

		ret = head + gpssn + ",S21," + Tools.getCurHMS() + "," + areaNo + ","
				+ type + ",N," + ldy + "," + luy + "," + "E," + ldx + "," + rdx
				+ end;
		//Log.getInstance().tianheLog(ret);
		hex = Tools.bytesToHexString(ret.getBytes());
		return hex;

	}

	public static String getLatLongString(double value, int type) {
		StringBuffer buf = new StringBuffer();

		int du = (int) value;
		int length = (type == 0) ? 3 : 2;

		String sdu = extentString(Integer.toString(du), length);

		double fen = ((value - du) * 60.0);
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumIntegerDigits(2);
		nf.setMinimumIntegerDigits(2);
		nf.setMaximumFractionDigits(3);
		nf.setMinimumFractionDigits(3);
		
		String sfen = nf.format(fen);//String.valueOf(fen).substring(0, 6);

		// String sfen = extentString(Integer.toString(fen), 2);
		//		
		// double ffen = (value - du) * 60.0 - fen;
		//		
		// String subfen = String.valueOf(ffen);

		// String ret = sdu + sfen + subfen.substring(subfen.indexOf(".") + 1);

		String ret = sdu + sfen;
		// buf.append(extentString(Integer.toString(subfen), 4));
		return ret;
	}

	/**
	 * 将字符串扩充成指定长度，扩充的方式是在前面增加'0'
	 * 
	 * @param org
	 *            String
	 * @param length
	 *            int
	 * @return String
	 */
	public static String extentString(String org, int length) {
		if (org == null) {
			return null;
		}

		if (org.length() == length) {
			return org;
		}

		StringBuffer buf = new StringBuffer();
		int num = length - org.length();
		for (int i = 0; i < num; i++) {
			buf.append('0');
		}
		buf.append(org);
		return buf.toString();
	}

	// 偏航报警
	public String setLineAlarm(Request req) {
		String hex = "";

		return hex;
	}

	// 取消区域设置
	public String cancleArea(Request req) {
		String hex = "";
		String gpssn = req.getDeviceId();
		String areaNo = RequestUtil.getReqData(req, "areaNo");//0取消所有区域
		 
		String ret = head + gpssn + ",S21," + Tools.getCurHMS() + ","+areaNo+",0" + end;
		hex = Tools.bytesToHexString(ret.getBytes());
		//Log.getInstance().tianheLog(ret);
		return hex;

	}

	public String cancleLine(Request req) {
		String hex = "";

		return hex;

	}

	// 报警参数设置
	public String alarmParams(Request req) {
		// C M 1 4C54:1001|219|202;5;2;5 571
		// C M 223 4C54:13455141504|219|202;2;2;5 6DD
		String ret = "";
		String type = RequestUtil.getReqData(req, "type");
		String gpssn = req.getDeviceId();
		String timelen = RequestUtil.getReqData(req, "timelen");
		 
		if (type.equals("3")) {
			ret = head + gpssn + ",S18," + Tools.getCurHMS() + "," + timelen
					+ ",1"

					+ end;
		}
		//Log.getInstance().tianheLog(ret);
		String hex = Tools.bytesToHexString(ret.getBytes());

		return hex;
	}
	
	//报警开关
	public String switchAlarm(Request req){
		String type = RequestUtil.getReqData(req, "type");
		
		return "";
	}
	
	public String fortificationSetting(Request req) {
		//*XX,YYYYYYYYYY,S6,HHMMSS,M#
		String ret = "";
		String type = RequestUtil.getReqData(req, "type");
		if (type.equals("0")){
			ret = head+req.getDeviceId()+",S6,"+Tools.getCurHMS()+",3"+end;
		}else if(type.equals("1")){
			ret = head+req.getDeviceId()+",S6,"+Tools.getCurHMS()+",2"+end;
		}
		return Tools.bytesToHexString(ret.getBytes());
	}

}
