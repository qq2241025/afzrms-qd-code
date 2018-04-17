/**
 * 
 */
package com.mapabc.gater.directl.encode.guomai;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.NumberFormat;

import com.mapabc.gater.directl.Log;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.constant.StructionType;
import com.mapabc.gater.directl.dbutil.DbOperation;
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
public class GuoMaiAlarm extends AlarmAdaptor {

	private String centerId = "1";
	private String centerPwd = "";
	private String oemCode = "1";

	public GuoMaiAlarm() {

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
		
		String typeCode = req.getDeviceType();
		
		if(typeCode.equals("GP-UCSTC-GPRS")){
			centerPwd = Integer.parseInt("000007D9", 16)+"";
		}else{
			centerPwd = deviceId;
		}
		String hex = GuoMaiProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, centerPwd, protocalNo, ptlByte);
		//Log.getInstance().guomaiLog(req.getDeviceId() + "设置超速指令：" + hex);

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
		String typeCode = req.getDeviceType();
		
		if(typeCode.equals("GP-UCSTC-GPRS")){
			centerPwd = Integer.parseInt("000007D9", 16)+"";
		}else{
			centerPwd = deviceId;
		}
		String hex = GuoMaiProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, centerPwd, "18", null);

		//Log.getInstance().guomaiLog(deviceId + " 取消紧急报警指令：" + hex);

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
		String deviceId = req.getDeviceId();
		 
		String seq = Tools.getRandomString(2);

		String areaNo = RequestUtil.getReqData(req, "areaNo");
		String points = RequestUtil.getReqData(req, "points");
		String[] arrPoints = points.split(";");
		int count = arrPoints.length;
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
			String x = this.DU2DuFen(pointXY.getX() + "");
			String y = this.DU2DuFen(pointXY.getY() + "");
			ptlCont += x + y;
		}
		byte[] ptlBytes = Tools.fromHexString(ptlCont);
		String typeCode = req.getDeviceType();
		
		if(typeCode.equals("GP-UCSTC-GPRS")){
			centerPwd = Integer.parseInt("000007D9", 16)+"";
		}else{
			centerPwd = deviceId;
		}
		String hex = GuoMaiProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, centerPwd, "07", ptlBytes);

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
				//Log.getInstance().errorLog("设置偏航坐标反偏转异常", e);
				e.printStackTrace();
				continue;
			}
			String x = this.DU2DuFen(pointXY.x+"");
			String y = this.DU2DuFen(pointXY.y+"");
			ptlCont += x + y;
		}
		byte[] ptlBytes = Tools.fromHexString(ptlCont);
		String typeCode = req.getDeviceType();
		
		if(typeCode.equals("GP-UCSTC-GPRS")){
			centerPwd = Integer.parseInt("000007D9", 16)+"";
		}else{
			centerPwd = deviceId;
		}
		String hex = GuoMaiProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, centerPwd, ptlNo, ptlBytes);

		return hex;
	}

	// 区域监控设置
	public String areaControlSetting(Request req) {
		String ret = "";
		String deviceId = req.getDeviceId();
		 

		String seq = Tools.getRandomString(2);

		String areaNo = RequestUtil.getReqData(req, "areaNo");
		String speed = RequestUtil.getReqData(req, "limitSpeed");
		String spdDuration = RequestUtil.getReqData(req, "speedDuration");
		String alarmType = RequestUtil.getReqData(req, "alarmType");
		String areaDuration = RequestUtil.getReqData(req, "areaDuration");
		String typehex = "";
		
		if (alarmType.equals("0")) {
			typehex = "02";
		} else if (alarmType.equals("1")) {
			typehex = "01";
		} else if (alarmType.equals("2")) {
			typehex = "03";
		} else {
			typehex = "00";
		}
		if(speed.equals("")){
			speed = "0";
		}
		
		
		String ptlCont = "";
		
		if(req.getDeviceType().equals("GP-UCSTC-GPRS")){
			ptlCont = Tools.convertToHex(areaNo, 2)+Tools.convertToHex(speed, 2)
			+ Tools.convertToHex(spdDuration, 2) + typehex
			+ Tools.convertToHex(areaDuration, 2);
		}else{
			ptlCont = Tools.convertToHex(areaNo, 2)
			+ Tools.convertToHex(spdDuration, 2) + typehex
			+ Tools.convertToHex(areaDuration, 2);
		}
		
		byte[] ptlBytes = Tools.fromHexString(ptlCont);
		String typeCode = req.getDeviceType();
		
		if(typeCode.equals("GP-UCSTC-GPRS")){
			centerPwd = Integer.parseInt("000007D9", 16)+"";
		}else{
			centerPwd = deviceId;
		}
		String hex = GuoMaiProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, centerPwd, "09", ptlBytes);

		return hex;
	}
	//查看区域监控
	// 查看区域监控设置
	public String viewAreaControl(Request req) {
		String ret = "";
		String deviceId = req.getDeviceId();
		 

		String seq = Tools.getRandomString(2);

		String areaNos = RequestUtil.getReqData(req, "areaNos");
		String[] arrNos = areaNos.split(",");
		String hexNos = "";
		for (int j = 0; j < arrNos.length; j++) {
			hexNos += Tools.convertToHex(arrNos[j].trim(), 2);
		}
		String ptlCont = hexNos;
		byte[] ptlBytes = Tools.fromHexString(ptlCont);
		String typeCode = req.getDeviceType();
		
		if(typeCode.equals("GP-UCSTC-GPRS")){
			centerPwd = Integer.parseInt("000007D9", 16)+"";
		}else{
			centerPwd = deviceId;
		}
		String hex = GuoMaiProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, centerPwd, "0a", ptlBytes);

		return hex;
	}
	//查看区域
	// 查看围栏
	public String viewAreas(Request req) {
		String ret = "";
		String deviceId = req.getDeviceId();
		 
		String seq = Tools.getRandomString(2);

		String areaNos = RequestUtil.getReqData(req, "areaNos");
		String[] arrNos = areaNos.split(",");
		String hexNos = "";
		for (int j = 0; j < arrNos.length; j++) {
			hexNos += Tools.convertToHex(arrNos[j].trim(), 2);
		}
		String ptlCont = Tools.int2Hexstring(arrNos.length, 2) + hexNos;
		byte[] ptlBytes = Tools.fromHexString(ptlCont);
		String typeCode = req.getDeviceType();
		
		if(typeCode.equals("GP-UCSTC-GPRS")){
			centerPwd = Integer.parseInt("000007D9", 16)+"";
		}else{
			centerPwd = deviceId;
		}
		String hex = GuoMaiProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, centerPwd, "08", ptlBytes);

		return hex;
	}
	//查看线路
	//查看线路
	public String viewLines(Request req){
		String ret = "";
		String deviceId = req.getDeviceId();
		 

		String seq = Tools.getRandomString(2);

		String lineNos = RequestUtil.getReqData(req, "lineNos");
		String[] liNos = lineNos.split(",");
		String hexNos = "";
		for (int j = 0; j < liNos.length; j++) {
			hexNos += Tools.convertToHex(liNos[j].trim(), 2);
		}
		String ptlCont = Tools.int2Hexstring(liNos.length, 2) + hexNos;
		byte[] ptlBytes = Tools.fromHexString(ptlCont);
		String typeCode = req.getDeviceType();
		
		if(typeCode.equals("GP-UCSTC-GPRS")){
			centerPwd = Integer.parseInt("000007D9", 16)+"";
		}else{
			centerPwd = deviceId;
		}
		String hex = GuoMaiProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, centerPwd, "0C", ptlBytes);

		return hex;
	}
	// 度转换为度分形式
	//线路监控设置(UCSTC)
	public String lineControlSetting(Request req){
		String ret = "";
		String deviceId = req.getDeviceId();
		 

		String seq = Tools.getRandomString(2);

		String lineNo = RequestUtil.getReqData(req, "lineNo");
		String speed = RequestUtil.getReqData(req, "limitSpeed");
		String spdDuration = RequestUtil.getReqData(req, "speedDuration");
		String alarmType = RequestUtil.getReqData(req, "alarmType");
		String lineDuration = RequestUtil.getReqData(req, "lineDuration");
		String offset = RequestUtil.getReqData(req, "offset");
		String startTime = RequestUtil.getReqData(req, "startTime");
		String endTime = RequestUtil.getReqData(req, "endTime");
		String typehex = "";
		
		if (alarmType.equals("0")) {
			typehex = "02";
		} else if (alarmType.equals("1")) {
			typehex = "01";
		} else if (alarmType.equals("2")) {
			typehex = "03";
		} else {
			typehex = "00";
		}
		if(speed.equals("")){
			speed = "0";
		}
		
		String ptlCont = "";
		
		if(req.getDeviceType().equals("GP-UCSTC-GPRS")){
			ptlCont = Tools.convertToHex(lineNo, 2)+Tools.convertToHex(speed, 2)
			+ Tools.convertToHex(typehex, 2) + startTime+endTime+Tools.convertToHex(offset, 4);
			
		}else{
			return null;
		}
		
		byte[] ptlBytes = Tools.fromHexString(ptlCont);
		String typeCode = req.getDeviceType();
		
		if(typeCode.equals("GP-UCSTC-GPRS")){
			centerPwd = Integer.parseInt("000007D9", 16)+"";
		}else{
			centerPwd = deviceId;
		}
		String hex = GuoMaiProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, centerPwd, "0D", ptlBytes);

		return hex;
	}
	//查看线路监控
	public String viewLineControl(Request req){
		String ret = "";
		String deviceId = req.getDeviceId();
		 

		String seq = Tools.getRandomString(2);

		String lineNos = RequestUtil.getReqData(req, "lineNos");
		String[] linNos = lineNos.split(",");
		String hexNos = "";
		for (int j = 0; j < linNos.length; j++) {
			hexNos += Tools.convertToHex(linNos[j].trim(), 2);
		}
		String ptlCont = hexNos;
		byte[] ptlBytes = Tools.fromHexString(ptlCont);
		String typeCode = req.getDeviceType();
		
		if(typeCode.equals("GP-UCSTC-GPRS")){
			centerPwd = Integer.parseInt("000007D9", 16)+"";
		}else{
			centerPwd = deviceId;
		}
		String hex = GuoMaiProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, centerPwd, "0E", ptlBytes);

		return hex;
	}
	public String DU2DuFen(String xy) {
		int du = (int) Double.parseDouble(xy);
		String[] xys = xy.split("\\.");
		double fen = Double.parseDouble("0." + xys[1]) * 60;

		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(3);
		String sfen = nf.format(fen);

		String df = String.valueOf(du) + sfen.replaceAll("\\.", "");
		while (df.length() < 8) {
			df = "0" + df;
		}
		return df;

	}

	public static void main(String[] args) {
		String x = "16.234433233";
		GuoMaiAlarm a = new GuoMaiAlarm();
		System.out.println(a.DU2DuFen(x));
	}

}
