package com.mapabc.gater.directl.encode.sege;

import java.nio.*;
import java.text.NumberFormat;


import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.AlarmAdaptor;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;
import com.mapabc.geom.DPoint;

public class SegLPG52Alarm extends AlarmAdaptor {
	public SegLPG52Alarm() {
	}

	String centerSn = "01";
	String zuoSn = "001";

	public String overspeedAlarm(Request req) {
		String cmdsn = centerSn + zuoSn;
		String inSeq = Tools.getRandomString(4);
		while (inSeq.length() < 5) {
			inSeq = "0" + inSeq;
		}
		cmdsn = cmdsn + inSeq;
		String speed = RequestUtil.getReqData(req, "max");
		if (speed.trim().length() == 0)
			speed = "0";
		if (!speed.equals("0")) {
			String tmpSpeed = "000";
			if (speed != null) {
				try {
					double dSpeed = Double.parseDouble(speed);
					if (dSpeed > 999) {
						dSpeed = 999;
					}
					double hSpeed = dSpeed / 1.852;
					int iSpeed = (int) hSpeed;
					String tmpSpeed1 = "" + iSpeed;
					tmpSpeed = tmpSpeed1;
					for (int i = 0; i < 3 - tmpSpeed1.length(); i++) {
						tmpSpeed = "0" + tmpSpeed;
					}
				} catch (java.lang.NumberFormatException ex) {
				}
			}
			ByteBuffer buf = ByteBuffer.allocate(27);
			buf.put((byte) 0x5B);
			buf.put((byte) 0x31);
			buf.put(cmdsn.getBytes());
			buf.put((byte) 13);
			buf.put("(CMD,SPD,".getBytes());
			buf.put(tmpSpeed.getBytes());
			buf.put((byte) 0x29);
			buf.put((byte) 0x5D);
			String ret =  Tools.bytesToHexString(buf.array()) ;

			return ret;
		} else {
			ByteBuffer buf = ByteBuffer.allocate(27);
			buf.put((byte) 0x5B);
			buf.put((byte) 0x31);
			buf.put(cmdsn.getBytes());
			buf.put((byte) 13);
			buf.put("(CMD,SPD,000)".getBytes());
			buf.put((byte) 0x5D);
			String ret = Tools.bytesToHexString(buf.array()) ;

			return ret;
		}
	}
	//终端不支持，由中心实现
	public String areaAlarm(Request req) { 
		String cmdsn = centerSn+zuoSn; 
        String inSeq = Tools.getRandomString(4);
		 while(inSeq.length()<5){
			inSeq = "0"+inSeq;
		 }
		 cmdsn = cmdsn + inSeq;
		 String pointstr = (String) req.getDatas().get("points");
			String[] points = pointstr.split(";");

			String xys = "";
			String[] maxLngLat = Tools.getRecMaxLntLat(points[0], points[1]);
			String[] minLngLat = Tools.getRecMinLntLat(points[0], points[1]);

			double maxLongitude = Double.parseDouble(maxLngLat[0]);
			double maxLatitude = Double.parseDouble(maxLngLat[1]);
			
			double minLongitude = Double.parseDouble(minLngLat[0]);
			double minLatitude = Double.parseDouble(minLngLat[1]);
			
		String ret = "";
		String cmd = "(CMD,RNG," + covertDuToDUFEN(minLatitude) + "N," + covertDuToDUFEN(minLongitude) + "E,"
				+ covertDuToDUFEN(maxLatitude) + "N," + covertDuToDUFEN(maxLongitude) + "E)";
		ByteBuffer buf = ByteBuffer.allocate(14 + cmd.length());
		buf.put((byte) 0x5B);
		buf.put((byte) 0x32);
		buf.put(cmdsn.getBytes());
		buf.put((byte) cmd.length());
		buf.put(cmd.getBytes());
		buf.put((byte) 0x5D);
		ret = Tools.bytesToHexString(buf.array()) ;
	 
		return ret;
	}
 
	private String covertDuToDUFEN(double xys) {

		String ret = "";
		String xy = String.valueOf(xys);
		String du = xy.substring(0, xy.lastIndexOf("."));// 
		String fen = "0" + xy.substring(xy.lastIndexOf("."));//  
		double df = Double.parseDouble(fen) * 60;// 
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumIntegerDigits(2);
		nf.setMinimumIntegerDigits(2);
		nf.setMaximumFractionDigits(4);
		nf.setMinimumFractionDigits(4);
		String sfen = nf.format(df).replaceAll("\\,", "");
		ret = du + sfen;
		return ret;
	}

	public static void main(String[] args) {
	}

}
