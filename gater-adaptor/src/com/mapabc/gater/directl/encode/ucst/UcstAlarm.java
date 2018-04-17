/**
 * 
 */
package com.mapabc.gater.directl.encode.ucst;

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
public class UcstAlarm extends AlarmAdaptor
{

	private String centerId = "1";
	private String centerPwd = "";
	private String oemCode = "1";

	public UcstAlarm() {

	}

	public String overspeedAlarm(Request req) {
		String ret = "";
		String hexS = "";
		String time = "";
		String protocalNo = "06";
	 
		
		String seq = Tools.getRandomString(2);
		String deviceId = req.getDeviceId();

		String maxSpeed = RequestUtil.getReqData(req, "max");
		time = RequestUtil.getReqData(req, "duration");
		int duraT = Integer.parseInt(time);
		int maxS = Integer.parseInt(maxSpeed);

		if (maxS <= 0) {
			hexS = "00";
		}
		if (maxS > 255) {
			hexS = "FF";
		}
		if (duraT < 1) {
			duraT = 1;
		} else if (duraT > 255) {
			duraT = 255;
		}

		hexS = Tools.int2Hexstring(maxS, 2);
		String duraHex = Tools.int2Hexstring(duraT, 2);

		String protocalCont = hexS + duraHex;
		byte[] ptlByte = Tools.fromHexString(protocalCont);
		
		String hex = UcstProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, deviceId, protocalNo, ptlByte);
		//Log.getInstance().ucstLog(req.getDeviceId() + "设置超速指令：" + hex);

		return hex;
	}

	/*
	 * 取消报警，没有定义的报警用取消当前报警实现即 v=0 @param seq:序列号 @param v:0取消当前报警 1紧急报警 2劫警
	 * 3区域报警 4超速报警
	 */
	public String cancelAlarm(Request req) {
		String deviceId = req.getDeviceId();
		 
		
		String seq = Tools.getRandomString(2);
		String type = RequestUtil.getReqData(req, "type");

		String hex = UcstProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, deviceId, "18", null);

		//Log.getInstance().ucstLog(deviceId + " 取消紧急报警指令：" + hex);

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
		String ret = "";
		String deviceId = req.getDeviceId(); ;
		 
		
		String seq = Tools.getRandomString(2);

		String areaNo = RequestUtil.getReqData(req, "areaNo");
		String points = RequestUtil.getReqData(req, "points");
		String[] arrPoints = points.split(";");
		int count = arrPoints.length;
		if (count < 3){
			//Log.getInstance().ucstLog(deviceId+" 设置的区域点至少要3个点！");
			return null;
		}
		String ptlNo = "07";
		String ptlCont = "0101" + Tools.int2Hexstring(count, 2);// 限定区域总数为1，编号为1
		CoordinateConvertService coordService = new MapabcCoordServiceImpl();
		DPoint pointXY = null;
		
		for (int i = 0; i < count; i++) {
			String[] point = arrPoints[i].split(",");
			
			if (point == null || point.length < 2)
				continue;
			try {
				pointXY = coordService.reverseDeflect(Double
						.parseDouble(point[0]), Double.parseDouble(point[1]));
			} catch (Exception e) {
				//Log.getInstance().errorLog("设置区域坐标反偏转异常", e);
				e.printStackTrace();
				continue;
			}
			String x = this.DU2DuFen(pointXY.x+"");
			String y = this.DU2DuFen(pointXY.y+"");
			ptlCont += x + y;
		}
		byte[] ptlBytes = Tools.fromHexString(ptlCont);
		String hex = UcstProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, deviceId, "07", ptlBytes);

		return hex;

	}

	// 偏航报警
	public String lineAlarm(Request req) {
		String ret = "";
		String deviceId = req.getDeviceId();
		 
		String seq = Tools.getRandomString(2);

		String areaNo = RequestUtil.getReqData(req, "lineNo");
		String points = RequestUtil.getReqData(req, "points");
		String[] arrPoints = points.split(";");
		int count = arrPoints.length;
		String ptlNo = "0b";
		String ptlCont = "0101" + Tools.int2Hexstring(count, 2);// 限定线路总数为1，编号为1

		for (int i = 0; i < count; i++) {
			String[] point = arrPoints[i].split(",");
			if (point == null || point.length < 2)
				continue;
			String x = this.DU2DuFen(point[0]);
			String y = this.DU2DuFen(point[1]);
			ptlCont += x + y;
		}
		byte[] ptlBytes = Tools.fromHexString(ptlCont);
		String hex = UcstProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, deviceId, ptlNo, ptlBytes);

		return hex;
	}

	//区域监控设置
	public String areaControlSetting(Request req) {
		String ret = "";
		String deviceId = req.getDeviceId();
		String seqs = RequestUtil.getReqData(req, "sequence");
		 
		
		String seq = Tools.getRandomString(2);

		String areaNo = RequestUtil.getReqData(req, "areaNo");
		String spd = RequestUtil.getReqData(req, "limitSpeed");
		String spdDuration = RequestUtil.getReqData(req, "speedDuration");
		String alarmType = RequestUtil.getReqData(req, "alarmType");
		String areaDuration = RequestUtil.getReqData(req, "areaDuration");
		if (areaNo=="" || spd=="" || spdDuration=="" || alarmType=="" || areaDuration=="" ){
			//Log.getInstance().ucstLog(deviceId+" 设置区域监控参数存在空值.");
			return null;
		}

		String typehex = "";
		if (alarmType.equals("0")) {
			typehex = "02";
		} else if (alarmType.equals("1")) {
			typehex = "01";
		} else if (alarmType.equals("2")) {
			typehex = "03";
		} else {
			typehex = "03";
		}
		String ptlCont = Tools.convertToHex(areaNo, 2)+ Tools.convertToHex(spd, 2)
				+ Tools.convertToHex(spdDuration, 2) + typehex
				+ Tools.convertToHex(areaDuration, 2)+"00002359";
		byte[] ptlBytes = Tools.fromHexString(ptlCont);

		String hex = UcstProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, deviceId, "09", ptlBytes);

		return hex;
	}
	
	//查看区域监控设置
	public String viewAreaControl(Request req) {
		String ret = "";
		String deviceId = req.getDeviceId();
		String seqs = RequestUtil.getReqData(req, "sequence");
		 
		
		
		String seq = Tools.getRandomString(2);

		String areaNos = RequestUtil.getReqData(req, "areaNos");
		String[] arrNos = areaNos.split(",");
		String hexNos = "";
		for (int j=0; j<arrNos.length; j++){
			hexNos += Tools.convertToHex(arrNos[j].trim(), 2);
		}
		String ptlCont = hexNos;
		byte[] ptlBytes = Tools.fromHexString(ptlCont);

		String hex = UcstProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, deviceId, "0a", ptlBytes);

		return hex;
	}
	
	
	//查看围栏
	public String viewAreas(Request req) {
		String ret = "";
		String deviceId = req.getDeviceId();
		 
		
		
		String seq = Tools.getRandomString(2);

		String areaNos = RequestUtil.getReqData(req, "areaNos");
		String[] arrNos = areaNos.split(",");
		String hexNos = "";
		for (int j=0; j<arrNos.length; j++){
			hexNos += Tools.convertToHex(arrNos[j].trim(), 2);
		}
		String ptlCont = Tools.int2Hexstring(arrNos.length, 2)+hexNos;
		byte[] ptlBytes = Tools.fromHexString(ptlCont);

		String hex = UcstProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, deviceId, "08", ptlBytes);

		return hex;
	}
	
	//线路监控设置
	public String lineControlSetting(Request req) {
		String ret = "";
		String deviceId = req.getDeviceId();
		 
		
		String seq = Tools.getRandomString(2);

		String areaNo = RequestUtil.getReqData(req, "lineNo");
		String spd = RequestUtil.getReqData(req, "limitSpeed");
		String spdDuration = RequestUtil.getReqData(req, "speedDuration");
		String alarmType = RequestUtil.getReqData(req, "alarmType");
		String areaDuration = RequestUtil.getReqData(req, "lineDuration");
		String offset = RequestUtil.getReqData(req, "offset");
		if (areaNo=="" || spd=="" || spdDuration=="" || alarmType=="" || areaDuration==""||offset==""){
			//Log.getInstance().ucstLog(deviceId+" 设置路线监控参数存在空值.");
			return null;
		}
		int intOffset = Integer.parseInt(offset);
		if (intOffset <10){
			offset = "10";
		}
		if (intOffset > 2550){
			offset = "2550";
		}
		String typehex = "";
		if (alarmType.equals("0")) {//出
			typehex = "02";
		} else if (alarmType.equals("1")) {//进
			typehex = "00";
		} else if (alarmType.equals("2")) {//禁入禁出
			typehex = "01";
		} else {//其他值取消
			typehex = "03";
		}
		String ptlCont = Tools.convertToHex(areaNo, 2)+ Tools.convertToHex(spd, 2)
				+ Tools.convertToHex(spdDuration, 2) + typehex
				+ Tools.convertToHex(areaDuration, 2)+"00002359";
		byte[] ptlBytes = Tools.fromHexString(ptlCont);

		String hex = UcstProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, deviceId, "0d", ptlBytes);

		return hex;
	}
	//查看线路监控设置
	public String viewLineControl(Request req) {
		String ret = "";
		String deviceId = req.getDeviceId();
		 
		
		String seq = Tools.getRandomString(2);

		String areaNos = RequestUtil.getReqData(req, "lineNos");
		String[] arrNos = areaNos.split(",");
		String hexNos = "";
		for (int j=0; j<arrNos.length; j++){
			hexNos += Tools.convertToHex(arrNos[j].trim(), 2);
		}
		String ptlCont = hexNos;
		byte[] ptlBytes = Tools.fromHexString(ptlCont);

		String hex = UcstProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, deviceId, "0e", ptlBytes);

		return hex;
	}
	
	//查看线路
	public String viewLines(Request req) {
		String ret = "";
		String deviceId = req.getDeviceId();
		String seqs = RequestUtil.getReqData(req, "sequence");
		 
		
		
		String seq = Tools.getRandomString(2);

		String areaNos = RequestUtil.getReqData(req, "lineNos");
		String[] arrNos = areaNos.split(",");
		String hexNos = "";
		for (int j=0; j<arrNos.length; j++){
			hexNos += Tools.convertToHex(arrNos[j].trim(), 2);
		}
		String ptlCont = Tools.int2Hexstring(arrNos.length, 2)+hexNos;
		byte[] ptlBytes = Tools.fromHexString(ptlCont);

		String hex = UcstProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, deviceId, "0c", ptlBytes);

		return hex;
	}
	public String alarmParams(Request req) {
	 	String deviceId = req.getDeviceId();
		String interval = RequestUtil.getReqData(req, "interval");
		
		String seq = Tools.getRandomString(2);
		
		String ptlCont = "010f02"+Tools.convertToHex(interval, 4);
		byte[] ptlBytes = Tools.fromHexString(ptlCont);

		String hex = UcstProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, deviceId, "1b", ptlBytes);

		
		return hex;
	}

	// 度转换为度分形式
	public String DU2DuFen(String xy) {
		int du = (int) Double.parseDouble(xy);
		String[] xys = xy.split("\\.");
		double fen = Double.parseDouble("0." + xys[1]) * 60;

		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(3);
		nf.setMaximumIntegerDigits(2);
		nf.setMinimumFractionDigits(3);
		nf.setMinimumIntegerDigits(2);
		String sfen = nf.format(fen);

		String df = String.valueOf(du) + sfen.replaceAll("\\.", "");
		while (df.length() < 8) {
			df = "0" + df;
		}
		return df;

	}

	public static void main(String[] args) {
		String x = "23.18013237070759";
		UcstAlarm a = new UcstAlarm();
		System.out.println(a.DU2DuFen(x));
	}

}
